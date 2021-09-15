package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface Function<T, R> {
   R apply(T var1);

   default <V> Function<V, R> compose(Function<? super V, ? extends T> var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         return this.apply(var1.apply(var2));
      };
   }

   default <V> Function<T, V> andThen(Function<? super R, ? extends V> var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         return var1.apply(this.apply(var2));
      };
   }

   static <T> Function<T, T> identity() {
      return (var0) -> {
         return var0;
      };
   }
}
