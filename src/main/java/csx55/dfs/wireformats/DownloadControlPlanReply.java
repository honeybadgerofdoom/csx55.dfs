package csx55.dfs.wireformats;

import csx55.dfs.util.ChunkLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DownloadControlPlanReply extends Event {

    private int numberOfChunks;
    private List<ChunkLocation> chunkLocationList;

    public DownloadControlPlanReply(List<ChunkLocation> chunkLocationList) {
        super(Protocol.DOWNLOAD_CONTROL_PLAN_REPLY);
        this.numberOfChunks = chunkLocationList.size();
        this.chunkLocationList = chunkLocationList;
    }

    public DownloadControlPlanReply(byte[] bytes) throws IOException {
        super(bytes);
    }

    @Override
    protected void marshall() throws IOException {
        dataOutputStream.writeInt(numberOfChunks);
        for (ChunkLocation chunkLocation : chunkLocationList) {
            marshallBytes(chunkLocation.getBytes());
        }
    }

    @Override
    protected void unmarshall() throws IOException {
        this.numberOfChunks = dataInputStream.readInt();
        this.chunkLocationList = new ArrayList<>();
        for (int i = 0; i < numberOfChunks; i++) {
            ChunkLocation chunkLocation = new ChunkLocation(unmarshallBytes());
            chunkLocationList.add(chunkLocation);
        }
    }

    @Override
    public String toString() {
        String rtn = "{\n";
        for (ChunkLocation chunkLocation : chunkLocationList) {
            rtn += "\t" + chunkLocation + "\n";
        }
        rtn += "}";
        return rtn;
    }

}
