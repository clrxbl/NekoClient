package dev.neko.nekoinjector.utils;

import java.util.StringJoiner;

public class EncodingUtil {
   public static byte[] decodeFromByteFormat(String string) {
      String[] split = string.split("\\.");
      byte[] bytes = new byte[split.length];

      for(int i = 0; i < bytes.length; ++i) {
         bytes[i] = Byte.parseByte(split[i]);
      }

      return bytes;
   }

   public static String encodeToByteFormat(byte[] bytes) {
      StringJoiner joiner = new StringJoiner(".");

      for(byte b : bytes) {
         joiner.add(String.valueOf((int)b));
      }

      return joiner.toString();
   }

   public static byte[] reverseBytes(byte[] bytes) {
      byte[] reversedBytes = new byte[bytes.length];

      for(int i = 0; i < bytes.length; ++i) {
         byte originalByte = bytes[i];
         byte reversedByte = 0;

         for(int j = 0; j < 8; ++j) {
            reversedByte = (byte)(reversedByte << 1);
            reversedByte = (byte)(reversedByte | originalByte & 1);
            originalByte = (byte)(originalByte >> 1);
         }

         reversedBytes[i] = reversedByte;
      }

      return reversedBytes;
   }

   public static byte[] restoreReversedBytes(byte[] reversedBytes) {
      byte[] bytes = new byte[reversedBytes.length];

      for(int i = 0; i < reversedBytes.length; ++i) {
         byte reversedByte = reversedBytes[i];
         byte originalByte = 0;

         for(int j = 0; j < 8; ++j) {
            originalByte = (byte)(originalByte << 1);
            originalByte = (byte)(originalByte | reversedByte & 1);
            reversedByte = (byte)(reversedByte >> 1);
         }

         bytes[i] = originalByte;
      }

      return bytes;
   }
}
