package csx55.dfs.util;

import csx55.dfs.chunk.ChunkMetadata;
import csx55.dfs.transport.TCPSender;
import csx55.dfs.wireformats.Event;
import csx55.dfs.wireformats.Heartbeat;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/*
Proxy for a ChunkServer to be used by the Controller
 */
public class ChunkServerProxy {

    private final String id;  // This is ip:port
    private List<HeartbeatChunkData> chunkList;  // ChunkMetadata objects for each Chunk stored at the ChunkServer
    private int spaceLeft;  // Room left for storage. Starts a 1GB
    private TCPSender tcpSender;  // Reference for writing to this ChunkServer node

    public ChunkServerProxy(String ipAddress, int portNumber) {
        this.id = ipAddress + ":" + portNumber;
        this.chunkList = new ArrayList<>();
        initializeTCPSender(ipAddress, portNumber);
        this.spaceLeft = Configs.GB;
    }


    /*
    Update with a heartbeat
     */
    public void handleHeartbeat(Heartbeat heartbeat) {
        this.spaceLeft = heartbeat.getSpaceLeft();

        // FIXME This needs to add, not change the ref (ref should be final)
        this.chunkList = heartbeat.getHeartbeatChunkDataList();
    }


    /*
    Sets up the TCPSender used by writeToSocket()
     */
    private void initializeTCPSender(String ipAddress, int portNumber) {
        try {
            Socket socket = new Socket(ipAddress, portNumber);
            this.tcpSender = new TCPSender(socket);
        } catch (IOException e) {
            System.out.println("Failed to initialize TCPSender " + e);
        }
    }


    /*
    Thread-safe method that writes an Event to the socket associated with this ChunkServer
     */
    public synchronized void writeToSocket(Event event) {
        if (this.tcpSender == null) {
            System.err.println("TCPSender has not been initialize yet.");
        }
        else {
            try {
                byte[] bytes = event.getBytes();
                this.tcpSender.sendData(bytes);
            } catch (IOException e) {
                System.out.println("Failed to write to socket " + id);
            }
        }
    }


    /*
    Formats the space left with GB/MB/KB/B
     */
    private String formatSpaceLeft() {
        if (spaceLeft == Configs.GB) return "1 GB";
        if (spaceLeft > Configs.MB) {
            int mb = Math.floorDiv(spaceLeft, Configs.MB);
            int kb = spaceLeft % Configs.MB;
            return mb + " MB " + kb + " KB";
        }
        if (spaceLeft > Configs.KB) {
            int kb = Math.floorDiv(spaceLeft, Configs.KB);
            int b = spaceLeft % Configs.KB;
            return kb + " KB " + b + " B";
        }
        return spaceLeft + " B";
    }


    /*
    Print out ChunkMetadata associated with this ChunkServerProxy
     */
    public String printChunkMetadata() {
        String rtn = "Chunk Metadata: {\n";
        for (HeartbeatChunkData chunkData : chunkList) {
            rtn +=  "\t" + chunkData + "\n";
        }
        rtn += "}";
        return rtn;
    }


    /*
        Formats members into a table row
         */
    @Override
    public String toString() {
        return String.format("| %-17s | %17s |", id, formatSpaceLeft());
    }

    public String getId() {
        return id;
    }

    public boolean hasSpaceLeft() {
        return spaceLeft > Configs.CHUNK_SIZE;
    }

}
