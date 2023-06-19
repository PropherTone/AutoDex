package com.tool.autodex

import com.tool.autodex.dom.insertIdToIdXml
import com.tool.autodex.dom.insertResToPublicXml
import com.tool.autodex.sax.getIdXmlFilteredMap
import com.tool.autodex.sax.getPublicXmlFilteredMap
import com.tool.autodex.sax.getTargetResMap
import java.io.File
import javax.xml.parsers.SAXParserFactory

const val NAME_TAG_HEADER = "beach_"

class Constant(val originDexPath: String, val targetDexPath: String) {

    val originPublicXmlPath = "$originDexPath\\res\\values\\public.xml"
    val targetPublicXmlPath = "$targetDexPath\\res\\values\\public.xml"
    val originIdXmlPath = "$originDexPath\\res\\values\\ids.xml"
    val targetIdXmlPath = "$targetDexPath\\res\\values\\ids.xml"

    private var originMap: Map<String, String> = mutableMapOf()
    private var targetMap: Map<String, String> = mutableMapOf()

    constructor(
        constant: Constant,
        originMap: Map<String, String>,
        targetMap: Map<String, String>
    ) : this(constant.originDexPath, constant.targetDexPath) {
        this.originMap = originMap
        this.targetMap = targetMap
    }

    fun getOriginMap() = originMap
    fun getTargetMap() = targetMap

    fun isIllegal(): Boolean {
        if (originDexPath.isEmpty()) {
            println("OriginDexPath should not be empty")
            return true
        }
        if (targetDexPath.isEmpty()) {
            println("TargetDexPath should not be empty")
            return true
        }
        if (originDexPath == targetDexPath) {
            println("OriginDexPath should not be equals TargetDexPath")
            return true
        }
        return false
    }
}

fun main() {
    println("Original path:")
    val originalPath = readLine()
    println("Target path:")
    val targetPath = readLine()
    Constant(
        originalPath ?: "",
        targetPath ?: ""
    ).takeIf { !it.isIllegal() }?.run {
        combineFiles(dir = "res", override = true) {
            val name = it.name
            name.startsWith(NAME_TAG_HEADER) || name.contains("ic_launcher")
        }
        combineXml()?.combineFiles(dir = "smali", mode = FileWriteMode.REWRITE) { true }
    }
    println("Done")
}

@JvmInline
value class FileWriteMode(val mode: String) {
    companion object {
        val REWRITE = FileWriteMode("REWRITE")
        val COPY = FileWriteMode("COPY")
    }
}

private inline fun Constant.combineFiles(
    dir: String,
    override: Boolean = false,
    mode: FileWriteMode = FileWriteMode.COPY,
    fileFilter: (File) -> Boolean
) {
    val originResPath = "${originDexPath}\\$dir"
    val targetResPath = "${targetDexPath}\\$dir"
    val targetResFile = File(targetResPath)
    if (!targetResFile.exists() || !targetResFile.isDirectory) return
    targetResFile.walkTopDown().forEach { file ->
        if (!file.isFile) return@forEach
        if (!fileFilter(file)) return@forEach
        try {
            if (mode == FileWriteMode.COPY) {
                file.copyTo(File("$originResPath${file.filterPath(targetResPath)}"), override)
                return@forEach
            }
            val contents = file.readLines().toMutableList()
            var isReplaced = false
            for (i in contents.indices) {
                with(contents[i]) {
                    if (!contains("const v") || !contains("const p")) return@with
                    val id = substring(indexOf("0x"), length)
                    getTargetMap()[id]?.let { type ->
                        getOriginMap()[type]?.let { targetId ->
                            contents[i] = replace(id, targetId)
                            isReplaced = true
                        }
                    }
                }
            }
            if (isReplaced) File("$originResPath${file.filterPath(targetResPath)}")
                .printWriter()
                .use { writer -> contents.forEach { content -> writer.println(content) } }
            else file.copyTo(File("$originResPath${file.filterPath(targetResPath)}"), override)
        } catch (e: FileAlreadyExistsException) {
            return@forEach
        }
    }
}

private fun Constant.combineXml(): Constant? {
    return SAXParserFactory.newInstance().newSAXParser().xmlReader?.let {
        val targetResMap = it.getPublicXmlFilteredMap(targetPublicXmlPath)
        val originMap = insertResToPublicXml(originPublicXmlPath, targetResMap)
        val idXmlFilteredMap = it.getIdXmlFilteredMap(targetIdXmlPath)
        insertIdToIdXml(originIdXmlPath, idXmlFilteredMap)
        it.getPublicXmlFilteredMap(originPublicXmlPath)
        originMap?.let { map ->
            Constant(this, map, it.getTargetResMap(targetPublicXmlPath))
        }
    }
}
