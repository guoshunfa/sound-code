package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface DoubleUnaryOperator {
   double applyAsDouble(double var1);

   default DoubleUnaryOperator compose(DoubleUnaryOperator var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         return this.applyAsDouble(var1.applyAsDouble(var2));
      };
   }

   default DoubleUnaryOperator andThen(DoubleUnaryOperator var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         return var1.applyAsDouble(this.applyAsDouble(var2));
      };
   }

   static DoubleUnaryOperator identity() {
      return (var0) -> {
         return var0;
      };
   }
}
