package csx55.dfs.wireformats;

import java.io.IOException;

public class RepairChunkDataPlane extends Event {

    private byte[] chunkBytes;
    private String filepath;
    private int sequenceNumber;

    public RepairChunkDataPlane(byte[] chunkBytes, String filepath, int sequenceNumber) {
        super(Protocol.REPAIR_CHUNK_DATA_PLANE);
        this.chunkBytes = chunkBytes;
        this.filepath = filepath;
        this.sequenceNumber = sequenceNumber;
    }

    public RepairChunkDataPlane(byte[] bytes) throws IOException {
        super(bytes);
    }

    @Override
    protected void marshall() throws IOException {
        marshallBytes(chunkBytes);
        marshallString(filepath);
        dataOutputStream.writeInt(sequenceNumber);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.chunkBytes = unmarshallBytes();
        this.filepath = unmarshallString();
        this.sequenceNumber = dataInputStream.readInt();
    }

    @Override
    public String toString() {
        return "RepairChunkDataPlane for '" + filepath + "' #" + sequenceNumber;
    }

}
