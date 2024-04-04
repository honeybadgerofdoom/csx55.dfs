package csx55.dfs.util;

import csx55.dfs.chunk.ChunkMetadata;
import csx55.dfs.transport.TCPSender;
import csx55.dfs.wireformats.Event;

import java.io.IOException;
import java.net.Socket;

public class ChunkServerProxy {

    private final String id;
    private final ChunkMetadata chunkMetadata;
    private int spaceLeft;
    private TCPSender tcpSender;

    public ChunkServerProxy(String ipAddress, int portNumber) {
        this.id = ipAddress + ":" + portNumber;
        initializeTCPSender(ipAddress, portNumber);
        this.chunkMetadata = new ChunkMetadata();
//        this.spaceLeft = Configs.GB;
        this.spaceLeft =  (60 * Configs.MB) + (17 * Configs.KB);
    }

    private void initializeTCPSender(String ipAddress, int portNumber) {
        try {
            Socket socket = new Socket(ipAddress, portNumber);
            this.tcpSender = new TCPSender(socket);
        } catch (IOException e) {
            System.out.println("Failed to initialize TCPSender " + e);
        }
    }

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

    @Override
    public String toString() {
        return String.format("| %-17s | %17s |", id, formatSpaceLeft());
    }

}
