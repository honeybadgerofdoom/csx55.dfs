package csx55.chord.wireformats;

import csx55.chord.node.PeerRef;
import csx55.chord.node.PeerInfo;

import java.io.*;

public class RegisterResponse extends Event {

    private PeerInfo peerInfo;
    private String info;
    private int statusCode;
    private int hashedId;

    public RegisterResponse(PeerRef peerRef, String info, int statusCode, int hashedId) {
        super(Protocol.REGISTER_RESPONSE);
        this.peerInfo = new PeerInfo(peerRef);
        this.info = info;
        this.statusCode = statusCode;
        this.hashedId = hashedId;
    }

    public RegisterResponse(byte[] bytes) throws IOException {
        super(bytes);
    }

    @Override
    protected void marshall() throws IOException {
        marshallPeerInfo(peerInfo);
        marshallString(info);
        dataOutputStream.writeInt(statusCode);
        dataOutputStream.writeInt(hashedId);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.peerInfo = unmarshallPeerInfo();
        this.info = unmarshallString();
        this.statusCode = dataInputStream.readInt();
        this.hashedId = dataInputStream.readInt();
    }

    public String getInfo() {
        return info;
    }

    public int getHashedId() {
        return hashedId;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public PeerInfo getPeerNodeInfo() {
        return peerInfo;
    }

}