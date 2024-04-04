package csx55.dfs.util;

import csx55.dfs.wireformats.Event;
import csx55.dfs.wireformats.RegisterRequest;

import java.util.ArrayList;
import java.util.List;

public class ChunkServerManager {

    private final List<ChunkServerProxy> chunkServers;

    public ChunkServerManager() {
        this.chunkServers = new ArrayList<>();
    }

    public void add(RegisterRequest registerRequest) {
        ChunkServerProxy chunkServerProxy = new ChunkServerProxy(registerRequest.getIpAddress(), registerRequest.getPortNumber());
        this.chunkServers.add(chunkServerProxy);
    }

    public void sendToAllChunkServers(Event event) {
        for (ChunkServerProxy chunkServerProxy : chunkServers) {
            chunkServerProxy.writeToSocket(event);
        }
    }

}
