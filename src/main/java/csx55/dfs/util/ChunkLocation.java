package csx55.dfs.util;

import csx55.dfs.chunk.ChunkMetadata;
import csx55.dfs.wireformats.Event;

import java.io.IOException;

public class ChunkLocation extends Event {

    private String ipAddress;
    private int portNumber;
    private int sequenceNumber;

    public ChunkLocation(String id, int sequenceNumber) {
        super(-1);
        String[] parts = id.split(":");
        this.ipAddress = parts[0];
        this.portNumber = Integer.parseInt(parts[1]);
        this.sequenceNumber = sequenceNumber;
    }

    public ChunkLocation(byte[] bytes) throws IOException {
        super(bytes);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    @Override
    protected void marshall() throws IOException {
        marshallString(ipAddress);
        dataOutputStream.writeInt(portNumber);
        dataOutputStream.writeInt(sequenceNumber);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.ipAddress = unmarshallString();
        this.portNumber = dataInputStream.readInt();
        this.sequenceNumber = dataInputStream.readInt();
    }

    @Override
    public int hashCode() {
        return ("" + sequenceNumber).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChunkLocation other = (ChunkLocation) obj;
        return this.sequenceNumber == other.getSequenceNumber();
    }

    @Override
    public String toString() {
        return ipAddress + ":" + portNumber + ": " + sequenceNumber;
    }

}
