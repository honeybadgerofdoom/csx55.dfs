package csx55.dfs.wireformats;

import csx55.dfs.util.ChunkServerInfo;
import csx55.dfs.util.Configs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocationsForChunkReply extends Event {

    private List<ChunkServerInfo> locations;
    private byte[] chunk;
    private int sequenceNumber;
    private String filepath;

    public LocationsForChunkReply(List<ChunkServerInfo> locations, LocationsForChunkRequest locationsForChunkRequest) {
        super(Protocol.LOCATIONS_FOR_CHUNK_REPLY);
        this.locations = locations;
        this.chunk = locationsForChunkRequest.getChunk();
        this.sequenceNumber = locationsForChunkRequest.getSequenceNumber();
        this.filepath = locationsForChunkRequest.getFilepath();
    }

    public LocationsForChunkReply(byte[] bytes) throws IOException {
        super(bytes);
    }

    public byte[] getChunk() {
        return chunk;
    }

    public List<ChunkServerInfo> getLocations() {
        return locations;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public String getFilepath() {
        return filepath;
    }

    @Override
    protected void marshall() throws IOException {
        for (ChunkServerInfo chunkServerInfo : locations) {
            marshallChunkServerInfo(chunkServerInfo);
        }
        marshallBytes(chunk);
        dataOutputStream.writeInt(sequenceNumber);
        marshallString(filepath);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.locations = new ArrayList<>();
        for (int i = 0; i < Configs.NUMBER_OF_REPLICAS; i++) {
            locations.add(unmarshallChunkServerInfo());
        }
        this.chunk = unmarshallBytes();
        this.sequenceNumber = dataInputStream.readInt();
        this.filepath = unmarshallString();
    }

    @Override
    public String toString() {
        String rtn = "Sequence #" + sequenceNumber + " {\n";
        for (ChunkServerInfo chunkServerInfo : locations) {
            rtn += "\t" + chunkServerInfo + "\n";
        }
        rtn += "}";
        return rtn;
    }

}
