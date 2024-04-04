package csx55.chord.wireformats;

import csx55.chord.node.PeerInfo;

import java.io.IOException;

public class Poke extends Event {

    private PeerInfo peerInfo;

    public Poke(PeerInfo peerInfo) {
        super(Protocol.POKE);
        this.peerInfo = peerInfo;
    }

    public Poke(byte[] bytes) throws IOException {
        super(bytes);
    }

    public PeerInfo getPeerInfo() {
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
