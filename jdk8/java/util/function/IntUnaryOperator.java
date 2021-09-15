package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface IntUnaryOperator {
   int applyAsInt(int var1);

   default IntUnaryOperator compose(IntUnaryOperator var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         return this.applyAsInt(var1.applyAsInt(var2));
      };
   }

   default IntUnaryOperator andThen(IntUnaryOperator var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         return var1.applyAsInt(this.applyAsInt(var2));
      };
   }

   static IntUnaryOperator identity() {
      return (var0) -> {
         return var0;
      };
   }
}
