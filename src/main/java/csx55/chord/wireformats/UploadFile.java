package csx55.chord.wireformats;

import java.io.IOException;

public class UploadFile extends Event {

    private int hash;
    private String filename;
    private byte[] file;

    public UploadFile(int hash, String filename, byte[] file) {
        super(Protocol.UPLOAD_FILE);
        this.hash = hash;
        this.filename = filename;
        this.file = file;
    }

    public UploadFile(byte[] bytes) throws IOException {
        super(bytes);
    }

    public int getHash() {
        return hash;
    }

    public byte[] getFile() {
        return file;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    protected void marshall() throws IOException {
        dataOutputStream.writeInt(hash);
        marshallString(filename);
        marshallBytes(file);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.hash = dataInputStream.readInt();
        this.filename = unmarshallString();
        this.file = unmarshallBytes();
    }

}
