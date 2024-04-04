package csx55.chord.wireformats;

import csx55.chord.node.PeerInfo;

import java.io.*;

public class DeregisterRequest extends Event {

    private PeerInfo peerInfo;

    public DeregisterRequest(PeerInfo peerInfo) {
        super(Protocol.DEREGISTER_REQUEST);
        this.peerInfo = peerInfo;
    }

    public DeregisterRequest(byte[] bytes) throws IOException {
        super(bytes);
    }

    @Override
    protected void marshall() throws IOException {
        marshallPeerInfo(peerInfo);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.peerInfo = unmarshallPeerInfo();
    }

    public PeerInfo getPeerInfo() {
        return peerInfo;
    }

}