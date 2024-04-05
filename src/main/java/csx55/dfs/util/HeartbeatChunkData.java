package csx55.dfs.util;

import csx55.dfs.chunk.Chunk;

import java.io.*;

public class HeartbeatChunkData {

    private final String filename;
    private final int sequenceNumber;

    public HeartbeatChunkData(Chunk chunk) {
        this.filename = chunk.getFilename();
        this.sequenceNumber = chunk.getChunkMetadata().getSequenceNumber();
    }

    public HeartbeatChunkData(byte[] bytes) throws IOException {
        ByteArrayInputStream bArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(bArrayInputStream));

        int elementLength = din.readInt();
        byte[] filenameBytes = new byte[elementLength];
        din.readFully(filenameBytes);
        this.filename = new String(filenameBytes);

        this.sequenceNumber = din.readInt();

        bArrayInputStream.close();
        din.close();
    }

    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        byte[] bytes = filename.getBytes();
        int elementLength = bytes.length;
        dout.writeInt(elementLength);
        dout.write(bytes);

        dout.writeInt(sequenceNumber);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        return marshalledBytes;
    }

    public String getFilename() {
        return filename;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    @Override
    public String toString() {
        return "Sequence Number: " + sequenceNumber;
    }

}
