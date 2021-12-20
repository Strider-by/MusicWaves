package by.musicwaves.service.util;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class PasswordWorker {

    private static final char[] HEX_ARRAY;
    private static final String SALT;
    private static final int ITERATIONS;
    private static final int KEY_LENGTH;

    static {
        HEX_ARRAY = "0123456789ABCDEF".toCharArray();
        SALT = "8A194F54U51A3E41EAC1433D77E3DF37003F300";
        ITERATIONS = 120;
        KEY_LENGTH = 80;
    }

    public static String processPasswordHashing(char[] passwordChars) {
        byte[] saltBytes = SALT.getBytes();

        byte[] hashedBytes = hashPassword(passwordChars, saltBytes);
        String hashedString = bytesToHex(hashedBytes);

        return hashedString;
    }

    private static byte[] hashPassword(char[] password, byte[] salt) {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
            SecretKey key = skf.generateSecret(spec);
            byte[] res = key.getEncoded();
            return res;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }

        return new String(hexChars);
    }
}
