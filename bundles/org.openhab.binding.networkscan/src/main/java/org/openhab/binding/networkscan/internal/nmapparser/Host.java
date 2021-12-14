package org.openhab.binding.networkscan.internal.nmapparser;

/**
 *
 * @author peter
 */
public class Host {

    private String name;
    private String ipv4;
    private String start;
    private String stop;
    private HostPorts hostports;

    public Host(String name, String ipv4) {
        this.name = name;
        this.ipv4 = ipv4;
    }

    public Host() {
        this.name = "undef";
        this.ipv4 = "undef";
        this.start = "undef";
        this.stop = "undef";
        hostports = new HostPorts();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setAddrIpv4(String ipv4) {
        this.ipv4 = ipv4;
    }

    public String getAddrIpv4() {
        return this.ipv4;
    }

    public void setTimeStart(String start) {
        this.start = start;
    }

    public String getTimeStart() {
        return this.start;
    }

    public void setTimeStop(String stop) {
        this.stop = stop;
    }

    public String getTimeStop() {
        return this.stop;
    }

    public void addPorts(HostPorts ports) {
        this.hostports = ports;
    }

    public HostPorts getPorts() {
        return this.hostports;
    }

}
