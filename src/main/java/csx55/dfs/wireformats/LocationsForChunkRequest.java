package csx55.dfs.wireformats;

import java.io.IOException;

public class LocationsForChunkRequest extends Event {


    public LocationsForChunkRequest() {
        super(Protocol.LOCATIONS_FOR_CHUNK_REQUEST);
    }

    public LocationsForChunkRequest(byte[] bytes) throws IOException {
        super(bytes);
    }

    @Override
    protected void marshall() throws IOException {
    }

    @Override
    protected void unmarshall() throws IOException {
    }

}
