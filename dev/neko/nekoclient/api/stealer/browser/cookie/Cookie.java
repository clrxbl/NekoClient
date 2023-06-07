package dev.neko.nekoclient.api.stealer.browser.cookie;

import java.util.Objects;

public class Cookie {
   private final String host;
   private final String path;
   private final String name;
   private final String value;
   private final long expires;
   private final boolean secure;
   private final boolean httpOnly;

   public Cookie(String host, String path, String name, String value, long expires, boolean secure, boolean httpOnly) {
      this.host = host;
      this.path = path;
      this.name = name;
      this.value = value;
      this.expires = expires;
      this.secure = secure;
      this.httpOnly = httpOnly;
   }

   public final String getValue() {
      return this.value;
   }

   public final String getPath() {
      return this.path;
   }

   public final String getHost() {
      return this.host;
   }

   public final String getName() {
      return this.name;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof Cookie)) {
         return false;
      } else {
         Cookie other = (Cookie)obj;
         return Objects.equals(this.name, other.getName())
            && Objects.equals(this.host, other.getHost())
            && Objects.equals(this.value, other.getValue())
            && Objects.equals(this.path, other.getPath());
      }
   }

   public final long getExpires() {
      return this.expires;
   }

   public final boolean isSecure() {
      return this.secure;
   }

   public final boolean isHttpOnly() {
      return this.httpOnly;
   }
}
