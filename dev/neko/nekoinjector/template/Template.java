package dev.neko.nekoinjector.template;

import dev.neko.nekoinjector.asm.Entry;
import dev.neko.nekoinjector.asm.EntryList;

public interface Template {
   boolean shouldSuggest(EntryList var1);

   boolean shouldInject(Entry var1);

   String getName();
}
