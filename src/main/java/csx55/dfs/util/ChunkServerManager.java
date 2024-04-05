package csx55.dfs.util;

import csx55.dfs.wireformats.Event;
import csx55.dfs.wireformats.RegisterRequest;

import java.util.*;


/*
Manages access to ChunkServers via ChunkServerProxies
 */
public class ChunkServerManager {

    private final List<ChunkServerProxy> chunkServers;
    private final Random rng;

    public ChunkServerManager() {
        this.chunkServers = new ArrayList<>();
        this.rng = new Random(42);
    }


    /*
    Adds a newly registered ChunkServer
     */
    public void add(RegisterRequest registerRequest) {
        ChunkServerProxy chunkServerProxy = new ChunkServerProxy(registerRequest.getIpAddress(), registerRequest.getPortNumber());
        this.chunkServers.add(chunkServerProxy);
    }


    /*
    Send any Event to every ChunkServer
     */
    public void sendToAllChunkServers(Event event) {
        for (ChunkServerProxy chunkServerProxy : chunkServers) {
            chunkServerProxy.writeToSocket(event);
        }
    }


    /*
    Finds 3 random ChunkServerProxy objects with >64kb spaceLeft
     */
    public Set<ChunkServerInfo> findLocationsForChunks() {
        List<ChunkServerProxy> candidates = new ArrayList<>();
        for (ChunkServerProxy chunkServerProxy : chunkServers) {
            if (chunkServerProxy.hasSpaceLeft()) candidates.add(chunkServerProxy);
        }
        Set<ChunkServerInfo> randomChunkServers = new HashSet<>();
        while (randomChunkServers.size() < Configs.NUMBER_OF_REPLICAS) {
            int idx = rng.nextInt(candidates.size());
            ChunkServerProxy chunkServerProxy = candidates.get(idx);
            ChunkServerInfo chunkServerInfo = new ChunkServerInfo(chunkServerProxy);
            randomChunkServers.add(chunkServerInfo);
        }
        return randomChunkServers;
    }


    /*
    Formats the chunkServers into a table
     */
    @Override
    public String toString() {
        String tableString = "";
        String horizontalTablePiece = "";
        int numDashes = 19;
        for (int i = 0; i < numDashes; i++) {
            horizontalTablePiece += "-";
        }
        String tableCorner = "+";
        String tableLine = tableCorner;
        int numCols = 2;
        for (int i = 0; i < numCols; i++) {
            tableLine += horizontalTablePiece + tableCorner;
        }
        tableString += tableLine + "\n";
        tableString += String.format("| %-17s | %17s |", "ID", "Space Left") + "\n";
        tableString += tableLine + "\n";

        for (ChunkServerProxy chunkServerProxy : chunkServers) {
            tableString += chunkServerProxy + "\n";
        }

        tableString += tableLine;

        return tableString;
    }

}
