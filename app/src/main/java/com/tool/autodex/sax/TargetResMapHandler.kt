package com.tool.autodex.sax

import com.tool.autodex.checkHeader
import org.xml.sax.Attributes

class TargetResMapHandler : XmlHandler<String, String>() {

    override fun Attributes.getTypeValue(): String = getValue(getIndex("id"))

    override fun Attributes.getNameValue(): String = getValue(getIndex("name"))

    override fun areTargetQName(qName: String?): Boolean = qName == "public"

    override fun addRes(map: MutableMap<String, String>, key: String, value: String) {
        if (!checkHeader(value)) return
        map[key] = value
    }

}