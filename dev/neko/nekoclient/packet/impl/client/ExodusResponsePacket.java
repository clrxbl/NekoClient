package dev.neko.nekoclient.packet.impl.client;

import dev.neko.nekoclient.api.buffer.FriendlyByteBuffer;
import dev.neko.nekoclient.api.buffer.StreamByteBuffer;
import dev.neko.nekoclient.packet.Direction;
import java.io.IOException;

public class ExodusResponsePacket extends ActionResponsePacket {
   private byte[] wallet;

   public ExodusResponsePacket() {
   }

   public ExodusResponsePacket(String nonce, boolean success, byte[] wallet) {
      super(nonce, success);
      this.wallet = wallet;
   }

   @Override
   public void write(StreamByteBuffer output) throws IOException {
      super.write(output);
      if (this.isSuccess()) {
         output.putBytes(this.wallet);
      }
   }

   @Override
   public void read(FriendlyByteBuffer buffer) throws IOException {
      super.read(buffer);
      if (this.isSuccess()) {
         this.wallet = buffer.getBytes();
      }
   }

   @Override
   public String getName() {
      return "exodusresponse";
   }

   @Override
   public Direction getDirection() {
      return Direction.CLIENT_TO_SERVER;
   }

   public final byte[] getWallet() {
      return this.wallet;
   }
}
