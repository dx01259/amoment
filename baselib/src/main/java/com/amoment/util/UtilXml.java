package com.derivative.base.util;

import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by xudeng on 2017/6/7.
 */
public class UtilXml {

    private Document xmlDocument;

    public UtilXml(String file) throws MalformedURLException, DocumentException {

        SAXReader reader = new SAXReader();
        xmlDocument = reader.read(new File(file));
    }

    public Element getRootDocument(){
        return xmlDocument.getRootElement();
    }

    public String toString(){
        if (xmlDocument != null){
            return xmlDocument.asXML();
        }
        return null;
    }

    public static Document parseXml(String xmlText) {
        try {
            return DocumentHelper.parseText(xmlText);
        }catch (DocumentException e){

        }
        return null;
    }

    public Node getElement(String nodeName)
    {
        if (xmlDocument != null)
        {
            Node node = xmlDocument.selectSingleNode(nodeName);
            return node;
        }

        return null;
    }

    public List<Node> getAllElements(String nodeName)
    {
        if (xmlDocument != null)
        {
            return xmlDocument.selectNodes(nodeName);
        }

        return null;
    }

    public String getAttribute(Node node, String attr){
        if (node != null) {
            return node.getDocument().getRootElement().attribute(attr).getValue();
        }
        return null;
    }

    public void Close() {
        if (xmlDocument != null) {
            xmlDocument.clearContent();
        }
    }
}
