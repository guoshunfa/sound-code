package java.util.stream;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

interface Sink<T> extends Consumer<T> {
   default void begin(long var1) {
   }

   default void end() {
   }

   default boolean cancellationRequested() {
      return false;
   }

   default void accept(int var1) {
      throw new IllegalStateException("called wrong accept method");
   }

   default void accept(long var1) {
      throw new IllegalStateException("called wrong accept method");
   }

   default void accept(double var1) {
      throw new IllegalStateException("called wrong accept method");
   }

   public abstract static class ChainedDouble<E_OUT> implements Sink.OfDouble {
      protected final Sink<? super E_OUT> downstream;

      public ChainedDouble(Sink<? super E_OUT> var1) {
         this.downstream = (Sink)Objects.requireNonNull(var1);
      }

      public void begin(long var1) {
         this.downstream.begin(var1);
      }

      public void end() {
         this.downstream.end();
      }

      public boolean cancellationRequested() {
         return this.downstream.cancellationRequested();
      }
   }

   public abstract static class ChainedLong<E_OUT> implements Sink.OfLong {
      protected final Sink<? super E_OUT> downstream;

      public ChainedLong(Sink<? super E_OUT> var1) {
         this.downstream = (Sink)Objects.requireNonNull(var1);
      }

      public void begin(long var1) {
         this.downstream.begin(var1);
      }

      public void end() {
         this.downstream.end();
      }

      public boolean cancellationRequested() {
         return this.downstream.cancellationRequested();
      }
   }

   public abstract static class ChainedInt<E_OUT> implements Sink.OfInt {
      protected final Sink<? super E_OUT> downstream;

      public ChainedInt(Sink<? super E_OUT> var1) {
         this.downstream = (Sink)Objects.requireNonNull(var1);
      }

      public void begin(long var1) {
         this.downstream.begin(var1);
      }

      public void end() {
         this.downstream.end();
      }

      public boolean cancellationRequested() {
         return this.downstream.cancellationRequested();
      }
   }

   public abstract static class ChainedReference<T, E_OUT> implements Sink<T> {
      protected final Sink<? super E_OUT> downstream;

      public ChainedReference(Sink<? super E_OUT> var1) {
         this.downstream = (Sink)Objects.requireNonNull(var1);
      }

      public void begin(long var1) {
         this.downstream.begin(var1);
      }

      public void end() {
         this.downstream.end();
      }

      public boolean cancellationRequested() {
         return this.downstream.cancellationRequested();
      }
   }

   public interface OfDouble extends Sink<Double>, DoubleConsumer {
      void accept(double var1);

      default void accept(Double var1) {
         if (Tripwire.ENABLED) {
            Tripwire.trip(this.getClass(), "{0} calling Sink.OfDouble.accept(Double)");
         }

         this.accept(var1);
      }
   }

   public interface OfLong extends Sink<Long>, LongConsumer {
      void accept(long var1);

      default void accept(Long var1) {
         if (Tripwire.ENABLED) {
            Tripwire.trip(this.getClass(), "{0} calling Sink.OfLong.accept(Long)");
         }

         this.accept(var1);
      }
   }

   public interface OfInt extends Sink<Integer>, IntConsumer {
      void accept(int var1);

      default void accept(Integer var1) {
         if (Tripwire.ENABLED) {
            Tripwire.trip(this.getClass(), "{0} calling Sink.OfInt.accept(Integer)");
         }

         this.accept(var1);
      }
   }
}
