package dev.neko.nekoclient.api.disconnect;

public class DisconnectException extends RuntimeException {
   private final DisconnectReason reason;

   public DisconnectException(DisconnectReason reason) {
      this.reason = reason;
   }

   public final DisconnectReason getReason() {
      return this.reason;
   }
}
