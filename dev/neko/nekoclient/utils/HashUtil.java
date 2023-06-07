package dev.neko.nekoclient.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
   public static String generateMD5Hash(byte[] bytes) throws NoSuchAlgorithmException {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      md5.update(bytes);
      return new BigInteger(1, md5.digest()).toString(16);
   }
}
