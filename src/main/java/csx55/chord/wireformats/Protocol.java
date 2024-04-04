package csx55.chord.wireformats;

public class Protocol {

    // message types
    public static final int REGISTER_REQUEST = 1;
    public static final int REGISTER_RESPONSE = 2;
    public static final int DEREGISTER_REQUEST = 3;
    public static final int DEREGISTER_RESPONSE = 4;
    public static final int DETAILS_FOR_NEW_NODE = 5;
    public static final int NEW_NODE_REQUEST = 6;
    public static final int UPDATE_FINGER_TABLE_JOIN = 7;
    public static final int UPDATE_FINGER_TABLE_LEAVE = 8;
    public static final int UPLOAD_FILE = 9;
    public static final int DOWNLOAD_FILE = 10;
    public static final int DOWNLOAD_DELIVERY = 11;
    public static final int POKE = 100;

    // status codes
    public static final byte SUCCESS = 1;
    public static final byte FAILURE = 0;

}