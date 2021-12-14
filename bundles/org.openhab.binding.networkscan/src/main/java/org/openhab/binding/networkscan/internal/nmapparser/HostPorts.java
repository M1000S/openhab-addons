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
public class HostPorts {

    private ArrayList<Port> ports;

    public HostPorts() {
        this.ports = new ArrayList<Port>();
    }

    public void addPort(Port port) {
        this.ports.add(port);
    }

    public int getLength() {
        return ports.size();
    }

    public Port getPort(int idx) {
        return ports.get(idx);

    }

}
