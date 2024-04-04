package csx55.chord.wireformats;

import csx55.chord.node.PeerInfo;

import java.io.IOException;

public class UpdateFingerTableOnJoin extends Event {

    private PeerInfo peerInfo;

    public UpdateFingerTableOnJoin(PeerInfo peerInfo) {
        super(Protocol.UPDATE_FINGER_TABLE_JOIN);
        this.peerInfo = peerInfo;
    }

    public UpdateFingerTableOnJoin(byte[] bytes) throws IOException {
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
