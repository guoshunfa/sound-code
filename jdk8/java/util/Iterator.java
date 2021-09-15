package java.util;

import java.util.function.Consumer;

public interface Iterator<E> {
   boolean hasNext();

   E next();

   default void remove() {
      throw new UnsupportedOperationException("remove");
   }

   default void forEachRemaining(Consumer<? super E> var1) {
      Objects.requireNonNull(var1);

      while(this.hasNext()) {
         var1.accept(this.next());
      }

   }
}
