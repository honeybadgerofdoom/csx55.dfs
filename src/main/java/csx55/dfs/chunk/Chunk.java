package csx55.dfs.chunk;


import csx55.dfs.util.Configs;
import csx55.dfs.wireformats.ChunkDelivery;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/*
Represents a chunk of a file
Has metadata
Adds validation info when writing a chunk (64KB) to disk
Checks validation info when reading
    -> If invalid slice is found, report to the Controller
 */
public class Chunk {

    private final ChunkMetadata chunkMetadata;
    private final Checksum[] checksums;
    private final String path;
    private final String filename;
    private final String root = "/tmp/chunk-server/";

    public Chunk(ChunkDelivery chunkDelivery) {
        this.checksums = new Checksum[8];  // Each slice is 8KB, each chunk is 64KB
        String filepath = chunkDelivery.getFilepath();
        int index = filepath.lastIndexOf("/");
        if (index >= 0) {
            this.path = filepath.substring(0, index + 1);
            this.filename = filepath.substring(index + 1);
            try {
                Files.createDirectories(Paths.get(root + path));
            } catch (IOException e) {
                System.out.println("Failed to create directory path '" + this.path + "'\n" + e);
            }
        }
        else {
            this.path = "";
            this.filename = filepath;
        }
        this.chunkMetadata = new ChunkMetadata(chunkDelivery.getSequenceNumber(), filename, path);  // Metadata for this chunk
        validateBytesOnStore(chunkDelivery.getChunkBytes());
        writeToDisk(chunkDelivery.getChunkBytes());
    }


    /*
    Validates the bytes before we write them to disk
     */
    private void validateBytesOnStore(byte[] chunk) {
        Checksum[] checksumArray = getChecksums(chunk);
        System.arraycopy(checksumArray, 0, checksums, 0, checksumArray.length);
    }


    /*
    Get
     */
    private Checksum[] getChecksums(byte[] chunk) {
        int numberOfSlices = chunk.length / Configs.SLICE_SIZE;  // There should be 8 slices always
        if (chunk.length % Configs.SLICE_SIZE != 0) {
            System.err.println("Detected tampering with file!");
            return null;
        }
        Checksum[] checksumArray = new Checksum[numberOfSlices];
        for (int i = 0; i < numberOfSlices; i++) {  // Iterate number of slices times
            int startIndex = i * Configs.SLICE_SIZE;
            int endIndex = (i + 1) * Configs.SLICE_SIZE;
            byte[] slice = Arrays.copyOfRange(chunk, startIndex, endIndex);  // Build a slice
            Checksum checksum = new Checksum(slice);
            checksumArray[i] = checksum;
        }
        return checksumArray;
    }


    /*
    Validate bytes before we read them
    Use checksums
     */
    private boolean chunkIsValid(byte[] chunk) {
        Checksum[] checksumArray = getChecksums(chunk);
        if (checksumArray == null) return false;
        for (int i = 0; i < checksums.length; i++) {
            if (!checksumArray[i].equals(checksums[i])) return false;
        }
        return true;
    }


    /*
    Validate the bytes
    If valid, return them
    Else, return null and contact the Controller b/c block was corrupted
     */
    public byte[] getChunkBytes() {
        try {
            byte[] chunk = Files.readAllBytes(Paths.get(getWholePath()));
            if (chunkIsValid(chunk)) {
                return chunk;
            }
            else {
                System.err.println("Found invalid checksum for chunk " + path + filename + ", sequence #" + chunkMetadata.getSequenceNumber());
                return null;
            }
        } catch (IOException e) {
            System.err.println("Failed to read file from disc " + e);
        }
        return null;
    }


    /*
        Write a byte[] to local storage
         */
    private void writeToDisk(byte[] bytes) {
        File outputFile = new File(getWholePath());
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(bytes);
        } catch (IOException e) {
            System.err.println("Failed to write file to disc " + e);
        }
    }

    public ChunkMetadata getChunkMetadata() {
        return chunkMetadata;
    }

    public String getFilename() {
        return filename;
    }

    private String getWholePath() {
        return root + path + filename + "_" + chunkMetadata.getSequenceNumber();
    }

    public boolean isTarget(String filename, int sequenceNumber) {
        return filename.equals(path + this.filename) && sequenceNumber == chunkMetadata.getSequenceNumber();
    }

    public String getChecksumStrings() {
        String rtn = "[";
        for (Checksum checksum : checksums) {
            rtn += checksum + ", ";
        }
        rtn = rtn.substring(0, rtn.length() - 2);
        rtn += "]";
        return rtn;
    }

}
