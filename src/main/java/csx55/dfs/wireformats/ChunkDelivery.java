package csx55.dfs.wireformats;

import java.io.IOException;

public class ChunkDelivery extends Event {

    private byte[] chunkBytes;
    private String filepath;
    private int sequenceNumber;
    private String listOfChunkServers;  // Each ChunkServer should be separated by "\n"

    public ChunkDelivery(byte[] chunkBytes, String filepath, int sequenceNumber, String listOfChunkServers) {
        super(Protocol.CHUNK_DELIVERY);
        this.chunkBytes = chunkBytes;
        this.filepath = filepath;
        this.sequenceNumber = sequenceNumber;
        this.listOfChunkServers = listOfChunkServers;
    }

    public ChunkDelivery(byte[] bytes) throws IOException {
        super(bytes);
    }

    public byte[] getChunkBytes() {
        return chunkBytes;
    }

    public String getFilepath() {
        return filepath;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public String getListOfChunkServers() {
        return listOfChunkServers;
    }

    @Override
    protected void marshall() throws IOException {
        marshallBytes(chunkBytes);
        marshallString(filepath);
        dataOutputStream.writeInt(sequenceNumber);
        marshallString(listOfChunkServers);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.chunkBytes = unmarshallBytes();
        this.filepath = unmarshallString();
        this.sequenceNumber = dataInputStream.readInt();
        this.listOfChunkServers = unmarshallString();
    }

}
