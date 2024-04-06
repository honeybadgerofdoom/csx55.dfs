package csx55.dfs.wireformats;

import java.io.IOException;

public class DownloadDataPlaneReply extends Event {

    public DownloadDataPlaneReply() {
        super(Protocol.DOWNLOAD_DATA_PLANE_REPLY);
    }

    public DownloadDataPlaneReply(byte[] bytes) throws IOException {
        super(bytes);
    }

    @Override
    protected void marshall() throws IOException {

    }

    @Override
    protected void unmarshall() throws IOException {

    }

}
