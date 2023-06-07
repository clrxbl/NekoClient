package dev.neko.nekoclient.packet.listener.impl;

import dev.neko.nekoclient.Client;
import dev.neko.nekoclient.packet.impl.server.HelloPacket;
import dev.neko.nekoclient.packet.listener.PacketListener;
import java.io.IOException;

public class HelloPacketListener implements PacketListener<HelloPacket> {
   public void call(HelloPacket packet, Client client, String id) throws IOException {
      client.send(new dev.neko.nekoclient.packet.impl.client.HelloPacket(client.getClientInfo(), client.getVersionInfo()));
   }
}
