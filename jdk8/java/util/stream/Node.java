package java.util.stream;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.LongConsumer;

interface Node<T> {
   Spliterator<T> spliterator();

   void forEach(Consumer<? super T> var1);

   default int getChildCount() {
      return 0;
   }

   default Node<T> getChild(int var1) {
      throw new IndexOutOfBoundsException();
   }

   default Node<T> truncate(long var1, long var3, IntFunction<T[]> var5) {
      if (var1 == 0L && var3 == this.count()) {
         return this;
      } else {
         Spliterator var6 = this.spliterator();
         long var7 = var3 - var1;
         Node.Builder var9 = Nodes.builder(var7, var5);
         var9.begin(var7);

         int var10;
         for(var10 = 0; (long)var10 < var1 && var6.tryAdvance((var0) -> {
         }); ++var10) {
         }

         for(var10 = 0; (long)var10 < var7 && var6.tryAdvance(var9); ++var10) {
         }

         var9.end();
         return var9.build();
      }
   }

   T[] asArray(IntFunction<T[]> var1);

   void copyInto(T[] var1, int var2);

   default StreamShape getShape() {
      return StreamShape.REFERENCE;
   }

   long count();

   public interface OfDouble extends Node.OfPrimitive<Double, DoubleConsumer, double[], Spliterator.OfDouble, Node.OfDouble> {
      default void forEach(Consumer<? super Double> var1) {
         if (var1 instanceof DoubleConsumer) {
            this.forEach((DoubleConsumer)var1);
         } else {
            if (Tripwire.ENABLED) {
               Tripwire.trip(this.getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)");
            }

            ((Spliterator.OfDouble)this.spliterator()).forEachRemaining(var1);
         }

      }

      default void copyInto(Double[] var1, int var2) {
         if (Tripwire.ENABLED) {
            Tripwire.trip(this.getClass(), "{0} calling Node.OfDouble.copyInto(Double[], int)");
         }

         double[] var3 = (double[])this.asPrimitiveArray();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            var1[var2 + var4] = var3[var4];
         }

      }

      default Node.OfDouble truncate(long var1, long var3, IntFunction<Double[]> var5) {
         if (var1 == 0L && var3 == this.count()) {
            return this;
         } else {
            long var6 = var3 - var1;
            Spliterator.OfDouble var8 = (Spliterator.OfDouble)this.spliterator();
            Node.Builder.OfDouble var9 = Nodes.doubleBuilder(var6);
            var9.begin(var6);

            int var10;
            for(var10 = 0; (long)var10 < var1 && var8.tryAdvance((var0) -> {
            }); ++var10) {
            }

            for(var10 = 0; (long)var10 < var6 && var8.tryAdvance((DoubleConsumer)var9); ++var10) {
            }

            var9.end();
            return var9.build();
         }
      }

      default double[] newArray(int var1) {
         return new double[var1];
      }

      default StreamShape getShape() {
         return StreamShape.DOUBLE_VALUE;
      }
   }

   public interface OfLong extends Node.OfPrimitive<Long, LongConsumer, long[], Spliterator.OfLong, Node.OfLong> {
      default void forEach(Consumer<? super Long> var1) {
         if (var1 instanceof LongConsumer) {
            this.forEach((LongConsumer)var1);
         } else {
            if (Tripwire.ENABLED) {
               Tripwire.trip(this.getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)");
            }

            ((Spliterator.OfLong)this.spliterator()).forEachRemaining(var1);
         }

      }

      default void copyInto(Long[] var1, int var2) {
         if (Tripwire.ENABLED) {
            Tripwire.trip(this.getClass(), "{0} calling Node.OfInt.copyInto(Long[], int)");
         }

         long[] var3 = (long[])this.asPrimitiveArray();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            var1[var2 + var4] = var3[var4];
         }

      }

      default Node.OfLong truncate(long var1, long var3, IntFunction<Long[]> var5) {
         if (var1 == 0L && var3 == this.count()) {
            return this;
         } else {
            long var6 = var3 - var1;
            Spliterator.OfLong var8 = (Spliterator.OfLong)this.spliterator();
            Node.Builder.OfLong var9 = Nodes.longBuilder(var6);
            var9.begin(var6);

            int var10;
            for(var10 = 0; (long)var10 < var1 && var8.tryAdvance((var0) -> {
            }); ++var10) {
            }

            for(var10 = 0; (long)var10 < var6 && var8.tryAdvance((LongConsumer)var9); ++var10) {
            }

            var9.end();
            return var9.build();
         }
      }

      default long[] newArray(int var1) {
         return new long[var1];
      }

      default StreamShape getShape() {
         return StreamShape.LONG_VALUE;
      }
   }

   public interface OfInt extends Node.OfPrimitive<Integer, IntConsumer, int[], Spliterator.OfInt, Node.OfInt> {
      default void forEach(Consumer<? super Integer> var1) {
         if (var1 instanceof IntConsumer) {
            this.forEach((IntConsumer)var1);
         } else {
            if (Tripwire.ENABLED) {
               Tripwire.trip(this.getClass(), "{0} calling Node.OfInt.forEachRemaining(Consumer)");
            }

            ((Spliterator.OfInt)this.spliterator()).forEachRemaining(var1);
         }

      }

      default void copyInto(Integer[] var1, int var2) {
         if (Tripwire.ENABLED) {
            Tripwire.trip(this.getClass(), "{0} calling Node.OfInt.copyInto(Integer[], int)");
         }

         int[] var3 = (int[])this.asPrimitiveArray();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            var1[var2 + var4] = var3[var4];
         }

      }

      default Node.OfInt truncate(long var1, long var3, IntFunction<Integer[]> var5) {
         if (var1 == 0L && var3 == this.count()) {
            return this;
         } else {
            long var6 = var3 - var1;
            Spliterator.OfInt var8 = (Spliterator.OfInt)this.spliterator();
            Node.Builder.OfInt var9 = Nodes.intBuilder(var6);
            var9.begin(var6);

            int var10;
            for(var10 = 0; (long)var10 < var1 && var8.tryAdvance((var0) -> {
            }); ++var10) {
            }

            for(var10 = 0; (long)var10 < var6 && var8.tryAdvance((IntConsumer)var9); ++var10) {
            }

            var9.end();
            return var9.build();
         }
      }

      default int[] newArray(int var1) {
         return new int[var1];
      }

      default StreamShape getShape() {
         return StreamShape.INT_VALUE;
      }
   }

   public interface OfPrimitive<T, T_CONS, T_ARR, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>, T_NODE extends Node.OfPrimitive<T, T_CONS, T_ARR, T_SPLITR, T_NODE>> extends Node<T> {
      T_SPLITR spliterator();

      void forEach(T_CONS var1);

      default T_NODE getChild(int var1) {
         throw new IndexOutOfBoundsException();
      }

      T_NODE truncate(long var1, long var3, IntFunction<T[]> var5);

      default T[] asArray(IntFunction<T[]> var1) {
         if (Tripwire.ENABLED) {
            Tripwire.trip(this.getClass(), "{0} calling Node.OfPrimitive.asArray");
         }

         long var2 = this.count();
         if (var2 >= 2147483639L) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
         } else {
            Object[] var4 = (Object[])var1.apply((int)this.count());
            this.copyInto(var4, 0);
            return var4;
         }
      }

      T_ARR asPrimitiveArray();

      T_ARR newArray(int var1);

      void copyInto(T_ARR var1, int var2);
   }

   public interface Builder<T> extends Sink<T> {
      Node<T> build();

      public interface OfDouble extends Node.Builder<Double>, Sink.OfDouble {
         Node.OfDouble build();
      }

      public interface OfLong extends Node.Builder<Long>, Sink.OfLong {
         Node.OfLong build();
      }

      public interface OfInt extends Node.Builder<Integer>, Sink.OfInt {
         Node.OfInt build();
      }
   }
}
