package csx55.chord.node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

public class PeerNodes {

    private final Set<PeerRef> peers;

    public PeerNodes() {
        this.peers = new HashSet<>();
    }

    public void add(PeerRef peer) {
        peers.add(peer);
    }

    public boolean isEmpty() {
        return peers.isEmpty();
    }

    public int size() {
        return peers.size();
    }

    public boolean contains(PeerRef peer) {
        return peers.contains(peer);
    }
    
    public boolean remove(PeerRef peer) {
        return peers.remove(peer);
    }

    public List<PeerRef> toList() {
        return new ArrayList<>(peers);
    }

    public boolean containsId(int id) {
        for (PeerRef peer : peers) {
            if (peer.getId() == id) {
                return true;
            }
        }
        return false;
    }

}
