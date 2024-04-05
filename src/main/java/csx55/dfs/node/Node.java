package csx55.dfs.node;

import csx55.dfs.transport.TCPServerThread;
import csx55.dfs.wireformats.Event;

import java.net.ServerSocket;
import java.net.Socket;

public interface Node {

    ServerSocket getServerSocket();
    void onEvent(Event event, Socket socket);

    default void startTCPServerThread() {
        TCPServerThread server = new TCPServerThread(this);
        Thread thread = new Thread(server);
        thread.start();
    }

}