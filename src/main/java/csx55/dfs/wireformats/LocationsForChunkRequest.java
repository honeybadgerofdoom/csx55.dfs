package csx55.dfs.wireformats;

import java.io.IOException;

public class LocationsForChunkRequest extends Event {

    private int sequenceNumber;
    private byte[] chunk;
    private String filepath;

    public LocationsForChunkRequest(int sequenceNumber, byte[] chunk, String filepath) {
        super(Protocol.LOCATIONS_FOR_CHUNK_REQUEST);
        this.sequenceNumber = sequenceNumber;
        this.chunk = chunk;
        this.filepath = filepath;
    }

    public LocationsForChunkRequest(byte[] bytes) throws IOException {
        super(bytes);
    }

    public byte[] getChunk() {
        return chunk;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public String getFilepath() {
        return filepath;
    }

    @Override
    protected void marshall() throws IOException {
        dataOutputStream.writeInt(sequenceNumber);
        marshallBytes(chunk);
        marshallString(filepath);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.sequenceNumber = dataInputStream.readInt();
        this.chunk = unmarshallBytes();
        this.filepath = unmarshallString();
    }

}
