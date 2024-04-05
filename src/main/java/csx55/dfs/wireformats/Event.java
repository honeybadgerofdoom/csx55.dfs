package csx55.dfs.wireformats;

import csx55.dfs.util.ChunkServerInfo;

import java.io.*;


/*
Abstract superclass defines behavior for Events
Implements helpers for marshalling/unmarhsalling data
 */
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

    protected void marshallChunkServerInfo(ChunkServerInfo chunkServerInfo) throws IOException {
        byte[] bytes = chunkServerInfo.getBytes();
        int elementLength = bytes.length;
        dataOutputStream.writeInt(elementLength);
        dataOutputStream.write(bytes);
    }

    protected ChunkServerInfo unmarshallChunkServerInfo() throws IOException {
        int chunkServerInfoLength = dataInputStream.readInt();
        byte[] bytes = new byte[chunkServerInfoLength];
        dataInputStream.readFully(bytes);
        return new ChunkServerInfo(bytes);
    }

    public int getType() {
        return messageType;
    }

}
