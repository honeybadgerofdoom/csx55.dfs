package csx55.chord.wireformats;

import csx55.chord.node.PeerInfo;

import java.io.IOException;

public class UpdateFingerTableOnLeave extends Event {

    private PeerInfo peerInfo;
    private PeerInfo successorInfo;
    private PeerInfo predecessorInfo;

    public UpdateFingerTableOnLeave(PeerInfo peerInfo, PeerInfo successorInfo, PeerInfo predecessorInfo) {
        super(Protocol.UPDATE_FINGER_TABLE_LEAVE);
        this.peerInfo = peerInfo;
        this.successorInfo = successorInfo;
        this.predecessorInfo = predecessorInfo;
    }

    public UpdateFingerTableOnLeave(byte[] bytes) throws IOException {
        super(bytes);
    }

    public PeerInfo getPeerInfo() {
        return peerInfo;
    }

    public PeerInfo getSuccessorInfo() {
        return successorInfo;
    }

    public PeerInfo getPredecessorInfo() {
        return predecessorInfo;
    }

    @Override
    protected void marshall() throws IOException {
        marshallPeerInfo(peerInfo);
        marshallPeerInfo(successorInfo);
        marshallPeerInfo(predecessorInfo);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.peerInfo = unmarshallPeerInfo();
        this.successorInfo = unmarshallPeerInfo();
        this.predecessorInfo = unmarshallPeerInfo();
    }

}
