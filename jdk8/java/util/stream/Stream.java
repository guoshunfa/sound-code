package java.util.stream;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.function.UnaryOperator;

public interface Stream<T> extends BaseStream<T, Stream<T>> {
   Stream<T> filter(Predicate<? super T> var1);

   <R> Stream<R> map(Function<? super T, ? extends R> var1);

   IntStream mapToInt(ToIntFunction<? super T> var1);

   LongStream mapToLong(ToLongFunction<? super T> var1);

   DoubleStream mapToDouble(ToDoubleFunction<? super T> var1);

   <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> var1);

   IntStream flatMapToInt(Function<? super T, ? extends IntStream> var1);

   LongStream flatMapToLong(Function<? super T, ? extends LongStream> var1);

   DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> var1);

   Stream<T> distinct();

   Stream<T> sorted();

   Stream<T> sorted(Comparator<? super T> var1);

   Stream<T> peek(Consumer<? super T> var1);

   Stream<T> limit(long var1);

   Stream<T> skip(long var1);

   void forEach(Consumer<? super T> var1);

   void forEachOrdered(Consumer<? super T> var1);

   Object[] toArray();

   <A> A[] toArray(IntFunction<A[]> var1);

   T reduce(T var1, BinaryOperator<T> var2);

   Optional<T> reduce(BinaryOperator<T> var1);

   <U> U reduce(U var1, BiFunction<U, ? super T, U> var2, BinaryOperator<U> var3);

   <R> R collect(Supplier<R> var1, BiConsumer<R, ? super T> var2, BiConsumer<R, R> var3);

   <R, A> R collect(Collector<? super T, A, R> var1);

   Optional<T> min(Comparator<? super T> var1);

   Optional<T> max(Comparator<? super T> var1);

   long count();

   boolean anyMatch(Predicate<? super T> var1);

   boolean allMatch(Predicate<? super T> var1);

   boolean noneMatch(Predicate<? super T> var1);

   Optional<T> findFirst();

   Optional<T> findAny();

   static <T> Stream.Builder<T> builder() {
      return new Streams.StreamBuilderImpl();
   }

   static <T> Stream<T> empty() {
      return StreamSupport.stream(Spliterators.emptySpliterator(), false);
   }

   static <T> Stream<T> of(T var0) {
      return StreamSupport.stream(new Streams.StreamBuilderImpl(var0), false);
   }

   @SafeVarargs
   static <T> Stream<T> of(T... var0) {
      return Arrays.stream(var0);
   }

   static <T> Stream<T> iterate(final T var0, final UnaryOperator<T> var1) {
      Objects.requireNonNull(var1);
      Iterator var2 = new Iterator<T>() {
         T t;

         {
            this.t = Streams.NONE;
         }

         public boolean hasNext() {
            return true;
         }

         public T next() {
            return this.t = this.t == Streams.NONE ? var0 : var1.apply(this.t);
         }
      };
      return StreamSupport.stream(Spliterators.spliteratorUnknownSize((Iterator)var2, 1040), false);
   }

   static <T> Stream<T> generate(Supplier<T> var0) {
      Objects.requireNonNull(var0);
      return StreamSupport.stream(new StreamSpliterators.InfiniteSupplyingSpliterator.OfRef(Long.MAX_VALUE, var0), false);
   }

   static <T> Stream<T> concat(Stream<? extends T> var0, Stream<? extends T> var1) {
      Objects.requireNonNull(var0);
      Objects.requireNonNull(var1);
      Streams.ConcatSpliterator.OfRef var2 = new Streams.ConcatSpliterator.OfRef(var0.spliterator(), var1.spliterator());
      Stream var3 = StreamSupport.stream(var2, var0.isParallel() || var1.isParallel());
      return (Stream)var3.onClose(Streams.composedClose(var0, var1));
   }

   public interface Builder<T> extends Consumer<T> {
      void accept(T var1);

      default Stream.Builder<T> add(T var1) {
         this.accept(var1);
         return this;
      }

      Stream<T> build();
   }
}
