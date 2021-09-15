package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface IntPredicate {
   boolean test(int var1);

   default IntPredicate and(IntPredicate var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         return this.test(var2) && var1.test(var2);
      };
   }

   default IntPredicate negate() {
      return (var1) -> {
         return !this.test(var1);
      };
   }

   default IntPredicate or(IntPredicate var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         return this.test(var2) || var1.test(var2);
      };
   }
}
