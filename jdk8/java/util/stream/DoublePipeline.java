package java.util.stream;

import java.util.DoubleSummaryStatistics;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntFunction;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;

abstract class DoublePipeline<E_IN> extends AbstractPipeline<E_IN, Double, DoubleStream> implements DoubleStream {
   DoublePipeline(Supplier<? extends Spliterator<Double>> var1, int var2, boolean var3) {
      super(var1, var2, var3);
   }

   DoublePipeline(Spliterator<Double> var1, int var2, boolean var3) {
      super(var1, var2, var3);
   }

   DoublePipeline(AbstractPipeline<?, E_IN, ?> var1, int var2) {
      super(var1, var2);
   }

   private static DoubleConsumer adapt(Sink<Double> var0) {
      if (var0 instanceof DoubleConsumer) {
         return (DoubleConsumer)var0;
      } else {
         if (Tripwire.ENABLED) {
            Tripwire.trip(AbstractPipeline.class, "using DoubleStream.adapt(Sink<Double> s)");
         }

         return var0::accept;
      }
   }

   private static Spliterator.OfDouble adapt(Spliterator<Double> var0) {
      if (var0 instanceof Spliterator.OfDouble) {
         return (Spliterator.OfDouble)var0;
      } else {
         if (Tripwire.ENABLED) {
            Tripwire.trip(AbstractPipeline.class, "using DoubleStream.adapt(Spliterator<Double> s)");
         }

         throw new UnsupportedOperationException("DoubleStream.adapt(Spliterator<Double> s)");
      }
   }

   final StreamShape getOutputShape() {
      return StreamShape.DOUBLE_VALUE;
   }

   final <P_IN> Node<Double> evaluateToNode(PipelineHelper<Double> var1, Spliterator<P_IN> var2, boolean var3, IntFunction<Double[]> var4) {
      return Nodes.collectDouble(var1, var2, var3);
   }

   final <P_IN> Spliterator<Double> wrap(PipelineHelper<Double> var1, Supplier<Spliterator<P_IN>> var2, boolean var3) {
      return new StreamSpliterators.DoubleWrappingSpliterator(var1, var2, var3);
   }

   final Spliterator.OfDouble lazySpliterator(Supplier<? extends Spliterator<Double>> var1) {
      return new StreamSpliterators.DelegatingSpliterator.OfDouble(var1);
   }

   final void forEachWithCancel(Spliterator<Double> var1, Sink<Double> var2) {
      Spliterator.OfDouble var3 = adapt(var1);
      DoubleConsumer var4 = adapt(var2);

      while(!var2.cancellationRequested() && var3.tryAdvance(var4)) {
      }

   }

   final Node.Builder<Double> makeNodeBuilder(long var1, IntFunction<Double[]> var3) {
      return Nodes.doubleBuilder(var1);
   }

   public final PrimitiveIterator.OfDouble iterator() {
      return Spliterators.iterator(this.spliterator());
   }

   public final Spliterator.OfDouble spliterator() {
      return adapt(super.spliterator());
   }

   public final Stream<Double> boxed() {
      return this.mapToObj(Double::valueOf);
   }

   public final DoubleStream map(final DoubleUnaryOperator var1) {
      Objects.requireNonNull(var1);
      return new DoublePipeline.StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
         Sink<Double> opWrapSink(int var1x, Sink<Double> var2) {
            return new Sink.ChainedDouble<Double>(var2) {
               public void accept(double var1x) {
                  this.downstream.accept(var1.applyAsDouble(var1x));
               }
            };
         }
      };
   }

   public final <U> Stream<U> mapToObj(final DoubleFunction<? extends U> var1) {
      Objects.requireNonNull(var1);
      return new ReferencePipeline.StatelessOp<Double, U>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
         Sink<Double> opWrapSink(int var1x, Sink<U> var2) {
            return new Sink.ChainedDouble<U>(var2) {
               public void accept(double var1x) {
                  this.downstream.accept(var1.apply(var1x));
               }
            };
         }
      };
   }

   public final IntStream mapToInt(final DoubleToIntFunction var1) {
      Objects.requireNonNull(var1);
      return new IntPipeline.StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
         Sink<Double> opWrapSink(int var1x, Sink<Integer> var2) {
            return new Sink.ChainedDouble<Integer>(var2) {
               public void accept(double var1x) {
                  this.downstream.accept(var1.applyAsInt(var1x));
               }
            };
         }
      };
   }

   public final LongStream mapToLong(final DoubleToLongFunction var1) {
      Objects.requireNonNull(var1);
      return new LongPipeline.StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
         Sink<Double> opWrapSink(int var1x, Sink<Long> var2) {
            return new Sink.ChainedDouble<Long>(var2) {
               public void accept(double var1x) {
                  this.downstream.accept(var1.applyAsLong(var1x));
               }
            };
         }
      };
   }

   public final DoubleStream flatMap(final DoubleFunction<? extends DoubleStream> var1) {
      return new DoublePipeline.StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
         Sink<Double> opWrapSink(int var1x, Sink<Double> var2) {
            return new Sink.ChainedDouble<Double>(var2) {
               public void begin(long var1x) {
                  this.downstream.begin(-1L);
               }

               public void accept(double var1x) {
                  DoubleStream var3 = (DoubleStream)var1.apply(var1x);
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

   public DoubleStream unordered() {
      return (DoubleStream)(!this.isOrdered() ? this : new DoublePipeline.StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_ORDERED) {
         Sink<Double> opWrapSink(int var1, Sink<Double> var2) {
            return var2;
         }
      });
   }

   public final DoubleStream filter(final DoublePredicate var1) {
      Objects.requireNonNull(var1);
      return new DoublePipeline.StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_SIZED) {
         Sink<Double> opWrapSink(int var1x, Sink<Double> var2) {
            return new Sink.ChainedDouble<Double>(var2) {
               public void begin(long var1x) {
                  this.downstream.begin(-1L);
               }

               public void accept(double var1x) {
                  if (var1.test(var1x)) {
                     this.downstream.accept(var1x);
                  }

               }
            };
         }
      };
   }

   public final DoubleStream peek(final DoubleConsumer var1) {
      Objects.requireNonNull(var1);
      return new DoublePipeline.StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, 0) {
         Sink<Double> opWrapSink(int var1x, Sink<Double> var2) {
            return new Sink.ChainedDouble<Double>(var2) {
               public void accept(double var1x) {
                  var1.accept(var1x);
                  this.downstream.accept(var1x);
               }
            };
         }
      };
   }

   public final DoubleStream limit(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException(Long.toString(var1));
      } else {
         return SliceOps.makeDouble(this, 0L, var1);
      }
   }

   public final DoubleStream skip(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException(Long.toString(var1));
      } else if (var1 == 0L) {
         return this;
      } else {
         long var3 = -1L;
         return SliceOps.makeDouble(this, var1, var3);
      }
   }

   public final DoubleStream sorted() {
      return SortedOps.makeDouble(this);
   }

   public final DoubleStream distinct() {
      return this.boxed().distinct().mapToDouble((var0) -> {
         return var0;
      });
   }

   public void forEach(DoubleConsumer var1) {
      this.evaluate(ForEachOps.makeDouble(var1, false));
   }

   public void forEachOrdered(DoubleConsumer var1) {
      this.evaluate(ForEachOps.makeDouble(var1, true));
   }

   public final double sum() {
      double[] var1 = (double[])this.collect(() -> {
         return new double[3];
      }, (var0, var1x) -> {
         Collectors.sumWithCompensation(var0, var1x);
         var0[2] += var1x;
      }, (var0, var1x) -> {
         Collectors.sumWithCompensation(var0, var1x[0]);
         Collectors.sumWithCompensation(var0, var1x[1]);
         var0[2] += var1x[2];
      });
      return Collectors.computeFinalSum(var1);
   }

   public final OptionalDouble min() {
      return this.reduce(Math::min);
   }

   public final OptionalDouble max() {
      return this.reduce(Math::max);
   }

   public final OptionalDouble average() {
      double[] var1 = (double[])this.collect(() -> {
         return new double[4];
      }, (var0, var1x) -> {
         int var10002 = var0[2]++;
         Collectors.sumWithCompensation(var0, var1x);
         var0[3] += var1x;
      }, (var0, var1x) -> {
         Collectors.sumWithCompensation(var0, var1x[0]);
         Collectors.sumWithCompensation(var0, var1x[1]);
         var0[2] += var1x[2];
         var0[3] += var1x[3];
      });
      return var1[2] > 0.0D ? OptionalDouble.of(Collectors.computeFinalSum(var1) / var1[2]) : OptionalDouble.empty();
   }

   public final long count() {
      return this.mapToLong((var0) -> {
         return 1L;
      }).sum();
   }

   public final DoubleSummaryStatistics summaryStatistics() {
      return (DoubleSummaryStatistics)this.collect(DoubleSummaryStatistics::new, DoubleSummaryStatistics::accept, DoubleSummaryStatistics::combine);
   }

   public final double reduce(double var1, DoubleBinaryOperator var3) {
      return (Double)this.evaluate(ReduceOps.makeDouble(var1, var3));
   }

   public final OptionalDouble reduce(DoubleBinaryOperator var1) {
      return (OptionalDouble)this.evaluate(ReduceOps.makeDouble(var1));
   }

   public final <R> R collect(Supplier<R> var1, ObjDoubleConsumer<R> var2, BiConsumer<R, R> var3) {
      BinaryOperator var4 = (var1x, var2x) -> {
         var3.accept(var1x, var2x);
         return var1x;
      };
      return this.evaluate(ReduceOps.makeDouble(var1, var2, var4));
   }

   public final boolean anyMatch(DoublePredicate var1) {
      return (Boolean)this.evaluate(MatchOps.makeDouble(var1, MatchOps.MatchKind.ANY));
   }

   public final boolean allMatch(DoublePredicate var1) {
      return (Boolean)this.evaluate(MatchOps.makeDouble(var1, MatchOps.MatchKind.ALL));
   }

   public final boolean noneMatch(DoublePredicate var1) {
      return (Boolean)this.evaluate(MatchOps.makeDouble(var1, MatchOps.MatchKind.NONE));
   }

   public final OptionalDouble findFirst() {
      return (OptionalDouble)this.evaluate(FindOps.makeDouble(true));
   }

   public final OptionalDouble findAny() {
      return (OptionalDouble)this.evaluate(FindOps.makeDouble(false));
   }

   public final double[] toArray() {
      return (double[])Nodes.flattenDouble((Node.OfDouble)this.evaluateToArrayNode((var0) -> {
         return new Double[var0];
      })).asPrimitiveArray();
   }

   abstract static class StatefulOp<E_IN> extends DoublePipeline<E_IN> {
      StatefulOp(AbstractPipeline<?, E_IN, ?> var1, StreamShape var2, int var3) {
         super(var1, var3);

         assert var1.getOutputShape() == var2;

      }

      final boolean opIsStateful() {
         return true;
      }

      abstract <P_IN> Node<Double> opEvaluateParallel(PipelineHelper<Double> var1, Spliterator<P_IN> var2, IntFunction<Double[]> var3);
   }

   abstract static class StatelessOp<E_IN> extends DoublePipeline<E_IN> {
      StatelessOp(AbstractPipeline<?, E_IN, ?> var1, StreamShape var2, int var3) {
         super(var1, var3);

         assert var1.getOutputShape() == var2;

      }

      final boolean opIsStateful() {
         return false;
      }
   }

   static class Head<E_IN> extends DoublePipeline<E_IN> {
      Head(Supplier<? extends Spliterator<Double>> var1, int var2, boolean var3) {
         super(var1, var2, var3);
      }

      Head(Spliterator<Double> var1, int var2, boolean var3) {
         super(var1, var2, var3);
      }

      final boolean opIsStateful() {
         throw new UnsupportedOperationException();
      }

      final Sink<E_IN> opWrapSink(int var1, Sink<Double> var2) {
         throw new UnsupportedOperationException();
      }

      public void forEach(DoubleConsumer var1) {
         if (!this.isParallel()) {
            DoublePipeline.adapt(this.sourceStageSpliterator()).forEachRemaining(var1);
         } else {
            super.forEach(var1);
         }

      }

      public void forEachOrdered(DoubleConsumer var1) {
         if (!this.isParallel()) {
            DoublePipeline.adapt(this.sourceStageSpliterator()).forEachRemaining(var1);
         } else {
            super.forEachOrdered(var1);
         }

      }
   }
}
