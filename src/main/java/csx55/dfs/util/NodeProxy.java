package csx55.dfs.util;

import csx55.dfs.wireformats.Event;

import java.io.IOException;

public class NodeProxy extends Event {

    private final String SEPARATOR = ":";
    private String id;
    private String ipAddress;
    private int portNumber;

    public NodeProxy(String ipAddress, int portNumber) {
        super(-1);
        this.id = ipAddress + SEPARATOR + portNumber;
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
    }

    public NodeProxy(String id) {
        super(-1);
        this.id = id;
        String[] parts = id.split(SEPARATOR);
        this.ipAddress = parts[0];
        this.portNumber = Integer.parseInt(parts[1]);
    }

    public NodeProxy(byte[] bytes) throws IOException {
        super(bytes);
    }

    public String getId() {
        return id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPortNumber() {
        return portNumber;
    }

    @Override
    protected void marshall() throws IOException {
        marshallString(id);
        marshallString(ipAddress);
        dataOutputStream.writeInt(portNumber);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.id = unmarshallString();
        this.ipAddress = unmarshallString();
        this.portNumber = dataInputStream.readInt();
    }

    @Override
    public String toString() {
        return id;
    }

}
