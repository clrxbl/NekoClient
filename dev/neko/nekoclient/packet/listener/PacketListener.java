package dev.neko.nekoclient.packet.listener;

import dev.neko.nekoclient.Client;
import dev.neko.nekoclient.packet.Packet;
import java.io.IOException;

public interface PacketListener<T extends Packet> {
   void call(T var1, Client var2, String var3) throws IOException;
}
