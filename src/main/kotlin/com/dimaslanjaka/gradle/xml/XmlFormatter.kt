package com.dimaslanjaka.gradle.xml

import org.apache.xml.serialize.OutputFormat
import org.apache.xml.serialize.XMLSerializer
import org.w3c.dom.Document
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import java.io.IOException
import java.io.StringReader
import java.io.StringWriter
import java.io.Writer
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException


/**
 * Pretty-prints xml, supplied as a string.
 *
 *
 * eg.
 * `
 * String formattedXml = new XmlFormatter().format("<tag><nested>hello</nested></tag>");
` *
 */
class XmlFormatter {
    fun format(unformattedXml: String): String {
        return try {
            val document = parseXmlFile(unformattedXml)
            val format = OutputFormat(document)
            format.lineWidth = 65
            format.indenting = true
            format.indent = 2
            val out: Writer = StringWriter()
            val serializer = XMLSerializer(out, format)
            serializer.serialize(document)
            out.toString()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun parseXmlFile(`in`: String): Document {
        return try {
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val `is` = InputSource(StringReader(`in`))
            db.parse(`is`)
        } catch (e: ParserConfigurationException) {
            throw RuntimeException(e)
        } catch (e: SAXException) {
            throw RuntimeException(e)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val unformattedXml = """<?xml version="1.0" encoding="UTF-8"?><QueryMessage
        xmlns="http://www.SDMX.org/resources/SDMXML/schemas/v2_0/message"
        xmlns:query="http://www.SDMX.org/resources/SDMXML/schemas/v2_0/query">
    <Query>
        <query:CategorySchemeWhere>
   					         <query:AgencyID>ECB
</query:AgencyID>
        </query:CategorySchemeWhere>
    </Query>
</QueryMessage>"""
            println(XmlFormatter().format(unformattedXml))
        }
    }
}