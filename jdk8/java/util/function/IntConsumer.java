package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface IntConsumer {
   void accept(int var1);

   default IntConsumer andThen(IntConsumer var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         this.accept(var2);
         var1.accept(var2);
      };
   }
}
