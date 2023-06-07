package dev.neko.nekoclient.packet.impl.server;

import dev.neko.nekoclient.api.buffer.FriendlyByteBuffer;
import dev.neko.nekoclient.api.buffer.StreamByteBuffer;
import dev.neko.nekoclient.packet.Direction;
import dev.neko.nekoclient.packet.Packet;
import java.io.IOException;

public class HelloPacket implements Packet {
   @Override
   public void read(FriendlyByteBuffer input) throws IOException {
   }

   @Override
   public void write(StreamByteBuffer output) throws IOException {
   }

   @Override
   public String getName() {
      return "hello";
   }

   @Override
   public Direction getDirection() {
      return Direction.SERVER_TO_CLIENT;
   }
}
