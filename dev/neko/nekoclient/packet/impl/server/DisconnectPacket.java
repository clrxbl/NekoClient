package dev.neko.nekoclient.packet.impl.server;

import dev.neko.nekoclient.api.buffer.FriendlyByteBuffer;
import dev.neko.nekoclient.api.buffer.StreamByteBuffer;
import dev.neko.nekoclient.api.disconnect.DisconnectReason;
import dev.neko.nekoclient.packet.Direction;
import dev.neko.nekoclient.packet.Packet;
import java.io.IOException;

public class DisconnectPacket implements Packet {
   private DisconnectReason reason;

   public DisconnectPacket() {
   }

   public DisconnectPacket(DisconnectReason reason) {
      this.reason = reason;
   }

   @Override
   public void read(FriendlyByteBuffer buffer) throws IOException {
      this.reason = DisconnectReason.valueOf(buffer.getString());
   }

   @Override
   public void write(StreamByteBuffer buffer) throws IOException {
      buffer.putString(this.reason.name());
   }

   @Override
   public String getName() {
      return "disconnect";
   }

   @Override
   public Direction getDirection() {
      return Direction.SERVER_TO_CLIENT;
   }

   public final DisconnectReason getReason() {
      return this.reason;
   }
}
