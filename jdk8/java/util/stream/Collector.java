package java.util.stream;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Collector<T, A, R> {
   Supplier<A> supplier();

   BiConsumer<A, T> accumulator();

   BinaryOperator<A> combiner();

   Function<A, R> finisher();

   Set<Collector.Characteristics> characteristics();

   static <T, R> Collector<T, R, R> of(Supplier<R> var0, BiConsumer<R, T> var1, BinaryOperator<R> var2, Collector.Characteristics... var3) {
      Objects.requireNonNull(var0);
      Objects.requireNonNull(var1);
      Objects.requireNonNull(var2);
      Objects.requireNonNull(var3);
      Set var4 = var3.length == 0 ? Collectors.CH_ID : Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH, (Enum[])var3));
      return new Collectors.CollectorImpl(var0, var1, var2, var4);
   }

   static <T, A, R> Collector<T, A, R> of(Supplier<A> var0, BiConsumer<A, T> var1, BinaryOperator<A> var2, Function<A, R> var3, Collector.Characteristics... var4) {
      Objects.requireNonNull(var0);
      Objects.requireNonNull(var1);
      Objects.requireNonNull(var2);
      Objects.requireNonNull(var3);
      Objects.requireNonNull(var4);
      Set var5 = Collectors.CH_NOID;
      if (var4.length > 0) {
         EnumSet var6 = EnumSet.noneOf(Collector.Characteristics.class);
         Collections.addAll(var6, var4);
         var5 = Collections.unmodifiableSet(var6);
      }

      return new Collectors.CollectorImpl(var0, var1, var2, var3, var5);
   }

   public static enum Characteristics {
      CONCURRENT,
      UNORDERED,
      IDENTITY_FINISH;
   }
}
