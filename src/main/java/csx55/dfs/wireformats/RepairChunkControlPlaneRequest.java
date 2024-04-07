package csx55.dfs.wireformats;

import csx55.dfs.util.NodeProxy;

import java.io.IOException;

public class RepairChunkControlPlaneRequest extends Event {

    private NodeProxy clientProxy,  chunkServerProxy;
    private String filepath;
    private int sequenceNumber;

    public RepairChunkControlPlaneRequest(NodeProxy clientProxy, NodeProxy chunkServerProxy, String filepath, int sequenceNumber) {
        super(Protocol.REPAIR_CHUNK_CONTROL_PLANE_REQUEST);
        this.clientProxy = clientProxy;
        this.chunkServerProxy = chunkServerProxy;
        this.filepath = filepath;
        this.sequenceNumber = sequenceNumber;
    }

    public RepairChunkControlPlaneRequest(byte[] bytes) throws IOException {
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
