package java.util.stream;

import java.util.LongSummaryStatistics;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.IntFunction;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;

abstract class LongPipeline<E_IN> extends AbstractPipeline<E_IN, Long, LongStream> implements LongStream {
   LongPipeline(Supplier<? extends Spliterator<Long>> var1, int var2, boolean var3) {
      super(var1, var2, var3);
   }

   LongPipeline(Spliterator<Long> var1, int var2, boolean var3) {
      super(var1, var2, var3);
   }

   LongPipeline(AbstractPipeline<?, E_IN, ?> var1, int var2) {
      super(var1, var2);
   }

   private static LongConsumer adapt(Sink<Long> var0) {
      if (var0 instanceof LongConsumer) {
         return (LongConsumer)var0;
      } else {
         if (Tripwire.ENABLED) {
            Tripwire.trip(AbstractPipeline.class, "using LongStream.adapt(Sink<Long> s)");
         }

         return var0::accept;
      }
   }

   private static Spliterator.OfLong adapt(Spliterator<Long> var0) {
      if (var0 instanceof Spliterator.OfLong) {
         return (Spliterator.OfLong)var0;
      } else {
         if (Tripwire.ENABLED) {
            Tripwire.trip(AbstractPipeline.class, "using LongStream.adapt(Spliterator<Long> s)");
         }

         throw new UnsupportedOperationException("LongStream.adapt(Spliterator<Long> s)");
      }
   }

   final StreamShape getOutputShape() {
      return StreamShape.LONG_VALUE;
   }

   final <P_IN> Node<Long> evaluateToNode(PipelineHelper<Long> var1, Spliterator<P_IN> var2, boolean var3, IntFunction<Long[]> var4) {
      return Nodes.collectLong(var1, var2, var3);
   }

   final <P_IN> Spliterator<Long> wrap(PipelineHelper<Long> var1, Supplier<Spliterator<P_IN>> var2, boolean var3) {
      return new StreamSpliterators.LongWrappingSpliterator(var1, var2, var3);
   }

   final Spliterator.OfLong lazySpliterator(Supplier<? extends Spliterator<Long>> var1) {
      return new StreamSpliterators.DelegatingSpliterator.OfLong(var1);
   }

   final void forEachWithCancel(Spliterator<Long> var1, Sink<Long> var2) {
      Spliterator.OfLong var3 = adapt(var1);
      LongConsumer var4 = adapt(var2);

      while(!var2.cancellationRequested() && var3.tryAdvance(var4)) {
      }

   }

   final Node.Builder<Long> makeNodeBuilder(long var1, IntFunction<Long[]> var3) {
      return Nodes.longBuilder(var1);
   }

   public final PrimitiveIterator.OfLong iterator() {
      return Spliterators.iterator(this.spliterator());
   }

   public final Spliterator.OfLong spliterator() {
      return adapt(super.spliterator());
   }

   public final DoubleStream asDoubleStream() {
      return new DoublePipeline.StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
         Sink<Long> opWrapSink(int var1, Sink<Double> var2) {
            return new Sink.ChainedLong<Double>(var2) {
               public void accept(long var1) {
                  this.downstream.accept((double)var1);
               }
            };
         }
      };
   }

   public final Stream<Long> boxed() {
      return this.mapToObj(Long::valueOf);
   }

   public final LongStream map(final LongUnaryOperator var1) {
      Objects.requireNonNull(var1);
      return new LongPipeline.StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
         Sink<Long> opWrapSink(int var1x, Sink<Long> var2) {
            return new Sink.ChainedLong<Long>(var2) {
               public void accept(long var1x) {
                  this.downstream.accept(var1.applyAsLong(var1x));
               }
            };
         }
      };
   }

   public final <U> Stream<U> mapToObj(final LongFunction<? extends U> var1) {
      Objects.requireNonNull(var1);
      return new ReferencePipeline.StatelessOp<Long, U>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
         Sink<Long> opWrapSink(int var1x, Sink<U> var2) {
            return new Sink.ChainedLong<U>(var2) {
               public void accept(long var1x) {
                  this.downstream.accept(var1.apply(var1x));
               }
            };
         }
      };
   }

   public final IntStream mapToInt(final LongToIntFunction var1) {
      Objects.requireNonNull(var1);
      return new IntPipeline.StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
         Sink<Long> opWrapSink(int var1x, Sink<Integer> var2) {
            return new Sink.ChainedLong<Integer>(var2) {
               public void accept(long var1x) {
                  this.downstream.accept(var1.applyAsInt(var1x));
               }
            };
         }
      };
   }

   public final DoubleStream mapToDouble(final LongToDoubleFunction var1) {
      Objects.requireNonNull(var1);
      return new DoublePipeline.StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
         Sink<Long> opWrapSink(int var1x, Sink<Double> var2) {
            return new Sink.ChainedLong<Double>(var2) {
               public void accept(long var1x) {
                  this.downstream.accept(var1.applyAsDouble(var1x));
               }
            };
         }
      };
   }

   public final LongStream flatMap(final LongFunction<? extends LongStream> var1) {
      return new LongPipeline.StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
         Sink<Long> opWrapSink(int var1x, Sink<Long> var2) {
            return new Sink.ChainedLong<Long>(var2) {
               public void begin(long var1x) {
                  this.downstream.begin(-1L);
               }

               public void accept(long var1x) {
                  LongStream var3 = (LongStream)var1.apply(var1x);
                  Throwable var4 = null;

                  try {
                     if (var3 != null) {
                        var3.sequential().forEach((var1xx) -> {
                           this.downstream.accept(var1xx);
                        });
                     }
                  } catch (Throwable var13) {
                     var4 = var13;
                     throw var13;
                  } finally {
                     if (var3 != null) {
                        if (var4 != null) {
                           try {
                              var3.close();
                           } catch (Throwable var12) {
                              var4.addSuppressed(var12);
                           }
                        } else {
                           var3.close();
                        }
                     }

                  }

               }
            };
         }
      };
   }

   public LongStream unordered() {
      return (LongStream)(!this.isOrdered() ? this : new LongPipeline.StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_ORDERED) {
         Sink<Long> opWrapSink(int var1, Sink<Long> var2) {
            return var2;
         }
      });
   }

   public final LongStream filter(final LongPredicate var1) {
      Objects.requireNonNull(var1);
      return new LongPipeline.StatelessOp<Long>(this, StreamShape.LONG_VALUE, StreamOpFlag.NOT_SIZED) {
         Sink<Long> opWrapSink(int var1x, Sink<Long> var2) {
            return new Sink.ChainedLong<Long>(var2) {
               public void begin(long var1x) {
                  this.downstream.begin(-1L);
               }

               public void accept(long var1x) {
                  if (var1.test(var1x)) {
                     this.downstream.accept(var1x);
                  }

               }
            };
         }
      };
   }

   public final LongStream peek(final LongConsumer var1) {
      Objects.requireNonNull(var1);
      return new LongPipeline.StatelessOp<Long>(this, StreamShape.LONG_VALUE, 0) {
         Sink<Long> opWrapSink(int var1x, Sink<Long> var2) {
            return new Sink.ChainedLong<Long>(var2) {
               public void accept(long var1x) {
                  var1.accept(var1x);
                  this.downstream.accept(var1x);
               }
            };
         }
      };
   }

   public final LongStream limit(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException(Long.toString(var1));
      } else {
         return SliceOps.makeLong(this, 0L, var1);
      }
   }

   public final LongStream skip(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException(Long.toString(var1));
      } else {
         return (LongStream)(var1 == 0L ? this : SliceOps.makeLong(this, var1, -1L));
      }
   }

   public final LongStream sorted() {
      return SortedOps.makeLong(this);
   }

   public final LongStream distinct() {
      return this.boxed().distinct().mapToLong((var0) -> {
         return var0;
      });
   }

   public void forEach(LongConsumer var1) {
      this.evaluate(ForEachOps.makeLong(var1, false));
   }

   public void forEachOrdered(LongConsumer var1) {
      this.evaluate(ForEachOps.makeLong(var1, true));
   }

   public final long sum() {
      return this.reduce(0L, Long::sum);
   }

   public final OptionalLong min() {
      return this.reduce(Math::min);
   }

   public final OptionalLong max() {
      return this.reduce(Math::max);
   }

   public final OptionalDouble average() {
      long[] var1 = (long[])this.collect(() -> {
         return new long[2];
      }, (var0, var1x) -> {
         int var10002 = var0[0]++;
         var0[1] += var1x;
      }, (var0, var1x) -> {
         var0[0] += var1x[0];
         var0[1] += var1x[1];
      });
      return var1[0] > 0L ? OptionalDouble.of((double)var1[1] / (double)var1[0]) : OptionalDouble.empty();
   }

   public final long count() {
      return this.map((var0) -> {
         return 1L;
      }).sum();
   }

   public final LongSummaryStatistics summaryStatistics() {
      return (LongSummaryStatistics)this.collect(LongSummaryStatistics::new, LongSummaryStatistics::accept, LongSummaryStatistics::combine);
   }

   public final long reduce(long var1, LongBinaryOperator var3) {
      return (Long)this.evaluate(ReduceOps.makeLong(var1, var3));
   }

   public final OptionalLong reduce(LongBinaryOperator var1) {
      return (OptionalLong)this.evaluate(ReduceOps.makeLong(var1));
   }

   public final <R> R collect(Supplier<R> var1, ObjLongConsumer<R> var2, BiConsumer<R, R> var3) {
      BinaryOperator var4 = (var1x, var2x) -> {
         var3.accept(var1x, var2x);
         return var1x;
      };
      return this.evaluate(ReduceOps.makeLong(var1, var2, var4));
   }

   public final boolean anyMatch(LongPredicate var1) {
      return (Boolean)this.evaluate(MatchOps.makeLong(var1, MatchOps.MatchKind.ANY));
   }

   public final boolean allMatch(LongPredicate var1) {
      return (Boolean)this.evaluate(MatchOps.makeLong(var1, MatchOps.MatchKind.ALL));
   }

   public final boolean noneMatch(LongPredicate var1) {
      return (Boolean)this.evaluate(MatchOps.makeLong(var1, MatchOps.MatchKind.NONE));
   }

   public final OptionalLong findFirst() {
      return (OptionalLong)this.evaluate(FindOps.makeLong(true));
   }

   public final OptionalLong findAny() {
      return (OptionalLong)this.evaluate(FindOps.makeLong(false));
   }

   public final long[] toArray() {
      return (long[])Nodes.flattenLong((Node.OfLong)this.evaluateToArrayNode((var0) -> {
         return new Long[var0];
      })).asPrimitiveArray();
   }

   abstract static class StatefulOp<E_IN> extends LongPipeline<E_IN> {
      StatefulOp(AbstractPipeline<?, E_IN, ?> var1, StreamShape var2, int var3) {
         super(var1, var3);

         assert var1.getOutputShape() == var2;

      }

      final boolean opIsStateful() {
         return true;
      }

      abstract <P_IN> Node<Long> opEvaluateParallel(PipelineHelper<Long> var1, Spliterator<P_IN> var2, IntFunction<Long[]> var3);
   }

   abstract static class StatelessOp<E_IN> extends LongPipeline<E_IN> {
      StatelessOp(AbstractPipeline<?, E_IN, ?> var1, StreamShape var2, int var3) {
         super(var1, var3);

         assert var1.getOutputShape() == var2;

      }

      final boolean opIsStateful() {
         return false;
      }
   }

   static class Head<E_IN> extends LongPipeline<E_IN> {
      Head(Supplier<? extends Spliterator<Long>> var1, int var2, boolean var3) {
         super(var1, var2, var3);
      }

      Head(Spliterator<Long> var1, int var2, boolean var3) {
         super(var1, var2, var3);
      }

      final boolean opIsStateful() {
         throw new UnsupportedOperationException();
      }

      final Sink<E_IN> opWrapSink(int var1, Sink<Long> var2) {
         throw new UnsupportedOperationException();
      }

      public void forEach(LongConsumer var1) {
         if (!this.isParallel()) {
            LongPipeline.adapt(this.sourceStageSpliterator()).forEachRemaining(var1);
         } else {
            super.forEach(var1);
         }

      }

      public void forEachOrdered(LongConsumer var1) {
         if (!this.isParallel()) {
            LongPipeline.adapt(this.sourceStageSpliterator()).forEachRemaining(var1);
         } else {
            super.forEachOrdered(var1);
         }

      }
   }
}
