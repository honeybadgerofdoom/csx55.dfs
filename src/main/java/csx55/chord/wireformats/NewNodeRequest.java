package csx55.chord.wireformats;

import csx55.chord.node.PeerInfo;

import java.io.*;

public class NewNodeRequest extends Event {

    private PeerInfo peerInfo;

    public NewNodeRequest(PeerInfo peerInfo) {
        super(Protocol.NEW_NODE_REQUEST);
        this.peerInfo = peerInfo;
    }

    public NewNodeRequest(byte[] bytes) throws IOException {
        super(bytes);
    }

    public PeerInfo getPeerNodeInfo() {
        return peerInfo;
    }

    @Override
    protected void marshall() throws IOException {
        marshallPeerInfo(peerInfo);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.peerInfo = unmarshallPeerInfo();
    }
}
