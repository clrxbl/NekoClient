package dev.neko.nekoclient.api.stealer.msa.auth.credentials;

import com.eclipsesource.json.JsonObject;
import java.time.OffsetDateTime;

public class XSTSCredentials {
   private final String token;
   private final long expiration;
   private final String userHash;

   public XSTSCredentials(String token, long expiration, String userHash) {
      this.token = token;
      this.expiration = expiration;
      this.userHash = userHash;
   }

   public final String getToken() {
      return this.token;
   }

   public final long getExpiration() {
      return this.expiration;
   }

   public final String getUserHash() {
      return this.userHash;
   }

   public static XSTSCredentials parseResponse(JsonObject json) {
      return new XSTSCredentials(
         json.get("Token").asString(),
         OffsetDateTime.parse(json.get("NotAfter").asString()).toInstant().toEpochMilli(),
         json.get("DisplayClaims").asObject().get("xui").asArray().get(0).asObject().get("uhs").asString()
      );
   }
}
