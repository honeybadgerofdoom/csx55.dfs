package csx55.chord.node;

import csx55.chord.transport.TCPServerThread;
import csx55.chord.wireformats.Event;

import java.net.ServerSocket;
import java.net.Socket;

public interface Node {

    int getPortNumber();
    ServerSocket getServerSocket();
    void onEvent(Event event, Socket socket);

    default void startTCPServerThread() {
        TCPServerThread server = new TCPServerThread(this);
        Thread thread = new Thread(server);
        thread.start();
    }

}