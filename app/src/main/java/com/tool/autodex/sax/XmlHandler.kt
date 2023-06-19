package com.tool.autodex.sax

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

abstract class XmlHandler<K, V> : DefaultHandler() {
    private val map = mutableMapOf<K, V>()

    override fun startElement(
        uri: String?,
        localName: String?,
        qName: String?,
        attributes: Attributes?
    ) {
        super.startElement(uri, localName, qName, attributes)
        if (!areTargetQName(qName)) return
        attributes?.let {
            addRes(map, it.getTypeValue(), it.getNameValue())
        }
    }

    abstract fun Attributes.getTypeValue(): String

    abstract fun Attributes.getNameValue(): String

    abstract fun areTargetQName(qName: String?): Boolean

    abstract fun addRes(map: MutableMap<K, V>, key: String, value: String)

    fun getMap(): MutableMap<K, V> = map
}