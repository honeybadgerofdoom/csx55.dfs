package csx55.dfs.replication;

import csx55.dfs.cli.ClientCLIManager;
import csx55.dfs.node.Node;
import csx55.dfs.transport.TCPReceiverThread;
import csx55.dfs.transport.TCPSender;
import csx55.dfs.wireformats.Event;
import csx55.dfs.wireformats.LocationsForChunkReply;
import csx55.dfs.wireformats.LocationsForChunkRequest;
import csx55.dfs.wireformats.Protocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/*
Client allows for uploading/downloading files
Sends control plane traffic to Controller
Sends data plane traffic to ChunkServers
 */
public class Client implements Node {

    private final String controllerIpAddress;
    private final int controllerPortNumber;
    private Socket socketToController;

    public Client(String controllerIpAddress, int controllerPortNumber) {
        this.controllerIpAddress = controllerIpAddress;
        this.controllerPortNumber = controllerPortNumber;
    }


    /*
    Handle incoming network events
     */
    public void onEvent(Event event, Socket socket) {
        switch (event.getType()) {
            case Protocol.LOCATIONS_FOR_CHUNK_REPLY:
                handleLocationsForChunkReply((LocationsForChunkReply) event);
                break;
            default:
                System.out.println("onEvent trying to process invalid event type: " + event.getType());
        }
    }


    /*
    Handle LocationsForChunkReply from Controller
     */
    private void handleLocationsForChunkReply(LocationsForChunkReply locationsForChunkReply) {
        System.out.println("Received locations from Controller\n" + locationsForChunkReply);
    }


    /*
    Send LocationsForChunkRequest to Controller
     */
    private void sendLocationsForChunkRequest() {
        LocationsForChunkRequest locationsForChunkRequest = new LocationsForChunkRequest();
        sendToController(locationsForChunkRequest);
    }


    /*
    Send an event to the controller
     */
    private void sendToController(Event event) {
        try {
            TCPSender sender = new TCPSender(socketToController);
            sender.sendData(event.getBytes());
        } catch (IOException e) {
            System.err.println("Failed to send event to controller " + e);
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
    Setup CLI
     */
    public void doWork() {
        connectToController();
        manageCLI();
    }


    /*
    Upload a file
     */
    public void upload(String filepath) {
        sendLocationsForChunkRequest();
    }


    /*
    Download a file
     */
    public void download(String filepath) {
        System.out.println("Implement Download: " + filepath);
    }


    /*
    Manage CLI input
     */
    public void manageCLI() {
        ClientCLIManager cliManager = new ClientCLIManager(this);
        Thread thread = new Thread(cliManager);
        thread.start();
    }


    public ServerSocket getServerSocket() {
        return null;
    }


    /*
    Entrypoint
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            String registryIpAddress = args[0];
            int registryPortNumber = Integer.parseInt(args[1]);
            Client client = new Client(registryIpAddress, registryPortNumber);
            client.doWork();
        }
        else {
            System.err.println("Invalid Usage. Provide IP/Port of Registry");
        }
    }

}
