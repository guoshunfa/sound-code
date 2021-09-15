package java.util.stream;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;

public interface IntStream extends BaseStream<Integer, IntStream> {
   IntStream filter(IntPredicate var1);

   IntStream map(IntUnaryOperator var1);

   <U> Stream<U> mapToObj(IntFunction<? extends U> var1);

   LongStream mapToLong(IntToLongFunction var1);

   DoubleStream mapToDouble(IntToDoubleFunction var1);

   IntStream flatMap(IntFunction<? extends IntStream> var1);

   IntStream distinct();

   IntStream sorted();

   IntStream peek(IntConsumer var1);

   IntStream limit(long var1);

   IntStream skip(long var1);

   void forEach(IntConsumer var1);

   void forEachOrdered(IntConsumer var1);

   int[] toArray();

   int reduce(int var1, IntBinaryOperator var2);

   OptionalInt reduce(IntBinaryOperator var1);

   <R> R collect(Supplier<R> var1, ObjIntConsumer<R> var2, BiConsumer<R, R> var3);

   int sum();

   OptionalInt min();

   OptionalInt max();

   long count();

   OptionalDouble average();

   IntSummaryStatistics summaryStatistics();

   boolean anyMatch(IntPredicate var1);

   boolean allMatch(IntPredicate var1);

   boolean noneMatch(IntPredicate var1);

   OptionalInt findFirst();

   OptionalInt findAny();

   LongStream asLongStream();

   DoubleStream asDoubleStream();

   Stream<Integer> boxed();

   IntStream sequential();

   IntStream parallel();

   PrimitiveIterator.OfInt iterator();

   Spliterator.OfInt spliterator();

   static IntStream.Builder builder() {
      return new Streams.IntStreamBuilderImpl();
   }

   static IntStream empty() {
      return StreamSupport.intStream(Spliterators.emptyIntSpliterator(), false);
   }

   static IntStream of(int var0) {
      return StreamSupport.intStream(new Streams.IntStreamBuilderImpl(var0), false);
   }

   static IntStream of(int... var0) {
      return Arrays.stream(var0);
   }

   static IntStream iterate(final int var0, final IntUnaryOperator var1) {
      Objects.requireNonNull(var1);
      PrimitiveIterator.OfInt var2 = new PrimitiveIterator.OfInt() {
         int t = var0;

         public boolean hasNext() {
            return true;
         }

         public int nextInt() {
            int var1x = this.t;
            this.t = var1.applyAsInt(this.t);
            return var1x;
         }
      };
      return StreamSupport.intStream(Spliterators.spliteratorUnknownSize((PrimitiveIterator.OfInt)var2, 1296), false);
   }

   static IntStream generate(IntSupplier var0) {
      Objects.requireNonNull(var0);
      return StreamSupport.intStream(new StreamSpliterators.InfiniteSupplyingSpliterator.OfInt(Long.MAX_VALUE, var0), false);
   }

   static IntStream range(int var0, int var1) {
      return var0 >= var1 ? empty() : StreamSupport.intStream(new Streams.RangeIntSpliterator(var0, var1, false), false);
   }

   static IntStream rangeClosed(int var0, int var1) {
      return var0 > var1 ? empty() : StreamSupport.intStream(new Streams.RangeIntSpliterator(var0, var1, true), false);
   }

   static IntStream concat(IntStream var0, IntStream var1) {
      Objects.requireNonNull(var0);
      Objects.requireNonNull(var1);
      Streams.ConcatSpliterator.OfInt var2 = new Streams.ConcatSpliterator.OfInt(var0.spliterator(), var1.spliterator());
      IntStream var3 = StreamSupport.intStream(var2, var0.isParallel() || var1.isParallel());
      return (IntStream)var3.onClose(Streams.composedClose(var0, var1));
   }

   public interface Builder extends IntConsumer {
      void accept(int var1);

      default IntStream.Builder add(int var1) {
         this.accept(var1);
         return this;
      }

      IntStream build();
   }
}
