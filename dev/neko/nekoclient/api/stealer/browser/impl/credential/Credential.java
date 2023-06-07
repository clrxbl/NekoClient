package dev.neko.nekoclient.api.stealer.browser.impl.credential;

import java.net.URL;
import java.util.Objects;

public class Credential {
   private final URL host;
   private final String username;
   private final String password;

   public Credential(URL host, String username, String password) {
      this.host = host;
      this.username = username;
      this.password = password;
   }

   public final URL getHost() {
      return this.host;
   }

   public final String getUsername() {
      return this.username;
   }

   public final String getPassword() {
      return this.password;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof Credential)) {
         return super.equals(obj);
      } else {
         Credential other = (Credential)obj;
         return Objects.equals(other.getHost(), this.host)
            && Objects.equals(other.getUsername(), this.username)
            && Objects.equals(other.getPassword(), this.password);
      }
   }
}
