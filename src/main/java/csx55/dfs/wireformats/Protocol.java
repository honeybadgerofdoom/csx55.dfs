package csx55.dfs.wireformats;


/*
Wireformat protocols for differentiating Events
 */
public class Protocol {

    public static final int REGISTER_REQUEST = 1;
    public static final int CHUNK_DELIVERY = 2;
    public static final int LOCATIONS_FOR_CHUNK_REQUEST = 3;
    public static final int LOCATIONS_FOR_CHUNK_REPLY = 4;
    public static final int PRINT_CHUNKS = 5;
    public static final int HEARTBEAT = 6;
    public static final int DOWNLOAD_CONTROL_PLANE_REQUEST = 7;
    public static final int DOWNLOAD_CONTROL_PLANE_REPLY = 8;
    public static final int DOWNLOAD_DATA_PLANE_REQUEST = 9;
    public static final int DOWNLOAD_DATA_PLANE_REPLY = 10;
    public static final int REPAIR_CHUNK_CONTROL_PLANE_REQUEST = 11;
    public static final int POKE = 100;

}