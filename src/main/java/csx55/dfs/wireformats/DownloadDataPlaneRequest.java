package csx55.dfs.wireformats;

import java.io.IOException;

public class DownloadDataPlaneRequest extends Event {

    private String filename;
    private int sequenceNumber;

    public DownloadDataPlaneRequest(String filename, int sequenceNumber) {
        super(Protocol.DOWNLOAD_DATA_PLANE_REQUEST);
        this.filename = filename;
        this.sequenceNumber = sequenceNumber;
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

    @Override
    protected void marshall() throws IOException {
        marshallString(filename);
        dataOutputStream.writeInt(sequenceNumber);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.filename = unmarshallString();
        this.sequenceNumber = dataInputStream.readInt();
    }

}
