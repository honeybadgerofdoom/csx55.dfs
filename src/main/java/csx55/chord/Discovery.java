package csx55.chord;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import csx55.chord.cli.DiscoveryCLIManager;
import csx55.chord.node.Node;
import csx55.chord.node.PeerRef;
import csx55.chord.transport.TCPSender;
import csx55.chord.node.PeerInfo;
import csx55.chord.node.PeerNodes;
import csx55.chord.wireformats.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Discovery implements Node {

    private final int portNumber;
    private ServerSocket serverSocket;
    private final PeerNodes peerNodes;

    public Discovery(int portNumber) {
        this.portNumber = portNumber;
        this.peerNodes = new PeerNodes();
    }

    public void doWork() {
        assignServerSocket();
        startTCPServerThread();
        manageCLI();
    }

    private void assignServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.portNumber);
        } catch (IOException e) {
            System.out.println("ERROR Failed to create ServerSocket...\n" + e);
        }
    }

    public void manageCLI() {
        DiscoveryCLIManager cliManager = new DiscoveryCLIManager(this);
        Thread thread = new Thread(cliManager);
        thread.start();
    }

    public int getPortNumber() {
        return this.portNumber;
    }

    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }

    public void onEvent(Event event, Socket socket) {
        switch (event.getType()) {
            case Protocol.REGISTER_REQUEST:
                handleRegisterRequest((RegisterRequest) event, socket);
                break;
            case Protocol.DEREGISTER_REQUEST:
                handleDeregisterRequest((DeregisterRequest) event, socket);
                break;
            default:
                System.out.println("onEvent trying to process invalid event type: " + event.getType());
        }
    }

    private synchronized void handleRegisterRequest(RegisterRequest registerRequest, Socket socket) {
        PeerInfo peerInfo = registerRequest.getPeerInfo();
        PeerRef peerRef = new PeerRef(peerInfo);
        RegisterResponse registerResponse = buildRegistrationResponse(peerInfo, peerRef, socket);
        peerNodes.add(peerRef);

        try {
            TCPSender sender = new TCPSender(socket);
            byte[] bytes = registerResponse.getBytes();
            sender.sendData(bytes);
        } catch (IOException e) {
            System.out.println("Failed to create TCPSender in handleRegisterRequest");
        }
    }

    private RegisterResponse buildRegistrationResponse(PeerInfo peerInfo, PeerRef peerRef, Socket socket) {
        String hashCheck = checkHashCode(peerInfo);
        int id = peerInfo.getId();
        boolean hashCodeUnique = !peerNodes.containsId(id);
        boolean ipMatches = checkIpAddress(socket, peerInfo.getIpAddress());

        int statusCode = hashCodeUnique && ipMatches ? Protocol.SUCCESS : Protocol.FAILURE;

        if (!hashCodeUnique) {
            id = rehash(peerInfo, id);
        }

        peerRef.setId(id);

        String uniqueHashString = hashCodeUnique ? "HashCode is unique." : "Identified a collision in the hash space. New id: " + id;
        String ipMatchString = ipMatches ? "IP Address matches Socket origin." : "IP Address does not match Socket origin.";
        String info;
        PeerRef returnedPeer;

        if (peerNodes.isEmpty()) {
            info = "Registration Successful, you're the first peer!";
            returnedPeer = peerRef;
        }
        else {
            int numberOfPeers = peerNodes.size() + 1;
            info = "Registration successful. There are now " + numberOfPeers + " peers in the system.";
            returnedPeer = getRandomPeer();
        }

        info += "\nHash Check: " + hashCheck + "\nUnique Check: " + uniqueHashString + "\nIP Check: " + ipMatchString;
        return new RegisterResponse(returnedPeer, info, statusCode, id);
    }

    private int rehash(PeerInfo peerInfo, int id) {
        boolean hashCodeUnique = false;
        int suffix = 0;
        while (!hashCodeUnique) {
            String hashable = peerInfo.getIpAddress() + ":" + peerInfo.getPortNumber() + " | " + suffix;
            id = hashable.hashCode();
            hashCodeUnique = !peerNodes.containsId(id);
            suffix++;
        }
        return id;
    }

    private synchronized boolean checkIpAddress(Socket socket, String ip){
        InetAddress inetAddress = socket.getInetAddress();
        String remoteAddress = inetAddress.getHostAddress();
        return ip.equals(remoteAddress);
    }

    private String checkHashCode(PeerInfo peerInfo) {
        String hashString = peerInfo.getIpAddress() + ":" + peerInfo.getPortNumber();
        int id = peerInfo.getId();
        int rehashedId = hashString.hashCode();
        boolean checkSuccess = rehashedId == id;
        return checkSuccess ? "HashCode is correct. " + peerInfo.getIpAddress() + ":" + peerInfo.getPortNumber() + " -> " + id : "HashCode is incorrect. Provided: " + id + ", found: " + rehashedId + ".";
    }

    private PeerRef getRandomPeer() {
        int randomIndex = ThreadLocalRandom.current().nextInt(0, peerNodes.size());
        List<PeerRef> peersList = peerNodes.toList();
        return peersList.get(randomIndex);
    }

    private synchronized void handleDeregisterRequest(DeregisterRequest deregisterRequest, Socket socket) {
        byte statusCode = Protocol.FAILURE;
        PeerRef ref = new PeerRef(deregisterRequest.getPeerInfo());
        if (this.peerNodes.contains(ref)) {
            statusCode = Protocol.SUCCESS;
            this.peerNodes.remove(ref);
        }
        DeregisterResponse deregisterResponse = new DeregisterResponse(statusCode);
        try {
            TCPSender sender = new TCPSender(socket);
            byte[] bytes = deregisterResponse.getBytes();
            sender.sendData(bytes);
        } catch (IOException e) {
            System.out.println("Failed to create TCPSender in handleDeregisterRequest.");
        }
    }

    public void listPeerNodes() {
        List<PeerRef> peerNodeList = peerNodes.toList();
        peerNodeList.sort(null);
        for (PeerRef peer : peerNodeList) {
            System.out.println(peer.toFormatString());
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Invalid usage. Please provide a Port Number for the Registry.");
        }
        else {
            int registryPortNumber = Integer.parseInt(args[0]);
            Discovery node = new Discovery(registryPortNumber);
            node.doWork();
        }
    }
    
}