package org.openhab.binding.networkscan.internal.nmapparser;

import java.util.ArrayList;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author peter
 */
public class SubnetHosts {

    private ArrayList<Host> hosts;

    public SubnetHosts() {
        hosts = new ArrayList<Host>();
    }

    public void addHost(String name, String ipv4) {
        hosts.add(new Host(name, ipv4));
    }

    public void addHost(Host host) {
        hosts.add(host);
    }

    public String getAllHostString() {
        String ret = "";
        for (int itr = 0; itr < hosts.size(); itr++) {
            ret += hosts.get(itr).getName();
            ret += ",";
            ret += hosts.get(itr).getAddrIpv4();
            ret += ";";
        }
        return ret;
    }

    public String getAllHostPortString() {
        String ret = "";
        for (int itr = 0; itr < hosts.size(); itr++) {
            ret += hosts.get(itr).getName();
            ret += ",";
            ret += hosts.get(itr).getAddrIpv4();
            ret += ",";
            ret += hosts.get(itr).getPorts().getLength();
            if (hosts.get(itr).getPorts().getLength() > 0) {
                ret += ",";
                for (int itr2 = 0; itr2 < hosts.get(itr).getPorts().getLength(); itr2++) {
                    ret += hosts.get(itr).getPorts().getPort(itr2).getPortId();
                    ret += ",";
                    ret += hosts.get(itr).getPorts().getPort(itr2).getProtocoll();
                    ret += ",";
                    ret += hosts.get(itr).getPorts().getPort(itr2).getService();
                    ret += ",";
                    ret += hosts.get(itr).getPorts().getPort(itr2).getState();
                    if (itr2 < (hosts.get(itr).getPorts().getLength() - 1)) {
                        ret += ",";
                    }

                }
            }
            ret += ";";
        }
        return ret;
    }

}
