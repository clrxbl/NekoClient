package dev.neko.nekoinjector.template.impl;

import dev.neko.nekoinjector.template.SimpleTemplate;
import java.util.Arrays;
import java.util.Collections;

public class MinecraftClientTemplate extends SimpleTemplate {
   public MinecraftClientTemplate() {
      super(
         "minecraft-client",
         Collections.singletonList("net/minecraft/client/main/Main.class"),
         Arrays.asList("net/minecraft/client/main/Main.class", "net/minecraft/client/gui/GuiMultiplayer.class")
      );
   }
}
