package org.openhab.binding.networkmap.internal.subnet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

/**
 *
 * @author peter
 */

public class Subnet {

    private static final boolean DEBUG = true;

    private final ArrayList<Host> hosts;
    private final ArrayList<Ports> ports;

    public Subnet() {
        hosts = new ArrayList<>();
        ports = new ArrayList<>();
    }

    public Host getHost(Integer idx) {
        return hosts.get(idx);
    }

    public void addHost(Host host) {
        if (this.hosts.contains(host)) {

        } else {
            this.hosts.add(host);
        }
    }

    public int findHostbyAddr(String addr) {
        int hostIdx = -1;
        for (int idx = 0; idx < hosts.size(); idx++) {
            if (hosts.get(idx).getHostIPv4().equals(addr)) {
                hostIdx = idx;
            }
        }

        return hostIdx;
    }

    public void print(String string) {
        if (DEBUG) {
            // out.print(string);
        }
    }

    public Port parseNmapGreppablePort(String port) {
        Port myPort = new Port();
        String strPort = port.replace("///,", "");
        print(strPort + "\n");
        String[] portService = strPort.split("//");
        print(portService[0] + "\n");
        print(portService[1] + "\n");
        myPort.setPortService(portService[1]);
        String[] portinfos = portService[0].split("/");

        myPort.setPortNumber(portinfos[0]);
        print(portinfos[0] + "\n");
        myPort.setPortState(portinfos[1]);
        print(portinfos[1] + "\n");
        myPort.setPortProtocol(portinfos[2]);
        print(portinfos[2] + "\n");

        return myPort;
    }

    public void parseNmapGreppableLine(String line, String date) {
        if (!line.contains("#")) {
            String[] fields = line.split("\t");
            Host myHost = new Host();
            Ports myPorts = new Ports();
            for (String field : fields) {
                // print(field.split(" ")[0] + "\n");
                String[] subfields = field.split(" ");
                switch (subfields[0]) {
                    case "Host:":
                        print(subfields[0] + "\n");
                        print(subfields[1] + "\n");
                        print(subfields[2] + "\n");
                        myHost.setHostIPv4(subfields[1]);
                        myHost.setHostName(subfields[2]);
                        break;
                    case "Status:":
                        print(subfields[0] + "\n");
                        print(subfields[1] + "\n");
                        myHost.setHostStatus(subfields[1]);
                        break;
                    case "Ports:":
                        for (int i = 1; i < subfields.length; i++) {
                            print(subfields[i] + "\n");
                            myPorts.addPort(parseNmapGreppablePort(subfields[i]));
                        }
                        break;
                    default:
                        break;
                }
            }
            int hostIdx = findHostbyAddr(myHost.getHostIPv4());
            if (hostIdx < 0) {
                myHost.setLastUpdate(date);
                if (myHost.getHostStatus().equals("Up")) {
                    myHost.setHostLastSeen(date);
                }
                hosts.add(myHost);
                ports.add(myPorts);

            } else {
                if (myPorts.getLength() > 0) {
                    ports.set(hostIdx, myPorts);
                }

                // hosts.set(hostIdx,myHost);
                hosts.get(hostIdx).setHostName(myHost.getHostName());
                hosts.get(hostIdx).setHostLastSeen(date);
                if (!myHost.getHostStatus().equals("undef")) {
                    if (myHost.getHostStatus().equals("Up")) {
                        myHost.setHostLastSeen(date);
                    }
                    hosts.get(hostIdx).setHostStatus(myHost.getHostStatus());

                }

            }
        } else {
        }
    }

    public String getDate(String line) {
        String strDate = "undef";
        String[] words = line.split(" ");
        if (words.length >= 10) {
            if (words[4].equals("initiated")) {
                strDate = words[6] + " " + words[7] + " " + words[8] + " " + words[9];
            }
            // String dateInString = "Dec 18 20:21:51 2021"; //Mon, 05 May 1980";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d HH:mm:ss yyyy", Locale.ENGLISH);
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(strDate, formatter);
            strDate = dateTime.format(formatter2);
            print(strDate);
        }

        return strDate;
    }

    public void parseNmapGreppable(String output, int type) {
        if (output != null) {
            String date;

            String[] outputLines = output.split("\n");
            date = getDate(outputLines[0]);
            if (type == 1) {
                for (Host host : hosts) {
                    host.setHostStatus("Down");
                    host.setLastUpdate(date);
                }
            }
            for (String outputLine : outputLines) {
                parseNmapGreppableLine(outputLine, date);
            }
            // hosts.sort(null);
        }
    }

    public int getHostCount() {
        return hosts.size();
    }

    public String getHostStates() {
        String ret = "";

        for (int itr = 0; itr < hosts.size(); itr++) {
            ret += hosts.get(itr).getHostStatus();
            if (itr + 1 < hosts.size()) {
                ret += ";";
            }
        }

        return ret;
    }

    public String getHostLastSeen() {
        String ret = "";

        for (int itr = 0; itr < hosts.size(); itr++) {
            ret += hosts.get(itr).getHostLastSeen();
            if (itr + 1 < hosts.size()) {
                ret += ";";
            }
        }

        return ret;
    }

    public String getHostLastUpdate() {
        String ret = "";

        for (int itr = 0; itr < hosts.size(); itr++) {
            ret += hosts.get(itr).getLastUpdate();
            if (itr + 1 < hosts.size()) {
                ret += ";";
            }
        }

        return ret;
    }

    public String getHostNames(boolean stripped) {
        String ret = "";

        for (int itr = 0; itr < hosts.size(); itr++) {
            String name = hosts.get(itr).getHostName();
            name = name.replace("()", "noname");
            name = name.replace("(", "");
            name = name.replace(")", "");

            if ((stripped) && (name.contains("."))) {
                ret += name.substring(0, name.indexOf("."));

            } else {
                ret += name;
            }

            if (itr + 1 < hosts.size()) {
                ret += ";";
            }
        }

        return ret;
    }

    public String getHostAddresses() {
        String ret = "";
        for (int itr = 0; itr < hosts.size(); itr++) {
            ret += hosts.get(itr).getHostIPv4();
            if (itr + 1 < hosts.size()) {
                ret += ";";
            }
        }
        return ret;
    }
    // class
}
