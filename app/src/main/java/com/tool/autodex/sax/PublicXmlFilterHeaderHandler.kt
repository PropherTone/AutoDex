package com.tool.autodex.sax

import com.tool.autodex.checkHeader
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

class PublicXmlFilterHeaderHandler : DefaultHandler() {

    private val resMap = mutableMapOf<String, MutableList<String>>()

    override fun startElement(
        uri: String?,
        localName: String?,
        qName: String?,
        attributes: Attributes?
    ) {
        super.startElement(uri, localName, qName, attributes)
        if (qName == "public") {
//            println("startElement: $uri ,$localName ,$qName ,$attributes")
            attributes?.let {
                val type = it.getQName(0)
                val typeValue = it.getValue(0)
//                println("$type - $typeValue")
                val name = it.getQName(1)
                val nameValue = it.getValue(1)
//                println("$name - $nameValue")
//                val id = it.getQName(2)
//                val idValue = it.getValue(2)
//                println("$id - $idValue")
                if (checkHeader(nameValue)) addRes(typeValue, nameValue)
            }
//            println(">>>>>>>>>>>>>>>>>>>>")
        }
    }

    private fun addRes(key: String, value: String) {
        var list = resMap[key]
        if (list == null) resMap[key] = mutableListOf<String>().also { l -> list = l }
        if (list?.contains(value) == false) list?.add(value)
    }

    fun getMap(): MutableMap<String, MutableList<String>> = resMap

}