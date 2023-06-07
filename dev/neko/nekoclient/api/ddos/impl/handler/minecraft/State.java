package dev.neko.nekoclient.api.ddos.impl.handler.minecraft;

public enum State {
   HANDSHAKING(0),
   STATUS(1),
   LOGIN(2),
   PLAY(3);

   private final int id;

   private State(int id) {
      this.id = id;
   }

   public final int getId() {
      return this.id;
   }
}
