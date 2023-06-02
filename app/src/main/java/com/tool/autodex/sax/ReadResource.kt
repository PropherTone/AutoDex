package com.tool.autodex.sax

import org.xml.sax.InputSource
import org.xml.sax.XMLReader
import java.io.File

fun XMLReader.getOriginPublicXmlFilterHeaderMap(xmlPath: String) = with(this) {
    val publicXmlHandler = PublicXmlFilterHeaderHandler()
    this.contentHandler = publicXmlHandler
    this.parse(InputSource(File(xmlPath).inputStream()))
    publicXmlHandler.getMap()
}

fun XMLReader.getTargetXmlFilterHeaderMap(xmlPath: String) = with(this) {
    val publicXmlHandler = PublicXmlFilterHeaderHandler()
    this.contentHandler = publicXmlHandler
    this.parse(InputSource(File(xmlPath).inputStream()))
    publicXmlHandler.getMap()
}
