package csx55.chord.wireformats;

import java.io.*;

public class DeregisterResponse extends Event {

    private byte statusCode;

    public DeregisterResponse(byte statusCode) {
        super(Protocol.DEREGISTER_RESPONSE);
        this.statusCode = statusCode;
    }

    public DeregisterResponse(byte[] bytes) throws IOException {
        super(bytes);
    }

    @Override
    protected void marshall() throws IOException {
        dataOutputStream.writeByte(this.statusCode);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.statusCode = dataInputStream.readByte();
    }

    public int getStatusCode() {
        return this.statusCode;
    }

}