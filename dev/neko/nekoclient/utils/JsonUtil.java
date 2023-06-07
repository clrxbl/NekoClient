package dev.neko.nekoclient.utils;

import com.eclipsesource.json.JsonValue;
import java.util.Objects;
import java.util.function.Predicate;

public class JsonUtil {
   public static boolean is(JsonValue value, Predicate<JsonValue> predicate) {
      return Objects.nonNull(value) && predicate.test(value);
   }

   public static boolean isNot(JsonValue value, Predicate<JsonValue> predicate) {
      return !is(value, predicate);
   }
}
