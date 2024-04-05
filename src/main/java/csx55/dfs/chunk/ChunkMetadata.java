package csx55.dfs.chunk;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;


/*
Maintain metadata associated with a Chunk
 */
public class ChunkMetadata implements Comparable<ChunkMetadata> {

    private final DateTimeFormatter CUSTOM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private int versionNumber, sequenceNumber;
    private String timestamp, filename;

    public ChunkMetadata(int sequenceNumber, String filename) {
        this.sequenceNumber = sequenceNumber;
        this.filename = filename;
        this.versionNumber = 1;
        this.timestamp = LocalDateTime.now().format((CUSTOM_FORMATTER));
    }

    public ChunkMetadata(byte[] bytes) throws IOException {
        ByteArrayInputStream bArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(bArrayInputStream));

        int filenameLength = din.readInt();
        byte[] filenameBytes = new byte[filenameLength];
        din.readFully(filenameBytes);
        this.filename = new String(filenameBytes);

        int timestampLength = din.readInt();
        byte[] timestampBytes = new byte[timestampLength];
        din.readFully(timestampBytes);
        this.timestamp = new String(timestampBytes);

        this.sequenceNumber = din.readInt();
        this.versionNumber = din.readInt();

        bArrayInputStream.close();
        din.close();
    }

    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        byte[] filenameBytes = filename.getBytes();
        int filenameLength = filenameBytes.length;
        dout.writeInt(filenameLength);
        dout.write(filenameBytes);

        byte[] timestampBytes = timestamp.getBytes();
        int timestampLength = timestampBytes.length;
        dout.writeInt(timestampLength);
        dout.write(timestampBytes);

        dout.writeInt(sequenceNumber);
        dout.writeInt(versionNumber);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        return marshalledBytes;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getFilename() {
        return filename;
    }

    /*
    Formats members into readable output
     */
    @Override
    public String toString() {
        return "'" + filename +  "': { Sequence Number: " + sequenceNumber + " | Version Number: " + versionNumber + " | Timestamp: '" + timestamp + "' }";
    }

    @Override
    public int compareTo(ChunkMetadata chunkMetadata) {
        return Integer.compare(chunkMetadata.getSequenceNumber(), this.sequenceNumber);
    }
}
