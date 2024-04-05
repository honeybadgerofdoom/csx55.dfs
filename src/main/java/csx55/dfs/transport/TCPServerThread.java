package csx55.dfs.transport;

import csx55.dfs.node.Node;
import java.net.Socket;
import java.io.IOException;
import java.net.SocketException;


/*
Server thread creates vanilla sockets, spawns TCPReceiverThreads
 */
public class TCPServerThread implements Runnable {

    private final Node node;

    public TCPServerThread(Node node) {
        this.node = node;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket receiverSocket = this.node.getServerSocket().accept();
                try {
                    TCPReceiverThread receiver = new TCPReceiverThread(node, receiverSocket);
                    Thread thread = new Thread(receiver);
                    thread.start();
                } catch (IOException e) {
                    System.out.println("ERROR Adding the following Socket " + receiverSocket);
                }
            }
        } catch (SocketException e) {
            System.out.println("ERROR SocketException in the run() method of TCPServerThread...\n" + e);
        } catch (IOException e) {
            System.out.println("ERROR IOException in the run() method of TCPServerThread...\n" + e);
        }
        System.out.println("TCPServerThread ending.");
    }

}