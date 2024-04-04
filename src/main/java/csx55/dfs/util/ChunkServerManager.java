package csx55.dfs.util;

import csx55.dfs.wireformats.Event;
import csx55.dfs.wireformats.RegisterRequest;

import java.util.ArrayList;
import java.util.List;


/*
Manages access to ChunkServers via ChunkServerProxies
 */
public class ChunkServerManager {

    private final List<ChunkServerProxy> chunkServers;

    public ChunkServerManager() {
        this.chunkServers = new ArrayList<>();
    }


    /*
    Adds a newly registered ChunkServer
     */
    public void add(RegisterRequest registerRequest) {
        ChunkServerProxy chunkServerProxy = new ChunkServerProxy(registerRequest.getIpAddress(), registerRequest.getPortNumber());
        this.chunkServers.add(chunkServerProxy);
    }


    /*
    Send any Event to every ChunkServer
     */
    public void sendToAllChunkServers(Event event) {
        for (ChunkServerProxy chunkServerProxy : chunkServers) {
            chunkServerProxy.writeToSocket(event);
        }
    }


    /*
    Formats the chunkServers into a table
     */
    @Override
    public String toString() {
        String tableString = "";
        String horizontalTablePiece = "";
        int numDashes = 19;
        for (int i = 0; i < numDashes; i++) {
            horizontalTablePiece += "-";
        }
        String tableCorner = "+";
        String tableLine = tableCorner;
        int numCols = 2;
        for (int i = 0; i < numCols; i++) {
            tableLine += horizontalTablePiece + tableCorner;
        }
        tableString += tableLine + "\n";
        tableString += String.format("| %-17s | %17s |", "ID", "Space Left") + "\n";
        tableString += tableLine + "\n";

        for (ChunkServerProxy chunkServerProxy : chunkServers) {
            tableString += chunkServerProxy + "\n";
        }

        tableString += tableLine;

        return tableString;
    }

}
