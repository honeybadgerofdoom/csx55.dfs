package csx55.dfs.chunk;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

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

    public byte[] getDigest() {
        return digest;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Checksum other = (Checksum) obj;
        return Arrays.equals(other.getDigest(), this.digest);
    }

}
