package dev.neko.nekoclient.api.ddos.impl.handler.minecraft.type.impl;

import dev.neko.nekoclient.api.ddos.impl.handler.minecraft.type.Type;
import java.io.EOFException;
import java.nio.ByteBuffer;

public class BytesType extends Type<byte[]> {
   private VarIntType varIntType;

   public BytesType(byte[] bytes) {
      super(bytes);
      this.varIntType = new VarIntType(bytes.length);
   }

   public BytesType() {
      this.varIntType = new VarIntType();
   }

   @Override
   public void write(ByteBuffer buffer) {
      this.varIntType.write(buffer);
      buffer.put(this.value);
   }

   public byte[] read(ByteBuffer buffer) throws EOFException {
      byte[] bytes = (byte[])super.read(buffer);
      this.varIntType = new VarIntType(bytes.length);
      return bytes;
   }

   public byte[] read0(ByteBuffer buffer) throws EOFException {
      byte[] bytes = new byte[this.varIntType.read(buffer)];
      buffer.get(bytes);
      return bytes;
   }

   @Override
   public int size() {
      return this.varIntType.size() + ((byte[])this.value).length;
   }
}
