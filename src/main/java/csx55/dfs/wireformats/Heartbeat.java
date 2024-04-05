package csx55.dfs.wireformats;

import csx55.dfs.chunk.ChunkMetadata;
import csx55.dfs.util.HeartbeatChunkData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Heartbeat extends Event {

    private int spaceLeft;
    private int numberOfChunks;
    private List<HeartbeatChunkData> heartbeatChunkDataList;

    public Heartbeat(int spaceLeft, List<HeartbeatChunkData> heartbeatChunkDataList) {
        super(Protocol.HEARTBEAT);
        this.spaceLeft = spaceLeft;
        this.numberOfChunks = heartbeatChunkDataList.size();
        this.heartbeatChunkDataList = heartbeatChunkDataList;
    }

    public Heartbeat(byte[] bytes) throws IOException {
        super(bytes);
    }

    public int getSpaceLeft() {
        return spaceLeft;
    }

    public List<HeartbeatChunkData> getHeartbeatChunkDataList() {
        return heartbeatChunkDataList;
    }

    @Override
    protected void marshall() throws IOException {
        dataOutputStream.writeInt(spaceLeft);
        dataOutputStream.writeInt(numberOfChunks);
        for (HeartbeatChunkData heartbeatChunkData : heartbeatChunkDataList) {
            byte[] bytes = heartbeatChunkData.getBytes();
            marshallBytes(bytes);
        }
    }

    @Override
    protected void unmarshall() throws IOException {
        this.spaceLeft = dataInputStream.readInt();
        this.numberOfChunks = dataInputStream.readInt();
        this.heartbeatChunkDataList = new ArrayList<>();
        for (int i = 0; i < numberOfChunks; i++) {
            HeartbeatChunkData heartbeatChunkData = new HeartbeatChunkData(unmarshallBytes());
            heartbeatChunkDataList.add(heartbeatChunkData);
        }
    }

}
