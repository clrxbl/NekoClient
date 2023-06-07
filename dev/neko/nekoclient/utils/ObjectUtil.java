package dev.neko.nekoclient.utils;

import java.util.Objects;

public class ObjectUtil {
   public static <A, B> A unsafeCast(B b) {
      return (A)b;
   }

   public static <T> T requireNonNullElse(T value, T fallback) {
      return (T)(Objects.isNull(value) ? fallback : value);
   }

   public static <T> T requireNonNullAndExceptionElse(ObjectUtil.Catcher<T> value, T fallback) {
      try {
         T result = value.get();
         return (T)(Objects.isNull(result) ? fallback : result);
      } catch (Throwable var3) {
         return fallback;
      }
   }

   public static <T> T requireNonExceptionElse(ObjectUtil.Catcher<T> value, T fallback) {
      try {
         return value.get();
      } catch (Throwable var3) {
         return fallback;
      }
   }

   public interface Catcher<T> {
      T get() throws Throwable;
   }
}
