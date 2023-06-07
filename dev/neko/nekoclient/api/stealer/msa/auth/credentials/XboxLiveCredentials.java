package dev.neko.nekoclient.api.stealer.msa.auth.credentials;

import com.eclipsesource.json.JsonObject;
import java.time.OffsetDateTime;

public class XboxLiveCredentials {
   private final String token;
   private final long expiration;
   private final String userHash;

   public XboxLiveCredentials(String token, long expiration, String userHash) {
      this.token = token;
      this.expiration = expiration;
      this.userHash = userHash;
   }

   public final String getToken() {
      return this.token;
   }

   public final String getUserHash() {
      return this.userHash;
   }

   public final long getExpiration() {
      return this.expiration;
   }

   public static XboxLiveCredentials parseResponse(JsonObject json) {
      return new XboxLiveCredentials(
         json.get("Token").asString(),
         OffsetDateTime.parse(json.get("NotAfter").asString()).toInstant().toEpochMilli(),
         json.get("DisplayClaims").asObject().get("xui").asArray().get(0).asObject().get("uhs").asString()
      );
   }
}
