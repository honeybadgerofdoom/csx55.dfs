package csx55.dfs.wireformats;

import java.io.*;

public class RegisterRequest extends Event {

    String ipAddress;
    int portNumber;

    public RegisterRequest(String ipAddress, int portNumber) {
        super(Protocol.REGISTER_REQUEST);
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
    }

    public RegisterRequest(byte[] bytes) throws IOException {
        super(bytes);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPortNumber() {
        return portNumber;
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

    @Override
    public String toString() {
        return ipAddress + ":" + portNumber;
    }

}