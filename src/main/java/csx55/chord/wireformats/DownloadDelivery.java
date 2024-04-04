package csx55.chord.wireformats;

import java.io.IOException;

public class DownloadDelivery extends Event {

    private byte[] file;
    private String hopPath;
    private String filename;
    private boolean success;

    public DownloadDelivery(byte[] file, String hopPath, String filename, boolean success) {
        super(Protocol.DOWNLOAD_DELIVERY);
        this.file = file;
        this.hopPath = hopPath;
        this.filename = filename;
        this.success = success;
    }

    public DownloadDelivery(byte[] bytes) throws IOException {
        super(bytes);
    }

    public byte[] getFile() {
        return file;
    }

    public String getHopPath() {
        return hopPath;
    }

    public String getFilename() {
        return filename;
    }

    public boolean getSuccess() {
        return success;
    }

    @Override
    protected void marshall() throws IOException {
        marshallBytes(file);
        marshallString(hopPath);
        marshallString(filename);
        dataOutputStream.writeBoolean(success);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.file = unmarshallBytes();
        this.hopPath = unmarshallString();
        this.filename = unmarshallString();
        this.success = dataInputStream.readBoolean();
    }

    @Override
    public String toString() {
        return "Hop Path: " + hopPath + "\nfilename: " + filename + "\nSuccess: " + success;
    }
}
