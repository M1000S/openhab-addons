package org.openhab.binding.networkmap.internal.subnet;

/**
 *
 * @author peter
 */
public class Port {

    private String portNumber;
    private String portProtocol;
    private String portState;
    private String portService;

    public Port() {
        this.portNumber = "undef";
        this.portProtocol = "undef";
        this.portState = "undef";
        this.portService = "undef";
    }

    public String getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(String portNumber) {
        this.portNumber = portNumber;
    }

    public String getPortProtocol() {
        return portProtocol;
    }

    public void setPortProtocol(String portProtocol) {
        this.portProtocol = portProtocol;
    }

    public String getPortState() {
        return portState;
    }

    public void setPortState(String portState) {
        this.portState = portState;
    }

    public String getPortService() {
        return portService;
    }

    public void setPortService(String portService) {
        this.portService = portService;
    }

}
