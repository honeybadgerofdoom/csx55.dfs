package csx55.dfs.util;

import csx55.dfs.wireformats.Event;

import java.io.*;


/*
Data container for chunk server ip/port
 */
public class ChunkServerInfo extends Event {

    private String ipAddress;
    private int portNumber;

    public ChunkServerInfo(ChunkServerProxy chunkServerProxy) {
        super(-1);
        String[] parts = chunkServerProxy.getId().split(":");
        this.ipAddress = parts[0];
        this.portNumber = Integer.parseInt(parts[1]);
    }

    @Override
    protected void marshall() throws IOException {
        marshallString(ipAddress);
        dataOutputStream.writeInt(portNumber);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.ipAddress = unmarshallString();
        this.portNumber = dataInputStream.readInt();
    }

    public ChunkServerInfo(byte[] bytes) throws IOException {
        super(bytes);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPortNumber() {
        return portNumber;
    }


    /*
    Check if a given ID matches this ChunkServerInfo
     */
    public boolean matchesId(String id) {
        String[] parts = id.split(":");
        boolean matchesIp = ipAddress.equals(parts[0]);
        boolean matchesPort = portNumber == Integer.parseInt(parts[1]);
        return matchesIp && matchesPort;
    }

    @Override
    public String toString() {
        return "ChunkServerInfo [" + ipAddress + ":" + portNumber + "]";
    }

    @Override
    public int hashCode() {
        String id = ipAddress + ":" + portNumber;
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChunkServerInfo other = (ChunkServerInfo) obj;
        return this.ipAddress.equals(other.getIpAddress()) && this.portNumber == other.getPortNumber();
    }

}
