package com.tool.autodex.sax

import org.xml.sax.InputSource
import org.xml.sax.XMLReader
import java.io.File

fun XMLReader.getTargetResMap(xmlPath: String) = with(this) {
    val mapHandler = TargetResMapHandler()
    this.contentHandler = mapHandler
    this.parse(InputSource(File(xmlPath).inputStream()))
    mapHandler.getMap()
}

fun XMLReader.getPublicXmlFilteredMap(xmlPath: String) = with(this) {
    val publicXmlHandler = PublicXmlFilterHeaderHandler()
    this.contentHandler = publicXmlHandler
    this.parse(InputSource(File(xmlPath).inputStream()))
    publicXmlHandler.getMap()
}

fun XMLReader.getIdXmlFilteredMap(xmlPath: String) = with(this){
    val idXmlHandler = IdXmlFilterHeaderHandler()
    this.contentHandler = idXmlHandler
    this.parse(InputSource(File(xmlPath).inputStream()))
    idXmlHandler.getMap()
}
