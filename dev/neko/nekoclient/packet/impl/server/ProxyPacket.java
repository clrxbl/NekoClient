package dev.neko.nekoclient.packet.impl.server;

import dev.neko.nekoclient.api.buffer.FriendlyByteBuffer;
import dev.neko.nekoclient.api.buffer.StreamByteBuffer;
import dev.neko.nekoclient.packet.Direction;
import dev.neko.nekoclient.packet.impl.NoncePacket;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ProxyPacket extends NoncePacket {
   private int acceptorPort;
   private InetSocketAddress address;

   public ProxyPacket() {
   }

   public ProxyPacket(int acceptorPort, InetSocketAddress address) {
      this.acceptorPort = acceptorPort;
      this.address = address;
   }

   @Override
   public void read(FriendlyByteBuffer buffer) throws IOException {
      super.read(buffer);
      this.acceptorPort = buffer.getUnsignedShort();
      this.address = new InetSocketAddress(InetAddress.getByAddress(buffer.getBytes()), buffer.getUnsignedShort());
   }

   @Override
   public void write(StreamByteBuffer buffer) throws IOException {
      super.write(buffer);
      buffer.putUnsignedShort(this.acceptorPort);
      buffer.putBytes(this.address.getAddress().getAddress());
      buffer.putUnsignedShort(this.address.getPort());
   }

   @Override
   public String getName() {
      return "proxy";
   }

   @Override
   public Direction getDirection() {
      return Direction.SERVER_TO_CLIENT;
   }

   public final int getAcceptorPort() {
      return this.acceptorPort;
   }

   public final InetSocketAddress getAddress() {
      return this.address;
   }
}
