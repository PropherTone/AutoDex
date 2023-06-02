package com.tool.autodex

import com.tool.autodex.dom.insertResToPublicXml
import com.tool.autodex.sax.getOriginPublicXmlFilterHeaderMap
import com.tool.autodex.sax.getTargetXmlFilterHeaderMap
import java.io.File
import javax.xml.parsers.SAXParserFactory

const val NAME_TAG_HEADER = ""

class Constant {
    val originDexPath = ""
    val targetDexPath = ""

    val originPublicXmlPath =
        ""
    val targetPublicXmlPath =
        ""
}

fun main(): Unit = with(Constant()) {

}

private fun Constant.combineResFile(fileFilter: (File) -> Boolean) {
    val originResPath = "${originDexPath}\\res"
    val targetResPath = "${targetDexPath}\\res"
    val targetResFile = File(targetResPath)
    if (targetResFile.exists() && targetResFile.isDirectory) {
        targetResFile.walkTopDown().forEach { file ->
            if (!file.isFile) return@forEach
            if (fileFilter(file)) {
                file.copyTo(File("$originResPath${file.filterPath(targetResPath)}"),true)
            }
        }
    }
}

private fun Constant.combineXml() {
    SAXParserFactory.newInstance().newSAXParser().xmlReader?.let {
        val originResMap = it.getOriginPublicXmlFilterHeaderMap(originPublicXmlPath)
        println(originResMap)
        val targetResMap = it.getTargetXmlFilterHeaderMap(targetPublicXmlPath)
        insertResToPublicXml(originPublicXmlPath, targetResMap)
    }
}

