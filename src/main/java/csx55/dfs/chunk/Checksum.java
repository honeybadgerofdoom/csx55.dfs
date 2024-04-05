package csx55.dfs.chunk;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
Used to validate a chunk
 */
public class Checksum {

    private byte[] digest;

    public Checksum(byte[] slice) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(slice);
            this.digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Failed to build MessageDigest instance " + e);
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

}
