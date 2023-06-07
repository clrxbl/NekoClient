package dev.neko.nekoclient.api.ddos.impl.handler.minecraft.type.impl;

import dev.neko.nekoclient.api.ddos.impl.handler.minecraft.type.Type;
import java.io.EOFException;
import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDType extends Type<UUID> {
   public UUIDType(UUID value) {
      super(value);
   }

   public UUIDType() {
   }

   @Override
   public void write(ByteBuffer buffer) {
      buffer.putLong(this.value.getMostSignificantBits());
      buffer.putLong(this.value.getLeastSignificantBits());
   }

   protected UUID read0(ByteBuffer buffer) throws EOFException {
      return new UUID(buffer.getLong(), buffer.getLong());
   }

   @Override
   public int size() {
      return 16;
   }
}
