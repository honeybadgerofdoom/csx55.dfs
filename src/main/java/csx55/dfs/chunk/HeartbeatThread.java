package csx55.dfs.chunk;

import csx55.dfs.replication.ChunkServer;
import csx55.dfs.wireformats.Heartbeat;

public class HeartbeatThread implements Runnable {

    private final ChunkServer chunkServer;

    public HeartbeatThread(ChunkServer chunkServer) {
        this.chunkServer = chunkServer;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(15 * 60 * 1000);
            } catch (InterruptedException e) {
                System.err.println("Error while trying to sleep HeartbeatThread " + e);
            }
            Heartbeat heartbeat = chunkServer.getHeartbeat();
            chunkServer.writeToController(heartbeat);
        }
    }

}
