package java.util.function;

import java.util.Comparator;
import java.util.Objects;

@FunctionalInterface
public interface BinaryOperator<T> extends BiFunction<T, T, T> {
   static <T> BinaryOperator<T> minBy(Comparator<? super T> var0) {
      Objects.requireNonNull(var0);
      return (var1, var2) -> {
         return var0.compare(var1, var2) <= 0 ? var1 : var2;
      };
   }

   static <T> BinaryOperator<T> maxBy(Comparator<? super T> var0) {
      Objects.requireNonNull(var0);
      return (var1, var2) -> {
         return var0.compare(var1, var2) >= 0 ? var1 : var2;
      };
   }
}
