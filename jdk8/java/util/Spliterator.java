package java.util;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

public interface Spliterator<T> {
   int ORDERED = 16;
   int DISTINCT = 1;
   int SORTED = 4;
   int SIZED = 64;
   int NONNULL = 256;
   int IMMUTABLE = 1024;
   int CONCURRENT = 4096;
   int SUBSIZED = 16384;

   boolean tryAdvance(Consumer<? super T> var1);

   default void forEachRemaining(Consumer<? super T> var1) {
      while(this.tryAdvance(var1)) {
      }

   }

   Spliterator<T> trySplit();

   long estimateSize();

   default long getExactSizeIfKnown() {
      return (this.characteristics() & 64) == 0 ? -1L : this.estimateSize();
   }

   int characteristics();

   default boolean hasCharacteristics(int var1) {
      return (this.characteristics() & var1) == var1;
   }

   default Comparator<? super T> getComparator() {
      throw new IllegalStateException();
   }

   public interface OfDouble extends Spliterator.OfPrimitive<Double, DoubleConsumer, Spliterator.OfDouble> {
      Spliterator.OfDouble trySplit();

      boolean tryAdvance(DoubleConsumer var1);

      default void forEachRemaining(DoubleConsumer var1) {
         while(this.tryAdvance(var1)) {
         }

      }

      default boolean tryAdvance(Consumer<? super Double> var1) {
         if (var1 instanceof DoubleConsumer) {
            return this.tryAdvance((DoubleConsumer)var1);
         } else {
            if (Tripwire.ENABLED) {
               Tripwire.trip(this.getClass(), "{0} calling Spliterator.OfDouble.tryAdvance((DoubleConsumer) action::accept)");
            }

            var1.getClass();
            return this.tryAdvance(var1::accept);
         }
      }

      default void forEachRemaining(Consumer<? super Double> var1) {
         if (var1 instanceof DoubleConsumer) {
            this.forEachRemaining((DoubleConsumer)var1);
         } else {
            if (Tripwire.ENABLED) {
               Tripwire.trip(this.getClass(), "{0} calling Spliterator.OfDouble.forEachRemaining((DoubleConsumer) action::accept)");
            }

            this.forEachRemaining(var1::accept);
         }

      }
   }

   public interface OfLong extends Spliterator.OfPrimitive<Long, LongConsumer, Spliterator.OfLong> {
      Spliterator.OfLong trySplit();

      boolean tryAdvance(LongConsumer var1);

      default void forEachRemaining(LongConsumer var1) {
         while(this.tryAdvance(var1)) {
         }

      }

      default boolean tryAdvance(Consumer<? super Long> var1) {
         if (var1 instanceof LongConsumer) {
            return this.tryAdvance((LongConsumer)var1);
         } else {
            if (Tripwire.ENABLED) {
               Tripwire.trip(this.getClass(), "{0} calling Spliterator.OfLong.tryAdvance((LongConsumer) action::accept)");
            }

            var1.getClass();
            return this.tryAdvance(var1::accept);
         }
      }

      default void forEachRemaining(Consumer<? super Long> var1) {
         if (var1 instanceof LongConsumer) {
            this.forEachRemaining((LongConsumer)var1);
         } else {
            if (Tripwire.ENABLED) {
               Tripwire.trip(this.getClass(), "{0} calling Spliterator.OfLong.forEachRemaining((LongConsumer) action::accept)");
            }

            this.forEachRemaining(var1::accept);
         }

      }
   }

   public interface OfInt extends Spliterator.OfPrimitive<Integer, IntConsumer, Spliterator.OfInt> {
      Spliterator.OfInt trySplit();

      boolean tryAdvance(IntConsumer var1);

      default void forEachRemaining(IntConsumer var1) {
         while(this.tryAdvance(var1)) {
         }

      }

      default boolean tryAdvance(Consumer<? super Integer> var1) {
         if (var1 instanceof IntConsumer) {
            return this.tryAdvance((IntConsumer)var1);
         } else {
            if (Tripwire.ENABLED) {
               Tripwire.trip(this.getClass(), "{0} calling Spliterator.OfInt.tryAdvance((IntConsumer) action::accept)");
            }

            var1.getClass();
            return this.tryAdvance(var1::accept);
         }
      }

      default void forEachRemaining(Consumer<? super Integer> var1) {
         if (var1 instanceof IntConsumer) {
            this.forEachRemaining((IntConsumer)var1);
         } else {
            if (Tripwire.ENABLED) {
               Tripwire.trip(this.getClass(), "{0} calling Spliterator.OfInt.forEachRemaining((IntConsumer) action::accept)");
            }

            this.forEachRemaining(var1::accept);
         }

      }
   }

   public interface OfPrimitive<T, T_CONS, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>> extends Spliterator<T> {
      T_SPLITR trySplit();

      boolean tryAdvance(T_CONS var1);

      default void forEachRemaining(T_CONS var1) {
         while(this.tryAdvance(var1)) {
         }

      }
   }
}
