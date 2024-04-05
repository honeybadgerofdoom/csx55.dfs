package csx55.dfs.chunk;


import csx55.dfs.util.Configs;
import csx55.dfs.util.HeartbeatChunkData;
import csx55.dfs.wireformats.ChunkDelivery;
import csx55.dfs.wireformats.Heartbeat;

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


    /*
    Return a Heartbeat message representing the state of the ChunkManager
    ToDo ensure this only gets data since LAST heartbeat (for minor heartbeat)
     */
    public Heartbeat getHeartbeat() {
        List<HeartbeatChunkData> heartbeatChunkDataList = new ArrayList<>();
        for (Chunk chunk : chunks) {
            HeartbeatChunkData heartbeatChunkData = new HeartbeatChunkData(chunk);
            heartbeatChunkDataList.add(heartbeatChunkData);
        }
        return new Heartbeat(spaceLeft, heartbeatChunkDataList);
    }


    public void printChunks() {
        for (Chunk chunk : chunks) {
            ChunkMetadata chunkMetadata = chunk.getChunkMetadata();
            System.out.println(chunk.getFilename() + " { " + chunkMetadata + " }");
        }
    }

}
