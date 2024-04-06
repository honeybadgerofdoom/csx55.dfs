package csx55.dfs.wireformats;

import java.io.IOException;

public class DownloadDataPlaneReply extends Event implements Comparable<DownloadDataPlaneReply> {

    private byte[] chunkBytes;
    private String filename;
    private int sequenceNumber;
    private int numberOfChunks;

    public DownloadDataPlaneReply(byte[] chunkBytes, String filename, int sequenceNumber, int numberOfChunks) {
        super(Protocol.DOWNLOAD_DATA_PLANE_REPLY);
        this.chunkBytes = chunkBytes;
        this.filename = filename;
        this.sequenceNumber = sequenceNumber;
        this.numberOfChunks = numberOfChunks;
    }

    public DownloadDataPlaneReply(byte[] bytes) throws IOException {
        super(bytes);
    }

    public byte[] getChunkBytes() {
        return chunkBytes;
    }

    public String getFilename() {
        return filename;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public int getNumberOfChunks() {
        return numberOfChunks;
    }

    @Override
    protected void marshall() throws IOException {
        marshallBytes(chunkBytes);
        marshallString(filename);
        dataOutputStream.writeInt(sequenceNumber);
        dataOutputStream.writeInt(numberOfChunks);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.chunkBytes = unmarshallBytes();
        this.filename = unmarshallString();
        this.sequenceNumber = dataInputStream.readInt();
        this.numberOfChunks = dataInputStream.readInt();
    }

    @Override
    public String toString() {
        return "Reply with " + chunkBytes.length + " bytes for '" + filename + "', sequence #" + sequenceNumber;
    }


    @Override
    public int compareTo(DownloadDataPlaneReply downloadDataPlaneReply) {
        return Integer.compare(downloadDataPlaneReply.getSequenceNumber(), this.sequenceNumber);
    }
}
