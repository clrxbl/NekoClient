package dev.neko.nekoinjector.asm;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EntryList extends ArrayList<Entry> {
   public Entry getBy(Predicate<? super Entry> predicate) {
      return this.stream().filter(predicate).findFirst().orElse(null);
   }

   public Entry getByPath(String path) {
      return this.getBy(
         entry -> Objects.equals(entry.getJarEntry().getName(), path) || Objects.equals(String.format("/%s", entry.getJarEntry().getName()), path)
      );
   }

   public List<Entry> classes() {
      return this.stream().filter(Entry::isClass).collect(Collectors.toList());
   }
}
