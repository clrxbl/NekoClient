package dev.neko.nekoclient.api.ddos.impl.handler.minecraft.type;

import java.io.EOFException;
import java.nio.ByteBuffer;

public abstract class Type<T> {
   protected T value;

   public Type(T value) {
      this.value = value;
   }

   public Type() {
   }

   public abstract void write(ByteBuffer var1);

   public T read(ByteBuffer buffer) throws EOFException {
      this.value = this.read0(buffer);
      return this.value;
   }

   protected abstract T read0(ByteBuffer var1) throws EOFException;

   public abstract int size();

   public final T getValue() {
      return this.value;
   }
}
