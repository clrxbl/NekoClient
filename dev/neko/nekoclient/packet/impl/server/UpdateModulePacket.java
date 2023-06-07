package dev.neko.nekoclient.packet.impl.server;

import dev.neko.nekoclient.api.buffer.FriendlyByteBuffer;
import dev.neko.nekoclient.api.buffer.StreamByteBuffer;
import dev.neko.nekoclient.packet.Direction;
import dev.neko.nekoclient.packet.Packet;
import java.io.IOException;

public class UpdateModulePacket implements Packet {
   private String moduleName;
   private boolean enabled;

   public UpdateModulePacket(String moduleName, boolean enabled) {
      this.moduleName = moduleName;
      this.enabled = enabled;
   }

   public UpdateModulePacket() {
   }

   @Override
   public void read(FriendlyByteBuffer buffer) throws IOException {
      this.moduleName = buffer.getString();
      this.enabled = buffer.getBoolean();
   }

   @Override
   public void write(StreamByteBuffer buffer) throws IOException {
      buffer.putString(this.moduleName);
      buffer.putBoolean(this.enabled);
   }

   @Override
   public Direction getDirection() {
      return Direction.SERVER_TO_CLIENT;
   }

   @Override
   public String getName() {
      return "updatemodule";
   }

   public final String getModuleName() {
      return this.moduleName;
   }

   public final boolean isEnabled() {
      return this.enabled;
   }
}
