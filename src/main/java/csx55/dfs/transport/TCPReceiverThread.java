package csx55.dfs.transport;

import java.io.DataInputStream;
import java.net.Socket;
import java.io.IOException;
import csx55.dfs.node.Node;
import csx55.dfs.wireformats.Event;
import csx55.dfs.wireformats.EventFactory;

public class TCPReceiverThread implements Runnable {

    private final Node node;
    private Socket socket;
    private final DataInputStream din;
    private final String socketString;
    
    public TCPReceiverThread(Node node, Socket socket) throws IOException { 
        this.node = node;
        this.socket = socket;
        this.socketString = socket.getRemoteSocketAddress().toString();
        this.din = new DataInputStream(socket.getInputStream()); 
    }

    @Override
    public void run() {

        int dataLength;
        boolean keepGoing = true;

        while (this.socket != null && keepGoing) {
            try {
                dataLength = this.din.readInt();
                byte[] data = new byte[dataLength];
                this.din.readFully(data, 0, dataLength);

                EventFactory eventFactory = EventFactory.getInstance();
                Event event = eventFactory.getEvent(data);
                this.node.onEvent(event);
                /*
                * ToDo
                *  - Fix 'null' printing out when a peer leaves?
                * */
            } catch (IOException se) {
                System.out.println(se.getMessage());
                break;
            }
        }
        this.socket = null;
    }
    
}