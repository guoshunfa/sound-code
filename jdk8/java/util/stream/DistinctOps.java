package java.util.stream;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntFunction;

final class DistinctOps {
   private DistinctOps() {
   }

   static <T> ReferencePipeline<T, T> makeRef(AbstractPipeline<?, T, ?> var0) {
      return new ReferencePipeline.StatefulOp<T, T>(var0, StreamShape.REFERENCE, StreamOpFlag.IS_DISTINCT | StreamOpFlag.NOT_SIZED) {
         <P_IN> Node<T> reduce(PipelineHelper<T> var1, Spliterator<P_IN> var2) {
            TerminalOp var3 = ReduceOps.makeRef(LinkedHashSet::new, HashSet::add, AbstractCollection::addAll);
            return Nodes.node((Collection)var3.evaluateParallel(var1, var2));
         }

         <P_IN> Node<T> opEvaluateParallel(PipelineHelper<T> var1, Spliterator<P_IN> var2, IntFunction<T[]> var3) {
            if (StreamOpFlag.DISTINCT.isKnown(var1.getStreamAndOpFlags())) {
               return var1.evaluate(var2, false, var3);
            } else if (StreamOpFlag.ORDERED.isKnown(var1.getStreamAndOpFlags())) {
               return this.reduce(var1, var2);
            } else {
               AtomicBoolean var4 = new AtomicBoolean(false);
               ConcurrentHashMap var5 = new ConcurrentHashMap();
               TerminalOp var6 = ForEachOps.makeRef((var2x) -> {
                  if (var2x == null) {
                     var4.set(true);
                  } else {
                     var5.putIfAbsent(var2x, Boolean.TRUE);
                  }

               }, false);
               var6.evaluateParallel(var1, var2);
               Object var7 = var5.keySet();
               if (var4.get()) {
                  var7 = new HashSet((Collection)var7);
                  ((Set)var7).add((Object)null);
               }

               return Nodes.node((Collection)var7);
            }
         }

         <P_IN> Spliterator<T> opEvaluateParallelLazy(PipelineHelper<T> var1, Spliterator<P_IN> var2) {
            if (StreamOpFlag.DISTINCT.isKnown(var1.getStreamAndOpFlags())) {
               return var1.wrapSpliterator(var2);
            } else {
               return (Spliterator)(StreamOpFlag.ORDERED.isKnown(var1.getStreamAndOpFlags()) ? this.reduce(var1, var2).spliterator() : new StreamSpliterators.DistinctSpliterator(var1.wrapSpliterator(var2)));
            }
         }

         Sink<T> opWrapSink(int var1, Sink<T> var2) {
            Objects.requireNonNull(var2);
            if (StreamOpFlag.DISTINCT.isKnown(var1)) {
               return var2;
            } else {
               return StreamOpFlag.SORTED.isKnown(var1) ? new Sink.ChainedReference<T, T>(var2) {
                  boolean seenNull;
                  T lastSeen;

                  public void begin(long var1) {
                     this.seenNull = false;
                     this.lastSeen = null;
                     this.downstream.begin(-1L);
                  }

                  public void end() {
                     this.seenNull = false;
                     this.lastSeen = null;
                     this.downstream.end();
                  }

                  public void accept(T var1) {
                     if (var1 == null) {
                        if (!this.seenNull) {
                           this.seenNull = true;
                           this.downstream.accept(this.lastSeen = null);
                        }
                     } else if (this.lastSeen == null || !var1.equals(this.lastSeen)) {
                        this.downstream.accept(this.lastSeen = var1);
                     }

                  }
               } : new Sink.ChainedReference<T, T>(var2) {
                  Set<T> seen;

                  public void begin(long var1) {
                     this.seen = new HashSet();
                     this.downstream.begin(-1L);
                  }

                  public void end() {
                     this.seen = null;
                     this.downstream.end();
                  }

                  public void accept(T var1) {
                     if (!this.seen.contains(var1)) {
                        this.seen.add(var1);
                        this.downstream.accept(var1);
                     }

                  }
               };
            }
         }
      };
   }
}
