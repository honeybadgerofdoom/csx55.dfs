package csx55.dfs.replication;

import csx55.dfs.cli.ClientCLIManager;
import csx55.dfs.node.Node;
import csx55.dfs.transport.TCPReceiverThread;
import csx55.dfs.transport.TCPSender;
import csx55.dfs.util.ChunkServerInfo;
import csx55.dfs.util.Configs;
import csx55.dfs.wireformats.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;


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
            case Protocol.DOWNLOAD_CONTROL_PLAN_REPLY:
                handleDownloadControlPlanReply((DownloadControlPlanReply) event);
                break;
            default:
                System.out.println("onEvent trying to process invalid event type: " + event.getType());
        }
    }


    /*
    Handle Download Control Plane reply
     */
    public void handleDownloadControlPlanReply(DownloadControlPlanReply downloadControlPlanReply) {
        System.out.println("Received DownloadControlPlanReply");
    }


    /*
    Handle LocationsForChunkReply from Controller
     */
    private void handleLocationsForChunkReply(LocationsForChunkReply locationsForChunkReply) {
        ChunkDelivery chunkDelivery = new ChunkDelivery(locationsForChunkReply);
        ChunkServerInfo firstChunkServer = locationsForChunkReply.getLocations().get(0);
        try {
            Socket socket = new Socket(firstChunkServer.getIpAddress(), firstChunkServer.getPortNumber());
            TCPSender sender = new TCPSender(socket);
            sender.sendData(chunkDelivery.getBytes());
        } catch (IOException e) {
            System.err.println("Failed to send ChunkDelivery");
        }
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
        byte[] file = getFileAsBytes(filepath);
        if (file != null) {
            int numberOfChunks = file.length / Configs.CHUNK_SIZE;  // Find total number of chunks
            if (file.length % Configs.CHUNK_SIZE > 0) numberOfChunks++;  // Increment if we have a chunk w/ leftover
            for (int i = 0; i < numberOfChunks; i++) {  // Iterate number of chunks times
                int startIndex = i * Configs.CHUNK_SIZE;
                int endIndex = (i + 1) * Configs.CHUNK_SIZE;
                byte[] chunk = Arrays.copyOfRange(file, startIndex, endIndex);  // Build a chunk
                int sequenceNumber = i + 1;
                sendLocationsForChunkRequest(chunk, sequenceNumber, filepath);  // Send the request for this chunk
            }
        }
    }


    /*
    Send LocationsForChunkRequest to Controller
     */
    private void sendLocationsForChunkRequest(byte[] chunk, int sequenceNumber, String filepath) {
        LocationsForChunkRequest locationsForChunkRequest = new LocationsForChunkRequest(sequenceNumber, chunk, filepath);  // Build event
        sendToController(locationsForChunkRequest);  // Send it to the Controller
    }


    /*
    Upload a file and store as a byte[]
     */
    private byte[] getFileAsBytes(String filepath) {
        try {
            return Files.readAllBytes(Paths.get(filepath));
        } catch (IOException e) {
            System.err.println("'" + filepath + "' does not exist.");
            return null;
        }
    }


    /*
    Download a file
     */
    public void download(String filepath) {
        System.out.println("Implement Download: " + filepath);
        DownloadControlPlanRequest downloadControlPlanRequest = new DownloadControlPlanRequest(filepath);
        sendToController(downloadControlPlanRequest);
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
