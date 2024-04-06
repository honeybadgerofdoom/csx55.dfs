package csx55.dfs.wireformats;

import java.io.IOException;

public class DownloadDataPlaneReply extends Event {

    private byte[] chunkBytes;
    private String filename;
    private int sequenceNumber;

    public DownloadDataPlaneReply(byte[] chunkBytes, String filename, int sequenceNumber) {
        super(Protocol.DOWNLOAD_DATA_PLANE_REPLY);
        this.chunkBytes = chunkBytes;
        this.filename = filename;
        this.sequenceNumber = sequenceNumber;
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

    @Override
    protected void marshall() throws IOException {
        marshallBytes(chunkBytes);
        marshallString(filename);
        dataOutputStream.writeInt(sequenceNumber);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.chunkBytes = unmarshallBytes();
        this.filename = unmarshallString();
        this.sequenceNumber = dataInputStream.readInt();
    }

    @Override
    public String toString() {
        return "Reply with " + chunkBytes.length + " bytes for '" + filename + "', sequence #" + sequenceNumber;
    }

}
