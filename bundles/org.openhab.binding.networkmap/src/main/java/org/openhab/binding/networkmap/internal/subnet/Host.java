package org.openhab.binding.networkmap.internal.subnet;

/**
 *
 * @author peter
 */
public class Host {

    private String hostIPv4;
    private String hostName;
    private String hostStatus;
    private String hostLastSeen;
    private String hostLastUpdate;

    public Host() {
        this.hostIPv4 = "undef";
        this.hostName = "undef";
        this.hostStatus = "undef";
        this.hostLastSeen = "undef";
        this.hostLastUpdate = "undef";
    }

    public String getHostLastSeen() {
        return hostLastSeen;
    }

    public void setHostLastSeen(String hostLastSeen) {
        this.hostLastSeen = hostLastSeen;
    }

    public String getLastUpdate() {
        return hostLastUpdate;
    }

    public void setLastUpdate(String hostLastUpdate) {
        this.hostLastUpdate = hostLastUpdate;
    }

    public String getHostStatus() {
        return hostStatus;
    }

    public void setHostStatus(String hostStatus) {
        this.hostStatus = hostStatus;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostName() {
        return this.hostName;
    }

    public void setHostIPv4(String hostIPv4) {
        this.hostIPv4 = hostIPv4;
    }

    public String getHostIPv4() {
        return this.hostIPv4;
    }

}
