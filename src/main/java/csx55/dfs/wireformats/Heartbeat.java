package csx55.dfs.wireformats;

import csx55.dfs.chunk.ChunkMetadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Heartbeat extends Event {

    private int spaceLeft;
    private int numberOfChunks;
    private List<ChunkMetadata> chunkMetadataList;
    private String chunkServerID;

    public Heartbeat(int spaceLeft, List<ChunkMetadata> chunkMetadataList, String chunkServerID) {
        super(Protocol.HEARTBEAT);
        this.spaceLeft = spaceLeft;
        this.numberOfChunks = chunkMetadataList.size();
        this.chunkMetadataList = chunkMetadataList;
        this.chunkServerID = chunkServerID;
    }

    public Heartbeat(byte[] bytes) throws IOException {
        super(bytes);
    }

    public int getSpaceLeft() {
        return spaceLeft;
    }

    public List<ChunkMetadata> getChunkMetadataList() {
        return chunkMetadataList;
    }

    public String getChunkServerID() {
        return chunkServerID;
    }

    @Override
    protected void marshall() throws IOException {
        dataOutputStream.writeInt(spaceLeft);
        dataOutputStream.writeInt(numberOfChunks);
        for (ChunkMetadata chunkMetadata : chunkMetadataList) {
            byte[] bytes = chunkMetadata.getBytes();
            marshallBytes(bytes);
        }
        marshallString(chunkServerID);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.spaceLeft = dataInputStream.readInt();
        this.numberOfChunks = dataInputStream.readInt();
        this.chunkMetadataList = new ArrayList<>();
        for (int i = 0; i < numberOfChunks; i++) {
            ChunkMetadata chunkMetadata = new ChunkMetadata(unmarshallBytes());
            chunkMetadataList.add(chunkMetadata);
        }
        this.chunkServerID = unmarshallString();
    }

}
