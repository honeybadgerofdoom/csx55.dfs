package csx55.dfs.wireformats;

import csx55.dfs.util.NodeProxy;

import java.io.IOException;

// ToDo Send the clientProxy, serverWithBadChunkProxy, filepath, and sequenceNumber to the ChunkLocation we found

public class RepairChunkControlPlaneReply extends Event {

    private NodeProxy clientProxy,  chunkServerProxy;
    private String filepath;
    private int sequenceNumber;

    public RepairChunkControlPlaneReply(RepairChunkControlPlaneRequest repairChunkControlPlaneRequest) {
        super(Protocol.REPAIR_CHUNK_CONTROL_PLANE_REPLY);
        this.clientProxy = repairChunkControlPlaneRequest.getClientProxy();
        this.chunkServerProxy = repairChunkControlPlaneRequest.getChunkServerProxy();
        this.filepath = repairChunkControlPlaneRequest.getFilepath();
        this.sequenceNumber = repairChunkControlPlaneRequest.getSequenceNumber();
    }

    public RepairChunkControlPlaneReply(byte[] bytes) throws IOException {
        super(bytes);
    }

    public NodeProxy getClientProxy() {
        return clientProxy;
    }

    public NodeProxy getChunkServerProxy() {
        return chunkServerProxy;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public String getFilepath() {
        return filepath;
    }

    @Override
    protected void marshall() throws IOException {
        marshallEvent(clientProxy);
        marshallEvent(chunkServerProxy);
        marshallString(filepath);
        dataOutputStream.writeInt(sequenceNumber);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.clientProxy = new NodeProxy(unmarshallBytes());
        this.chunkServerProxy = new NodeProxy(unmarshallBytes());
        this.filepath = unmarshallString();
        this.sequenceNumber = dataInputStream.readInt();
    }

    @Override
    public String toString() {
        return "Client: " + clientProxy + ", Chunk Server: " + chunkServerProxy + ", File Info: '" + filepath + "' #" + sequenceNumber;
    }

}
