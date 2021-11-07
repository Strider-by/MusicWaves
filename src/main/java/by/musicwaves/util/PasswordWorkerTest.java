//package by.musicwaves.util;
//
//import javax.crypto.SecretKeyFactory;
//import javax.crypto.spec.PBEKeySpec;
//import java.security.NoSuchAlgorithmException;
//import java.security.SecureRandom;
//import java.security.spec.InvalidKeySpecException;
//import java.security.spec.KeySpec;
//
//public class PasswordWorker {
//
//    private final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
//
//    public static String processPasswordHashing(CharSequence password) {
//        SecureRandom random = new SecureRandom();
//        byte[] salt = new byte[16];
//        random.nextBytes(salt);
//
//        KeySpec spec = new PBEKeySpec(password.toString().toCharArray(), salt, 65536, 128);
//        SecretKeyFactory factory = null;
//        try {
//            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        try {
//            byte[] hash = factory.generateSecret(spec).getEncoded();
//        } catch (InvalidKeySpecException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static String bytesToHex(byte[] bytes)
//    {
//        char[] hexChars = new char[bytes.length * 2];
//        for (int j = 0 ; j < bytes.length ; j++)
//        {
//            int v = bytes[j] & 0xFF;
//            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
//            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
//        }
//
//        return new String(hexChars);
//    }
//}
