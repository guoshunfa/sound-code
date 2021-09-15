package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface BiPredicate<T, U> {
   boolean test(T var1, U var2);

   default BiPredicate<T, U> and(BiPredicate<? super T, ? super U> var1) {
      Objects.requireNonNull(var1);
      return (var2, var3) -> {
         return this.test(var2, var3) && var1.test(var2, var3);
      };
   }

   default BiPredicate<T, U> negate() {
      return (var1, var2) -> {
         return !this.test(var1, var2);
      };
   }

   default BiPredicate<T, U> or(BiPredicate<? super T, ? super U> var1) {
      Objects.requireNonNull(var1);
      return (var2, var3) -> {
         return this.test(var2, var3) || var1.test(var2, var3);
      };
   }
}
