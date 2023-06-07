package dev.neko.nekoclient.packet.impl.server;

import dev.neko.nekoclient.api.buffer.FriendlyByteBuffer;
import dev.neko.nekoclient.api.buffer.StreamByteBuffer;
import dev.neko.nekoclient.packet.Direction;
import dev.neko.nekoclient.packet.Packet;
import java.io.IOException;

public class KeepAlivePacket implements Packet {
   @Override
   public void read(FriendlyByteBuffer buffer) throws IOException {
   }

   @Override
   public void write(StreamByteBuffer buffer) throws IOException {
   }

   @Override
   public Direction getDirection() {
      return Direction.SERVER_TO_CLIENT;
   }

   @Override
   public String getName() {
      return "keepalive";
   }
}
