package dev.neko.nekoclient.api.stealer.discord;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DiscordAccount {
   private final String token;
   private final String id;
   private final String username;
   private final String discriminator;
   private final String email;
   private final String phone;
   private final boolean verified;
   private final boolean mfa;
   private final List<String> badges;
   private final List<String> paymentSources;

   public DiscordAccount(
      String token,
      String id,
      String username,
      String discriminator,
      String email,
      String phone,
      boolean verified,
      boolean mfa,
      List<String> badges,
      List<String> paymentSources
   ) {
      this.token = token;
      this.id = id;
      this.username = username;
      this.discriminator = discriminator;
      this.email = email;
      this.phone = phone;
      this.verified = verified;
      this.mfa = mfa;
      this.badges = badges;
      this.paymentSources = paymentSources;
   }

   public static DiscordAccount parse(String token, JsonObject profile, JsonObject user, JsonArray paymentSources) {
      return new DiscordAccount(
         token,
         user.get("id").asString(),
         user.get("username").asString(),
         user.get("discriminator").asString(),
         user.get("email").asString(),
         user.get("phone").asString(),
         user.get("verified").asBoolean(),
         user.get("mfa_enabled").asBoolean(),
         profile.get("badges").asArray().values().stream().map(JsonValue::asObject).map(object -> object.get("id").asString()).collect(Collectors.toList()),
         paymentSources.values()
            .stream()
            .map(JsonValue::asObject)
            .filter(source -> !source.getBoolean("invalid", true))
            .map(source -> source.get("brand"))
            .filter(Objects::nonNull)
            .map(JsonValue::asString)
            .collect(Collectors.toList())
      );
   }

   public final String getToken() {
      return this.token;
   }

   public final String getId() {
      return this.id;
   }

   public final String getUsername() {
      return this.username;
   }

   public final String getDiscriminator() {
      return this.discriminator;
   }

   public final List<String> getBadges() {
      return this.badges;
   }

   public final String getEmail() {
      return this.email;
   }

   public final String getPhone() {
      return this.phone;
   }

   public final boolean isVerified() {
      return this.verified;
   }

   public final boolean isMfa() {
      return this.mfa;
   }

   public final List<String> getPaymentSources() {
      return this.paymentSources;
   }
}
