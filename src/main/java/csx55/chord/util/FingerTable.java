package csx55.chord.util;

import csx55.chord.Peer;
import csx55.chord.node.PeerInfo;
import csx55.chord.node.PeerRef;
import csx55.chord.wireformats.*;

import java.util.*;

public class FingerTable {

    private final FingerTableEntry[] table;
    private final Peer peer;

    public FingerTable(Peer peer) {
        this.peer = peer;
        this.table = new FingerTableEntry[Configs.TABLE_SIZE];
    }

    // Updating the first node in the system's finger table
    public void update() {
        PeerRef peerRef = new PeerRef(peer);
        for (int i = 0; i < Configs.TABLE_SIZE; i++) {
            int index = findNextNodeLocation(i + 1);
            table[i] = new FingerTableEntry(index, peerRef);
        }
    }

    public String getSuccessorString() {
        return new PeerInfo(this.table[0].getPeerRef()).toString();
    }

    public void sendToSuccessor(Event event) {
        PeerRef successorPeerRef = table[0].getPeerRef();
        successorPeerRef.writeToSocket(event);
    }

    public int getSuccessorId() {
        return table[0].getPeerRef().getId();
    }

    public PeerRef getSuccessorPeer() {
        return table[0].getPeerRef();
    }

    // Updating a new node's finger table with its successor's info
    public void update(DetailsForNewNode detailsForNewNode) {
        List<PeerInfo> fingerTableInfo = detailsForNewNode.getFingerTableInfo();
        PeerInfo successorInfo = detailsForNewNode.getSuccessorInfo();
        Map<Integer, PeerRef> peerRefMap = new HashMap<>();
        PeerRef successorPeer = new PeerRef(successorInfo);
        peerRefMap.put(successorPeer.getId(), successorPeer);
        table[0] = new FingerTableEntry(findNextNodeLocation(1), successorPeer);

        for (int i = 1; i < Configs.TABLE_SIZE; i++) {
            int nextLocation = findNextNodeLocation(i + 1);

            // if nextLocation is "between myself and the successor", my successor is the successor
            if (isBetween(peer.getId(), successorInfo.getId(), nextLocation)) {
                table[i] = new FingerTableEntry(nextLocation, successorPeer);
            }

            // else, use the successor's fingerTable to find the smallest id > nextLocation
            else {
                PeerInfo nextPeer;
                PeerInfo peerInfo = findPeerWithSmallestIdGreaterThanTarget(nextLocation, fingerTableInfo);
                // if nextId is "between nextLocation and myself", I'm the successor
                if (isBetween(peerInfo.getId(), peer.getId(), nextLocation)) {
                    nextPeer = new PeerInfo(peer);
                }
                // else, the node found from the fingerTable is the successor
                else {
                    nextPeer = peerInfo;
                }
                if (!peerRefMap.containsKey(nextPeer.getId())) {
                    peerRefMap.put(nextPeer.getId(), new PeerRef(nextPeer));
                }
                table[i] = new FingerTableEntry(nextLocation, peerRefMap.get(nextPeer.getId()));
            }
        }

    }

    // Call this when the node gets a message to update its finger table when a new node has joined
    public void update(UpdateFingerTableOnJoin updateFingerTableOnJoin) {
        PeerInfo peerInfo = updateFingerTableOnJoin.getPeerInfo();
        PeerRef newPeerRef = null;
        for (FingerTableEntry entry : table) {
            int currentFingerTableRowValue = entry.getIndex(); // This is the result of p + 2 ^ (i - 1)
            int currentEntryId = entry.getPeerRef().getId(); // This is the current successor
            if (isBetween(currentFingerTableRowValue, currentEntryId, peerInfo.getId())) {
                if (newPeerRef == null) newPeerRef = new PeerRef(peerInfo);
                entry.setPeerNodeRef(newPeerRef);
            }
        }
    }

    public void update(UpdateFingerTableOnLeave updateFingerTableOnLeave) {
        PeerInfo peerInfo = updateFingerTableOnLeave.getPeerInfo();
        PeerInfo successor = updateFingerTableOnLeave.getSuccessorInfo();
        PeerRef newPeerRef = null;
        for (FingerTableEntry entry : table) {
            if (entry.getPeerRef().getId() == peerInfo.getId()) {
                if (newPeerRef == null) newPeerRef = new PeerRef(successor);
                entry.setPeerNodeRef(newPeerRef);
            }
        }
    }


    public PeerRef findSuccessorOfTarget(int targetId) {
        if (targetId == peer.getId()) {
            return new PeerRef(peer);
        }
        if (isBetween(peer.getPredecessor().getId(), peer.getId(), targetId)) {
            return new PeerRef(peer);
        }
        PeerRef peerRef = table[0].getPeerRef();
        for (FingerTableEntry entry : table) {
            PeerRef current = entry.getPeerRef();
            int currentId = current.getId();
            if (isBetween(peer.getId(), targetId, currentId)) {
                peerRef = current;
            }
        }
        return peerRef;
    }

    public boolean isBetween(int first, int second, int target) {
        if (first < second) return first < target && target <= second;
        else return first < target || target <= second;
    }

    // FIXME This function is using purely '>' which doesn't work with negative values. I sus
    private PeerInfo findPeerWithSmallestIdGreaterThanTarget(int target, List<PeerInfo> fingerTableInfo) {
        int min = Integer.MAX_VALUE;
        PeerInfo targetPeer = fingerTableInfo.get(fingerTableInfo.size() - 1);
        for (PeerInfo peerInfo : fingerTableInfo) {
            int peerNodeId = peerInfo.getId();
            if (peerNodeId > target && peerNodeId < min) {
                min = peerNodeId;
                targetPeer = peerInfo;
            }
        }
        return targetPeer;
    }

    public FingerTableEntry[] getTable() {
        return this.table;
    }

    private int findNextNodeLocation(int i) {
        return (peer.getId() + (int) Math.pow(2, i - 1)) % (int) Math.pow(2, Configs.TABLE_SIZE);
    }

    public String toFormatString() {
        String str = "";
        int idx = 0;
        for (FingerTableEntry fingerTableEntry : table) {
            str += fingerTableEntry.toFormatString();
            if (idx < table.length - 1) str += "\n";
            idx++;
        }
        return str;
    }

    @Override
    public String toString() {
        String tableString = "";
        String horizontalTablePiece = "";
        int numDashes = 19;
        for (int i = 0; i < numDashes; i++) {
            horizontalTablePiece += "-";
        }
        String tableCorner = "+";
        String tableLine = tableCorner;
        int numCols = 4;
        for (int i = 0; i < numCols; i++) {
            tableLine += horizontalTablePiece + tableCorner;
        }
        tableString += tableLine + "\n";
        tableString += String.format("| %-17s | %17s | %17s | %17s |", "k", "succ(k)", "succ(k) IP", "succ(k) Port") + "\n";
        tableString += tableLine + "\n";

        for (FingerTableEntry fingerTableEntry : table) {
            tableString += fingerTableEntry + "\n";
        }

        tableString += tableLine + "\n";

        return tableString;
    }

}
