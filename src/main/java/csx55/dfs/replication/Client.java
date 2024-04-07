package csx55.dfs.replication;

import csx55.dfs.cli.ClientCLIManager;
import csx55.dfs.node.Node;
import csx55.dfs.transport.TCPReceiverThread;
import csx55.dfs.transport.TCPSender;
import csx55.dfs.util.*;
import csx55.dfs.wireformats.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/*
Client allows for uploading/downloading files
Sends control plane traffic to Controller
Sends data plane traffic to ChunkServers
 */
public class Client implements Node {

    private final String controllerIpAddress;
    private final int controllerPortNumber;
    private Socket socketToController;
    private final Map<String, Socket> socketMap;
    private final Map<String, FileDownloadThread> downloadThreadMap;;
    private String ipAddress;
    private int portNumber;
    private String id;
    private ServerSocket serverSocket;

    public Client(String controllerIpAddress, int controllerPortNumber) {
        this.controllerIpAddress = controllerIpAddress;
        this.controllerPortNumber = controllerPortNumber;
        this.socketMap = new HashMap<>();
        this.downloadThreadMap = new HashMap<>();
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
    Handle incoming network events
     */
    public void onEvent(Event event, Socket socket) {
        switch (event.getType()) {
            case Protocol.LOCATIONS_FOR_CHUNK_REPLY:
                handleLocationsForChunkReply((LocationsForChunkReply) event);
                break;
            case Protocol.DOWNLOAD_CONTROL_PLANE_REPLY:
                handleDownloadControlPlanReply((DownloadControlPlaneReply) event);
                break;
            case Protocol.DOWNLOAD_DATA_PLANE_REPLY:
                handleDownloadDataPlaneReply((DownloadDataPlaneReply) event);
                break;
            default:
                System.out.println("onEvent trying to process invalid event type: " + event.getType());
        }
    }


    /*
    Handle download data plane reply
     */
    private void handleDownloadDataPlaneReply(DownloadDataPlaneReply downloadDataPlaneReply) {
        downloadThreadMap.get(downloadDataPlaneReply.getFilename()).addChunk(downloadDataPlaneReply);
    }


    /*
    Handle Download Control Plane reply
     */
    private void handleDownloadControlPlanReply(DownloadControlPlaneReply downloadControlPlaneReply) {
        FileDownloadThread fileDownloadThread =
                new FileDownloadThread(
                        downloadControlPlaneReply.getNewFileName(),
                        downloadControlPlaneReply.getChunkLocationList().size()
                );
        Thread dlThread = new Thread(fileDownloadThread);
        dlThread.start();
        downloadThreadMap.put(downloadControlPlaneReply.getFilename(), fileDownloadThread);
        for (ChunkLocation chunkLocation : downloadControlPlaneReply.getChunkLocationList()) {
            try {
                String key = chunkLocation.getIpAddress() + ":" + chunkLocation.getPortNumber();
                if (!socketMap.containsKey(key)) {
                    Socket socket = new Socket(chunkLocation.getIpAddress(), chunkLocation.getPortNumber());
                    socketMap.put(key, socket);
                    TCPReceiverThread tcpReceiverThread = new TCPReceiverThread(this, socket);
                    Thread thread = new Thread(tcpReceiverThread);
                    thread.start();
                }
                TCPSender sender = new TCPSender(socketMap.get(key));
                NodeProxy clientProxy = new NodeProxy(ipAddress, portNumber);
                DownloadDataPlaneRequest downloadDataPlaneRequest =
                        new DownloadDataPlaneRequest(
                                downloadControlPlaneReply.getFilename(),
                                chunkLocation.getSequenceNumber(),
                                downloadControlPlaneReply.getChunkLocationList().size(),
                                clientProxy
                        );
                sender.sendData(downloadDataPlaneRequest.getBytes());
            } catch (IOException e) {
                System.err.println("Failed to send DownloadDataPlaneRequest to " + chunkLocation + " " + e);
            }
        }
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
        assignIpAddress();
        assignServerSocketAndPort();
        startTCPServerThread();
        connectToController();
        manageCLI();
    }


    /*
    Upload a file
     */
    public void upload(String source, String destination) {
        destination = formatInput(destination);
        byte[] file = getFileAsBytes(source);
        if (file != null) {
            int numberOfChunks = file.length / Configs.CHUNK_SIZE;  // Find total number of chunks
            if (file.length % Configs.CHUNK_SIZE > 0) numberOfChunks++;  // Increment if we have a chunk w/ leftover
            for (int i = 0; i < numberOfChunks; i++) {  // Iterate number of chunks times
                int startIndex = i * Configs.CHUNK_SIZE;
                int endIndex = (i + 1) * Configs.CHUNK_SIZE;
                byte[] chunk = Arrays.copyOfRange(file, startIndex, endIndex);  // Build a chunk
                int sequenceNumber = i + 1;
                sendLocationsForChunkRequest(chunk, sequenceNumber, source, destination);  // Send the request for this chunk
            }
        }
    }


    /*
    Format the destination filepath so it can start with / or not
     */
    private String formatInput(String path) {
        if (path.charAt(0) == '.') {
            System.err.println("This path should not start with a '.'");
        }
        if (path.charAt(0) != '/') {  // Doesn't start with a '/', just add that
            char[] str = new char[path.length() + 1];
            str[0] = '/';
            for (int i = 0; i < path.length(); i++) {
                str[i+1] = path.charAt(i);
            }
            path = new String(str);
        }
        return path;
    }


    /*
    Send LocationsForChunkRequest to Controller
     */
    private void sendLocationsForChunkRequest(byte[] chunk, int sequenceNumber, String source, String destination) {
        LocationsForChunkRequest locationsForChunkRequest = new LocationsForChunkRequest(sequenceNumber, chunk, source, destination);  // Build event
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
    public void download(String source, String destination) {
        source = formatInput(source);
        DownloadControlPlaneRequest downloadControlPlaneRequest = new DownloadControlPlaneRequest(source, destination);
        sendToController(downloadControlPlaneRequest);
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
        return this.serverSocket;
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
