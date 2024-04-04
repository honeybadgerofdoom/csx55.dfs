package csx55.chord.node;

import csx55.chord.Peer;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;

import java.io.IOException;

public class PeerInfo implements Comparable<PeerInfo> {

    private final int id;
    private final String ipAddress;
    private final int portNumber;

    public PeerInfo() {
        this.id = 1;
        this.ipAddress = "";
        this.portNumber = 1;
    }

    public PeerInfo(PeerRef peerRef) {
        this.id = peerRef.getId();
        this.ipAddress = peerRef.getIpAddress();
        this.portNumber = peerRef.getPortNumber();
    }

    public PeerInfo(Peer peer) {
        this.id = peer.getId();
        this.ipAddress = peer.getIpAddress();
        this.portNumber = peer.getPortNumber();
    }

    public PeerInfo(byte[] bytes) throws IOException {
        ByteArrayInputStream bArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(bArrayInputStream));

        this.id = din.readInt();

        int elementLength = din.readInt();
        byte[] ipBytes = new byte[elementLength];
        din.readFully(ipBytes);
        this.ipAddress = new String(ipBytes);

        this.portNumber = din.readInt();

        bArrayInputStream.close();
        din.close();
    }

    public int getId() {
        return id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(this.id);

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

    @Override
    public String toString() {
        return this.id + " " + this.ipAddress + ":" + this.portNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PeerInfo other = (PeerInfo) obj;
        return this.id == other.id;
    }

    @Override
    public int compareTo(PeerInfo peerInfo) {
        return Integer.compare(peerInfo.getId(), id);
    }

}
