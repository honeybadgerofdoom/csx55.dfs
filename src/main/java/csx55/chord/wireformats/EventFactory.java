package csx55.chord.wireformats;

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
            case Protocol.REGISTER_RESPONSE:
                return new RegisterResponse(bytes);
            case Protocol.DEREGISTER_REQUEST:
                return new DeregisterRequest(bytes);
            case Protocol.DEREGISTER_RESPONSE:
                return new DeregisterResponse(bytes);
            case Protocol.DETAILS_FOR_NEW_NODE:
                return new DetailsForNewNode(bytes);
            case Protocol.NEW_NODE_REQUEST:
                return new NewNodeRequest(bytes);
            case Protocol.UPDATE_FINGER_TABLE_JOIN:
                return new UpdateFingerTableOnJoin(bytes);
            case Protocol.UPDATE_FINGER_TABLE_LEAVE:
                return new UpdateFingerTableOnLeave(bytes);
            case Protocol.UPLOAD_FILE:
                return new UploadFile(bytes);
            case Protocol.DOWNLOAD_FILE:
                return new DownloadFile(bytes);
            case Protocol.DOWNLOAD_DELIVERY:
                return new DownloadDelivery(bytes);
            case Protocol.POKE:
                return new Poke(bytes);
            default:
                System.out.println("getEvent() found no matching route, messageType: " + messageType);
                return null;
        }

    }
    
}