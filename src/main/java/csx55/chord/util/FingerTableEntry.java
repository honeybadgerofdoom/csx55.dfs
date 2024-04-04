package csx55.chord.util;

import csx55.chord.node.PeerRef;

public class FingerTableEntry {

    private int index;
    private PeerRef peerRef;

    public FingerTableEntry(int index, PeerRef peerRef) {
        this.index = index;
        this.peerRef = peerRef;
    }

    public int getIndex() {
        return index;
    }

    public PeerRef getPeerRef() {
        return peerRef;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setPeerNodeRef(PeerRef peerRef) {
        this.peerRef = peerRef;
    }

    public String toFormatString() {
        return index + " " + peerRef.getId();
    }

    @Override
    public String toString() {
        return String.format("| %-17s | %17s | %17s | %17d |", index, peerRef.getId(), peerRef.getIpAddress(), peerRef.getPortNumber());
    }

}
