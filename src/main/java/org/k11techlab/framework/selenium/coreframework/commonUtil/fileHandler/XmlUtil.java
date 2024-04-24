package org.k11techlab.framework.selenium.coreframework.commonUtil.fileHandler;

import org.k11techlab.framework.selenium.coreframework.exceptions.AutomationError;
import org.k11techlab.framework.selenium.coreframework.commonUtil.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.logging.Logger;

public class XmlUtil {
    private static final Logger LOGGER = Logger.getLogger(XmlUtil.class.getName());

    //method to covert XML document to String
    public static String DocumentToString(Document docObj) {
        try {
            DOMSource domSource = new DOMSource(docObj);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (TransformerException e) {
            e.printStackTrace();
            throw new AutomationError("Error in Document to String Conversion");
        }
    }


    //method to get Document Object from XML with XML filepath as argument
    public static Document createDocumentObject(String xmlFilePath)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(xmlFilePath));
        return doc;
    }

    //method to get Tag names and values from XML document
    public static HashMap<String, String> getTagsAndValuesFromXML(String xmlFilePath, String... tagNames) {
        Document document = null;
        try {
            document = createDocumentObject(xmlFilePath);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            throw new AutomationError("Unable to parse the XML file.");
        }
        return (getTagsAndValuesFromXML(document, tagNames));
    }

    public static HashMap<String, String> getTagsAndValuesFromXML(Document documentObj, String... tagNames) {
        Document doc = documentObj;
        HashMap<String, String> tagsAndValues = new HashMap<String, String>();

        //get xml Text Content
        for (String tagName : tagNames) {
            String nodevalue = doc.getElementsByTagName(tagName).item(0).getTextContent();
            //using item(o) to get the 1st node with tagName.

            tagsAndValues.put(tagName, nodevalue);
        }
        if (tagsAndValues.isEmpty()) {
            WaitUtil.waitSeconds(3);
            for (String tagName : tagNames) {
                String nodevalue = doc.getElementsByTagName(tagName).item(0).getTextContent();
                tagsAndValues.put(tagName, nodevalue);
            }
        }
        return tagsAndValues;
    }


    public static String getTagValueFromXML(String uri, String tagName) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        DocumentBuilder builder = null;
        String tagValue = "";
        try {
            builder = dbFactory.newDocumentBuilder();
            Document document = null;
            try {
                document = builder.parse(uri);
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            tagValue = document.getElementsByTagName(tagName).item(0).getTextContent();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return tagValue;
    }


}
