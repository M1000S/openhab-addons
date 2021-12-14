package org.openhab.binding.networkscan.internal.nmapparser;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author peter
 */
public class NmapParser {
    private File nmapFile;
    private String docRoot;
    private SubnetHosts subhosts;
    private int ctHosts;

    private String NmapParser_ParsePortStatusNode(NodeList portNode) {
        String portStatus = "undef";
        for (int itr = 0; itr < portNode.getLength(); itr++) {
            if (portNode.item(itr).getNodeName().compareTo("state") == 0) {
                portStatus = ((Element) portNode.item(itr)).getAttribute("state");
            } else {
            }
        }
        return portStatus;
    }

    private String NmapParser_ParsePortServiceNode(NodeList portNode) {
        String portService = "undef";
        for (int itr = 0; itr < portNode.getLength(); itr++) {
            if (portNode.item(itr).getNodeName().compareTo("service") == 0) {
                portService = ((Element) portNode.item(itr)).getAttribute("name");
            } else {
            }
        }
        return portService;
    }

    private HostPorts NmapParser_ParseHostPortNode(NodeList hostPortNode) {
        HostPorts hostports = new HostPorts();

        for (int itr = 0; itr < hostPortNode.getLength(); itr++) {
            if (hostPortNode.item(itr).getNodeName().compareTo("port") == 0) {
                Port port = new Port();
                port.setProtocoll(((Element) hostPortNode.item(itr)).getAttribute("protocol"));
                port.setPortId(((Element) hostPortNode.item(itr)).getAttribute("portid"));
                port.setState(NmapParser_ParsePortStatusNode(hostPortNode.item(itr).getChildNodes()));
                port.setService(NmapParser_ParsePortServiceNode(hostPortNode.item(itr).getChildNodes()));
                hostports.addPort(port);
            } else {
            }
        }

        return hostports;
    }

    private String NmapParser_ParseHostNameNode(NodeList hostNameNode) {
        String hostname = "undef";
        for (int itr = 0; itr < hostNameNode.getLength(); itr++) {
            if (hostNameNode.item(itr).getNodeName().compareTo("hostname") == 0) {
                String atype = ((Element) hostNameNode.item(itr)).getAttribute("type");
                if (atype.compareTo("PTR") == 0) {
                    hostname = ((Element) hostNameNode.item(itr)).getAttribute("name");
                }
            } else {
            }
        }
        return hostname;
    }

    private Host NmapParser_ParseHostNode(NodeList hostNode) {
        Host myHost;
        myHost = new Host();
        for (int itr = 0; itr < hostNode.getLength(); itr++) {
            myHost.setTimeStart(((Element) hostNode.item(itr).getParentNode()).getAttribute("starttime"));
            myHost.setTimeStop(((Element) hostNode.item(itr).getParentNode()).getAttribute("endtime"));
            if (hostNode.item(itr).getNodeName().compareTo("address") == 0) {
                String atype = ((Element) hostNode.item(itr)).getAttribute("addrtype");
                if (atype.compareTo("ipv4") == 0) {
                    String haddr = ((Element) hostNode.item(itr)).getAttribute("addr");
                    myHost.setAddrIpv4(haddr);
                }
            } else if (hostNode.item(itr).getNodeName().compareTo("hostnames") == 0) {
                myHost.setName(NmapParser_ParseHostNameNode(hostNode.item(itr).getChildNodes()));
            } else if (hostNode.item(itr).getNodeName().compareTo("ports") == 0) {
                myHost.addPorts(NmapParser_ParseHostPortNode(hostNode.item(itr).getChildNodes()));
            } else {
            }
        }
        return myHost;
    }

    public NmapParser(String fileName) {

        this.ctHosts = 0;

        try {
            this.nmapFile = new File(fileName);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // an instance of builder to parse the specified xml file
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(fileName);
            doc.getDocumentElement().normalize();
            this.docRoot = doc.getDocumentElement().getNodeName();
            this.subhosts = new SubnetHosts();
            NodeList nodeList = doc.getElementsByTagName("host");
            ctHosts = nodeList.getLength();
            for (int itr = 0; itr < ctHosts; itr++) {
                Node node = nodeList.item(itr);
                Element eElement = (Element) node;
                NodeList hostNode = eElement.getChildNodes();
                this.subhosts.addHost(NmapParser_ParseHostNode(hostNode));
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.out.print("exception");
        }
    }

    public String getHostString() {
        String hosts = "undef";
        hosts = this.subhosts.getAllHostPortString();
        return hosts;
    }

    public int getHostCount() {
        return this.ctHosts;
    }
}
