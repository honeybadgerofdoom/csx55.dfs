package csx55.dfs.wireformats;

import csx55.dfs.util.ChunkLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DownloadControlPlaneReply extends Event {

    private int numberOfChunks;
    private List<ChunkLocation> chunkLocationList;
    private String filename;
    private String newFileName;

    public DownloadControlPlaneReply(List<ChunkLocation> chunkLocationList, String filename, String newFileName) {
        super(Protocol.DOWNLOAD_CONTROL_PLANE_REPLY);
        this.numberOfChunks = chunkLocationList.size();
        this.chunkLocationList = chunkLocationList;
        this.filename = filename;
        this.newFileName = newFileName;
    }

    public DownloadControlPlaneReply(byte[] bytes) throws IOException {
        super(bytes);
    }

    public List<ChunkLocation> getChunkLocationList() {
        return chunkLocationList;
    }

    public String getFilename() {
        return filename;
    }

    public String getNewFileName() {
        return newFileName;
    }

    @Override
    protected void marshall() throws IOException {
        dataOutputStream.writeInt(numberOfChunks);
        for (ChunkLocation chunkLocation : chunkLocationList) {
            marshallBytes(chunkLocation.getBytes());
        }
        marshallString(filename);
        marshallString(newFileName);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.numberOfChunks = dataInputStream.readInt();
        this.chunkLocationList = new ArrayList<>();
        for (int i = 0; i < numberOfChunks; i++) {
            ChunkLocation chunkLocation = new ChunkLocation(unmarshallBytes());
            chunkLocationList.add(chunkLocation);
        }
        this.filename = unmarshallString();
        this.newFileName = unmarshallString();
    }

    @Override
    public String toString() {
        String rtn = filename + ": {\n";
        for (ChunkLocation chunkLocation : chunkLocationList) {
            rtn += "\t" + chunkLocation + "\n";
        }
        rtn += "}";
        return rtn;
    }

}
