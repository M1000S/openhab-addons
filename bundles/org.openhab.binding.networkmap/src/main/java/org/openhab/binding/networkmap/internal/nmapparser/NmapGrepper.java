package org.openhab.binding.networkmap.internal.nmapparser;

public class NmapGrepper {

    private SubnetHosts subhosts;
    private int ctHosts;

    public NmapGrepper(String output) {
        this.subhosts = new SubnetHosts();
        this.ctHosts = 0;

        if (output != null && output != "") {
            String[] outputLines = output.split("\n");
            for (int line = 0; line < outputLines.length; line++) {
                if (outputLines[line].contains("Host")) {
                    String[] fields = outputLines[line].split("\t");
                    for (int field = 0; field < fields.length; field++) {

                        if (fields[field].contains("Host:")) {
                            this.ctHosts++;
                            String[] hostfield = fields[field].split(" ");
                            Host myHost = new Host();
                            myHost.setAddrIpv4(hostfield[1]);
                            String hostip = hostfield[2].replace("(", "");
                            myHost.setName(hostip.replace(")", ""));
                            this.subhosts.addHost(myHost);
                        }
                    }
                }
            }
        }
    }

    public String getHostString() {
        String hosts = "undef";
        hosts = this.subhosts.getAllHostString();
        return hosts;
    }

    public SubnetHosts getSubHosts() {

        return this.subhosts;

    }

    public int getHostCount() {
        return this.ctHosts;
    }
}
