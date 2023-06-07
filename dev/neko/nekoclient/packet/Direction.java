package dev.neko.nekoclient.packet;

import dev.neko.nekoclient.api.info.Side;

public enum Direction {
   CLIENT_TO_SERVER(Side.CLIENT, Side.SERVER),
   SERVER_TO_CLIENT(Side.SERVER, Side.CLIENT);

   private final Side sender;
   private final Side receiver;

   private Direction(Side sender, Side receiver) {
      this.sender = sender;
      this.receiver = receiver;
   }

   public final Side getSender() {
      return this.sender;
   }

   public final Side getReceiver() {
      return this.receiver;
   }
}
