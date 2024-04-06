package csx55.dfs.wireformats;

import java.io.IOException;

public class DownloadDataPlaneRequest extends Event {

    private String filename;
    private int sequenceNumber;
    private int numberOfChunks;

    public DownloadDataPlaneRequest(String filename, int sequenceNumber, int numberOfChunks) {
        super(Protocol.DOWNLOAD_DATA_PLANE_REQUEST);
        this.filename = filename;
        this.sequenceNumber = sequenceNumber;
        this.numberOfChunks = numberOfChunks;
    }

    public DownloadDataPlaneRequest(byte[] bytes) throws IOException {
        super(bytes);
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
        marshallString(filename);
        dataOutputStream.writeInt(sequenceNumber);
        dataOutputStream.writeInt(numberOfChunks);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.filename = unmarshallString();
        this.sequenceNumber = dataInputStream.readInt();
        this.numberOfChunks = dataInputStream.readInt();
    }

    @Override
    public String toString() {
        return "Requesting '" + filename + "', sequence #" + sequenceNumber;
    }

}
