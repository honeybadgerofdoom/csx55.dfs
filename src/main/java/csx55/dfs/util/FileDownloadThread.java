package csx55.dfs.util;

import csx55.dfs.wireformats.DownloadDataPlaneReply;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FileDownloadThread implements Runnable {

    private final Lock loopLock = new ReentrantLock();
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
        String path = Configs.pathFromPathAndName(filepath);
        if (!path.equals(filepath)) {
            try {
                Files.createDirectories(Paths.get(path));
            } catch (IOException e) {
                System.err.println("Failed to create directory for " + filepath + " " + e);
            }
        }
        File outputFile = new File(filepath);
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            for (DownloadDataPlaneReply downloadDataPlaneReply : chunks) {
                byte[] bytes = downloadDataPlaneReply.getChunkBytes();
                int i;
                for (i = bytes.length - 1; i >= 0 && bytes[i] == 0; i--) { }
                if (i != bytes.length - 1) bytes = Arrays.copyOfRange(bytes, 0, i + 1);
                outputStream.write(bytes);
            }
        } catch (IOException e) {
            System.err.println("Failed to write file to disc " + e);
        }
    }

    public void addChunk(DownloadDataPlaneReply downloadDataPlaneReply) {
        chunks[downloadDataPlaneReply.getSequenceNumber() - 1] = downloadDataPlaneReply;
        try {
            loopLock.lock();
            currentNumberOfChunksGathered++;
            if (currentNumberOfChunksGathered == numberOfChunks) waitingForChunks = false;
        } catch (Exception ignored) { } finally {
            loopLock.unlock();
        }
    }

}
