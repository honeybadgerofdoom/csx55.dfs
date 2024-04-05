package csx55.dfs.wireformats;

import java.io.IOException;

public class PrintChunks extends Event {

    public PrintChunks() {
        super(Protocol.PRINT_CHUNKS);
    }

    public PrintChunks(byte[] bytes) throws IOException {
        super(bytes);
    }

    @Override
    protected void marshall() throws IOException {

    }

    @Override
    protected void unmarshall() throws IOException {

    }

}
