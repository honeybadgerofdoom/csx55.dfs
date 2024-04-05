package csx55.dfs.chunk;


import csx55.dfs.wireformats.ChunkDelivery;

import java.util.ArrayList;
import java.util.List;


/*
Manages Chunks for a ChunkServer
 */
public class ChunkManager {

    private final List<Chunk> chunks;

    public ChunkManager() {
        this.chunks = new ArrayList<>();
    }


    /*
    Creates a new chunk object
     */
    public void addChunk(ChunkDelivery chunkDelivery) {
        Chunk chunk = new Chunk(chunkDelivery);
        this.chunks.add(chunk);
    }

    public void printChunks() {
        for (Chunk chunk : chunks) {
            ChunkMetadata chunkMetadata = chunk.getChunkMetadata();
            System.out.println("Chunk { " + chunkMetadata + " }");
        }
    }

}
