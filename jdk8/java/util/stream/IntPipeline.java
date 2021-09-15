package java.util.stream;

import java.util.IntSummaryStatistics;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;

abstract class IntPipeline<E_IN> extends AbstractPipeline<E_IN, Integer, IntStream> implements IntStream {
   IntPipeline(Supplier<? extends Spliterator<Integer>> var1, int var2, boolean var3) {
      super(var1, var2, var3);
   }

   IntPipeline(Spliterator<Integer> var1, int var2, boolean var3) {
      super(var1, var2, var3);
   }

   IntPipeline(AbstractPipeline<?, E_IN, ?> var1, int var2) {
      super(var1, var2);
   }

   private static IntConsumer adapt(Sink<Integer> var0) {
      if (var0 instanceof IntConsumer) {
         return (IntConsumer)var0;
      } else {
         if (Tripwire.ENABLED) {
            Tripwire.trip(AbstractPipeline.class, "using IntStream.adapt(Sink<Integer> s)");
         }

         return var0::accept;
      }
   }

   private static Spliterator.OfInt adapt(Spliterator<Integer> var0) {
      if (var0 instanceof Spliterator.OfInt) {
         return (Spliterator.OfInt)var0;
      } else {
         if (Tripwire.ENABLED) {
            Tripwire.trip(AbstractPipeline.class, "using IntStream.adapt(Spliterator<Integer> s)");
         }

         throw new UnsupportedOperationException("IntStream.adapt(Spliterator<Integer> s)");
      }
   }

   final StreamShape getOutputShape() {
      return StreamShape.INT_VALUE;
   }

   final <P_IN> Node<Integer> evaluateToNode(PipelineHelper<Integer> var1, Spliterator<P_IN> var2, boolean var3, IntFunction<Integer[]> var4) {
      return Nodes.collectInt(var1, var2, var3);
   }

   final <P_IN> Spliterator<Integer> wrap(PipelineHelper<Integer> var1, Supplier<Spliterator<P_IN>> var2, boolean var3) {
      return new StreamSpliterators.IntWrappingSpliterator(var1, var2, var3);
   }

   final Spliterator.OfInt lazySpliterator(Supplier<? extends Spliterator<Integer>> var1) {
      return new StreamSpliterators.DelegatingSpliterator.OfInt(var1);
   }

   final void forEachWithCancel(Spliterator<Integer> var1, Sink<Integer> var2) {
      Spliterator.OfInt var3 = adapt(var1);
      IntConsumer var4 = adapt(var2);

      while(!var2.cancellationRequested() && var3.tryAdvance(var4)) {
      }

   }

   final Node.Builder<Integer> makeNodeBuilder(long var1, IntFunction<Integer[]> var3) {
      return Nodes.intBuilder(var1);
   }

   public final PrimitiveIterator.OfInt iterator() {
      return Spliterators.iterator(this.spliterator());
   }

   public final Spliterator.OfInt spliterator() {
      return adapt(super.spliterator());
   }

   public final LongStream asLongStream() {
      return new LongPipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
         Sink<Integer> opWrapSink(int var1, Sink<Long> var2) {
            return new Sink.ChainedInt<Long>(var2) {
               public void accept(int var1) {
                  this.downstream.accept((long)var1);
               }
            };
         }
      };
   }

   public final DoubleStream asDoubleStream() {
      return new DoublePipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
         Sink<Integer> opWrapSink(int var1, Sink<Double> var2) {
            return new Sink.ChainedInt<Double>(var2) {
               public void accept(int var1) {
                  this.downstream.accept((double)var1);
               }
            };
         }
      };
   }

   public final Stream<Integer> boxed() {
      return this.mapToObj(Integer::valueOf);
   }

   public final IntStream map(final IntUnaryOperator var1) {
      Objects.requireNonNull(var1);
      return new IntPipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
         Sink<Integer> opWrapSink(int var1x, Sink<Integer> var2) {
            return new Sink.ChainedInt<Integer>(var2) {
               public void accept(int var1x) {
                  this.downstream.accept(var1.applyAsInt(var1x));
               }
            };
         }
      };
   }

   public final <U> Stream<U> mapToObj(final IntFunction<? extends U> var1) {
      Objects.requireNonNull(var1);
      return new ReferencePipeline.StatelessOp<Integer, U>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
         Sink<Integer> opWrapSink(int var1x, Sink<U> var2) {
            return new Sink.ChainedInt<U>(var2) {
               public void accept(int var1x) {
                  this.downstream.accept(var1.apply(var1x));
               }
            };
         }
      };
   }

   public final LongStream mapToLong(final IntToLongFunction var1) {
      Objects.requireNonNull(var1);
      return new LongPipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
         Sink<Integer> opWrapSink(int var1x, Sink<Long> var2) {
            return new Sink.ChainedInt<Long>(var2) {
               public void accept(int var1x) {
                  this.downstream.accept(var1.applyAsLong(var1x));
               }
            };
         }
      };
   }

   public final DoubleStream mapToDouble(final IntToDoubleFunction var1) {
      Objects.requireNonNull(var1);
      return new DoublePipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
         Sink<Integer> opWrapSink(int var1x, Sink<Double> var2) {
            return new Sink.ChainedInt<Double>(var2) {
               public void accept(int var1x) {
                  this.downstream.accept(var1.applyAsDouble(var1x));
               }
            };
         }
      };
   }

   public final IntStream flatMap(final IntFunction<? extends IntStream> var1) {
      return new IntPipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
         Sink<Integer> opWrapSink(int var1x, Sink<Integer> var2) {
            return new Sink.ChainedInt<Integer>(var2) {
               public void begin(long var1x) {
                  this.downstream.begin(-1L);
               }

               public void accept(int var1x) {
                  IntStream var2 = (IntStream)var1.apply(var1x);
                  Throwable var3 = null;

                  try {
                     if (var2 != null) {
                        var2.sequential().forEach((var1xx) -> {
                           this.downstream.accept(var1xx);
                        });
                     }
                  } catch (Throwable var12) {
                     var3 = var12;
                     throw var12;
                  } finally {
                     if (var2 != null) {
                        if (var3 != null) {
                           try {
                              var2.close();
                           } catch (Throwable var11) {
                              var3.addSuppressed(var11);
                           }
                        } else {
                           var2.close();
                        }
                     }

                  }

               }
            };
         }
      };
   }

   public IntStream unordered() {
      return (IntStream)(!this.isOrdered() ? this : new IntPipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_ORDERED) {
         Sink<Integer> opWrapSink(int var1, Sink<Integer> var2) {
            return var2;
         }
      });
   }

   public final IntStream filter(final IntPredicate var1) {
      Objects.requireNonNull(var1);
      return new IntPipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE, StreamOpFlag.NOT_SIZED) {
         Sink<Integer> opWrapSink(int var1x, Sink<Integer> var2) {
            return new Sink.ChainedInt<Integer>(var2) {
               public void begin(long var1x) {
                  this.downstream.begin(-1L);
               }

               public void accept(int var1x) {
                  if (var1.test(var1x)) {
                     this.downstream.accept(var1x);
                  }

               }
            };
         }
      };
   }

   public final IntStream peek(final IntConsumer var1) {
      Objects.requireNonNull(var1);
      return new IntPipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE, 0) {
         Sink<Integer> opWrapSink(int var1x, Sink<Integer> var2) {
            return new Sink.ChainedInt<Integer>(var2) {
               public void accept(int var1x) {
                  var1.accept(var1x);
                  this.downstream.accept(var1x);
               }
            };
         }
      };
   }

   public final IntStream limit(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException(Long.toString(var1));
      } else {
         return SliceOps.makeInt(this, 0L, var1);
      }
   }

   public final IntStream skip(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException(Long.toString(var1));
      } else {
         return (IntStream)(var1 == 0L ? this : SliceOps.makeInt(this, var1, -1L));
      }
   }

   public final IntStream sorted() {
      return SortedOps.makeInt(this);
   }

   public final IntStream distinct() {
      return this.boxed().distinct().mapToInt((var0) -> {
         return var0;
      });
   }

   public void forEach(IntConsumer var1) {
      this.evaluate(ForEachOps.makeInt(var1, false));
   }

   public void forEachOrdered(IntConsumer var1) {
      this.evaluate(ForEachOps.makeInt(var1, true));
   }

   public final int sum() {
      return this.reduce(0, Integer::sum);
   }

   public final OptionalInt min() {
      return this.reduce(Math::min);
   }

   public final OptionalInt max() {
      return this.reduce(Math::max);
   }

   public final long count() {
      return this.mapToLong((var0) -> {
         return 1L;
      }).sum();
   }

   public final OptionalDouble average() {
      long[] var1 = (long[])this.collect(() -> {
         return new long[2];
      }, (var0, var1x) -> {
         int var10002 = var0[0]++;
         var0[1] += (long)var1x;
      }, (var0, var1x) -> {
         var0[0] += var1x[0];
         var0[1] += var1x[1];
      });
      return var1[0] > 0L ? OptionalDouble.of((double)var1[1] / (double)var1[0]) : OptionalDouble.empty();
   }

   public final IntSummaryStatistics summaryStatistics() {
      return (IntSummaryStatistics)this.collect(IntSummaryStatistics::new, IntSummaryStatistics::accept, IntSummaryStatistics::combine);
   }

   public final int reduce(int var1, IntBinaryOperator var2) {
      return (Integer)this.evaluate(ReduceOps.makeInt(var1, var2));
   }

   public final OptionalInt reduce(IntBinaryOperator var1) {
      return (OptionalInt)this.evaluate(ReduceOps.makeInt(var1));
   }

   public final <R> R collect(Supplier<R> var1, ObjIntConsumer<R> var2, BiConsumer<R, R> var3) {
      BinaryOperator var4 = (var1x, var2x) -> {
         var3.accept(var1x, var2x);
         return var1x;
      };
      return this.evaluate(ReduceOps.makeInt(var1, var2, var4));
   }

   public final boolean anyMatch(IntPredicate var1) {
      return (Boolean)this.evaluate(MatchOps.makeInt(var1, MatchOps.MatchKind.ANY));
   }

   public final boolean allMatch(IntPredicate var1) {
      return (Boolean)this.evaluate(MatchOps.makeInt(var1, MatchOps.MatchKind.ALL));
   }

   public final boolean noneMatch(IntPredicate var1) {
      return (Boolean)this.evaluate(MatchOps.makeInt(var1, MatchOps.MatchKind.NONE));
   }

   public final OptionalInt findFirst() {
      return (OptionalInt)this.evaluate(FindOps.makeInt(true));
   }

   public final OptionalInt findAny() {
      return (OptionalInt)this.evaluate(FindOps.makeInt(false));
   }

   public final int[] toArray() {
      return (int[])Nodes.flattenInt((Node.OfInt)this.evaluateToArrayNode((var0) -> {
         return new Integer[var0];
      })).asPrimitiveArray();
   }

   abstract static class StatefulOp<E_IN> extends IntPipeline<E_IN> {
      StatefulOp(AbstractPipeline<?, E_IN, ?> var1, StreamShape var2, int var3) {
         super(var1, var3);

         assert var1.getOutputShape() == var2;

      }

      final boolean opIsStateful() {
         return true;
      }

      abstract <P_IN> Node<Integer> opEvaluateParallel(PipelineHelper<Integer> var1, Spliterator<P_IN> var2, IntFunction<Integer[]> var3);
   }

   abstract static class StatelessOp<E_IN> extends IntPipeline<E_IN> {
      StatelessOp(AbstractPipeline<?, E_IN, ?> var1, StreamShape var2, int var3) {
         super(var1, var3);

         assert var1.getOutputShape() == var2;

      }

      final boolean opIsStateful() {
         return false;
      }
   }

   static class Head<E_IN> extends IntPipeline<E_IN> {
      Head(Supplier<? extends Spliterator<Integer>> var1, int var2, boolean var3) {
         super(var1, var2, var3);
      }

      Head(Spliterator<Integer> var1, int var2, boolean var3) {
         super(var1, var2, var3);
      }

      final boolean opIsStateful() {
         throw new UnsupportedOperationException();
      }

      final Sink<E_IN> opWrapSink(int var1, Sink<Integer> var2) {
         throw new UnsupportedOperationException();
      }

      public void forEach(IntConsumer var1) {
         if (!this.isParallel()) {
            IntPipeline.adapt(this.sourceStageSpliterator()).forEachRemaining(var1);
         } else {
            super.forEach(var1);
         }

      }

      public void forEachOrdered(IntConsumer var1) {
         if (!this.isParallel()) {
            IntPipeline.adapt(this.sourceStageSpliterator()).forEachRemaining(var1);
         } else {
            super.forEachOrdered(var1);
         }

      }
   }
}
