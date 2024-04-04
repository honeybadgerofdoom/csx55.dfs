package csx55.chord.wireformats;

import csx55.chord.node.PeerRef;
import csx55.chord.util.Configs;
import csx55.chord.util.FingerTable;
import csx55.chord.util.FingerTableEntry;
import csx55.chord.node.PeerInfo;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class DetailsForNewNode extends Event {

    private PeerInfo predecessorInfo;
    private PeerInfo successorInfo;
    private List<PeerInfo> fingerTableInfo;

    public DetailsForNewNode(PeerInfo predecessorInfo, PeerInfo successorInfo, FingerTable fingerTable) {
        super(Protocol.DETAILS_FOR_NEW_NODE);
        this.predecessorInfo = predecessorInfo;
        this.successorInfo = successorInfo;
        fingerTableInfo = new ArrayList<>();
        for (FingerTableEntry entry : fingerTable.getTable()) {
            PeerRef peerRef = entry.getPeerRef();
            fingerTableInfo.add(new PeerInfo(peerRef));
        }
    }

    public DetailsForNewNode(byte[] bytes) throws IOException {
        super(bytes);
    }

    @Override
    protected void marshall() throws IOException {
        marshallPeerInfo(this.predecessorInfo);
        marshallPeerInfo(successorInfo);
        for (PeerInfo peerNode : this.fingerTableInfo) {
            marshallPeerInfo(peerNode);
        }
    }

    @Override
    protected void unmarshall() throws IOException {
        this.predecessorInfo = unmarshallPeerInfo();
        this.successorInfo = unmarshallPeerInfo();
        this.fingerTableInfo = new ArrayList<>();
        for (int i = 0; i < Configs.TABLE_SIZE; i++) {
            PeerInfo peer = unmarshallPeerInfo();
            this.fingerTableInfo.add(peer);
        }
    }

    public PeerInfo getPredecessorInfo() {
        return predecessorInfo;
    }

    public List<PeerInfo> getFingerTableInfo() {
        return fingerTableInfo;
    }

    public PeerInfo getSuccessorInfo() {
        return successorInfo;
    }

    @Override
    public String toString() {
        return "Predecessor: " + predecessorInfo + "\nSuccessorInfo: " + successorInfo;
    }

}
