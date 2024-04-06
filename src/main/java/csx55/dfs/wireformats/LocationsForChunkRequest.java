package csx55.dfs.wireformats;

import java.io.IOException;

public class LocationsForChunkRequest extends Event {

    private int sequenceNumber;
    private byte[] chunk;
    private String filepath;
    private String destination;

    public LocationsForChunkRequest(int sequenceNumber, byte[] chunk, String filepath, String destination) {
        super(Protocol.LOCATIONS_FOR_CHUNK_REQUEST);
        this.sequenceNumber = sequenceNumber;
        this.chunk = chunk;
        this.filepath = filepath;
        this.destination = destination;
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

    public String getDestination() {
        return destination;
    }

    @Override
    protected void marshall() throws IOException {
        dataOutputStream.writeInt(sequenceNumber);
        marshallBytes(chunk);
        marshallString(filepath);
        marshallString(destination);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.sequenceNumber = dataInputStream.readInt();
        this.chunk = unmarshallBytes();
        this.filepath = unmarshallString();
        this.destination = unmarshallString();
    }

}
