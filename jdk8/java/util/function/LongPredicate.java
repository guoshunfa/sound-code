package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface LongPredicate {
   boolean test(long var1);

   default LongPredicate and(LongPredicate var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         return this.test(var2) && var1.test(var2);
      };
   }

   default LongPredicate negate() {
      return (var1) -> {
         return !this.test(var1);
      };
   }

   default LongPredicate or(LongPredicate var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         return this.test(var2) || var1.test(var2);
      };
   }
}
