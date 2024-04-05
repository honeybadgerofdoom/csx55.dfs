package csx55.dfs.wireformats;

import csx55.dfs.util.ChunkServerInfo;
import csx55.dfs.util.Configs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocationsForChunkReply extends Event {

    List<ChunkServerInfo> locations;

    public LocationsForChunkReply(List<ChunkServerInfo> locations) {
        super(Protocol.LOCATIONS_FOR_CHUNK_REPLY);
        this.locations = locations;
    }

    public LocationsForChunkReply(byte[] bytes) throws IOException {
        super(bytes);
    }

    @Override
    protected void marshall() throws IOException {
        for (ChunkServerInfo chunkServerInfo : locations) {
            marshallChunkServerInfo(chunkServerInfo);
        }
    }

    @Override
    protected void unmarshall() throws IOException {
        this.locations = new ArrayList<>();
        for (int i = 0; i < Configs.NUMBER_OF_REPLICAS; i++) {
            locations.add(unmarshallChunkServerInfo());
        }
    }

    @Override
    public String toString() {
        String rtn = "LocationsForChunkReply {\n";
        for (ChunkServerInfo chunkServerInfo : locations) {
            rtn += "\t" + chunkServerInfo + "\n";
        }
        rtn += "}";
        return rtn;
    }

}
