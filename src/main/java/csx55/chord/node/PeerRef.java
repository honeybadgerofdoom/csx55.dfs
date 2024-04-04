package csx55.chord.node;

import csx55.chord.Peer;
import csx55.chord.transport.TCPSender;
import csx55.chord.util.Configs;
import csx55.chord.wireformats.Event;

import java.io.IOException;
import java.net.Socket;

public class PeerRef implements Comparable<PeerRef> {

    private TCPSender tcpSender = null;
    private int id;
    private final int portNumber;
    private final String ipAddress;

    public PeerRef(PeerInfo peerInfo) {
        this.id = peerInfo.getId();
        this.ipAddress = peerInfo.getIpAddress();
        this.portNumber = peerInfo.getPortNumber();
        initializeTCPSender(ipAddress, portNumber);
    }

    public PeerRef(Peer peer) {
        this.id = peer.getId();
        this.ipAddress = peer.getIpAddress();
        this.portNumber = peer.getPortNumber();
    }

    public void setId(int id) {
        this.id = id;
    }

    private void initializeTCPSender(String ipAddress, int portNumber) {
        try {
            Socket socket = new Socket(ipAddress, portNumber);
            this.tcpSender = new TCPSender(socket);
        } catch (IOException e) {
            System.out.println("Failed to initialize TCPSender " + e);
        }
    }

    public synchronized void writeToSocket(Event event) {
        if (this.tcpSender == null) {
            System.err.println("TCPSender has not been initialize yet.");
        }
        else {
            try {
                byte[] bytes = event.getBytes();
                this.tcpSender.sendData(bytes);
            } catch (IOException e) {
                System.out.println("Failed to write to socket " + ipAddress + ":" + portNumber);
            }
        }
    }

    public int getId() {
        return id;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public String toString() {
        return String.format("| %-17s | %17s | %17d |", id, ipAddress, portNumber);
    }

    public String toInfoString() {
        PeerInfo info = new PeerInfo(this);
        return info.toString();
    }

    public String toFormatString() {
        return id + " " + ipAddress + ":" + portNumber;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PeerRef other = (PeerRef) obj;
        return this.id == other.id;
    }

    @Override
    public int compareTo(PeerRef peerRef) {
        return Integer.compare(peerRef.getId(), id);
    }
}
