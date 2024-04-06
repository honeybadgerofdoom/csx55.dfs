package csx55.dfs.wireformats;

import csx55.dfs.util.ChunkServerInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChunkDelivery extends Event {

    private byte[] chunkBytes;
    private String filepath;
    private int sequenceNumber;
    private List<ChunkServerInfo> chunkServerInfoList;
    private int numberOfChunkServers;

    public ChunkDelivery(LocationsForChunkReply locationsForChunkReply) {
        super(Protocol.CHUNK_DELIVERY);
        this.chunkBytes = locationsForChunkReply.getChunk();
        this.filepath = locationsForChunkReply.getDestination();
        this.sequenceNumber = locationsForChunkReply.getSequenceNumber();
        this.chunkServerInfoList = locationsForChunkReply.getLocations();
        this.numberOfChunkServers = chunkServerInfoList.size();
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

    public List<ChunkServerInfo> getChunkServerInfoList() {
        return chunkServerInfoList;
    }

    public boolean isLastChunkServer(String id) {
        return chunkServerInfoList.get(numberOfChunkServers - 1).matchesId(id);
    }

    public ChunkServerInfo getNext(String id) {
        for (int i = 0; i < numberOfChunkServers - 1; i++) {
            ChunkServerInfo current = chunkServerInfoList.get(i);
            if (current.matchesId(id) ) {
                return chunkServerInfoList.get(i + 1);
            }
        }
        return null;
    }

    @Override
    protected void marshall() throws IOException {
        marshallBytes(chunkBytes);
        marshallString(filepath);
        dataOutputStream.writeInt(sequenceNumber);
        dataOutputStream.writeInt(numberOfChunkServers);
        for (ChunkServerInfo chunkServerInfo : chunkServerInfoList) {
            marshallChunkServerInfo(chunkServerInfo);
        }
    }

    @Override
    protected void unmarshall() throws IOException {
        this.chunkBytes = unmarshallBytes();
        this.filepath = unmarshallString();
        this.sequenceNumber = dataInputStream.readInt();
        this.numberOfChunkServers = dataInputStream.readInt();
        this.chunkServerInfoList = new ArrayList<>();
        for (int i = 0; i < numberOfChunkServers; i++) {
            ChunkServerInfo chunkServerInfo = unmarshallChunkServerInfo();
            chunkServerInfoList.add(chunkServerInfo);
        }
    }

}
