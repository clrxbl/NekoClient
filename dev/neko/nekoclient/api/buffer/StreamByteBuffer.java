package dev.neko.nekoclient.api.buffer;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Objects;

public class StreamByteBuffer {
   private ByteBuffer buffer = ByteBuffer.allocate(0);

   public StreamByteBuffer put(byte b) {
      this.expand(1);
      this.buffer.put(b);
      return this;
   }

   public StreamByteBuffer put(byte[] bytes) {
      this.expand(bytes.length);
      this.buffer.put(bytes);
      return this;
   }

   public StreamByteBuffer putLong(long l) {
      this.expand(8);
      this.buffer.putLong(l);
      return this;
   }

   public StreamByteBuffer putInt(int i) {
      this.expand(4);
      this.buffer.putInt(i);
      return this;
   }

   public StreamByteBuffer putDouble(double d) {
      this.expand(8);
      this.buffer.putDouble(d);
      return this;
   }

   public StreamByteBuffer putChar(char c) {
      this.expand(2);
      this.buffer.putChar(c);
      return this;
   }

   public StreamByteBuffer putShort(short s) {
      this.expand(2);
      this.buffer.putShort(s);
      return this;
   }

   public StreamByteBuffer putFloat(float f) {
      this.expand(4);
      this.buffer.putFloat(f);
      return this;
   }

   public StreamByteBuffer putBoolean(boolean b) {
      return this.put((byte)(b ? 1 : 0));
   }

   public StreamByteBuffer putString(String string) {
      return this.putBytes(Objects.isNull(string) ? null : string.getBytes());
   }

   public StreamByteBuffer putBytes(byte[] bytes) {
      if (Objects.isNull(bytes)) {
         this.putInt(-1);
      } else {
         this.putInt(bytes.length);
         this.put(bytes);
      }

      return this;
   }

   public StreamByteBuffer putUnsignedShort(int s) {
      this.expand(2);
      this.buffer.putShort((short)(s & 65535));
      return this;
   }

   public void expand(int expansion) {
      ByteBuffer buffer = ByteBuffer.allocate(this.buffer.capacity() + expansion);
      ((Buffer)this.buffer).flip();
      buffer.put(this.buffer);
      this.buffer = buffer;
   }

   public ByteBuffer getBuffer() {
      return this.buffer;
   }

   public ByteBuffer flip() {
      ((Buffer)this.buffer).flip();
      return this.buffer;
   }
}
