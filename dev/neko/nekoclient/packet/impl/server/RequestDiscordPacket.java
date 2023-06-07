package dev.neko.nekoclient.packet.impl.server;

import dev.neko.nekoclient.packet.Direction;
import dev.neko.nekoclient.packet.impl.NoncePacket;

public class RequestDiscordPacket extends NoncePacket {
   @Override
   public Direction getDirection() {
      return Direction.SERVER_TO_CLIENT;
   }

   @Override
   public String getName() {
      return "requestdiscord";
   }
}
