package dev.neko.nekoclient.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class Registry<T> {
   private final List<T> objects = new ArrayList<>();

   public void register(T... objects) {
      this.objects.addAll(Arrays.asList(objects));
   }

   public void unregister(T... objects) {
      this.objects.removeAll(Arrays.asList(objects));
   }

   public T getBy(Predicate<? super T> predicate) {
      return this.objects.stream().filter(predicate).findFirst().orElse((T)null);
   }

   public final List<T> getObjects() {
      return Collections.unmodifiableList(this.objects);
   }
}
