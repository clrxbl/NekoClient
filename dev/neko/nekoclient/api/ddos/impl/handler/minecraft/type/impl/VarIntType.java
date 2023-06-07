package dev.neko.nekoclient.api.ddos.impl.handler.minecraft.type.impl;

import dev.neko.nekoclient.api.ddos.impl.handler.minecraft.type.Type;
import java.nio.ByteBuffer;

public class VarIntType extends Type<Integer> {
   public static final int MAX_SIZE = 2097151;
   public static final int MAX_BYTES = 5;

   public VarIntType(int value) {
      super(value);
   }

   public VarIntType() {
   }

   @Override
   public void write(ByteBuffer buffer) {
      int v;
      for(v = this.value; (v & -128) != 0; v >>>= 7) {
         buffer.put((byte)(v & 127 | 128));
      }

      buffer.put((byte)v);
   }

   public Integer read0(ByteBuffer buffer) {
      int var = 0;
      int moves = 0;

      byte buff;
      do {
         buff = buffer.get();
         var |= (buff & 127) << moves++ * 7;
         if (moves > 5) {
            throw new RuntimeException("VarInt too big");
         }
      } while((buff & 128) == 128);

      return var;
   }

   @Override
   public int size() {
      int v = this.value;
      if (v < 0) {
         v = ~v;
      }

      int count = 1;

      while((v >>>= 7) != 0) {
         ++count;
      }

      return count;
   }
}
