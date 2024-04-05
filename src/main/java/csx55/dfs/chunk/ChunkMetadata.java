package csx55.dfs.chunk;

import java.time.LocalDateTime;


/*
Maintain metadata associated with a Chunk
 */
public class ChunkMetadata {

    private int versionNumber, sequenceNumber;
    private LocalDateTime timestamp;

    public ChunkMetadata(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
        this.versionNumber = 1;
        this.timestamp = LocalDateTime.now();
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /*
        Formats members into readable output
         */
    @Override
    public String toString() {
        return "Sequence Number: " + sequenceNumber + " | Version Number: " + versionNumber + " | Timestamp: " + timestamp;
    }

}
