package org.openhab.binding.networkmap.internal.subnet;

import java.util.ArrayList;

/**
 *
 * @author peter
 */
public class Ports {

    private final ArrayList<Port> ports;

    public Ports() {
        this.ports = new ArrayList<>();
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
