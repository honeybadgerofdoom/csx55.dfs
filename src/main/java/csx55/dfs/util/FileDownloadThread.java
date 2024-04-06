package csx55.dfs.util;

import csx55.dfs.wireformats.DownloadDataPlaneReply;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileDownloadThread implements Runnable {

    private final String filepath;
    private final int numberOfChunks;
    private final DownloadDataPlaneReply[] chunks;
    private int currentNumberOfChunksGathered = 0;
    private volatile boolean waitingForChunks = true;

    public FileDownloadThread(String filepath, int numberOfChunks) {
        this.filepath = filepath;
        this.numberOfChunks = numberOfChunks;
        this.chunks = new DownloadDataPlaneReply[numberOfChunks];  // index = sequence# - 1
    }

    @Override
    public void run() {
        while (waitingForChunks) { }
        writeFile();
    }

    private void writeFile() {
        System.out.println("All chunks gathered, writing to file '" + filepath + "'");
        File outputFile = new File(filepath);

        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            for (int i = 0; i < numberOfChunks; i++) {
                DownloadDataPlaneReply downloadDataPlaneReply = chunks[i];
                outputStream.write(downloadDataPlaneReply.getChunkBytes());
            }
        } catch (IOException e) {
            System.err.println("Failed to write file to disc " + e);
        }
    }

    public synchronized void addChunk(DownloadDataPlaneReply downloadDataPlaneReply) {
        chunks[downloadDataPlaneReply.getSequenceNumber() - 1] = downloadDataPlaneReply;
        currentNumberOfChunksGathered++;
        System.out.println("(" + currentNumberOfChunksGathered + "/" + numberOfChunks + ")");
        if (currentNumberOfChunksGathered == numberOfChunks) waitingForChunks = false;
        System.out.println("Waiting: " + waitingForChunks);
    }

}
