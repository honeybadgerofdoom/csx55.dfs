package csx55.dfs.replication;

import java.io.IOException;
import java.net.ServerSocket;

import csx55.dfs.node.Node;
import csx55.dfs.wireformats.*;

public class Controller implements Node {

    private final int portNumber;
    private ServerSocket serverSocket;

    public Controller(int portNumber) {
        this.portNumber = portNumber;
    }

    public void doWork() {
        assignServerSocket();
        startTCPServerThread();
    }

    private void assignServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.portNumber);
        } catch (IOException e) {
            System.out.println("ERROR Failed to create ServerSocket...\n" + e);
        }
    }

    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }

    public void onEvent(Event event) {
        switch (event.getType()) {
            case Protocol.REGISTER_REQUEST:
                handleRegisterRequest((RegisterRequest) event);
                break;
            default:
                System.out.println("onEvent trying to process invalid event type: " + event.getType());
        }
    }

    private synchronized void handleRegisterRequest(RegisterRequest registerRequest) {
        System.out.println("Got register request from " + registerRequest);
    }

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