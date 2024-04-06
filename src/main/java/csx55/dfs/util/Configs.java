package csx55.dfs.util;

public class Configs {

    public static final int B = 1;
    public static final int KB = 1024 * B;
    public static final int MB = 1024 * KB;
    public static final int GB = 1024 * MB;
    public static final int CHUNK_SIZE = 64 * KB;
    public static final int SLICE_SIZE = 8 * KB;
    public static final int NUMBER_OF_REPLICAS = 3;

    public static String filenameFromPath(String filepath) {
        String name = filepath;
        int index = name.lastIndexOf("/");
        if (index >= 0) {
            name = name.substring(index + 1);
        }
        return name;
    }

    public static String pathFromPathAndName(String filepath) {
        String path = filepath;
        int index = path.lastIndexOf("/");
        if (index >= 0) {
            path = path.substring(0, index + 1);
        }
        return path;
    }

}
