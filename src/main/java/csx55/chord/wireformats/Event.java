package csx55.chord.wireformats;

import csx55.chord.node.PeerInfo;

import java.io.*;

public abstract class Event {

    private final int messageType;
    protected DataInputStream dataInputStream;
    protected DataOutputStream dataOutputStream;

    protected abstract void marshall() throws IOException;
    protected abstract void unmarshall() throws IOException;

    public Event(int messageType) {
        this.messageType = messageType;
    }

    public Event(byte[] bytes) throws IOException {
        ByteArrayInputStream bArrayInputStream = new ByteArrayInputStream(bytes);
        this.dataInputStream = new DataInputStream(new BufferedInputStream(bArrayInputStream));

        messageType = dataInputStream.readInt();
        unmarshall();

        bArrayInputStream.close();
        dataInputStream.close();
    }

    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        this.dataOutputStream = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dataOutputStream.writeInt(messageType);
        marshall();

        dataOutputStream.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dataOutputStream.close();
        return marshalledBytes;
    }

    protected void marshallString(String string) throws IOException {
        byte[] bytes = string.getBytes();
        marshallBytes(bytes);
    }

    protected String unmarshallString() throws IOException {
        byte[] bytes = unmarshallBytes();
        return new String(bytes);
    }

    protected void marshallBytes(byte[] bytes) throws IOException {
        int elementLength = bytes.length;
        dataOutputStream.writeInt(elementLength);
        dataOutputStream.write(bytes);
    }

    protected byte[] unmarshallBytes() throws IOException {
        int elementLength = dataInputStream.readInt();
        byte[] bytes = new byte[elementLength];
        dataInputStream.readFully(bytes);
        return bytes;
    }

    protected void marshallPeerInfo(PeerInfo peerInfo) throws IOException {
        byte[] peerInfoBytes = peerInfo.getBytes();
        int elementLength = peerInfoBytes.length;
        dataOutputStream.writeInt(elementLength);
        dataOutputStream.write(peerInfoBytes);
    }

    protected PeerInfo unmarshallPeerInfo() throws IOException {
        int peerInfoLength = dataInputStream.readInt();
        byte[] peerInfoBytes = new byte[peerInfoLength];
        dataInputStream.readFully(peerInfoBytes);
        return new PeerInfo(peerInfoBytes);
    }

    public int getType() {
        return messageType;
    }

}
