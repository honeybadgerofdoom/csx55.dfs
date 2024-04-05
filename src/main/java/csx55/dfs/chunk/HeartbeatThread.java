package csx55.dfs.chunk;

import csx55.dfs.replication.ChunkServer;
import csx55.dfs.wireformats.Heartbeat;

import java.util.concurrent.TimeUnit;

public class HeartbeatThread implements Runnable {

    private final ChunkServer chunkServer;

    public HeartbeatThread(ChunkServer chunkServer) {
        this.chunkServer = chunkServer;
    }

    @Override
    public void run() {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(15);
            } catch (InterruptedException e) {
                System.err.println("Error while trying to sleep HeartbeatThread " + e);
            }
            Heartbeat heartbeat = chunkServer.getHeartbeat();
            chunkServer.writeToController(heartbeat);
        }
    }

}
