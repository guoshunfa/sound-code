package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface DoublePredicate {
   boolean test(double var1);

   default DoublePredicate and(DoublePredicate var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         return this.test(var2) && var1.test(var2);
      };
   }

   default DoublePredicate negate() {
      return (var1) -> {
         return !this.test(var1);
      };
   }

   default DoublePredicate or(DoublePredicate var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         return this.test(var2) || var1.test(var2);
      };
   }
}
