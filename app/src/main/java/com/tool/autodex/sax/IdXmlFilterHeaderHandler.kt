package com.tool.autodex.sax

import com.tool.autodex.checkHeader
import org.xml.sax.Attributes

class IdXmlFilterHeaderHandler : XmlHandler<String, MutableList<String>>() {

    override fun Attributes.getTypeValue(): String = getValue(getIndex("type"))

    override fun Attributes.getNameValue(): String = getValue(getIndex("name"))

    override fun areTargetQName(qName: String?): Boolean = qName == "item"

    override fun addRes(map: MutableMap<String, MutableList<String>>, key: String, value: String) {
        if (!checkHeader(value)) return
        var list = map[key]
        if (list == null) map[key] = mutableListOf<String>().also { l -> list = l }
        if (list?.contains(value) == false) list?.add(value)
    }

}