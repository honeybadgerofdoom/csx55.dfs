package csx55.dfs.testing;

import csx55.dfs.wireformats.Event;
import csx55.dfs.wireformats.Protocol;

import java.io.IOException;

public class Poke extends Event {

    private String message;

    public Poke(String message) {
        super(Protocol.POKE);
        this.message = message;
    }

    public Poke(byte[] bytes) throws IOException {
        super(bytes);
    }

    public String getMessage() {
        return message;
    }

    @Override
    protected void marshall() throws IOException {
        marshallString(message);
    }

    @Override
    protected void unmarshall() throws IOException {
        this.message = unmarshallString();
    }
}