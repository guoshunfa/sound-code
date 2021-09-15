package java.util.stream;

import java.util.Spliterator;
import java.util.concurrent.CountedCompleter;
import java.util.function.IntFunction;

final class SliceOps {
   private SliceOps() {
   }

   private static long calcSize(long var0, long var2, long var4) {
      return var0 >= 0L ? Math.max(-1L, Math.min(var0 - var2, var4)) : -1L;
   }

   private static long calcSliceFence(long var0, long var2) {
      long var4 = var2 >= 0L ? var0 + var2 : Long.MAX_VALUE;
      return var4 >= 0L ? var4 : Long.MAX_VALUE;
   }

   private static <P_IN> Spliterator<P_IN> sliceSpliterator(StreamShape var0, Spliterator<P_IN> var1, long var2, long var4) {
      assert var1.hasCharacteristics(16384);

      long var6 = calcSliceFence(var2, var4);
      switch(var0) {
      case REFERENCE:
         return new StreamSpliterators.SliceSpliterator.OfRef(var1, var2, var6);
      case INT_VALUE:
         return new StreamSpliterators.SliceSpliterator.OfInt((Spliterator.OfInt)var1, var2, var6);
      case LONG_VALUE:
         return new StreamSpliterators.SliceSpliterator.OfLong((Spliterator.OfLong)var1, var2, var6);
      case DOUBLE_VALUE:
         return new StreamSpliterators.SliceSpliterator.OfDouble((Spliterator.OfDouble)var1, var2, var6);
      default:
         throw new IllegalStateException("Unknown shape " + var0);
      }
   }

   private static <T> IntFunction<T[]> castingArray() {
      return (var0) -> {
         return (Object[])(new Object[var0]);
      };
   }

   public static <T> Stream<T> makeRef(AbstractPipeline<?, T, ?> var0, final long var1, final long var3) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("Skip must be non-negative: " + var1);
      } else {
         return new ReferencePipeline.StatefulOp<T, T>(var0, StreamShape.REFERENCE, flags(var3)) {
            Spliterator<T> unorderedSkipLimitSpliterator(Spliterator<T> var1x, long var2, long var4, long var6) {
               if (var2 <= var6) {
                  var4 = var4 >= 0L ? Math.min(var4, var6 - var2) : var6 - var2;
                  var2 = 0L;
               }

               return new StreamSpliterators.UnorderedSliceSpliterator.OfRef(var1x, var2, var4);
            }

            <P_IN> Spliterator<T> opEvaluateParallelLazy(PipelineHelper<T> var1x, Spliterator<P_IN> var2) {
               long var3x = var1x.exactOutputSizeIfKnown(var2);
               if (var3x > 0L && var2.hasCharacteristics(16384)) {
                  return new StreamSpliterators.SliceSpliterator.OfRef(var1x.wrapSpliterator(var2), var1, SliceOps.calcSliceFence(var1, var3));
               } else {
                  return !StreamOpFlag.ORDERED.isKnown(var1x.getStreamAndOpFlags()) ? this.unorderedSkipLimitSpliterator(var1x.wrapSpliterator(var2), var1, var3, var3x) : ((Node)(new SliceOps.SliceTask(this, var1x, var2, SliceOps.castingArray(), var1, var3)).invoke()).spliterator();
               }
            }

            <P_IN> Node<T> opEvaluateParallel(PipelineHelper<T> var1x, Spliterator<P_IN> var2, IntFunction<T[]> var3x) {
               long var4 = var1x.exactOutputSizeIfKnown(var2);
               Spliterator var6;
               if (var4 > 0L && var2.hasCharacteristics(16384)) {
                  var6 = SliceOps.sliceSpliterator(var1x.getSourceShape(), var2, var1, var3);
                  return Nodes.collect(var1x, var6, true, var3x);
               } else if (!StreamOpFlag.ORDERED.isKnown(var1x.getStreamAndOpFlags())) {
                  var6 = this.unorderedSkipLimitSpliterator(var1x.wrapSpliterator(var2), var1, var3, var4);
                  return Nodes.collect(this, var6, true, var3x);
               } else {
                  return (Node)(new SliceOps.SliceTask(this, var1x, var2, var3x, var1, var3)).invoke();
               }
            }

            Sink<T> opWrapSink(int var1x, Sink<T> var2) {
               return new Sink.ChainedReference<T, T>(var2) {
                  long n = var1;
                  long m = var3 >= 0L ? var3 : Long.MAX_VALUE;

                  public void begin(long var1x) {
                     this.downstream.begin(SliceOps.calcSize(var1x, var1, this.m));
                  }

                  public void accept(T var1x) {
                     if (this.n == 0L) {
                        if (this.m > 0L) {
                           --this.m;
                           this.downstream.accept(var1x);
                        }
                     } else {
                        --this.n;
                     }

                  }

                  public boolean cancellationRequested() {
                     return this.m == 0L || this.downstream.cancellationRequested();
                  }
               };
            }
         };
      }
   }

   public static IntStream makeInt(AbstractPipeline<?, Integer, ?> var0, final long var1, final long var3) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("Skip must be non-negative: " + var1);
      } else {
         return new IntPipeline.StatefulOp<Integer>(var0, StreamShape.INT_VALUE, flags(var3)) {
            Spliterator.OfInt unorderedSkipLimitSpliterator(Spliterator.OfInt var1x, long var2, long var4, long var6) {
               if (var2 <= var6) {
                  var4 = var4 >= 0L ? Math.min(var4, var6 - var2) : var6 - var2;
                  var2 = 0L;
               }

               return new StreamSpliterators.UnorderedSliceSpliterator.OfInt(var1x, var2, var4);
            }

            <P_IN> Spliterator<Integer> opEvaluateParallelLazy(PipelineHelper<Integer> var1x, Spliterator<P_IN> var2) {
               long var3x = var1x.exactOutputSizeIfKnown(var2);
               if (var3x > 0L && var2.hasCharacteristics(16384)) {
                  return new StreamSpliterators.SliceSpliterator.OfInt((Spliterator.OfInt)var1x.wrapSpliterator(var2), var1, SliceOps.calcSliceFence(var1, var3));
               } else {
                  return (Spliterator)(!StreamOpFlag.ORDERED.isKnown(var1x.getStreamAndOpFlags()) ? this.unorderedSkipLimitSpliterator((Spliterator.OfInt)var1x.wrapSpliterator(var2), var1, var3, var3x) : ((Node)(new SliceOps.SliceTask(this, var1x, var2, (var0) -> {
                     return new Integer[var0];
                  }, var1, var3)).invoke()).spliterator());
               }
            }

            <P_IN> Node<Integer> opEvaluateParallel(PipelineHelper<Integer> var1x, Spliterator<P_IN> var2, IntFunction<Integer[]> var3x) {
               long var4 = var1x.exactOutputSizeIfKnown(var2);
               if (var4 > 0L && var2.hasCharacteristics(16384)) {
                  Spliterator var7 = SliceOps.sliceSpliterator(var1x.getSourceShape(), var2, var1, var3);
                  return Nodes.collectInt(var1x, var7, true);
               } else if (!StreamOpFlag.ORDERED.isKnown(var1x.getStreamAndOpFlags())) {
                  Spliterator.OfInt var6 = this.unorderedSkipLimitSpliterator((Spliterator.OfInt)var1x.wrapSpliterator(var2), var1, var3, var4);
                  return Nodes.collectInt(this, var6, true);
               } else {
                  return (Node)(new SliceOps.SliceTask(this, var1x, var2, var3x, var1, var3)).invoke();
               }
            }

            Sink<Integer> opWrapSink(int var1x, Sink<Integer> var2) {
               return new Sink.ChainedInt<Integer>(var2) {
                  long n = var1;
                  long m = var3 >= 0L ? var3 : Long.MAX_VALUE;

                  public void begin(long var1x) {
                     this.downstream.begin(SliceOps.calcSize(var1x, var1, this.m));
                  }

                  public void accept(int var1x) {
                     if (this.n == 0L) {
                        if (this.m > 0L) {
                           --this.m;
                           this.downstream.accept(var1x);
                        }
                     } else {
                        --this.n;
                     }

                  }

                  public boolean cancellationRequested() {
                     return this.m == 0L || this.downstream.cancellationRequested();
                  }
               };
            }
         };
      }
   }

   public static LongStream makeLong(AbstractPipeline<?, Long, ?> var0, final long var1, final long var3) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("Skip must be non-negative: " + var1);
      } else {
         return new LongPipeline.StatefulOp<Long>(var0, StreamShape.LONG_VALUE, flags(var3)) {
            Spliterator.OfLong unorderedSkipLimitSpliterator(Spliterator.OfLong var1x, long var2, long var4, long var6) {
               if (var2 <= var6) {
                  var4 = var4 >= 0L ? Math.min(var4, var6 - var2) : var6 - var2;
                  var2 = 0L;
               }

               return new StreamSpliterators.UnorderedSliceSpliterator.OfLong(var1x, var2, var4);
            }

            <P_IN> Spliterator<Long> opEvaluateParallelLazy(PipelineHelper<Long> var1x, Spliterator<P_IN> var2) {
               long var3x = var1x.exactOutputSizeIfKnown(var2);
               if (var3x > 0L && var2.hasCharacteristics(16384)) {
                  return new StreamSpliterators.SliceSpliterator.OfLong((Spliterator.OfLong)var1x.wrapSpliterator(var2), var1, SliceOps.calcSliceFence(var1, var3));
               } else {
                  return (Spliterator)(!StreamOpFlag.ORDERED.isKnown(var1x.getStreamAndOpFlags()) ? this.unorderedSkipLimitSpliterator((Spliterator.OfLong)var1x.wrapSpliterator(var2), var1, var3, var3x) : ((Node)(new SliceOps.SliceTask(this, var1x, var2, (var0) -> {
                     return new Long[var0];
                  }, var1, var3)).invoke()).spliterator());
               }
            }

            <P_IN> Node<Long> opEvaluateParallel(PipelineHelper<Long> var1x, Spliterator<P_IN> var2, IntFunction<Long[]> var3x) {
               long var4 = var1x.exactOutputSizeIfKnown(var2);
               if (var4 > 0L && var2.hasCharacteristics(16384)) {
                  Spliterator var7 = SliceOps.sliceSpliterator(var1x.getSourceShape(), var2, var1, var3);
                  return Nodes.collectLong(var1x, var7, true);
               } else if (!StreamOpFlag.ORDERED.isKnown(var1x.getStreamAndOpFlags())) {
                  Spliterator.OfLong var6 = this.unorderedSkipLimitSpliterator((Spliterator.OfLong)var1x.wrapSpliterator(var2), var1, var3, var4);
                  return Nodes.collectLong(this, var6, true);
               } else {
                  return (Node)(new SliceOps.SliceTask(this, var1x, var2, var3x, var1, var3)).invoke();
               }
            }

            Sink<Long> opWrapSink(int var1x, Sink<Long> var2) {
               return new Sink.ChainedLong<Long>(var2) {
                  long n = var1;
                  long m = var3 >= 0L ? var3 : Long.MAX_VALUE;

                  public void begin(long var1x) {
                     this.downstream.begin(SliceOps.calcSize(var1x, var1, this.m));
                  }

                  public void accept(long var1x) {
                     if (this.n == 0L) {
                        if (this.m > 0L) {
                           --this.m;
                           this.downstream.accept(var1x);
                        }
                     } else {
                        --this.n;
                     }

                  }

                  public boolean cancellationRequested() {
                     return this.m == 0L || this.downstream.cancellationRequested();
                  }
               };
            }
         };
      }
   }

   public static DoubleStream makeDouble(AbstractPipeline<?, Double, ?> var0, final long var1, final long var3) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("Skip must be non-negative: " + var1);
      } else {
         return new DoublePipeline.StatefulOp<Double>(var0, StreamShape.DOUBLE_VALUE, flags(var3)) {
            Spliterator.OfDouble unorderedSkipLimitSpliterator(Spliterator.OfDouble var1x, long var2, long var4, long var6) {
               if (var2 <= var6) {
                  var4 = var4 >= 0L ? Math.min(var4, var6 - var2) : var6 - var2;
                  var2 = 0L;
               }

               return new StreamSpliterators.UnorderedSliceSpliterator.OfDouble(var1x, var2, var4);
            }

            <P_IN> Spliterator<Double> opEvaluateParallelLazy(PipelineHelper<Double> var1x, Spliterator<P_IN> var2) {
               long var3x = var1x.exactOutputSizeIfKnown(var2);
               if (var3x > 0L && var2.hasCharacteristics(16384)) {
                  return new StreamSpliterators.SliceSpliterator.OfDouble((Spliterator.OfDouble)var1x.wrapSpliterator(var2), var1, SliceOps.calcSliceFence(var1, var3));
               } else {
                  return (Spliterator)(!StreamOpFlag.ORDERED.isKnown(var1x.getStreamAndOpFlags()) ? this.unorderedSkipLimitSpliterator((Spliterator.OfDouble)var1x.wrapSpliterator(var2), var1, var3, var3x) : ((Node)(new SliceOps.SliceTask(this, var1x, var2, (var0) -> {
                     return new Double[var0];
                  }, var1, var3)).invoke()).spliterator());
               }
            }

            <P_IN> Node<Double> opEvaluateParallel(PipelineHelper<Double> var1x, Spliterator<P_IN> var2, IntFunction<Double[]> var3x) {
               long var4 = var1x.exactOutputSizeIfKnown(var2);
               if (var4 > 0L && var2.hasCharacteristics(16384)) {
                  Spliterator var7 = SliceOps.sliceSpliterator(var1x.getSourceShape(), var2, var1, var3);
                  return Nodes.collectDouble(var1x, var7, true);
               } else if (!StreamOpFlag.ORDERED.isKnown(var1x.getStreamAndOpFlags())) {
                  Spliterator.OfDouble var6 = this.unorderedSkipLimitSpliterator((Spliterator.OfDouble)var1x.wrapSpliterator(var2), var1, var3, var4);
                  return Nodes.collectDouble(this, var6, true);
               } else {
                  return (Node)(new SliceOps.SliceTask(this, var1x, var2, var3x, var1, var3)).invoke();
               }
            }

            Sink<Double> opWrapSink(int var1x, Sink<Double> var2) {
               return new Sink.ChainedDouble<Double>(var2) {
                  long n = var1;
                  long m = var3 >= 0L ? var3 : Long.MAX_VALUE;

                  public void begin(long var1x) {
                     this.downstream.begin(SliceOps.calcSize(var1x, var1, this.m));
                  }

                  public void accept(double var1x) {
                     if (this.n == 0L) {
                        if (this.m > 0L) {
                           --this.m;
                           this.downstream.accept(var1x);
                        }
                     } else {
                        --this.n;
                     }

                  }

                  public boolean cancellationRequested() {
                     return this.m == 0L || this.downstream.cancellationRequested();
                  }
               };
            }
         };
      }
   }

   private static int flags(long var0) {
      return StreamOpFlag.NOT_SIZED | (var0 != -1L ? StreamOpFlag.IS_SHORT_CIRCUIT : 0);
   }

   private static final class SliceTask<P_IN, P_OUT> extends AbstractShortCircuitTask<P_IN, P_OUT, Node<P_OUT>, SliceOps.SliceTask<P_IN, P_OUT>> {
      private final AbstractPipeline<P_OUT, P_OUT, ?> op;
      private final IntFunction<P_OUT[]> generator;
      private final long targetOffset;
      private final long targetSize;
      private long thisNodeSize;
      private volatile boolean completed;

      SliceTask(AbstractPipeline<P_OUT, P_OUT, ?> var1, PipelineHelper<P_OUT> var2, Spliterator<P_IN> var3, IntFunction<P_OUT[]> var4, long var5, long var7) {
         super(var2, var3);
         this.op = var1;
         this.generator = var4;
         this.targetOffset = var5;
         this.targetSize = var7;
      }

      SliceTask(SliceOps.SliceTask<P_IN, P_OUT> var1, Spliterator<P_IN> var2) {
         super((AbstractShortCircuitTask)var1, var2);
         this.op = var1.op;
         this.generator = var1.generator;
         this.targetOffset = var1.targetOffset;
         this.targetSize = var1.targetSize;
      }

      protected SliceOps.SliceTask<P_IN, P_OUT> makeChild(Spliterator<P_IN> var1) {
         return new SliceOps.SliceTask(this, var1);
      }

      protected final Node<P_OUT> getEmptyResult() {
         return Nodes.emptyNode(this.op.getOutputShape());
      }

      protected final Node<P_OUT> doLeaf() {
         if (this.isRoot()) {
            long var5 = StreamOpFlag.SIZED.isPreserved(this.op.sourceOrOpFlags) ? this.op.exactOutputSizeIfKnown(this.spliterator) : -1L;
            Node.Builder var3 = this.op.makeNodeBuilder(var5, this.generator);
            Sink var4 = this.op.opWrapSink(this.helper.getStreamAndOpFlags(), var3);
            this.helper.copyIntoWithCancel(this.helper.wrapSink(var4), this.spliterator);
            return var3.build();
         } else {
            Node var1 = ((Node.Builder)this.helper.wrapAndCopyInto(this.helper.makeNodeBuilder(-1L, this.generator), this.spliterator)).build();
            this.thisNodeSize = var1.count();
            this.completed = true;
            this.spliterator = null;
            return var1;
         }
      }

      public final void onCompletion(CountedCompleter<?> var1) {
         if (!this.isLeaf()) {
            this.thisNodeSize = ((SliceOps.SliceTask)this.leftChild).thisNodeSize + ((SliceOps.SliceTask)this.rightChild).thisNodeSize;
            Node var2;
            if (this.canceled) {
               this.thisNodeSize = 0L;
               var2 = this.getEmptyResult();
            } else if (this.thisNodeSize == 0L) {
               var2 = this.getEmptyResult();
            } else if (((SliceOps.SliceTask)this.leftChild).thisNodeSize == 0L) {
               var2 = (Node)((SliceOps.SliceTask)this.rightChild).getLocalResult();
            } else {
               var2 = Nodes.conc(this.op.getOutputShape(), (Node)((SliceOps.SliceTask)this.leftChild).getLocalResult(), (Node)((SliceOps.SliceTask)this.rightChild).getLocalResult());
            }

            this.setLocalResult(this.isRoot() ? this.doTruncate(var2) : var2);
            this.completed = true;
         }

         if (this.targetSize >= 0L && !this.isRoot() && this.isLeftCompleted(this.targetOffset + this.targetSize)) {
            this.cancelLaterNodes();
         }

         super.onCompletion(var1);
      }

      protected void cancel() {
         super.cancel();
         if (this.completed) {
            this.setLocalResult(this.getEmptyResult());
         }

      }

      private Node<P_OUT> doTruncate(Node<P_OUT> var1) {
         long var2 = this.targetSize >= 0L ? Math.min(var1.count(), this.targetOffset + this.targetSize) : this.thisNodeSize;
         return var1.truncate(this.targetOffset, var2, this.generator);
      }

      private boolean isLeftCompleted(long var1) {
         long var3 = this.completed ? this.thisNodeSize : this.completedSize(var1);
         if (var3 >= var1) {
            return true;
         } else {
            SliceOps.SliceTask var5 = (SliceOps.SliceTask)this.getParent();

            for(SliceOps.SliceTask var6 = this; var5 != null; var5 = (SliceOps.SliceTask)var5.getParent()) {
               if (var6 == var5.rightChild) {
                  SliceOps.SliceTask var7 = (SliceOps.SliceTask)var5.leftChild;
                  if (var7 != null) {
                     var3 += var7.completedSize(var1);
                     if (var3 >= var1) {
                        return true;
                     }
                  }
               }

               var6 = var5;
            }

            return var3 >= var1;
         }
      }

      private long completedSize(long var1) {
         if (this.completed) {
            return this.thisNodeSize;
         } else {
            SliceOps.SliceTask var3 = (SliceOps.SliceTask)this.leftChild;
            SliceOps.SliceTask var4 = (SliceOps.SliceTask)this.rightChild;
            if (var3 != null && var4 != null) {
               long var5 = var3.completedSize(var1);
               return var5 >= var1 ? var5 : var5 + var4.completedSize(var1);
            } else {
               return this.thisNodeSize;
            }
         }
      }
   }
}
