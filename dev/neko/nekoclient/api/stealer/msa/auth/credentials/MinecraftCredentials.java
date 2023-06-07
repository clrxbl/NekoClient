package dev.neko.nekoclient.api.stealer.msa.auth.credentials;

import com.eclipsesource.json.JsonObject;

public class MinecraftCredentials {
   private final String accessToken;
   private final long expiration;

   public MinecraftCredentials(String accessToken, long expiration) {
      this.accessToken = accessToken;
      this.expiration = expiration;
   }

   public final String getAccessToken() {
      return this.accessToken;
   }

   public final long getExpiration() {
      return this.expiration;
   }

   public static MinecraftCredentials parseResponse(JsonObject json) {
      return new MinecraftCredentials(json.get("access_token").asString(), System.currentTimeMillis() + json.get("expires_in").asLong() * 1000L);
   }
}
