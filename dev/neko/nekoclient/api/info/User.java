package dev.neko.nekoclient.api.info;

public class User {
   private final String name;
   private final String hostname;
   private final String home;
   private final String country;
   private final String language;

   public User(String name, String hostname, String home, String country, String language) {
      this.name = name;
      this.hostname = hostname;
      this.home = home;
      this.country = country;
      this.language = language;
   }

   public final String getName() {
      return this.name;
   }

   public final String getHome() {
      return this.home;
   }

   public final String getCountry() {
      return this.country;
   }

   public final String getLanguage() {
      return this.language;
   }

   public final String getHostname() {
      return this.hostname;
   }
}
