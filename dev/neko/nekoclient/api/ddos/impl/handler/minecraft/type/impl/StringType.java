package dev.neko.nekoclient.api.ddos.impl.handler.minecraft.type.impl;

import dev.neko.nekoclient.api.ddos.impl.handler.minecraft.type.Type;
import java.io.EOFException;
import java.nio.ByteBuffer;

public class StringType extends Type<String> {
   private VarIntType varIntType;

   public StringType(String string) {
      super(string);
      this.varIntType = new VarIntType(string.getBytes().length);
   }

   public StringType() {
      this.varIntType = new VarIntType();
   }

   @Override
   public void write(ByteBuffer buffer) {
      this.varIntType.write(buffer);
      buffer.put(this.value.getBytes());
   }

   public String read(ByteBuffer buffer) throws EOFException {
      String string = (String)super.read(buffer);
      this.varIntType = new VarIntType(string.length());
      return string;
   }

   public String read0(ByteBuffer buffer) throws EOFException {
      byte[] bytes = new byte[this.varIntType.read(buffer)];
      buffer.get(bytes);
      return new String(bytes);
   }

   @Override
   public int size() {
      return this.varIntType.size() + this.value.getBytes().length;
   }
}
