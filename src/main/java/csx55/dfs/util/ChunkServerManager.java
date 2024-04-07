package csx55.dfs.util;

import csx55.dfs.wireformats.Event;
import csx55.dfs.wireformats.Heartbeat;
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
    Update with a heartbeat
     */
    public void handleHeartbeat(Heartbeat heartbeat) {
        ChunkServerProxy chunkServerProxy = getChunkServerProxyById(heartbeat.getChunkServerID());  // Get the right reference
        if (chunkServerProxy != null) {
            chunkServerProxy.handleHeartbeat(heartbeat);  // Update it
        }
        else {
            System.err.println("Failed to find ChunkServerProxy from heartbeat with ID " + heartbeat.getChunkServerID() + " ");
        }
    }


    /*
    Gets all chunks for a given file
     */
    public List<ChunkLocation> getChunks(String filename, String path) {
        Set<ChunkLocation> chunkLocationList = new HashSet<>();
        for (ChunkServerProxy chunkServerProxy : chunkServers) {
            chunkServerProxy.getFileChunks(filename, path, chunkLocationList);
        }
        return new ArrayList<>(chunkLocationList);
    }


    /*
    Gets one chunk of a specific file, ignoring a given ChunkServer
     */
    public ChunkServerProxy getChunk(String filename, String path, int sequenceNumber, NodeProxy serverWithBadChunk) {
        for (ChunkServerProxy chunkServerProxy : chunkServers) {
            if (chunkServerProxy.getId().equals(serverWithBadChunk.getId())) continue;
            if (chunkServerProxy.hasChunk(filename, path, sequenceNumber)) {
                return chunkServerProxy;
            }
        }
        return null;
    }


    /*
    Get the right ChunkServerProxy reference
     */
    private ChunkServerProxy getChunkServerProxyById(String id) {
        for (ChunkServerProxy chunkServerProxy : chunkServers) {
            if (chunkServerProxy.getId().equals(id)) return chunkServerProxy;
            }
        return null;
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
    Print all chunk metadata held by ChunkServerManager
     */
    public void printChunkMetadata() {
        for (ChunkServerProxy chunkServerProxy : chunkServers) {
            System.out.println(chunkServerProxy.getChunkMetadataString());
        }
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
