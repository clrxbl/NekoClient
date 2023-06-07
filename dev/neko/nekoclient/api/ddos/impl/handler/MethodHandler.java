package dev.neko.nekoclient.api.ddos.impl.handler;

import com.eclipsesource.json.JsonObject;
import dev.neko.nekoclient.api.ddos.Protocol;
import java.io.IOException;
import java.net.InetSocketAddress;

public interface MethodHandler {
   void init(Protocol var1, InetSocketAddress var2, String var3, JsonObject var4) throws IOException, IllegalArgumentException;

   void run(Protocol var1, InetSocketAddress var2) throws IOException;

   default void cleanup() {
   }

   default InetSocketAddress transformAddress(InetSocketAddress address, String host) {
      return address;
   }
}
