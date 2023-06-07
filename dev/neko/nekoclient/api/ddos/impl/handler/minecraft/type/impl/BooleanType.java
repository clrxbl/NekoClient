package dev.neko.nekoclient.api.ddos.impl.handler.minecraft.type.impl;

import dev.neko.nekoclient.api.ddos.impl.handler.minecraft.type.Type;
import java.io.EOFException;
import java.nio.ByteBuffer;

public class BooleanType extends Type<Boolean> {
   public BooleanType(boolean value) {
      super(value);
   }

   public BooleanType() {
   }

   @Override
   public void write(ByteBuffer buffer) {
      buffer.put((byte)(this.value ? 1 : 0));
   }

   protected Boolean read0(ByteBuffer buffer) throws EOFException {
      return buffer.get() == 1;
   }

   @Override
   public int size() {
      return 1;
   }
}
