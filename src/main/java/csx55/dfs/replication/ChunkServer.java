package csx55.dfs.replication;

import csx55.dfs.chunk.ChunkManager;
import csx55.dfs.chunk.HeartbeatThread;
import csx55.dfs.node.Node;
import csx55.dfs.testing.Poke;
import csx55.dfs.util.ChunkServerInfo;
import csx55.dfs.wireformats.*;
import csx55.dfs.transport.TCPReceiverThread;
import csx55.dfs.transport.TCPSender;

import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


/*
ChunkServer stored chunks for files, manages replication and retrieval
 */
public class ChunkServer implements Node {

    private Socket socketToController;
    private ServerSocket serverSocket;
    private final String controllerIpAddress;
    private final int controllerPortNumber;
    private String ipAddress;
    private int portNumber;
    private String id;
    private final ChunkManager chunkManager;

    public ChunkServer(String controllerIpAddress, int controllerPortNumber) {
        this.controllerIpAddress = controllerIpAddress;
        this.controllerPortNumber = controllerPortNumber;
        this.chunkManager = new ChunkManager();
    }


    /*
    Setup objects, TCP connections
     */
    public void doWork() {
        assignIpAddress();
        assignServerSocketAndPort();
        startTCPServerThread();
        createDirectory();
        connectToController();
        registerSelf();
        startHeartbeat();
    }


    /*
    Start the HeartbeatThread
     */
    private void startHeartbeat() {
        HeartbeatThread heartbeatThread = new HeartbeatThread(this);
        Thread thread = new Thread(heartbeatThread);
        thread.start();
    }


    /*
    Create /tmp/ directory if it doesn't exist yet
     */
    private void createDirectory() {
        try {
            String filepath = "/tmp/chunk-server/";
            Files.createDirectories(Paths.get(filepath));
        } catch (IOException e) {
            System.err.println("Failed to create temp directory " + e);
        }
    }


    /*
    Get IP address, store it
     */
    private void assignIpAddress() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            this.ipAddress = addr.getHostName();
        } catch (UnknownHostException e) {
            System.err.println("ERROR Failed to get MessagingNode IP Address...\n" + e);
        }
    }


    /*
    Get port, initialize ServerSocket
     */
    private void assignServerSocketAndPort() {
        try {
            this.serverSocket = new ServerSocket(0);
            this.portNumber = this.serverSocket.getLocalPort();
            this.id = ipAddress + ":" + portNumber;
        } catch (IOException e) {
            System.err.println("ERROR Failed to create ServerSocket...\n" + e);
        }
    }


    /*
    Thread-safe writing to Controller socket
     */
    public synchronized void writeToController(Event event) {
        try {
            TCPSender tcpSender = new TCPSender(this.socketToController);
            byte[] bytes = event.getBytes();
            tcpSender.sendData(bytes);
        } catch (IOException e) {
            System.err.println("ERROR Trying to register self " + e);
        }
    }


    /*
    Connect to the Controller node, spin up TCPReceiverThread
     */
    private void connectToController() {
        try {
            this.socketToController = new Socket(controllerIpAddress, controllerPortNumber);
            TCPReceiverThread tcpReceiverThread = new TCPReceiverThread(this, this.socketToController);
            Thread thread = new Thread(tcpReceiverThread);
            thread.start();
        } catch (IOException e) {
            System.err.println("ERROR Failed to connect to Registry " + e);
        }
    }


    /*
    Send a RegisterRequest to the Controller
     */
    private void registerSelf() {
        RegisterRequest registerRequest = new RegisterRequest(ipAddress, portNumber);
        writeToController(registerRequest);
    }


    /*
    Handle incoming Events from TCPReceiverThread
     */
    public void onEvent(Event event, Socket socket) {
        if (event != null) {
            int type = event.getType();
            switch (type) {
                case Protocol.CHUNK_DELIVERY:
                    handleChunkDelivery((ChunkDelivery) event);
                    break;
                case Protocol.PRINT_CHUNKS:
                    chunkManager.printChunks();
                    break;
                case Protocol.POKE:
                    handlePoke((Poke) event);
                    break;
                default:
                    System.out.println("onEvent couldn't handle event type " + type);
            }
        }
    }


    /*
    Get a Heartbeat message from the ChunkManager
     */
    public Heartbeat getHeartbeat() {
        return chunkManager.getHeartbeat(id);
    }


    /*
    Handle a chunk delivery
    Pass the chunk to my chunkManager, forward message if need be
     */
    private synchronized void handleChunkDelivery(ChunkDelivery chunkDelivery) {
        chunkManager.addChunk(chunkDelivery);
        if (!chunkDelivery.isLastChunkServer(id)) {
            ChunkServerInfo next = chunkDelivery.getNext(id);
            if (next == null) {
                System.err.println("Failed to find ChunkServer to forward ChunkDelivery to");
            }
            else {
                try {
                    Socket socket = new Socket(next.getIpAddress(), next.getPortNumber());
                    TCPSender sender = new TCPSender(socket);
                    sender.sendData(chunkDelivery.getBytes());
                } catch (IOException e) {
                    System.err.println("Failed to forward ChunkDelivery " + e);
                }
            }
        }
    }


    /*
    Handle Poke Event
     */
    private void handlePoke(Poke poke) {
        System.out.println("Received poke: '" + poke.getMessage() + "'");
    }

    @Override
    public String toString() {
        return "Implement ChunkServer toString()";
    }

    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }


    /*
    Entrypoint
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            String controllerIpAddress = args[0];
            int controllerPortNumber = Integer.parseInt(args[1]);
            ChunkServer node = new ChunkServer(controllerIpAddress, controllerPortNumber);
            node.doWork();
        }
        else {
            System.err.println("Invalid Usage. Provide IP/Port of Registry");
        }
    }

}