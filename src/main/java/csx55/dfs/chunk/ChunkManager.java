package csx55.dfs.chunk;


import csx55.dfs.util.Configs;
import csx55.dfs.wireformats.ChunkDelivery;
import csx55.dfs.wireformats.Heartbeat;
import csx55.dfs.wireformats.RepairChunkDataPlane;

import java.util.ArrayList;
import java.util.List;


/*
Manages Chunks for a ChunkServer
 */
public class ChunkManager {

    private final List<Chunk> chunks;
    private int spaceLeft;

    public ChunkManager() {
        this.chunks = new ArrayList<>();
        this.spaceLeft = Configs.GB;
    }


    /*
    Creates a new chunk object
     */
    public void addChunk(ChunkDelivery chunkDelivery) {
        Chunk chunk = new Chunk(chunkDelivery);
        this.chunks.add(chunk);
        this.spaceLeft -= Configs.CHUNK_SIZE;
    }


    public byte[] retrieveChunk(String filename, int sequenceNumber) {
        for (Chunk chunk : chunks) {
            if (chunk.isTarget(filename, sequenceNumber)) {
                return chunk.getChunkBytes();
            }
        }
        return null;
    }


    /*
    Repair a chunk
     */
    public void repairChunk(RepairChunkDataPlane repairChunkDataPlane) {
        for (Chunk chunk : chunks) {
            if (chunk.isTarget(repairChunkDataPlane.getFilepath(), repairChunkDataPlane.getSequenceNumber())) {
                chunk.getChunkMetadata().updateMetadata();
                chunk.writeToDisk(repairChunkDataPlane.getChunkBytes());
                String chunkName = repairChunkDataPlane.getFilepath() + "_" + repairChunkDataPlane.getSequenceNumber();
                System.out.println("Chunk '" + chunkName + "' repaired.");
                break;
            }
        }

    }


    /*
    Return a Heartbeat message representing the state of the ChunkManager
    ToDo ensure this only gets data since LAST heartbeat (for minor heartbeat)
     */
    public Heartbeat getHeartbeat(String id) {
        List<ChunkMetadata> heartbeatChunkDataList = new ArrayList<>();
        for (Chunk chunk : chunks) {
            heartbeatChunkDataList.add(chunk.getChunkMetadata());
        }
        return new Heartbeat(spaceLeft, heartbeatChunkDataList, id);
    }


    public void printChunks() {
        for (Chunk chunk : chunks) {
            ChunkMetadata chunkMetadata = chunk.getChunkMetadata();
            System.out.println(chunk.getFilename() + " { " + chunkMetadata + " }");
        }
    }

}
