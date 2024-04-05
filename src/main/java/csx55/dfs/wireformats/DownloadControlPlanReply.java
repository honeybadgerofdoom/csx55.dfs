package csx55.dfs.wireformats;

import java.io.IOException;

public class DownloadControlPlanReply extends Event {

    public DownloadControlPlanReply() {
        super(Protocol.DOWNLOAD_CONTROL_PLAN_REPLY);
    }

    public DownloadControlPlanReply(byte[] bytes) throws IOException {
        super(bytes);
    }

    @Override
    protected void marshall() throws IOException {

    }

    @Override
    protected void unmarshall() throws IOException {

    }

}
