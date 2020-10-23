package com.dimaslanjaka.gradle.xml

import com.dimaslanjaka.gradle.utils.ConsoleColors.Companion.success
import com.dimaslanjaka.gradle.utils.file.FileHelper
import org.gradle.api.Project
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.SAXException
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.StringWriter
import java.util.*
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


object AndroidManifestPermission {
    @JvmStatic
    lateinit var manifestLocation: String
    lateinit var rootProject: File
    lateinit var doc: Document
    var listPerms: Array<String?>? = arrayOf(
            "android.permission.INTERNET",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.VIBRATE",
            "android.permission.GET_ACCOUNTS",
            "android.permission.RECEIVE_BOOT_COMPLETED",
            "android.permission.BLUETOOTH",
            "android.permission.WAKE_LOCK",
            "com.google.android.c2dm.permission.RECEIVE"
    )

    @JvmStatic
    fun apply(project: Project) {
        rootProject = project.rootDir
        success("Root Project", project.rootDir)
        if (File(rootProject, "src/main/AndroidManifest.xml").exists()) {
            manifestLocation = File(rootProject, "src/main/AndroidManifest.xml").absolutePath
            success("Manifest Located On", manifestLocation)
        }

        val test = fix(manifestLocation)
    }

    /*
    @JvmStatic
    fun main(args: Array<String>) {
        val builder = ProjectBuilder().setRoot("build/test")
        val project = builder.instance
        try {
            builder.newFile("src/main/AndroidManifest.xml", builder.getText("https://raw.githubusercontent.com/ezzieyguywuf/Settings-Profiles/master/AndroidManifest.xml"))
            apply(project)
            ConsoleColors.error("Root Project", project.rootDir)
            ConsoleColors.success("Manifest Located On", manifestLocation)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
     */

    @Throws(Exception::class)
    fun fix(xml: Any?) {
        val dbf = DocumentBuilderFactory.newInstance()
        val db: DocumentBuilder?
        var allowtowrite = false
        try {
            db = dbf.newDocumentBuilder()
            doc = when (xml) {
                is InputStream -> {
                    db.parse(xml as InputStream?)
                }
                is File -> {
                    db.parse(xml as File?)
                }
                is String -> {
                    db.parse(xml as String?)
                }
                else -> {
                    throw Exception("XML must be instance of InputStream/String/File")
                }
            }
            doc.documentElement.normalize()
            if (!doc.documentElement.nodeName.equals("manifest", ignoreCase = true)) {
                throw Exception(String.format("Invalid AndroidManifest.xml root(%s)", doc.documentElement.nodeName))
            }
            val permAttr: MutableList<String?> = ArrayList()
            if (doc.getElementsByTagName("uses-permission").length > 0) {
                val permList = removeDuplicates(doc.getElementsByTagName("uses-permission"), arrayOf("android:name"))
                if (permList != null) {
                    for (i in 0 until permList.length) {
                        val itemPerm = permList.item(i) as Element
                        val itemPermAttributes = itemPerm.attributes
                        for (q in 0 until itemPermAttributes.length) {
                            val attr = itemPermAttributes.item(q)
                            permAttr.add(attr.nodeName + " = \"" + attr.nodeValue + "\"")
                        }
                    }
                }
            }
            if (permAttr.size > 0) {
                for (itemPerms in listPerms!!) {
                    if (!itemPerms?.let { permAttr.toString().contains(it) }!!) {
                        allowtowrite = true
                        val newElement = doc.createElement("uses-permission")
                        newElement.setAttribute("android:name", itemPerms)
                        val r = doc.documentElement
                        // insert into first child of root element
                        r.insertBefore(newElement, r.firstChild)
                        r.normalize()
                    }
                }
            }
            val domSource = DOMSource(doc)
            val writer = StringWriter()
            val result = StreamResult(writer)
            val tf: TransformerFactory = TransformerFactory.newInstance()
            val transformer: Transformer = tf.newTransformer()
            //transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            //transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            transformer.transform(domSource, result)
            val xmlString = result.writer.toString()
            val formattedXml = XmlFormatter().format(xmlString)
            if (allowtowrite) FileHelper.createNew(xml, formattedXml)
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
        } catch (e: SAXException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun removeDuplicates(nodeList: NodeList?, attributeNames: Array<String?>?): NodeList? {
        val allValues = ArrayList<String?>()
        //System.out.println(nodeList.getLength());
        if (nodeList != null) {
            for (i in 0 until nodeList.length) {
                val element = nodeList.item(i) as Element
                if (attributeNames != null) {
                    for (attributeName in attributeNames) {
                        val item = element.getAttribute(attributeName)
                        //System.out.println(item);
                        if (!allValues.contains(item)) {
                            allValues.add(item)
                        } else {
                            // remove element from nodeList
                            element.parentNode.removeChild(element)
                        }
                    }
                }
            }
        }

        //System.out.println(nodeList.getLength());
        return nodeList
    }

    private fun removeDuplicates(node: Node?, aux: MutableList<String?>?) {
        //check if that node exists already
        if (aux != null) {
            if (node != null) {
                if (aux.contains(node.nodeName)) {
                    val parentNode = node.parentNode
                    val value = parentNode.textContent
                    parentNode.removeChild(node)
                    parentNode.textContent = value
                } else {
                    //add node name to aux list
                    aux.add(node.nodeName)
                }
            }
        }
        val nodeList = node?.childNodes
        if (nodeList != null) {
            for (i in 0 until nodeList.length) {
                val currentNode = nodeList.item(i)
                if (currentNode.nodeType == Node.ELEMENT_NODE) {
                    //calls this method for all the children which is Element
                    removeDuplicates(currentNode, aux)
                }
            }
        }
    }
}