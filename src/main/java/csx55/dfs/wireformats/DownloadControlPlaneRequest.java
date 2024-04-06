package csx55.dfs.wireformats;

import java.io.IOException;

public class DownloadControlPlaneRequest extends Event {

    private String filename;
    private String newFileName;

    public DownloadControlPlaneRequest(String filename, String newFileName) {
        super(Protocol.DOWNLOAD_CONTROL_PLANE_REQUEST);
        this.filename = filename;
        this.newFileName = newFileName;
    }

    public DownloadControlPlaneRequest(byte[] bytes) throws IOException {
        super(bytes);
    }

    public String getFilename() {
        return filename;
    }

    public String getNewFileName() {
        return newFileName;
    }

    @Override
    protected void marshall() throws IOException {
        marshallString(filename);
        marshallString(newFileName);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.filename = unmarshallString();
        this.newFileName = unmarshallString();
    }

    @Override
    public String toString() {
        return "Requesting " + filename;
    }

}
