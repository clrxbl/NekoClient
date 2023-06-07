package dev.neko.nekoclient.api.ddos.impl.handler.minecraft.type.impl;

import dev.neko.nekoclient.api.ddos.impl.handler.minecraft.type.Type;
import java.io.EOFException;
import java.nio.ByteBuffer;

public class UnsignedShortType extends Type<Integer> {
   public static final int SIZE = 2;

   public UnsignedShortType(int value) {
      super(value);
   }

   public UnsignedShortType() {
   }

   @Override
   public void write(ByteBuffer buffer) {
      buffer.put((byte)(this.value >>> 8 & 0xFF));
      buffer.put((byte)(this.value & 0xFF));
   }

   public Integer read0(ByteBuffer buffer) throws EOFException {
      int ch1 = buffer.get();
      int ch2 = buffer.get();
      if ((ch1 | ch2) < 0) {
         throw new EOFException();
      } else {
         return (ch1 << 8) + ch2;
      }
   }

   @Override
   public int size() {
      return 2;
   }
}
