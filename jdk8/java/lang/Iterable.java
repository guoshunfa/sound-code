package java.lang;

import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

public interface Iterable<T> {
   Iterator<T> iterator();

   default void forEach(Consumer<? super T> var1) {
      Objects.requireNonNull(var1);
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         var1.accept(var3);
      }

   }

   default Spliterator<T> spliterator() {
      return Spliterators.spliteratorUnknownSize((Iterator)this.iterator(), 0);
   }
}
