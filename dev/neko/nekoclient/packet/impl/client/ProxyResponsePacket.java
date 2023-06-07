package dev.neko.nekoclient.packet.impl.client;

import dev.neko.nekoclient.api.buffer.FriendlyByteBuffer;
import dev.neko.nekoclient.api.buffer.StreamByteBuffer;
import dev.neko.nekoclient.api.proxy.ProxyResponse;
import dev.neko.nekoclient.packet.Direction;
import dev.neko.nekoclient.packet.impl.NoncePacket;
import java.io.IOException;

public class ProxyResponsePacket extends NoncePacket {
   private ProxyResponse response;

   public ProxyResponsePacket() {
   }

   public ProxyResponsePacket(String nonce, ProxyResponse response) {
      super(nonce);
      this.response = response;
   }

   @Override
   public void read(FriendlyByteBuffer input) throws IOException {
      super.read(input);
      this.response = ProxyResponse.valueOf(input.getString());
   }

   @Override
   public void write(StreamByteBuffer output) throws IOException {
      super.write(output);
      output.putString(this.response.name());
   }

   @Override
   public Direction getDirection() {
      return Direction.CLIENT_TO_SERVER;
   }

   @Override
   public String getName() {
      return "proxyresponse";
   }

   public final ProxyResponse getResponse() {
      return this.response;
   }
}
