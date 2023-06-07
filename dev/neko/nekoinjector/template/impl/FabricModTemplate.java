package dev.neko.nekoinjector.template.impl;

import dev.neko.nekoinjector.asm.Entry;
import dev.neko.nekoinjector.asm.EntryList;
import dev.neko.nekoinjector.template.Template;

public class FabricModTemplate implements Template {
   private static final String MOD_INTERFACE = "net/fabricmc/api/ModInitializer";

   @Override
   public boolean shouldSuggest(EntryList entries) {
      return entries.stream().anyMatch(entry -> entry.isClass() && entry.getClassNode().interfaces.contains("net/fabricmc/api/ModInitializer"));
   }

   @Override
   public boolean shouldInject(Entry entry) {
      return entry.getClassNode().interfaces.contains("net/fabricmc/api/ModInitializer");
   }

   @Override
   public String getName() {
      return "minecraft-fabric-mod";
   }
}
