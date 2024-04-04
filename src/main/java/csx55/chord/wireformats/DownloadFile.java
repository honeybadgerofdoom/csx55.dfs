package csx55.chord.wireformats;

import csx55.chord.node.PeerInfo;

import java.io.IOException;

public class DownloadFile extends Event {

    private PeerInfo client;
    private String filename;
    private String hopPath;
    private int hash;

    public DownloadFile(PeerInfo client, String filename, int firstNode, int hash) {
        super(Protocol.DOWNLOAD_FILE);
        this.client = client;
        this.filename = filename;
        this.hopPath = "" + firstNode;
        this.hash = hash;
    }

    public DownloadFile(byte[] bytes) throws IOException {
        super(bytes);
    }

    public PeerInfo getClient() {
        return client;
    }

    public String getFilename() {
        return filename;
    }

    public String getHopPath() {
        return hopPath;
    }

    public int getHash() {
        return hash;
    }

    public void updateHopPath(int nodeId) {
        this.hopPath += "\n" + nodeId;
    }

    @Override
    protected void marshall() throws IOException {
        marshallPeerInfo(client);
        marshallString(filename);
        marshallString(hopPath);
        dataOutputStream.writeInt(hash);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.client = unmarshallPeerInfo();
        this.filename = unmarshallString();
        this.hopPath = unmarshallString();
        this.hash = dataInputStream.readInt();
    }
}
