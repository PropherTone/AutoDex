package com.tool.autodex.dom

import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

fun insertResToPublicXml(
    xmlPath: String,
    map: MutableMap<String, MutableList<String>>
): Map<String, String>? = try {
    DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(File(xmlPath)).run {
        documentElement.normalize()
        val resNode = getElementsByTagName("resources").item(0)
        val publicElement = getElementsByTagName("public")
        if (publicElement.length <= 0) return null
        var attrType: String? = null
        var attrId: Int = -1
        val resMap = mutableMapOf<String, String>()
        publicElement?.forEach { node ->
            if (node.nodeType != Node.ELEMENT_NODE) return@forEach
            node as Element
            val type = node.getAttribute("type")
            val id = node.getAttribute("id").replace("0x", "").toInt(16)
            if (attrType != null && attrType != type) {
                map[attrType]?.takeIf { it.isNotEmpty() }?.let { list ->
                    var tempId = attrId
                    list.forEach { value ->
                        val idAttr = "0x${Integer.toHexString(++tempId)}"
                        val element = createElement("public").also {
                            it.setAttribute("id", idAttr)
                            it.setAttribute("name", value)
                            it.setAttribute("type", attrType)
                        }
                        resNode.insertBefore(element, node)
                        resMap[value] = idAttr
                    }
                }
                map.remove(attrType)
            }
            attrType = type
            if (id > attrId) attrId = id
        }
        map.forEach { (k, v) ->
            val lastElement = publicElement.item(publicElement.length - 1) as Element?
            var tempId = (lastElement?.getAttribute("id")?.replace("0x", "") ?: return@forEach)
                .toInt(16) + 65536
            v.forEach { value ->
                val element = createElement("public").also {
                    it.setAttribute("id", "0x${Integer.toHexString(++tempId)}")
                    it.setAttribute("name", value)
                    it.setAttribute("type", k)
                }
                resNode.insertBefore(element, publicElement.item(publicElement.length))
            }
        }
        val factor = TransformerFactory.newInstance()
        val former = factor.newTransformer()
        val ds = DOMSource(this)
        val stream = StreamResult(xmlPath)
        former.transform(ds, stream)
        resMap
    }
} catch (e: Throwable) {
    e.printStackTrace()
    null
}


fun insertIdToIdXml(xmlPath: String, map: MutableMap<String, MutableList<String>>) {
    try {
        DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(File(xmlPath)).apply {
            documentElement.normalize()
            val resNode = getElementsByTagName("resources").item(0)
            val publicElement = getElementsByTagName("item")
            if (publicElement.length <= 0) return
            map.forEach { (k, v) ->
                v.forEach { value ->
                    val element = createElement("item").also {
                        it.setAttribute("name", value)
                        it.setAttribute("type", k)
                    }
                    resNode.insertBefore(element, publicElement.item(publicElement.length))
                }
            }
            val factor = TransformerFactory.newInstance()
            val former = factor.newTransformer()
            val ds = DOMSource(this)
            val stream = StreamResult(xmlPath)
            former.transform(ds, stream)
        }
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

private inline fun NodeList.forEach(iterator: (Node) -> Unit) {
    for (i in 0 until length) iterator(item(i))
}
