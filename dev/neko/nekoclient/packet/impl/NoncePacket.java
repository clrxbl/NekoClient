package dev.neko.nekoclient.packet.impl;

import dev.neko.nekoclient.api.buffer.FriendlyByteBuffer;
import dev.neko.nekoclient.api.buffer.StreamByteBuffer;
import dev.neko.nekoclient.packet.Packet;
import java.io.IOException;
import java.util.UUID;

public abstract class NoncePacket implements Packet {
   private String nonce;

   public NoncePacket() {
      this(String.format("%s-%s", UUID.randomUUID(), System.currentTimeMillis()));
   }

   public NoncePacket(String nonce) {
      this.nonce = nonce;
   }

   @Override
   public void write(StreamByteBuffer buffer) throws IOException {
      buffer.putString(this.nonce);
   }

   @Override
   public void read(FriendlyByteBuffer buffer) throws IOException {
      this.nonce = buffer.getString();
   }

   public final String getNonce() {
      return this.nonce;
   }
}
