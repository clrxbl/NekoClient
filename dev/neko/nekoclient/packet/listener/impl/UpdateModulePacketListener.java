package dev.neko.nekoclient.packet.listener.impl;

import dev.neko.nekoclient.Client;
import dev.neko.nekoclient.module.Module;
import dev.neko.nekoclient.packet.impl.server.UpdateModulePacket;
import dev.neko.nekoclient.packet.listener.PacketListener;
import java.io.IOException;
import java.util.Objects;

public class UpdateModulePacketListener implements PacketListener<UpdateModulePacket> {
   public void call(UpdateModulePacket packet, Client client, String id) throws IOException {
      Module module = client.getModuleRegistry().getByName(packet.getModuleName());
      if (!Objects.isNull(module)) {
         if (!Objects.equals(module.isEnabled(), packet.isEnabled())) {
            module.setEnabled(packet.isEnabled());
         }
      }
   }
}
