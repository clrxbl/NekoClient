package dev.neko.nekoclient.packet.listener.impl;

import dev.neko.nekoclient.Client;
import dev.neko.nekoclient.packet.impl.server.DisconnectPacket;
import dev.neko.nekoclient.packet.listener.PacketListener;
import java.io.IOException;

public class DisconnectPacketListener implements PacketListener<DisconnectPacket> {
   public void call(DisconnectPacket packet, Client client, String id) throws IOException {
      client.close();
   }
}
