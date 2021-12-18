package org.openhab.binding.networkmap.internal.nmapparser;

/**
 *
 * @author peter
 */
public class Port {
    private String portId;
    private String protocoll;
    private String state;
    private String service;

    public Port() {
        this.portId = "undef";
        this.protocoll = "undef";
        this.service = "undef";
        this.state = "undef";
    }

    public String getPortId() {
        return portId;
    }

    public void setPortId(String portId) {
        this.portId = portId;
    }

    public String getProtocoll() {
        return protocoll;
    }

    public void setProtocoll(String protocoll) {
        this.protocoll = protocoll;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

}
