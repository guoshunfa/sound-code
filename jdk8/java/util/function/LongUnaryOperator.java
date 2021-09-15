package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface LongUnaryOperator {
   long applyAsLong(long var1);

   default LongUnaryOperator compose(LongUnaryOperator var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         return this.applyAsLong(var1.applyAsLong(var2));
      };
   }

   default LongUnaryOperator andThen(LongUnaryOperator var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         return var1.applyAsLong(this.applyAsLong(var2));
      };
   }

   static LongUnaryOperator identity() {
      return (var0) -> {
         return var0;
      };
   }
}
