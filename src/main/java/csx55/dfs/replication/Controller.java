package csx55.dfs.replication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;

import csx55.dfs.node.Node;
import csx55.dfs.testing.ControllerCLIManager;
import csx55.dfs.testing.Poke;
import csx55.dfs.transport.TCPSender;
import csx55.dfs.util.ChunkServerInfo;
import csx55.dfs.util.ChunkServerManager;
import csx55.dfs.wireformats.*;


/*
Controller class facilitates control-plan traffic
Communicates with Clients and ChunkServers
 */
public class Controller implements Node {

    private final int portNumber;
    private ServerSocket serverSocket;
    private final ChunkServerManager chunkServerManager;

    public Controller(int portNumber) {
        this.portNumber = portNumber;
        this.chunkServerManager = new ChunkServerManager();
    }


    /*
    Setup necessary objects, threads, etc...
     */
    public void doWork() {
        assignServerSocket();
        startTCPServerThread();
        manageCLI();
    }


    /*
    Setup this object's ServerSocket
     */
    private void assignServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.portNumber);
        } catch (IOException e) {
            System.out.println("ERROR Failed to create ServerSocket...\n" + e);
        }
    }


    /*
    Get a reference to this object's ServerSocket
     */
    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }


    /*
    Handle Events received by the TCPReceiverThread
     */
    public void onEvent(Event event, Socket socket) {
        switch (event.getType()) {
            case Protocol.REGISTER_REQUEST:
                handleRegisterRequest((RegisterRequest) event);
                break;
            case Protocol.LOCATIONS_FOR_CHUNK_REQUEST:
                handleLocationsForChunkRequest((LocationsForChunkRequest) event, socket);
                break;
            case Protocol.HEARTBEAT:
                handleHeartbeat((Heartbeat) event);
                break;
            default:
                System.out.println("onEvent trying to process invalid event type: " + event.getType());
        }
    }


    /*
    Handle a Heartbeat
    We probably don't need to synchronize this because each heartbeat comes from a different ChunkServer, and
        ChunkServers have unique ID's. We can just update the correct reference w/o thread safety
     */
    private void handleHeartbeat(Heartbeat heartbeat) {
        chunkServerManager.handleHeartbeat(heartbeat);
    }


    /*
    Handle request for chunk locations
     */
    private void handleLocationsForChunkRequest(LocationsForChunkRequest locationsForChunkRequest, Socket socket) {
        Set<ChunkServerInfo> locations = chunkServerManager.findLocationsForChunks();
        LocationsForChunkReply locationsForChunkReply = new LocationsForChunkReply(new ArrayList<>(locations), locationsForChunkRequest);
        try {
            TCPSender sender = new TCPSender(socket);
            sender.sendData(locationsForChunkReply.getBytes());
        } catch (IOException e) {
            System.err.println("Failed to send LocationsForChunkReply " + e);
        }
    }


    /*
    Thread-safe method to allow registration of a new ChunkServer
     */
    private synchronized void handleRegisterRequest(RegisterRequest registerRequest) {
        this.chunkServerManager.add(registerRequest);
    }


    /*
    Test network connectivity to each ChunkServer
     */
    public void pokeChunkServers() {
        Poke poke = new Poke("Hello from Controller");
        chunkServerManager.sendToAllChunkServers(poke);
    }


    /*
    Print out all ChunkServers in a table
     */
    public void printChunkServers() {
        System.out.println(chunkServerManager);
    }


    /*
    Print out all ChunkServers in a table
     */
    public void printChunkServerChunks() {
        chunkServerManager.sendToAllChunkServers(new PrintChunks());
    }


    /*
    Print all chunk metadata in controller
     */
    public void printChunkMetadata() {
        chunkServerManager.printChunkMetadata();
    }


    /*
    Manages CLI input for the Controller
     */
    public void manageCLI() {
        ControllerCLIManager cliManager = new ControllerCLIManager(this);
        Thread thread = new Thread(cliManager);
        thread.start();
    }


    /*
    Entrypoint
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Invalid usage. Please provide a Port Number for the Registry.");
        }
        else {
            int registryPortNumber = Integer.parseInt(args[0]);
            Controller node = new Controller(registryPortNumber);
            node.doWork();
        }
    }
    
}