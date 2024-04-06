package csx55.dfs.chunk;

import csx55.dfs.util.ChunkServerInfo;
import csx55.dfs.wireformats.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.util.ArrayList;


/*
Maintain metadata associated with a Chunk
 */
public class ChunkMetadata extends Event implements Comparable<ChunkMetadata> {

    private final DateTimeFormatter CUSTOM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private int versionNumber, sequenceNumber;
    private String timestamp, filename, path;

    public ChunkMetadata(int sequenceNumber, String filename, String path) {
        super(-1);
        this.sequenceNumber = sequenceNumber;
        this.filename = filename;
        this.path = path;
        this.versionNumber = 1;
        this.timestamp = LocalDateTime.now().format((CUSTOM_FORMATTER));
    }

    public ChunkMetadata(byte[] bytes) throws IOException {
        super(bytes);
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

    public String getPath() {
        return path;
    }

    @Override
    protected void marshall() throws IOException {
        marshallString(timestamp);
        marshallString(filename);
        marshallString(path);
        dataOutputStream.writeInt(sequenceNumber);
        dataOutputStream.writeInt(versionNumber);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.timestamp = unmarshallString();
        this.filename = unmarshallString();
        this.path = unmarshallString();
        this.sequenceNumber = dataInputStream.readInt();
        this.versionNumber = dataInputStream.readInt();
    }

    /*
        Formats members into readable output
         */
    @Override
    public String toString() {
        return "'" + path + filename +  "': { Sequence Number: " + sequenceNumber + " | Version Number: " + versionNumber + " | Timestamp: '" + timestamp + "' }";
    }

    @Override
    public int compareTo(ChunkMetadata chunkMetadata) {
        return Integer.compare(chunkMetadata.getSequenceNumber(), this.sequenceNumber);
    }

    @Override
    public int hashCode() {
        return (filename + sequenceNumber).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChunkMetadata other = (ChunkMetadata) obj;
        return this.filename.equals(other.getFilename()) && this.sequenceNumber == other.getSequenceNumber();
    }

}
