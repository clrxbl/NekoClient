package dev.neko.nekoinjector.template.impl;

import dev.neko.nekoinjector.asm.Entry;
import dev.neko.nekoinjector.asm.EntryList;
import dev.neko.nekoinjector.template.Template;
import java.util.Objects;

public class ForgeModTemplate implements Template {
   private static final String MOD_ANNOTATION = "Lnet/minecraftforge/fml/common/Mod;";

   @Override
   public boolean shouldSuggest(EntryList entries) {
      return entries.stream()
         .anyMatch(
            entry -> entry.isClass()
                  && Objects.nonNull(entry.getClassNode().visibleAnnotations)
                  && entry.getClassNode()
                     .visibleAnnotations
                     .stream()
                     .anyMatch(annotationNode -> Objects.equals(annotationNode.desc, "Lnet/minecraftforge/fml/common/Mod;"))
         );
   }

   @Override
   public boolean shouldInject(Entry entry) {
      return Objects.nonNull(entry.getClassNode().visibleAnnotations)
         && entry.getClassNode()
            .visibleAnnotations
            .stream()
            .anyMatch(annotationNode -> Objects.equals(annotationNode.desc, "Lnet/minecraftforge/fml/common/Mod;"));
   }

   @Override
   public String getName() {
      return "minecraft-forge-mod";
   }
}
