package csx55.dfs.replication;

import csx55.dfs.node.Node;
import csx55.dfs.testing.Poke;
import csx55.dfs.wireformats.*;
import csx55.dfs.transport.TCPReceiverThread;
import csx55.dfs.transport.TCPSender;

import java.net.*;
import java.io.*;


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

    public ChunkServer(String controllerIpAddress, int controllerPortNumber) {
        this.controllerIpAddress = controllerIpAddress;
        this.controllerPortNumber = controllerPortNumber;
    }


    /*
    Setup objects, TCP connections
     */
    public void doWork() {
        assignIpAddress();
        assignServerSocketAndPort();
        startTCPServerThread();
        connectToController();
        registerSelf();
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
        } catch (IOException e) {
            System.err.println("ERROR Failed to create ServerSocket...\n" + e);
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
        try {
            TCPSender tcpSender = new TCPSender(this.socketToController);
            RegisterRequest registerRequest = new RegisterRequest(ipAddress, portNumber);
            byte[] bytes = registerRequest.getBytes();
            tcpSender.sendData(bytes);
        } catch (IOException e) {
            System.err.println("ERROR Trying to register self " + e);
        }
    }


    /*
    Handle incoming Events from TCPReceiverThread
     */
    public void onEvent(Event event) {
        if (event != null) {
            int type = event.getType();
            switch (type) {
                case Protocol.POKE:
                    handlePoke((Poke) event);
                    break;
                default:
                    System.out.println("onEvent couldn't handle event type " + type);
            }
        }
    }


    /*
    Handle Poke Event
     */
    private void handlePoke(Poke poke) {
        System.out.println("Received poke: '" + poke.getMessage() + "'");
    }


    /*
    Write a byte[] to local storage
     */
    private void write(byte[] file, String writePath) {
        File outputFile = new File(writePath);
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(file);
        } catch (IOException e) {
            System.err.println("Failed to write file to disc " + e);
        }
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