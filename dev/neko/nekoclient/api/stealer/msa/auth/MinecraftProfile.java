package dev.neko.nekoclient.api.stealer.msa.auth;

import com.eclipsesource.json.JsonObject;
import java.util.UUID;

public class MinecraftProfile {
   private final UUID id;
   private final String name;

   public MinecraftProfile(UUID id, String name) {
      this.id = id;
      this.name = name;
   }

   private static UUID convertProfileIdToUUID(String id) {
      return UUID.fromString(new StringBuilder(id).insert(8, "-").insert(13, "-").insert(18, "-").insert(23, "-").toString());
   }

   public static MinecraftProfile parseResponse(JsonObject json) {
      return new MinecraftProfile(convertProfileIdToUUID(json.get("id").asString()), json.get("name").asString());
   }

   public final String getName() {
      return this.name;
   }

   public final UUID getId() {
      return this.id;
   }
}
