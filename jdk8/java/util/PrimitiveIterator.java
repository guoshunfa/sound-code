package java.util;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

public interface PrimitiveIterator<T, T_CONS> extends Iterator<T> {
   void forEachRemaining(T_CONS var1);

   public interface OfDouble extends PrimitiveIterator<Double, DoubleConsumer> {
      double nextDouble();

      default void forEachRemaining(DoubleConsumer var1) {
         Objects.requireNonNull(var1);

         while(this.hasNext()) {
            var1.accept(this.nextDouble());
         }

      }

      default Double next() {
         if (Tripwire.ENABLED) {
            Tripwire.trip(this.getClass(), "{0} calling PrimitiveIterator.OfDouble.nextLong()");
         }

         return this.nextDouble();
      }

      default void forEachRemaining(Consumer<? super Double> var1) {
         if (var1 instanceof DoubleConsumer) {
            this.forEachRemaining((DoubleConsumer)var1);
         } else {
            Objects.requireNonNull(var1);
            if (Tripwire.ENABLED) {
               Tripwire.trip(this.getClass(), "{0} calling PrimitiveIterator.OfDouble.forEachRemainingDouble(action::accept)");
            }

            this.forEachRemaining(var1::accept);
         }

      }
   }

   public interface OfLong extends PrimitiveIterator<Long, LongConsumer> {
      long nextLong();

      default void forEachRemaining(LongConsumer var1) {
         Objects.requireNonNull(var1);

         while(this.hasNext()) {
            var1.accept(this.nextLong());
         }

      }

      default Long next() {
         if (Tripwire.ENABLED) {
            Tripwire.trip(this.getClass(), "{0} calling PrimitiveIterator.OfLong.nextLong()");
         }

         return this.nextLong();
      }

      default void forEachRemaining(Consumer<? super Long> var1) {
         if (var1 instanceof LongConsumer) {
            this.forEachRemaining((LongConsumer)var1);
         } else {
            Objects.requireNonNull(var1);
            if (Tripwire.ENABLED) {
               Tripwire.trip(this.getClass(), "{0} calling PrimitiveIterator.OfLong.forEachRemainingLong(action::accept)");
            }

            this.forEachRemaining(var1::accept);
         }

      }
   }

   public interface OfInt extends PrimitiveIterator<Integer, IntConsumer> {
      int nextInt();

      default void forEachRemaining(IntConsumer var1) {
         Objects.requireNonNull(var1);

         while(this.hasNext()) {
            var1.accept(this.nextInt());
         }

      }

      default Integer next() {
         if (Tripwire.ENABLED) {
            Tripwire.trip(this.getClass(), "{0} calling PrimitiveIterator.OfInt.nextInt()");
         }

         return this.nextInt();
      }

      default void forEachRemaining(Consumer<? super Integer> var1) {
         if (var1 instanceof IntConsumer) {
            this.forEachRemaining((IntConsumer)var1);
         } else {
            Objects.requireNonNull(var1);
            if (Tripwire.ENABLED) {
               Tripwire.trip(this.getClass(), "{0} calling PrimitiveIterator.OfInt.forEachRemainingInt(action::accept)");
            }

            this.forEachRemaining(var1::accept);
         }

      }
   }
}
