package csx55.dfs.chunk;


import csx55.dfs.util.Configs;
import csx55.dfs.wireformats.ChunkDelivery;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
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
        this.chunkMetadata = new ChunkMetadata(chunkDelivery.getSequenceNumber());  // Metadata for this chunk
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
        validateBytesOnStore(chunkDelivery.getChunkBytes());
        writeToDisk(chunkDelivery.getChunkBytes());
    }


    /*
    Validates the bytes before we write them to disk
     */
    private void validateBytesOnStore(byte[] bytes) {
        int numberOfSlices = bytes.length / Configs.SLICE_SIZE;  // There should be 8 slices always
        if (bytes.length % Configs.SLICE_SIZE != 0) {
            System.err.println(bytes.length % Configs.SLICE_SIZE + " leftover bytes that don't fit into " + numberOfSlices + " slices");
        }
        System.out.println("Found " + numberOfSlices + " slices (should be 8)");
        for (int i = 0; i < numberOfSlices - 1; i++) {  // Iterate number of slices times
            int startIndex = i * Configs.SLICE_SIZE;
            int endIndex = (i + 1) * Configs.SLICE_SIZE;
            byte[] slice = Arrays.copyOfRange(bytes, startIndex, endIndex);  // Build a slice
            Checksum checksum = new Checksum(slice);
            checksums[i] = checksum;
        }
    }


    /*
    Validate bytes before we read them
    Use checksums
     */
    private boolean chunkIsValid() {
        // ToDo Check the checksums for every slice of the chunk
        return false;
    }


    /*
    Validate the bytes
    If valid, return them
    Else, return null and contact the Controller b/c block was corrupted
     */
    public byte[] getBytes() {
        // ToDo Read the bytes from disk, call chunkIsValid()
        return null;
    }


    /*
        Write a byte[] to local storage
         */
    private void writeToDisk(byte[] bytes) {
        File outputFile = new File(root + path + filename + "_" + chunkMetadata.getSequenceNumber());
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
