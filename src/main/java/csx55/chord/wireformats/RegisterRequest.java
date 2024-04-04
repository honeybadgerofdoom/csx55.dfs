package csx55.chord.wireformats;

import csx55.chord.node.PeerInfo;

import java.io.*;

public class RegisterRequest extends Event {

    private PeerInfo peerInfo;

    public RegisterRequest(PeerInfo peerInfo) {
        super(Protocol.REGISTER_REQUEST);
        this.peerInfo = peerInfo;
    }

    public RegisterRequest(byte[] bytes) throws IOException {
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