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
            case Protocol.POKE:
                return new Poke(bytes);
            default:
                System.out.println("getEvent() found no matching route, messageType: " + messageType);
                return null;
        }

    }
    
}