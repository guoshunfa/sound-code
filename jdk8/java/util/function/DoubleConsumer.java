package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface DoubleConsumer {
   void accept(double var1);

   default DoubleConsumer andThen(DoubleConsumer var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         this.accept(var2);
         var1.accept(var2);
      };
   }
}
