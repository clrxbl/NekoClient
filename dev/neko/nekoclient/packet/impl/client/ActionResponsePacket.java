package dev.neko.nekoclient.packet.impl.client;

import dev.neko.nekoclient.api.buffer.FriendlyByteBuffer;
import dev.neko.nekoclient.api.buffer.StreamByteBuffer;
import dev.neko.nekoclient.packet.Direction;
import dev.neko.nekoclient.packet.impl.NoncePacket;
import java.io.IOException;

public class ActionResponsePacket extends NoncePacket {
   private boolean success;

   public ActionResponsePacket() {
   }

   public ActionResponsePacket(String nonce, boolean success) {
      super(nonce);
      this.success = success;
   }

   @Override
   public void write(StreamByteBuffer buffer) throws IOException {
      super.write(buffer);
      buffer.putBoolean(this.success);
   }

   @Override
   public void read(FriendlyByteBuffer buffer) throws IOException {
      super.read(buffer);
      this.success = buffer.getBoolean();
   }

   @Override
   public Direction getDirection() {
      return Direction.CLIENT_TO_SERVER;
   }

   @Override
   public String getName() {
      return "actionresponse";
   }

   public final boolean isSuccess() {
      return this.success;
   }
}
