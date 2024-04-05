package csx55.dfs.util;

import java.io.*;


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

    public ChunkServerInfo(byte[] bytes) throws IOException {
        ByteArrayInputStream bArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(bArrayInputStream));

        int elementLength = din.readInt();
        byte[] ipBytes = new byte[elementLength];
        din.readFully(ipBytes);
        this.ipAddress = new String(ipBytes);

        this.portNumber = din.readInt();

        bArrayInputStream.close();
        din.close();
    }

    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        byte[] bytes = ipAddress.getBytes();
        int elementLength = bytes.length;
        dout.writeInt(elementLength);
        dout.write(bytes);

        dout.writeInt(portNumber);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        return marshalledBytes;
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

}
