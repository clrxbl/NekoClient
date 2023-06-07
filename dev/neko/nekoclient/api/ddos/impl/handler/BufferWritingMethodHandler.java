package dev.neko.nekoclient.api.ddos.impl.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.function.Supplier;

public abstract class BufferWritingMethodHandler extends WriteMethodHandler {
   protected ByteBuffer buffer;

   @Override
   public void handle(ByteChannel channel, Supplier<Boolean> connected) throws IOException {
      channel.write(this.buffer.duplicate());
      channel.close();
   }
}
