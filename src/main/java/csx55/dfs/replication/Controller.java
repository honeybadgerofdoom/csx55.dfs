package csx55.dfs.replication;

import java.io.IOException;
import java.net.ServerSocket;

import csx55.dfs.cli.ClientCLIManager;
import csx55.dfs.node.Node;
import csx55.dfs.testing.ControllerCLIManager;
import csx55.dfs.testing.Poke;
import csx55.dfs.util.ChunkServerManager;
import csx55.dfs.wireformats.*;

public class Controller implements Node {

    private final int portNumber;
    private ServerSocket serverSocket;
    private final ChunkServerManager chunkServerManager;

    public Controller(int portNumber) {
        this.portNumber = portNumber;
        this.chunkServerManager = new ChunkServerManager();
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
        this.chunkServerManager.add(registerRequest);
    }

    public void pokeChunkServers() {
        Poke poke = new Poke("Hello from Controller");
        chunkServerManager.sendToAllChunkServers(poke);
    }

    public void printChunkServers() {
        System.out.println(chunkServerManager);
    }

    public void manageCLI() {
        ControllerCLIManager cliManager = new ControllerCLIManager(this);
        Thread thread = new Thread(cliManager);
        thread.start();
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