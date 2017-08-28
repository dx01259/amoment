package com.amoment.protocol.core;

import com.amoment.util.ResourceManager;
import com.amoment.util.UtilXml;
import org.dom4j.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProtocolFactory {

    public class ProtocolObject {
        public Integer id;
        public String name;
        public String encoding;
        public String impl;
    }

    private final String Config_File_Name = "protocols.xml";
    private final Map<Integer, ProtocolObject> protocolMap = new HashMap<>();
    private static final ProtocolFactory protocolFactory = new ProtocolFactory();


    private ProtocolFactory() {

        try {
            init();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ProtocolFactory instance() {
        return protocolFactory;
    }

    private void init() throws Exception {
        UtilXml utilXml = new UtilXml(ResourceManager.getUserDirectory() + Config_File_Name);
        List<Node> nodeList = utilXml.getElement("//protocols").selectNodes("protocol");
        for (Node node : nodeList
             ) {
            ProtocolObject protocolObject = getProtocol(node);
            protocolMap.put(protocolObject.id, protocolObject);
        }
    }

    private ProtocolObject getProtocol(Node node) {

        ProtocolObject protocol = new ProtocolObject();
        protocol.id = Integer.valueOf(node.selectSingleNode("id").getText());
        protocol.name = String.valueOf(node.selectSingleNode("name").getText());
        protocol.impl = String.valueOf(node.selectSingleNode("impl").getText());
        protocol.encoding = String.valueOf(node.selectSingleNode("encoding").getText());

        return protocol;
    }

    public Map<Integer, ProtocolObject> getProtocolMap() {
        return protocolMap;
    }
}
