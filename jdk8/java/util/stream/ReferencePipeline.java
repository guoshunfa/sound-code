package java.util.stream;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.LongConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

abstract class ReferencePipeline<P_IN, P_OUT> extends AbstractPipeline<P_IN, P_OUT, Stream<P_OUT>> implements Stream<P_OUT> {
   ReferencePipeline(Supplier<? extends Spliterator<?>> var1, int var2, boolean var3) {
      super(var1, var2, var3);
   }

   ReferencePipeline(Spliterator<?> var1, int var2, boolean var3) {
      super(var1, var2, var3);
   }

   ReferencePipeline(AbstractPipeline<?, P_IN, ?> var1, int var2) {
      super(var1, var2);
   }

   final StreamShape getOutputShape() {
      return StreamShape.REFERENCE;
   }

   final <P_IN> Node<P_OUT> evaluateToNode(PipelineHelper<P_OUT> var1, Spliterator<P_IN> var2, boolean var3, IntFunction<P_OUT[]> var4) {
      return Nodes.collect(var1, var2, var3, var4);
   }

   final <P_IN> Spliterator<P_OUT> wrap(PipelineHelper<P_OUT> var1, Supplier<Spliterator<P_IN>> var2, boolean var3) {
      return new StreamSpliterators.WrappingSpliterator(var1, var2, var3);
   }

   final Spliterator<P_OUT> lazySpliterator(Supplier<? extends Spliterator<P_OUT>> var1) {
      return new StreamSpliterators.DelegatingSpliterator(var1);
   }

   final void forEachWithCancel(Spliterator<P_OUT> var1, Sink<P_OUT> var2) {
      while(!var2.cancellationRequested() && var1.tryAdvance(var2)) {
      }

   }

   final Node.Builder<P_OUT> makeNodeBuilder(long var1, IntFunction<P_OUT[]> var3) {
      return Nodes.builder(var1, var3);
   }

   public final Iterator<P_OUT> iterator() {
      return Spliterators.iterator(this.spliterator());
   }

   public Stream<P_OUT> unordered() {
      return (Stream)(!this.isOrdered() ? this : new ReferencePipeline.StatelessOp<P_OUT, P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_ORDERED) {
         Sink<P_OUT> opWrapSink(int var1, Sink<P_OUT> var2) {
            return var2;
         }
      });
   }

   public final Stream<P_OUT> filter(final Predicate<? super P_OUT> var1) {
      Objects.requireNonNull(var1);
      return new ReferencePipeline.StatelessOp<P_OUT, P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SIZED) {
         Sink<P_OUT> opWrapSink(int var1x, Sink<P_OUT> var2) {
            return new Sink.ChainedReference<P_OUT, P_OUT>(var2) {
               public void begin(long var1x) {
                  this.downstream.begin(-1L);
               }

               public void accept(P_OUT var1x) {
                  if (var1.test(var1x)) {
                     this.downstream.accept(var1x);
                  }

               }
            };
         }
      };
   }

   public final <R> Stream<R> map(final Function<? super P_OUT, ? extends R> var1) {
      Objects.requireNonNull(var1);
      return new ReferencePipeline.StatelessOp<P_OUT, R>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
         Sink<P_OUT> opWrapSink(int var1x, Sink<R> var2) {
            return new Sink.ChainedReference<P_OUT, R>(var2) {
               public void accept(P_OUT var1x) {
                  this.downstream.accept(var1.apply(var1x));
               }
            };
         }
      };
   }

   public final IntStream mapToInt(final ToIntFunction<? super P_OUT> var1) {
      Objects.requireNonNull(var1);
      return new IntPipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
         Sink<P_OUT> opWrapSink(int var1x, Sink<Integer> var2) {
            return new Sink.ChainedReference<P_OUT, Integer>(var2) {
               public void accept(P_OUT var1x) {
                  this.downstream.accept(var1.applyAsInt(var1x));
               }
            };
         }
      };
   }

   public final LongStream mapToLong(final ToLongFunction<? super P_OUT> var1) {
      Objects.requireNonNull(var1);
      return new LongPipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
         Sink<P_OUT> opWrapSink(int var1x, Sink<Long> var2) {
            return new Sink.ChainedReference<P_OUT, Long>(var2) {
               public void accept(P_OUT var1x) {
                  this.downstream.accept(var1.applyAsLong(var1x));
               }
            };
         }
      };
   }

   public final DoubleStream mapToDouble(final ToDoubleFunction<? super P_OUT> var1) {
      Objects.requireNonNull(var1);
      return new DoublePipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
         Sink<P_OUT> opWrapSink(int var1x, Sink<Double> var2) {
            return new Sink.ChainedReference<P_OUT, Double>(var2) {
               public void accept(P_OUT var1x) {
                  this.downstream.accept(var1.applyAsDouble(var1x));
               }
            };
         }
      };
   }

   public final <R> Stream<R> flatMap(final Function<? super P_OUT, ? extends Stream<? extends R>> var1) {
      Objects.requireNonNull(var1);
      return new ReferencePipeline.StatelessOp<P_OUT, R>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
         Sink<P_OUT> opWrapSink(int var1x, Sink<R> var2) {
            return new Sink.ChainedReference<P_OUT, R>(var2) {
               public void begin(long var1x) {
                  this.downstream.begin(-1L);
               }

               public void accept(P_OUT var1x) {
                  Stream var2 = (Stream)var1.apply(var1x);
                  Throwable var3 = null;

                  try {
                     if (var2 != null) {
                        ((Stream)var2.sequential()).forEach(this.downstream);
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

   public final IntStream flatMapToInt(final Function<? super P_OUT, ? extends IntStream> var1) {
      Objects.requireNonNull(var1);
      return new IntPipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
         Sink<P_OUT> opWrapSink(int var1x, Sink<Integer> var2) {
            return new Sink.ChainedReference<P_OUT, Integer>(var2) {
               IntConsumer downstreamAsInt;

               {
                  Sink var10001 = this.downstream;
                  this.downstreamAsInt = var10001::accept;
               }

               public void begin(long var1x) {
                  this.downstream.begin(-1L);
               }

               public void accept(P_OUT var1x) {
                  IntStream var2 = (IntStream)var1.apply(var1x);
                  Throwable var3 = null;

                  try {
                     if (var2 != null) {
                        var2.sequential().forEach(this.downstreamAsInt);
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

   public final DoubleStream flatMapToDouble(final Function<? super P_OUT, ? extends DoubleStream> var1) {
      Objects.requireNonNull(var1);
      return new DoublePipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
         Sink<P_OUT> opWrapSink(int var1x, Sink<Double> var2) {
            return new Sink.ChainedReference<P_OUT, Double>(var2) {
               DoubleConsumer downstreamAsDouble;

               {
                  Sink var10001 = this.downstream;
                  this.downstreamAsDouble = var10001::accept;
               }

               public void begin(long var1x) {
                  this.downstream.begin(-1L);
               }

               public void accept(P_OUT var1x) {
                  DoubleStream var2 = (DoubleStream)var1.apply(var1x);
                  Throwable var3 = null;

                  try {
                     if (var2 != null) {
                        var2.sequential().forEach(this.downstreamAsDouble);
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

   public final LongStream flatMapToLong(final Function<? super P_OUT, ? extends LongStream> var1) {
      Objects.requireNonNull(var1);
      return new LongPipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
         Sink<P_OUT> opWrapSink(int var1x, Sink<Long> var2) {
            return new Sink.ChainedReference<P_OUT, Long>(var2) {
               LongConsumer downstreamAsLong;

               {
                  Sink var10001 = this.downstream;
                  this.downstreamAsLong = var10001::accept;
               }

               public void begin(long var1x) {
                  this.downstream.begin(-1L);
               }

               public void accept(P_OUT var1x) {
                  LongStream var2 = (LongStream)var1.apply(var1x);
                  Throwable var3 = null;

                  try {
                     if (var2 != null) {
                        var2.sequential().forEach(this.downstreamAsLong);
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

   public final Stream<P_OUT> peek(final Consumer<? super P_OUT> var1) {
      Objects.requireNonNull(var1);
      return new ReferencePipeline.StatelessOp<P_OUT, P_OUT>(this, StreamShape.REFERENCE, 0) {
         Sink<P_OUT> opWrapSink(int var1x, Sink<P_OUT> var2) {
            return new Sink.ChainedReference<P_OUT, P_OUT>(var2) {
               public void accept(P_OUT var1x) {
                  var1.accept(var1x);
                  this.downstream.accept(var1x);
               }
            };
         }
      };
   }

   public final Stream<P_OUT> distinct() {
      return DistinctOps.makeRef(this);
   }

   public final Stream<P_OUT> sorted() {
      return SortedOps.makeRef(this);
   }

   public final Stream<P_OUT> sorted(Comparator<? super P_OUT> var1) {
      return SortedOps.makeRef(this, var1);
   }

   public final Stream<P_OUT> limit(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException(Long.toString(var1));
      } else {
         return SliceOps.makeRef(this, 0L, var1);
      }
   }

   public final Stream<P_OUT> skip(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException(Long.toString(var1));
      } else {
         return (Stream)(var1 == 0L ? this : SliceOps.makeRef(this, var1, -1L));
      }
   }

   public void forEach(Consumer<? super P_OUT> var1) {
      this.evaluate(ForEachOps.makeRef(var1, false));
   }

   public void forEachOrdered(Consumer<? super P_OUT> var1) {
      this.evaluate(ForEachOps.makeRef(var1, true));
   }

   public final <A> A[] toArray(IntFunction<A[]> var1) {
      return (Object[])Nodes.flatten(this.evaluateToArrayNode(var1), var1).asArray(var1);
   }

   public final Object[] toArray() {
      return this.toArray((var0) -> {
         return new Object[var0];
      });
   }

   public final boolean anyMatch(Predicate<? super P_OUT> var1) {
      return (Boolean)this.evaluate(MatchOps.makeRef(var1, MatchOps.MatchKind.ANY));
   }

   public final boolean allMatch(Predicate<? super P_OUT> var1) {
      return (Boolean)this.evaluate(MatchOps.makeRef(var1, MatchOps.MatchKind.ALL));
   }

   public final boolean noneMatch(Predicate<? super P_OUT> var1) {
      return (Boolean)this.evaluate(MatchOps.makeRef(var1, MatchOps.MatchKind.NONE));
   }

   public final Optional<P_OUT> findFirst() {
      return (Optional)this.evaluate(FindOps.makeRef(true));
   }

   public final Optional<P_OUT> findAny() {
      return (Optional)this.evaluate(FindOps.makeRef(false));
   }

   public final P_OUT reduce(P_OUT var1, BinaryOperator<P_OUT> var2) {
      return this.evaluate(ReduceOps.makeRef((Object)var1, (BiFunction)var2, (BinaryOperator)var2));
   }

   public final Optional<P_OUT> reduce(BinaryOperator<P_OUT> var1) {
      return (Optional)this.evaluate(ReduceOps.makeRef(var1));
   }

   public final <R> R reduce(R var1, BiFunction<R, ? super P_OUT, R> var2, BinaryOperator<R> var3) {
      return this.evaluate(ReduceOps.makeRef(var1, var2, var3));
   }

   public final <R, A> R collect(Collector<? super P_OUT, A, R> var1) {
      Object var2;
      if (!this.isParallel() || !var1.characteristics().contains(Collector.Characteristics.CONCURRENT) || this.isOrdered() && !var1.characteristics().contains(Collector.Characteristics.UNORDERED)) {
         var2 = this.evaluate(ReduceOps.makeRef(var1));
      } else {
         var2 = var1.supplier().get();
         BiConsumer var3 = var1.accumulator();
         this.forEach((var2x) -> {
            var3.accept(var2, var2x);
         });
      }

      return var1.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH) ? var2 : var1.finisher().apply(var2);
   }

   public final <R> R collect(Supplier<R> var1, BiConsumer<R, ? super P_OUT> var2, BiConsumer<R, R> var3) {
      return this.evaluate(ReduceOps.makeRef(var1, var2, var3));
   }

   public final Optional<P_OUT> max(Comparator<? super P_OUT> var1) {
      return this.reduce(BinaryOperator.maxBy(var1));
   }

   public final Optional<P_OUT> min(Comparator<? super P_OUT> var1) {
      return this.reduce(BinaryOperator.minBy(var1));
   }

   public final long count() {
      return this.mapToLong((var0) -> {
         return 1L;
      }).sum();
   }

   abstract static class StatefulOp<E_IN, E_OUT> extends ReferencePipeline<E_IN, E_OUT> {
      StatefulOp(AbstractPipeline<?, E_IN, ?> var1, StreamShape var2, int var3) {
         super(var1, var3);

         assert var1.getOutputShape() == var2;

      }

      final boolean opIsStateful() {
         return true;
      }

      abstract <P_IN> Node<E_OUT> opEvaluateParallel(PipelineHelper<E_OUT> var1, Spliterator<P_IN> var2, IntFunction<E_OUT[]> var3);
   }

   abstract static class StatelessOp<E_IN, E_OUT> extends ReferencePipeline<E_IN, E_OUT> {
      StatelessOp(AbstractPipeline<?, E_IN, ?> var1, StreamShape var2, int var3) {
         super(var1, var3);

         assert var1.getOutputShape() == var2;

      }

      final boolean opIsStateful() {
         return false;
      }
   }

   static class Head<E_IN, E_OUT> extends ReferencePipeline<E_IN, E_OUT> {
      Head(Supplier<? extends Spliterator<?>> var1, int var2, boolean var3) {
         super(var1, var2, var3);
      }

      Head(Spliterator<?> var1, int var2, boolean var3) {
         super(var1, var2, var3);
      }

      final boolean opIsStateful() {
         throw new UnsupportedOperationException();
      }

      final Sink<E_IN> opWrapSink(int var1, Sink<E_OUT> var2) {
         throw new UnsupportedOperationException();
      }

      public void forEach(Consumer<? super E_OUT> var1) {
         if (!this.isParallel()) {
            this.sourceStageSpliterator().forEachRemaining(var1);
         } else {
            super.forEach(var1);
         }

      }

      public void forEachOrdered(Consumer<? super E_OUT> var1) {
         if (!this.isParallel()) {
            this.sourceStageSpliterator().forEachRemaining(var1);
         } else {
            super.forEachOrdered(var1);
         }

      }
   }
}
