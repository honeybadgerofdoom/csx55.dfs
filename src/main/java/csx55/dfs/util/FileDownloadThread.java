package csx55.dfs.util;

import csx55.dfs.wireformats.DownloadDataPlaneReply;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileDownloadThread implements Runnable {

    private final String filepath;
    private final int numberOfChunks;
    private final DownloadDataPlaneReply[] chunks;

    public FileDownloadThread(String filepath, int numberOfChunks) {
        this.filepath = filepath;
        this.numberOfChunks = numberOfChunks;
        this.chunks = new DownloadDataPlaneReply[numberOfChunks];  // index = sequence# - 1
    }

    @Override
    public void run() {
        while (chunks.length < numberOfChunks) { }
        writeFile();
    }

    private void writeFile() {
        byte[] bytes = new byte[numberOfChunks * Configs.CHUNK_SIZE];
        for (int i = 0; i < numberOfChunks; i++) {
            DownloadDataPlaneReply downloadDataPlaneReply = chunks[i];
            for (int j = 0; j < Configs.CHUNK_SIZE; j++) {
                int idx = (i + Configs.CHUNK_SIZE) + j;
                bytes[idx] = downloadDataPlaneReply.getChunkBytes()[j];
            }
        }
        File outputFile = new File(filepath);
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(bytes);
        } catch (IOException e) {
            System.err.println("Failed to write file to disc " + e);
        }
    }

    public void addChunk(DownloadDataPlaneReply downloadDataPlaneReply) {
        chunks[downloadDataPlaneReply.getSequenceNumber() - 1] = downloadDataPlaneReply;
    }

}
