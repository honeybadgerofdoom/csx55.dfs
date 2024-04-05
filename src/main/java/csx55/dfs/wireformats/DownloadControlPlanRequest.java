package csx55.dfs.wireformats;

import java.io.IOException;

public class DownloadControlPlanRequest extends Event {

    private String filename;

    public DownloadControlPlanRequest(String filename) {
        super(Protocol.DOWNLOAD_CONTROL_PLAN_REQUEST);
        this.filename = filename;
    }

    public DownloadControlPlanRequest(byte[] bytes) throws IOException {
        super(bytes);
    }

    @Override
    protected void marshall() throws IOException {
        marshallString(filename);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.filename = unmarshallString();
    }

    @Override
    public String toString() {
        return "Requesting " + filename;
    }

}
