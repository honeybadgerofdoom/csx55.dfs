package csx55.dfs.util;


/*
Data container for chunk server ip/port
 */
public class ChunkServerInfo {

    private final String ipAddress;
    private final int portNumber;

    public ChunkServerInfo(String ipAddress, int portNumber) {
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPortNumber() {
        return portNumber;
    }

    // ToDo ctor that takes bytes, write toBytes()

    /*
    Check if a given ID matches this ChunkServerInfo
     */
    public boolean matchesId(String id) {
        String[] parts = id.split(":");
        boolean matchesIp = ipAddress.equals(parts[0]);
        boolean matchesPort = portNumber == Integer.parseInt(parts[1]);
        return matchesIp && matchesPort;
    }

}
