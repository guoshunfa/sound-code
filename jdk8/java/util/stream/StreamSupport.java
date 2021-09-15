package java.util.stream;

import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Supplier;

public final class StreamSupport {
   private StreamSupport() {
   }

   public static <T> Stream<T> stream(Spliterator<T> var0, boolean var1) {
      Objects.requireNonNull(var0);
      return new ReferencePipeline.Head(var0, StreamOpFlag.fromCharacteristics(var0), var1);
   }

   public static <T> Stream<T> stream(Supplier<? extends Spliterator<T>> var0, int var1, boolean var2) {
      Objects.requireNonNull(var0);
      return new ReferencePipeline.Head(var0, StreamOpFlag.fromCharacteristics(var1), var2);
   }

   public static IntStream intStream(Spliterator.OfInt var0, boolean var1) {
      return new IntPipeline.Head(var0, StreamOpFlag.fromCharacteristics(var0), var1);
   }

   public static IntStream intStream(Supplier<? extends Spliterator.OfInt> var0, int var1, boolean var2) {
      return new IntPipeline.Head(var0, StreamOpFlag.fromCharacteristics(var1), var2);
   }

   public static LongStream longStream(Spliterator.OfLong var0, boolean var1) {
      return new LongPipeline.Head(var0, StreamOpFlag.fromCharacteristics(var0), var1);
   }

   public static LongStream longStream(Supplier<? extends Spliterator.OfLong> var0, int var1, boolean var2) {
      return new LongPipeline.Head(var0, StreamOpFlag.fromCharacteristics(var1), var2);
   }

   public static DoubleStream doubleStream(Spliterator.OfDouble var0, boolean var1) {
      return new DoublePipeline.Head(var0, StreamOpFlag.fromCharacteristics(var0), var1);
   }

   public static DoubleStream doubleStream(Supplier<? extends Spliterator.OfDouble> var0, int var1, boolean var2) {
      return new DoublePipeline.Head(var0, StreamOpFlag.fromCharacteristics(var1), var2);
   }
}
