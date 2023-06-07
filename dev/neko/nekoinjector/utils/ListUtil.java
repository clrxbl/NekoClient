package dev.neko.nekoinjector.utils;

import java.util.Enumeration;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ListUtil {
   public static <T, R> void toList(List<R> list, Enumeration<T> enumeration, Predicate<T> predicate, Function<? super T, ? extends R> mapper) {
      while(enumeration.hasMoreElements()) {
         T element = enumeration.nextElement();
         if (predicate.test(element)) {
            list.add(mapper.apply(element));
         }
      }
   }
}
