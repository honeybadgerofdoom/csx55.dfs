package csx55.dfs.wireformats;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

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
            default:
                System.out.println("getEvent() found no matching route, messageType: " + messageType);
                return null;
        }

    }
    
}