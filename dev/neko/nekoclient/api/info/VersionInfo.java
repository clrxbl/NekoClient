package dev.neko.nekoclient.api.info;

public class VersionInfo {
   private final Side side;
   private final String version;

   public VersionInfo(Side side, String version) {
      this.side = side;
      this.version = version;
   }

   public final Side getSide() {
      return this.side;
   }

   public final String getVersion() {
      return this.version;
   }
}
