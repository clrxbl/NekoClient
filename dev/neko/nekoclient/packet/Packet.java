package dev.neko.nekoclient.packet;

import dev.neko.nekoclient.api.buffer.FriendlyByteBuffer;
import dev.neko.nekoclient.api.buffer.StreamByteBuffer;
import java.io.IOException;

public interface Packet {
   void read(FriendlyByteBuffer var1) throws IOException;

   void write(StreamByteBuffer var1) throws IOException;

   Direction getDirection();

   String getName();

   default String getId() {
      return String.format("%s:%s", this.getDirection(), this.getName());
   }
}
