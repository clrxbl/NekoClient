package dev.neko.nekoclient.api.debugger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Debugger {
   private final InetSocketAddress address;
   private final int id;
   private final SocketChannel channel;

   public Debugger(InetSocketAddress address, int id) throws IOException {
      this.address = address;
      this.id = id;
      this.channel = SocketChannel.open();
   }

   public void connect() throws IOException {
      this.channel.configureBlocking(true);
      this.channel.socket().connect(this.address, 5000);
      this.channel.write((ByteBuffer)((Buffer)ByteBuffer.allocate(4).putInt(this.id)).flip());
   }

   public void close() throws IOException {
      this.channel.close();
   }

   public void debug(String text) throws IOException {
      this.debug(text.getBytes());
   }

   public void debug(byte[] bytes) throws IOException {
      this.channel.write((ByteBuffer)((Buffer)ByteBuffer.allocate(4).putInt(bytes.length)).flip());
      this.channel.write(ByteBuffer.wrap(bytes));
   }
}
