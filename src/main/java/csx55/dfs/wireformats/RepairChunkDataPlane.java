package csx55.dfs.wireformats;

import java.io.IOException;

public class RepairChunkDataPlane extends Event {

    public RepairChunkDataPlane() {
        super(Protocol.REPAIR_CHUNK_DATA_PLANE);
    }

    public RepairChunkDataPlane(byte[] bytes) throws IOException {
        super(bytes);
    }

    @Override
    protected void marshall() throws IOException {

    }

    @Override
    protected void unmarshall() throws IOException {

    }

}
