package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface LongConsumer {
   void accept(long var1);

   default LongConsumer andThen(LongConsumer var1) {
      Objects.requireNonNull(var1);
      return (var2) -> {
         this.accept(var2);
         var1.accept(var2);
      };
   }
}
