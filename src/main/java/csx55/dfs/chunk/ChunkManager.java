package csx55.dfs.chunk;


import csx55.dfs.util.Configs;
import csx55.dfs.wireformats.ChunkDelivery;

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

    public void printChunks() {
        for (Chunk chunk : chunks) {
            ChunkMetadata chunkMetadata = chunk.getChunkMetadata();
            System.out.println(chunk.getFilename() + " { " + chunkMetadata + " }, " + chunk.getChecksumStrings());
        }
    }

}
