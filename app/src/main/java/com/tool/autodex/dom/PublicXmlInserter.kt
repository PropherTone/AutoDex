package com.tool.autodex.dom

import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

fun insertIdToIdXML(xmlPath: String){

}

fun insertResToPublicXml(xmlPath: String, map: MutableMap<String, MutableList<String>>) {
    try {
        DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(File(xmlPath)).apply {
            documentElement.normalize()
            val resNode = getElementsByTagName("resources").item(0)
            val publicElement = getElementsByTagName("public")
            if (publicElement.length <= 0) return
            var attrType: String? = null
            var attrId: String? = null
            publicElement?.forEach { node ->
                if (node.nodeType != Node.ELEMENT_NODE) return@forEach
                node as Element
                val type = node.getAttribute("type")
                val id = node.getAttribute("id")
                if (attrType != null && attrType != type) {
                    map[attrType]?.takeIf { it.isNotEmpty() }?.let { list ->
                        println("$attrType,$attrId")
                        var tempId = (attrId?.replace("0x", "") ?: return@let).toInt(16)
                        list.forEach { value ->
                            val element = createElement("public").also {
                                it.setAttribute("id", "0x${Integer.toHexString(++tempId)}")
                                it.setAttribute("name", value)
                                it.setAttribute("type", attrType)
                            }
                            resNode.insertBefore(element, node)
                        }
                    }
                    map.remove(attrType)
                }
                attrType = type
                attrId = id
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
            val stream =
                StreamResult("")
            former.transform(ds, stream)
        }
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

private inline fun NodeList.forEach(iterator: (Node) -> Unit) {
    for (i in 0 until length) iterator(item(i))
}
