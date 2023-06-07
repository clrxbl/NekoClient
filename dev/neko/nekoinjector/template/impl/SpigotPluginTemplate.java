package dev.neko.nekoinjector.template.impl;

import dev.neko.nekoinjector.asm.Entry;
import dev.neko.nekoinjector.asm.EntryList;
import dev.neko.nekoinjector.template.Template;
import java.util.Objects;

public class SpigotPluginTemplate implements Template {
   private static final String PLUGIN_SUPERCLASS = "org/bukkit/plugin/java/JavaPlugin";

   @Override
   public boolean shouldSuggest(EntryList entries) {
      return entries.stream().anyMatch(entry -> entry.isClass() && Objects.equals(entry.getClassNode().superName, "org/bukkit/plugin/java/JavaPlugin"));
   }

   @Override
   public boolean shouldInject(Entry entry) {
      return Objects.equals(entry.getClassNode().superName, "org/bukkit/plugin/java/JavaPlugin");
   }

   @Override
   public String getName() {
      return "minecraft-spigot-plugin";
   }
}
