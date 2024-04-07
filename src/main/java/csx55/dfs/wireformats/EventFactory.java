package csx55.dfs.wireformats;

import csx55.dfs.testing.Poke;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;


/*
Factory class for Events
Singleton
 */
public class EventFactory {

    private final static EventFactory EventFactoryInstance = new EventFactory();

    private EventFactory() { };

    public static EventFactory getInstance() {
        return EventFactoryInstance;
    }

    public Event getEvent(byte[] bytes) throws IOException {
        ByteArrayInputStream bArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(bArrayInputStream));
        int messageType = din.readInt();
        switch(messageType) {
            case Protocol.REGISTER_REQUEST:
                return new RegisterRequest(bytes);
            case Protocol.CHUNK_DELIVERY:
                return new ChunkDelivery(bytes);
            case Protocol.LOCATIONS_FOR_CHUNK_REQUEST:
                return  new LocationsForChunkRequest(bytes);
            case Protocol.LOCATIONS_FOR_CHUNK_REPLY:
                return  new LocationsForChunkReply(bytes);
            case Protocol.PRINT_CHUNKS:
                return new PrintChunks(bytes);
            case Protocol.HEARTBEAT:
                return new Heartbeat(bytes);
            case Protocol.DOWNLOAD_CONTROL_PLANE_REQUEST:
                return new DownloadControlPlaneRequest(bytes);
            case Protocol.DOWNLOAD_CONTROL_PLANE_REPLY:
                return new DownloadControlPlaneReply(bytes);
            case Protocol.DOWNLOAD_DATA_PLANE_REQUEST:
                return new DownloadDataPlaneRequest(bytes);
            case Protocol.DOWNLOAD_DATA_PLANE_REPLY:
                return new DownloadDataPlaneReply(bytes);
            case Protocol.REPAIR_CHUNK_CONTROL_PLANE_REQUEST:
                return new RepairChunkControlPlaneRequest(bytes);
            case Protocol.REPAIR_CHUNK_CONTROL_PLANE_REPLY:
                return new RepairChunkControlPlaneReply(bytes);
            case Protocol.REPAIR_CHUNK_DATA_PLANE:
                return new RepairChunkDataPlane(bytes);
            case Protocol.POKE:
                return new Poke(bytes);
            default:
                System.out.println("getEvent() found no matching route, messageType: " + messageType);
                return null;
        }

    }
    
}