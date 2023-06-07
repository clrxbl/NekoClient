package dev.neko.nekoclient.api.buffer;

import java.io.EOFException;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Objects;

public class FriendlyByteBuffer {
   private final ByteBuffer buffer;

   public FriendlyByteBuffer(ByteBuffer buffer) {
      this.buffer = buffer;
   }

   public final byte get() {
      return this.buffer.get();
   }

   public final int getInt() {
      return this.buffer.getInt();
   }

   public final long getLong() {
      return this.buffer.getLong();
   }

   public final char getChar() {
      return this.buffer.getChar();
   }

   public final double getDouble() {
      return this.buffer.getDouble();
   }

   public final float getFloat() {
      return this.buffer.getFloat();
   }

   public final short getShort() {
      return this.buffer.getShort();
   }

   public final byte[] getBytes() {
      int length = this.buffer.getInt();
      if (length < 0) {
         return null;
      } else {
         byte[] bytes = new byte[length];
         this.buffer.get(bytes);
         return bytes;
      }
   }

   public final byte[] array() {
      return this.buffer.array();
   }

   public static FriendlyByteBuffer readFully(SocketChannel channel, int length) throws IOException {
      ByteBuffer buffer = ByteBuffer.allocate(length);

      while(buffer.remaining() > 0) {
         if (Objects.equals(channel.read(buffer), -1) && buffer.remaining() > 0) {
            throw new EOFException();
         }
      }

      ((Buffer)buffer).flip();
      return new FriendlyByteBuffer(buffer);
   }

   public final int getUnsignedShort() {
      return this.buffer.getShort() & 65535;
   }

   public final boolean getBoolean() {
      return this.buffer.get() == 1;
   }

   public final String getString() {
      byte[] bytes = this.getBytes();
      return Objects.isNull(bytes) ? null : new String(bytes);
   }

   public final ByteBuffer getBuffer() {
      return this.buffer;
   }
}
