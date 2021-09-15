package java.util.stream;

import java.util.Arrays;
import java.util.LongSummaryStatistics;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongSupplier;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;

public interface LongStream extends BaseStream<Long, LongStream> {
   LongStream filter(LongPredicate var1);

   LongStream map(LongUnaryOperator var1);

   <U> Stream<U> mapToObj(LongFunction<? extends U> var1);

   IntStream mapToInt(LongToIntFunction var1);

   DoubleStream mapToDouble(LongToDoubleFunction var1);

   LongStream flatMap(LongFunction<? extends LongStream> var1);

   LongStream distinct();

   LongStream sorted();

   LongStream peek(LongConsumer var1);

   LongStream limit(long var1);

   LongStream skip(long var1);

   void forEach(LongConsumer var1);

   void forEachOrdered(LongConsumer var1);

   long[] toArray();

   long reduce(long var1, LongBinaryOperator var3);

   OptionalLong reduce(LongBinaryOperator var1);

   <R> R collect(Supplier<R> var1, ObjLongConsumer<R> var2, BiConsumer<R, R> var3);

   long sum();

   OptionalLong min();

   OptionalLong max();

   long count();

   OptionalDouble average();

   LongSummaryStatistics summaryStatistics();

   boolean anyMatch(LongPredicate var1);

   boolean allMatch(LongPredicate var1);

   boolean noneMatch(LongPredicate var1);

   OptionalLong findFirst();

   OptionalLong findAny();

   DoubleStream asDoubleStream();

   Stream<Long> boxed();

   LongStream sequential();

   LongStream parallel();

   PrimitiveIterator.OfLong iterator();

   Spliterator.OfLong spliterator();

   static LongStream.Builder builder() {
      return new Streams.LongStreamBuilderImpl();
   }

   static LongStream empty() {
      return StreamSupport.longStream(Spliterators.emptyLongSpliterator(), false);
   }

   static LongStream of(long var0) {
      return StreamSupport.longStream(new Streams.LongStreamBuilderImpl(var0), false);
   }

   static LongStream of(long... var0) {
      return Arrays.stream(var0);
   }

   static LongStream iterate(final long var0, final LongUnaryOperator var2) {
      Objects.requireNonNull(var2);
      PrimitiveIterator.OfLong var3 = new PrimitiveIterator.OfLong() {
         long t = var0;

         public boolean hasNext() {
            return true;
         }

         public long nextLong() {
            long var1 = this.t;
            this.t = var2.applyAsLong(this.t);
            return var1;
         }
      };
      return StreamSupport.longStream(Spliterators.spliteratorUnknownSize((PrimitiveIterator.OfLong)var3, 1296), false);
   }

   static LongStream generate(LongSupplier var0) {
      Objects.requireNonNull(var0);
      return StreamSupport.longStream(new StreamSpliterators.InfiniteSupplyingSpliterator.OfLong(Long.MAX_VALUE, var0), false);
   }

   static LongStream range(long var0, long var2) {
      if (var0 >= var2) {
         return empty();
      } else if (var2 - var0 < 0L) {
         long var4 = var0 + Long.divideUnsigned(var2 - var0, 2L) + 1L;
         return concat(range(var0, var4), range(var4, var2));
      } else {
         return StreamSupport.longStream(new Streams.RangeLongSpliterator(var0, var2, false), false);
      }
   }

   static LongStream rangeClosed(long var0, long var2) {
      if (var0 > var2) {
         return empty();
      } else if (var2 - var0 + 1L <= 0L) {
         long var4 = var0 + Long.divideUnsigned(var2 - var0, 2L) + 1L;
         return concat(range(var0, var4), rangeClosed(var4, var2));
      } else {
         return StreamSupport.longStream(new Streams.RangeLongSpliterator(var0, var2, true), false);
      }
   }

   static LongStream concat(LongStream var0, LongStream var1) {
      Objects.requireNonNull(var0);
      Objects.requireNonNull(var1);
      Streams.ConcatSpliterator.OfLong var2 = new Streams.ConcatSpliterator.OfLong(var0.spliterator(), var1.spliterator());
      LongStream var3 = StreamSupport.longStream(var2, var0.isParallel() || var1.isParallel());
      return (LongStream)var3.onClose(Streams.composedClose(var0, var1));
   }

   public interface Builder extends LongConsumer {
      void accept(long var1);

      default LongStream.Builder add(long var1) {
         this.accept(var1);
         return this;
      }

      LongStream build();
   }
}
