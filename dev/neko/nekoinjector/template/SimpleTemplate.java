package dev.neko.nekoinjector.template;

import dev.neko.nekoinjector.asm.Entry;
import dev.neko.nekoinjector.asm.EntryList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public class SimpleTemplate implements Template {
   private final String name;
   private final List<String> suggest;
   private final List<String> injections;

   public SimpleTemplate(String name, List<String> suggest, List<String> injections) {
      this.name = name;
      this.suggest = suggest;
      this.injections = injections;
   }

   @Override
   public final boolean shouldSuggest(EntryList jarEntries) {
      return new HashSet(jarEntries.stream().map(Entry::getJarEntry).map(ZipEntry::getName).collect(Collectors.toList())).containsAll(this.suggest);
   }

   @Override
   public final String getName() {
      return this.name;
   }

   public final List<String> getSuggest() {
      return this.suggest;
   }

   public final List<String> getInjections() {
      return this.injections;
   }

   @Override
   public boolean shouldInject(Entry entry) {
      return this.injections.contains(entry.getJarEntry().getName())
         || this.injections.contains(String.format("/%s", this.injections.contains(entry.getJarEntry().getName())));
   }
}
