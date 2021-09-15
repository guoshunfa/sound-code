package java.util.stream;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;

public interface DoubleStream extends BaseStream<Double, DoubleStream> {
   DoubleStream filter(DoublePredicate var1);

   DoubleStream map(DoubleUnaryOperator var1);

   <U> Stream<U> mapToObj(DoubleFunction<? extends U> var1);

   IntStream mapToInt(DoubleToIntFunction var1);

   LongStream mapToLong(DoubleToLongFunction var1);

   DoubleStream flatMap(DoubleFunction<? extends DoubleStream> var1);

   DoubleStream distinct();

   DoubleStream sorted();

   DoubleStream peek(DoubleConsumer var1);

   DoubleStream limit(long var1);

   DoubleStream skip(long var1);

   void forEach(DoubleConsumer var1);

   void forEachOrdered(DoubleConsumer var1);

   double[] toArray();

   double reduce(double var1, DoubleBinaryOperator var3);

   OptionalDouble reduce(DoubleBinaryOperator var1);

   <R> R collect(Supplier<R> var1, ObjDoubleConsumer<R> var2, BiConsumer<R, R> var3);

   double sum();

   OptionalDouble min();

   OptionalDouble max();

   long count();

   OptionalDouble average();

   DoubleSummaryStatistics summaryStatistics();

   boolean anyMatch(DoublePredicate var1);

   boolean allMatch(DoublePredicate var1);

   boolean noneMatch(DoublePredicate var1);

   OptionalDouble findFirst();

   OptionalDouble findAny();

   Stream<Double> boxed();

   DoubleStream sequential();

   DoubleStream parallel();

   PrimitiveIterator.OfDouble iterator();

   Spliterator.OfDouble spliterator();

   static DoubleStream.Builder builder() {
      return new Streams.DoubleStreamBuilderImpl();
   }

   static DoubleStream empty() {
      return StreamSupport.doubleStream(Spliterators.emptyDoubleSpliterator(), false);
   }

   static DoubleStream of(double var0) {
      return StreamSupport.doubleStream(new Streams.DoubleStreamBuilderImpl(var0), false);
   }

   static DoubleStream of(double... var0) {
      return Arrays.stream(var0);
   }

   static DoubleStream iterate(final double var0, final DoubleUnaryOperator var2) {
      Objects.requireNonNull(var2);
      PrimitiveIterator.OfDouble var3 = new PrimitiveIterator.OfDouble() {
         double t = var0;

         public boolean hasNext() {
            return true;
         }

         public double nextDouble() {
            double var1 = this.t;
            this.t = var2.applyAsDouble(this.t);
            return var1;
         }
      };
      return StreamSupport.doubleStream(Spliterators.spliteratorUnknownSize((PrimitiveIterator.OfDouble)var3, 1296), false);
   }

   static DoubleStream generate(DoubleSupplier var0) {
      Objects.requireNonNull(var0);
      return StreamSupport.doubleStream(new StreamSpliterators.InfiniteSupplyingSpliterator.OfDouble(Long.MAX_VALUE, var0), false);
   }

   static DoubleStream concat(DoubleStream var0, DoubleStream var1) {
      Objects.requireNonNull(var0);
      Objects.requireNonNull(var1);
      Streams.ConcatSpliterator.OfDouble var2 = new Streams.ConcatSpliterator.OfDouble(var0.spliterator(), var1.spliterator());
      DoubleStream var3 = StreamSupport.doubleStream(var2, var0.isParallel() || var1.isParallel());
      return (DoubleStream)var3.onClose(Streams.composedClose(var0, var1));
   }

   public interface Builder extends DoubleConsumer {
      void accept(double var1);

      default DoubleStream.Builder add(double var1) {
         this.accept(var1);
         return this;
      }

      DoubleStream build();
   }
}
