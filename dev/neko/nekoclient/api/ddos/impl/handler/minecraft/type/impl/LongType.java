package dev.neko.nekoclient.api.ddos.impl.handler.minecraft.type.impl;

import dev.neko.nekoclient.api.ddos.impl.handler.minecraft.type.Type;
import java.io.EOFException;
import java.nio.ByteBuffer;

public class LongType extends Type<Long> {
   public static final int SIZE = 8;

   public LongType(long value) {
      super(value);
   }

   public LongType() {
   }

   @Override
   public void write(ByteBuffer buffer) {
      buffer.putLong(this.value);
   }

   protected Long read0(ByteBuffer buffer) throws EOFException {
      return buffer.getLong();
   }

   @Override
   public int size() {
      return 8;
   }
}
