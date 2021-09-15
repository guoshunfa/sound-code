package java.util.stream;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.CountedCompleter;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;

final class Nodes {
   static final long MAX_ARRAY_SIZE = 2147483639L;
   static final String BAD_SIZE = "Stream size exceeds max array size";
   private static final Node EMPTY_NODE = new Nodes.EmptyNode.OfRef();
   private static final Node.OfInt EMPTY_INT_NODE = new Nodes.EmptyNode.OfInt();
   private static final Node.OfLong EMPTY_LONG_NODE = new Nodes.EmptyNode.OfLong();
   private static final Node.OfDouble EMPTY_DOUBLE_NODE = new Nodes.EmptyNode.OfDouble();
   private static final int[] EMPTY_INT_ARRAY = new int[0];
   private static final long[] EMPTY_LONG_ARRAY = new long[0];
   private static final double[] EMPTY_DOUBLE_ARRAY = new double[0];

   private Nodes() {
      throw new Error("no instances");
   }

   static <T> Node<T> emptyNode(StreamShape var0) {
      switch(var0) {
      case REFERENCE:
         return EMPTY_NODE;
      case INT_VALUE:
         return EMPTY_INT_NODE;
      case LONG_VALUE:
         return EMPTY_LONG_NODE;
      case DOUBLE_VALUE:
         return EMPTY_DOUBLE_NODE;
      default:
         throw new IllegalStateException("Unknown shape " + var0);
      }
   }

   static <T> Node<T> conc(StreamShape var0, Node<T> var1, Node<T> var2) {
      switch(var0) {
      case REFERENCE:
         return new Nodes.ConcNode(var1, var2);
      case INT_VALUE:
         return new Nodes.ConcNode.OfInt((Node.OfInt)var1, (Node.OfInt)var2);
      case LONG_VALUE:
         return new Nodes.ConcNode.OfLong((Node.OfLong)var1, (Node.OfLong)var2);
      case DOUBLE_VALUE:
         return new Nodes.ConcNode.OfDouble((Node.OfDouble)var1, (Node.OfDouble)var2);
      default:
         throw new IllegalStateException("Unknown shape " + var0);
      }
   }

   static <T> Node<T> node(T[] var0) {
      return new Nodes.ArrayNode(var0);
   }

   static <T> Node<T> node(Collection<T> var0) {
      return new Nodes.CollectionNode(var0);
   }

   static <T> Node.Builder<T> builder(long var0, IntFunction<T[]> var2) {
      return (Node.Builder)(var0 >= 0L && var0 < 2147483639L ? new Nodes.FixedNodeBuilder(var0, var2) : builder());
   }

   static <T> Node.Builder<T> builder() {
      return new Nodes.SpinedNodeBuilder();
   }

   static Node.OfInt node(int[] var0) {
      return new Nodes.IntArrayNode(var0);
   }

   static Node.Builder.OfInt intBuilder(long var0) {
      return (Node.Builder.OfInt)(var0 >= 0L && var0 < 2147483639L ? new Nodes.IntFixedNodeBuilder(var0) : intBuilder());
   }

   static Node.Builder.OfInt intBuilder() {
      return new Nodes.IntSpinedNodeBuilder();
   }

   static Node.OfLong node(long[] var0) {
      return new Nodes.LongArrayNode(var0);
   }

   static Node.Builder.OfLong longBuilder(long var0) {
      return (Node.Builder.OfLong)(var0 >= 0L && var0 < 2147483639L ? new Nodes.LongFixedNodeBuilder(var0) : longBuilder());
   }

   static Node.Builder.OfLong longBuilder() {
      return new Nodes.LongSpinedNodeBuilder();
   }

   static Node.OfDouble node(double[] var0) {
      return new Nodes.DoubleArrayNode(var0);
   }

   static Node.Builder.OfDouble doubleBuilder(long var0) {
      return (Node.Builder.OfDouble)(var0 >= 0L && var0 < 2147483639L ? new Nodes.DoubleFixedNodeBuilder(var0) : doubleBuilder());
   }

   static Node.Builder.OfDouble doubleBuilder() {
      return new Nodes.DoubleSpinedNodeBuilder();
   }

   public static <P_IN, P_OUT> Node<P_OUT> collect(PipelineHelper<P_OUT> var0, Spliterator<P_IN> var1, boolean var2, IntFunction<P_OUT[]> var3) {
      long var4 = var0.exactOutputSizeIfKnown(var1);
      if (var4 >= 0L && var1.hasCharacteristics(16384)) {
         if (var4 >= 2147483639L) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
         } else {
            Object[] var7 = (Object[])var3.apply((int)var4);
            (new Nodes.SizedCollectorTask.OfRef(var1, var0, var7)).invoke();
            return node(var7);
         }
      } else {
         Node var6 = (Node)(new Nodes.CollectorTask.OfRef(var0, var3, var1)).invoke();
         return var2 ? flatten(var6, var3) : var6;
      }
   }

   public static <P_IN> Node.OfInt collectInt(PipelineHelper<Integer> var0, Spliterator<P_IN> var1, boolean var2) {
      long var3 = var0.exactOutputSizeIfKnown(var1);
      if (var3 >= 0L && var1.hasCharacteristics(16384)) {
         if (var3 >= 2147483639L) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
         } else {
            int[] var6 = new int[(int)var3];
            (new Nodes.SizedCollectorTask.OfInt(var1, var0, var6)).invoke();
            return node(var6);
         }
      } else {
         Node.OfInt var5 = (Node.OfInt)(new Nodes.CollectorTask.OfInt(var0, var1)).invoke();
         return var2 ? flattenInt(var5) : var5;
      }
   }

   public static <P_IN> Node.OfLong collectLong(PipelineHelper<Long> var0, Spliterator<P_IN> var1, boolean var2) {
      long var3 = var0.exactOutputSizeIfKnown(var1);
      if (var3 >= 0L && var1.hasCharacteristics(16384)) {
         if (var3 >= 2147483639L) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
         } else {
            long[] var6 = new long[(int)var3];
            (new Nodes.SizedCollectorTask.OfLong(var1, var0, var6)).invoke();
            return node(var6);
         }
      } else {
         Node.OfLong var5 = (Node.OfLong)(new Nodes.CollectorTask.OfLong(var0, var1)).invoke();
         return var2 ? flattenLong(var5) : var5;
      }
   }

   public static <P_IN> Node.OfDouble collectDouble(PipelineHelper<Double> var0, Spliterator<P_IN> var1, boolean var2) {
      long var3 = var0.exactOutputSizeIfKnown(var1);
      if (var3 >= 0L && var1.hasCharacteristics(16384)) {
         if (var3 >= 2147483639L) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
         } else {
            double[] var6 = new double[(int)var3];
            (new Nodes.SizedCollectorTask.OfDouble(var1, var0, var6)).invoke();
            return node(var6);
         }
      } else {
         Node.OfDouble var5 = (Node.OfDouble)(new Nodes.CollectorTask.OfDouble(var0, var1)).invoke();
         return var2 ? flattenDouble(var5) : var5;
      }
   }

   public static <T> Node<T> flatten(Node<T> var0, IntFunction<T[]> var1) {
      if (var0.getChildCount() > 0) {
         long var2 = var0.count();
         if (var2 >= 2147483639L) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
         } else {
            Object[] var4 = (Object[])var1.apply((int)var2);
            (new Nodes.ToArrayTask.OfRef(var0, var4, 0)).invoke();
            return node(var4);
         }
      } else {
         return var0;
      }
   }

   public static Node.OfInt flattenInt(Node.OfInt var0) {
      if (var0.getChildCount() > 0) {
         long var1 = var0.count();
         if (var1 >= 2147483639L) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
         } else {
            int[] var3 = new int[(int)var1];
            (new Nodes.ToArrayTask.OfInt(var0, var3, 0)).invoke();
            return node(var3);
         }
      } else {
         return var0;
      }
   }

   public static Node.OfLong flattenLong(Node.OfLong var0) {
      if (var0.getChildCount() > 0) {
         long var1 = var0.count();
         if (var1 >= 2147483639L) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
         } else {
            long[] var3 = new long[(int)var1];
            (new Nodes.ToArrayTask.OfLong(var0, var3, 0)).invoke();
            return node(var3);
         }
      } else {
         return var0;
      }
   }

   public static Node.OfDouble flattenDouble(Node.OfDouble var0) {
      if (var0.getChildCount() > 0) {
         long var1 = var0.count();
         if (var1 >= 2147483639L) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
         } else {
            double[] var3 = new double[(int)var1];
            (new Nodes.ToArrayTask.OfDouble(var0, var3, 0)).invoke();
            return node(var3);
         }
      } else {
         return var0;
      }
   }

   private static class CollectorTask<P_IN, P_OUT, T_NODE extends Node<P_OUT>, T_BUILDER extends Node.Builder<P_OUT>> extends AbstractTask<P_IN, P_OUT, T_NODE, Nodes.CollectorTask<P_IN, P_OUT, T_NODE, T_BUILDER>> {
      protected final PipelineHelper<P_OUT> helper;
      protected final LongFunction<T_BUILDER> builderFactory;
      protected final BinaryOperator<T_NODE> concFactory;

      CollectorTask(PipelineHelper<P_OUT> var1, Spliterator<P_IN> var2, LongFunction<T_BUILDER> var3, BinaryOperator<T_NODE> var4) {
         super(var1, var2);
         this.helper = var1;
         this.builderFactory = var3;
         this.concFactory = var4;
      }

      CollectorTask(Nodes.CollectorTask<P_IN, P_OUT, T_NODE, T_BUILDER> var1, Spliterator<P_IN> var2) {
         super((AbstractTask)var1, var2);
         this.helper = var1.helper;
         this.builderFactory = var1.builderFactory;
         this.concFactory = var1.concFactory;
      }

      protected Nodes.CollectorTask<P_IN, P_OUT, T_NODE, T_BUILDER> makeChild(Spliterator<P_IN> var1) {
         return new Nodes.CollectorTask(this, var1);
      }

      protected T_NODE doLeaf() {
         Node.Builder var1 = (Node.Builder)this.builderFactory.apply(this.helper.exactOutputSizeIfKnown(this.spliterator));
         return ((Node.Builder)this.helper.wrapAndCopyInto(var1, this.spliterator)).build();
      }

      public void onCompletion(CountedCompleter<?> var1) {
         if (!this.isLeaf()) {
            this.setLocalResult(this.concFactory.apply(((Nodes.CollectorTask)this.leftChild).getLocalResult(), ((Nodes.CollectorTask)this.rightChild).getLocalResult()));
         }

         super.onCompletion(var1);
      }

      private static final class OfDouble<P_IN> extends Nodes.CollectorTask<P_IN, Double, Node.OfDouble, Node.Builder.OfDouble> {
         OfDouble(PipelineHelper<Double> var1, Spliterator<P_IN> var2) {
            super(var1, var2, Nodes::doubleBuilder, Nodes.ConcNode.OfDouble::new);
         }
      }

      private static final class OfLong<P_IN> extends Nodes.CollectorTask<P_IN, Long, Node.OfLong, Node.Builder.OfLong> {
         OfLong(PipelineHelper<Long> var1, Spliterator<P_IN> var2) {
            super(var1, var2, Nodes::longBuilder, Nodes.ConcNode.OfLong::new);
         }
      }

      private static final class OfInt<P_IN> extends Nodes.CollectorTask<P_IN, Integer, Node.OfInt, Node.Builder.OfInt> {
         OfInt(PipelineHelper<Integer> var1, Spliterator<P_IN> var2) {
            super(var1, var2, Nodes::intBuilder, Nodes.ConcNode.OfInt::new);
         }
      }

      private static final class OfRef<P_IN, P_OUT> extends Nodes.CollectorTask<P_IN, P_OUT, Node<P_OUT>, Node.Builder<P_OUT>> {
         OfRef(PipelineHelper<P_OUT> var1, IntFunction<P_OUT[]> var2, Spliterator<P_IN> var3) {
            super(var1, var3, (var1x) -> {
               return Nodes.builder(var1x, var2);
            }, Nodes.ConcNode::new);
         }
      }
   }

   private abstract static class ToArrayTask<T, T_NODE extends Node<T>, K extends Nodes.ToArrayTask<T, T_NODE, K>> extends CountedCompleter<Void> {
      protected final T_NODE node;
      protected final int offset;

      ToArrayTask(T_NODE var1, int var2) {
         this.node = var1;
         this.offset = var2;
      }

      ToArrayTask(K var1, T_NODE var2, int var3) {
         super(var1);
         this.node = var2;
         this.offset = var3;
      }

      abstract void copyNodeToArray();

      abstract K makeChild(int var1, int var2);

      public void compute() {
         Nodes.ToArrayTask var1;
         int var2;
         int var3;
         for(var1 = this; var1.node.getChildCount() != 0; var1 = var1.makeChild(var3, var1.offset + var2)) {
            var1.setPendingCount(var1.node.getChildCount() - 1);
            var2 = 0;

            for(var3 = 0; var3 < var1.node.getChildCount() - 1; ++var3) {
               Nodes.ToArrayTask var4 = var1.makeChild(var3, var1.offset + var2);
               var2 = (int)((long)var2 + var4.node.count());
               var4.fork();
            }
         }

         var1.copyNodeToArray();
         var1.propagateCompletion();
      }

      private static final class OfDouble extends Nodes.ToArrayTask.OfPrimitive<Double, DoubleConsumer, double[], Spliterator.OfDouble, Node.OfDouble> {
         private OfDouble(Node.OfDouble var1, double[] var2, int var3) {
            super(var1, var2, var3, null);
         }

         // $FF: synthetic method
         OfDouble(Node.OfDouble var1, double[] var2, int var3, Object var4) {
            this(var1, var2, var3);
         }
      }

      private static final class OfLong extends Nodes.ToArrayTask.OfPrimitive<Long, LongConsumer, long[], Spliterator.OfLong, Node.OfLong> {
         private OfLong(Node.OfLong var1, long[] var2, int var3) {
            super(var1, var2, var3, null);
         }

         // $FF: synthetic method
         OfLong(Node.OfLong var1, long[] var2, int var3, Object var4) {
            this(var1, var2, var3);
         }
      }

      private static final class OfInt extends Nodes.ToArrayTask.OfPrimitive<Integer, IntConsumer, int[], Spliterator.OfInt, Node.OfInt> {
         private OfInt(Node.OfInt var1, int[] var2, int var3) {
            super(var1, var2, var3, null);
         }

         // $FF: synthetic method
         OfInt(Node.OfInt var1, int[] var2, int var3, Object var4) {
            this(var1, var2, var3);
         }
      }

      private static class OfPrimitive<T, T_CONS, T_ARR, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>, T_NODE extends Node.OfPrimitive<T, T_CONS, T_ARR, T_SPLITR, T_NODE>> extends Nodes.ToArrayTask<T, T_NODE, Nodes.ToArrayTask.OfPrimitive<T, T_CONS, T_ARR, T_SPLITR, T_NODE>> {
         private final T_ARR array;

         private OfPrimitive(T_NODE var1, T_ARR var2, int var3) {
            super(var1, var3);
            this.array = var2;
         }

         private OfPrimitive(Nodes.ToArrayTask.OfPrimitive<T, T_CONS, T_ARR, T_SPLITR, T_NODE> var1, T_NODE var2, int var3) {
            super(var1, var2, var3);
            this.array = var1.array;
         }

         Nodes.ToArrayTask.OfPrimitive<T, T_CONS, T_ARR, T_SPLITR, T_NODE> makeChild(int var1, int var2) {
            return new Nodes.ToArrayTask.OfPrimitive(this, ((Node.OfPrimitive)this.node).getChild(var1), var2);
         }

         void copyNodeToArray() {
            ((Node.OfPrimitive)this.node).copyInto(this.array, this.offset);
         }

         // $FF: synthetic method
         OfPrimitive(Node.OfPrimitive var1, Object var2, int var3, Object var4) {
            this(var1, var2, var3);
         }
      }

      private static final class OfRef<T> extends Nodes.ToArrayTask<T, Node<T>, Nodes.ToArrayTask.OfRef<T>> {
         private final T[] array;

         private OfRef(Node<T> var1, T[] var2, int var3) {
            super(var1, var3);
            this.array = var2;
         }

         private OfRef(Nodes.ToArrayTask.OfRef<T> var1, Node<T> var2, int var3) {
            super(var1, var2, var3);
            this.array = var1.array;
         }

         Nodes.ToArrayTask.OfRef<T> makeChild(int var1, int var2) {
            return new Nodes.ToArrayTask.OfRef(this, this.node.getChild(var1), var2);
         }

         void copyNodeToArray() {
            this.node.copyInto(this.array, this.offset);
         }

         // $FF: synthetic method
         OfRef(Node var1, Object[] var2, int var3, Object var4) {
            this(var1, var2, var3);
         }
      }
   }

   private abstract static class SizedCollectorTask<P_IN, P_OUT, T_SINK extends Sink<P_OUT>, K extends Nodes.SizedCollectorTask<P_IN, P_OUT, T_SINK, K>> extends CountedCompleter<Void> implements Sink<P_OUT> {
      protected final Spliterator<P_IN> spliterator;
      protected final PipelineHelper<P_OUT> helper;
      protected final long targetSize;
      protected long offset;
      protected long length;
      protected int index;
      protected int fence;

      SizedCollectorTask(Spliterator<P_IN> var1, PipelineHelper<P_OUT> var2, int var3) {
         assert var1.hasCharacteristics(16384);

         this.spliterator = var1;
         this.helper = var2;
         this.targetSize = AbstractTask.suggestTargetSize(var1.estimateSize());
         this.offset = 0L;
         this.length = (long)var3;
      }

      SizedCollectorTask(K var1, Spliterator<P_IN> var2, long var3, long var5, int var7) {
         super(var1);

         assert var2.hasCharacteristics(16384);

         this.spliterator = var2;
         this.helper = var1.helper;
         this.targetSize = var1.targetSize;
         this.offset = var3;
         this.length = var5;
         if (var3 < 0L || var5 < 0L || var3 + var5 - 1L >= (long)var7) {
            throw new IllegalArgumentException(String.format("offset and length interval [%d, %d + %d) is not within array size interval [0, %d)", var3, var3, var5, var7));
         }
      }

      public void compute() {
         Nodes.SizedCollectorTask var1 = this;

         Spliterator var2;
         Spliterator var3;
         long var4;
         for(var2 = this.spliterator; var2.estimateSize() > var1.targetSize && (var3 = var2.trySplit()) != null; var1 = var1.makeChild(var2, var1.offset + var4, var1.length - var4)) {
            var1.setPendingCount(1);
            var4 = var3.estimateSize();
            var1.makeChild(var3, var1.offset, var4).fork();
         }

         assert var1.offset + var1.length < 2147483639L;

         var1.helper.wrapAndCopyInto(var1, var2);
         var1.propagateCompletion();
      }

      abstract K makeChild(Spliterator<P_IN> var1, long var2, long var4);

      public void begin(long var1) {
         if (var1 > this.length) {
            throw new IllegalStateException("size passed to Sink.begin exceeds array length");
         } else {
            this.index = (int)this.offset;
            this.fence = this.index + (int)this.length;
         }
      }

      static final class OfDouble<P_IN> extends Nodes.SizedCollectorTask<P_IN, Double, Sink.OfDouble, Nodes.SizedCollectorTask.OfDouble<P_IN>> implements Sink.OfDouble {
         private final double[] array;

         OfDouble(Spliterator<P_IN> var1, PipelineHelper<Double> var2, double[] var3) {
            super(var1, var2, var3.length);
            this.array = var3;
         }

         OfDouble(Nodes.SizedCollectorTask.OfDouble<P_IN> var1, Spliterator<P_IN> var2, long var3, long var5) {
            super(var1, var2, var3, var5, var1.array.length);
            this.array = var1.array;
         }

         Nodes.SizedCollectorTask.OfDouble<P_IN> makeChild(Spliterator<P_IN> var1, long var2, long var4) {
            return new Nodes.SizedCollectorTask.OfDouble(this, var1, var2, var4);
         }

         public void accept(double var1) {
            if (this.index >= this.fence) {
               throw new IndexOutOfBoundsException(Integer.toString(this.index));
            } else {
               this.array[this.index++] = var1;
            }
         }
      }

      static final class OfLong<P_IN> extends Nodes.SizedCollectorTask<P_IN, Long, Sink.OfLong, Nodes.SizedCollectorTask.OfLong<P_IN>> implements Sink.OfLong {
         private final long[] array;

         OfLong(Spliterator<P_IN> var1, PipelineHelper<Long> var2, long[] var3) {
            super(var1, var2, var3.length);
            this.array = var3;
         }

         OfLong(Nodes.SizedCollectorTask.OfLong<P_IN> var1, Spliterator<P_IN> var2, long var3, long var5) {
            super(var1, var2, var3, var5, var1.array.length);
            this.array = var1.array;
         }

         Nodes.SizedCollectorTask.OfLong<P_IN> makeChild(Spliterator<P_IN> var1, long var2, long var4) {
            return new Nodes.SizedCollectorTask.OfLong(this, var1, var2, var4);
         }

         public void accept(long var1) {
            if (this.index >= this.fence) {
               throw new IndexOutOfBoundsException(Integer.toString(this.index));
            } else {
               this.array[this.index++] = var1;
            }
         }
      }

      static final class OfInt<P_IN> extends Nodes.SizedCollectorTask<P_IN, Integer, Sink.OfInt, Nodes.SizedCollectorTask.OfInt<P_IN>> implements Sink.OfInt {
         private final int[] array;

         OfInt(Spliterator<P_IN> var1, PipelineHelper<Integer> var2, int[] var3) {
            super(var1, var2, var3.length);
            this.array = var3;
         }

         OfInt(Nodes.SizedCollectorTask.OfInt<P_IN> var1, Spliterator<P_IN> var2, long var3, long var5) {
            super(var1, var2, var3, var5, var1.array.length);
            this.array = var1.array;
         }

         Nodes.SizedCollectorTask.OfInt<P_IN> makeChild(Spliterator<P_IN> var1, long var2, long var4) {
            return new Nodes.SizedCollectorTask.OfInt(this, var1, var2, var4);
         }

         public void accept(int var1) {
            if (this.index >= this.fence) {
               throw new IndexOutOfBoundsException(Integer.toString(this.index));
            } else {
               this.array[this.index++] = var1;
            }
         }
      }

      static final class OfRef<P_IN, P_OUT> extends Nodes.SizedCollectorTask<P_IN, P_OUT, Sink<P_OUT>, Nodes.SizedCollectorTask.OfRef<P_IN, P_OUT>> implements Sink<P_OUT> {
         private final P_OUT[] array;

         OfRef(Spliterator<P_IN> var1, PipelineHelper<P_OUT> var2, P_OUT[] var3) {
            super(var1, var2, var3.length);
            this.array = var3;
         }

         OfRef(Nodes.SizedCollectorTask.OfRef<P_IN, P_OUT> var1, Spliterator<P_IN> var2, long var3, long var5) {
            super(var1, var2, var3, var5, var1.array.length);
            this.array = var1.array;
         }

         Nodes.SizedCollectorTask.OfRef<P_IN, P_OUT> makeChild(Spliterator<P_IN> var1, long var2, long var4) {
            return new Nodes.SizedCollectorTask.OfRef(this, var1, var2, var4);
         }

         public void accept(P_OUT var1) {
            if (this.index >= this.fence) {
               throw new IndexOutOfBoundsException(Integer.toString(this.index));
            } else {
               this.array[this.index++] = var1;
            }
         }
      }
   }

   private static final class DoubleSpinedNodeBuilder extends SpinedBuffer.OfDouble implements Node.OfDouble, Node.Builder.OfDouble {
      private boolean building = false;

      DoubleSpinedNodeBuilder() {
      }

      public Spliterator.OfDouble spliterator() {
         assert !this.building : "during building";

         return super.spliterator();
      }

      public void forEach(DoubleConsumer var1) {
         assert !this.building : "during building";

         super.forEach(var1);
      }

      public void begin(long var1) {
         assert !this.building : "was already building";

         this.building = true;
         this.clear();
         this.ensureCapacity(var1);
      }

      public void accept(double var1) {
         assert this.building : "not building";

         super.accept(var1);
      }

      public void end() {
         assert this.building : "was not building";

         this.building = false;
      }

      public void copyInto(double[] var1, int var2) {
         assert !this.building : "during building";

         super.copyInto(var1, var2);
      }

      public double[] asPrimitiveArray() {
         assert !this.building : "during building";

         return (double[])super.asPrimitiveArray();
      }

      public Node.OfDouble build() {
         assert !this.building : "during building";

         return this;
      }
   }

   private static final class LongSpinedNodeBuilder extends SpinedBuffer.OfLong implements Node.OfLong, Node.Builder.OfLong {
      private boolean building = false;

      LongSpinedNodeBuilder() {
      }

      public Spliterator.OfLong spliterator() {
         assert !this.building : "during building";

         return super.spliterator();
      }

      public void forEach(LongConsumer var1) {
         assert !this.building : "during building";

         super.forEach(var1);
      }

      public void begin(long var1) {
         assert !this.building : "was already building";

         this.building = true;
         this.clear();
         this.ensureCapacity(var1);
      }

      public void accept(long var1) {
         assert this.building : "not building";

         super.accept(var1);
      }

      public void end() {
         assert this.building : "was not building";

         this.building = false;
      }

      public void copyInto(long[] var1, int var2) {
         assert !this.building : "during building";

         super.copyInto(var1, var2);
      }

      public long[] asPrimitiveArray() {
         assert !this.building : "during building";

         return (long[])super.asPrimitiveArray();
      }

      public Node.OfLong build() {
         assert !this.building : "during building";

         return this;
      }
   }

   private static final class IntSpinedNodeBuilder extends SpinedBuffer.OfInt implements Node.OfInt, Node.Builder.OfInt {
      private boolean building = false;

      IntSpinedNodeBuilder() {
      }

      public Spliterator.OfInt spliterator() {
         assert !this.building : "during building";

         return super.spliterator();
      }

      public void forEach(IntConsumer var1) {
         assert !this.building : "during building";

         super.forEach(var1);
      }

      public void begin(long var1) {
         assert !this.building : "was already building";

         this.building = true;
         this.clear();
         this.ensureCapacity(var1);
      }

      public void accept(int var1) {
         assert this.building : "not building";

         super.accept(var1);
      }

      public void end() {
         assert this.building : "was not building";

         this.building = false;
      }

      public void copyInto(int[] var1, int var2) throws IndexOutOfBoundsException {
         assert !this.building : "during building";

         super.copyInto(var1, var2);
      }

      public int[] asPrimitiveArray() {
         assert !this.building : "during building";

         return (int[])super.asPrimitiveArray();
      }

      public Node.OfInt build() {
         assert !this.building : "during building";

         return this;
      }
   }

   private static final class DoubleFixedNodeBuilder extends Nodes.DoubleArrayNode implements Node.Builder.OfDouble {
      DoubleFixedNodeBuilder(long var1) {
         super(var1);

         assert var1 < 2147483639L;

      }

      public Node.OfDouble build() {
         if (this.curSize < this.array.length) {
            throw new IllegalStateException(String.format("Current size %d is less than fixed size %d", this.curSize, this.array.length));
         } else {
            return this;
         }
      }

      public void begin(long var1) {
         if (var1 != (long)this.array.length) {
            throw new IllegalStateException(String.format("Begin size %d is not equal to fixed size %d", var1, this.array.length));
         } else {
            this.curSize = 0;
         }
      }

      public void accept(double var1) {
         if (this.curSize < this.array.length) {
            this.array[this.curSize++] = var1;
         } else {
            throw new IllegalStateException(String.format("Accept exceeded fixed size of %d", this.array.length));
         }
      }

      public void end() {
         if (this.curSize < this.array.length) {
            throw new IllegalStateException(String.format("End size %d is less than fixed size %d", this.curSize, this.array.length));
         }
      }

      public String toString() {
         return String.format("DoubleFixedNodeBuilder[%d][%s]", this.array.length - this.curSize, Arrays.toString(this.array));
      }
   }

   private static final class LongFixedNodeBuilder extends Nodes.LongArrayNode implements Node.Builder.OfLong {
      LongFixedNodeBuilder(long var1) {
         super(var1);

         assert var1 < 2147483639L;

      }

      public Node.OfLong build() {
         if (this.curSize < this.array.length) {
            throw new IllegalStateException(String.format("Current size %d is less than fixed size %d", this.curSize, this.array.length));
         } else {
            return this;
         }
      }

      public void begin(long var1) {
         if (var1 != (long)this.array.length) {
            throw new IllegalStateException(String.format("Begin size %d is not equal to fixed size %d", var1, this.array.length));
         } else {
            this.curSize = 0;
         }
      }

      public void accept(long var1) {
         if (this.curSize < this.array.length) {
            this.array[this.curSize++] = var1;
         } else {
            throw new IllegalStateException(String.format("Accept exceeded fixed size of %d", this.array.length));
         }
      }

      public void end() {
         if (this.curSize < this.array.length) {
            throw new IllegalStateException(String.format("End size %d is less than fixed size %d", this.curSize, this.array.length));
         }
      }

      public String toString() {
         return String.format("LongFixedNodeBuilder[%d][%s]", this.array.length - this.curSize, Arrays.toString(this.array));
      }
   }

   private static final class IntFixedNodeBuilder extends Nodes.IntArrayNode implements Node.Builder.OfInt {
      IntFixedNodeBuilder(long var1) {
         super(var1);

         assert var1 < 2147483639L;

      }

      public Node.OfInt build() {
         if (this.curSize < this.array.length) {
            throw new IllegalStateException(String.format("Current size %d is less than fixed size %d", this.curSize, this.array.length));
         } else {
            return this;
         }
      }

      public void begin(long var1) {
         if (var1 != (long)this.array.length) {
            throw new IllegalStateException(String.format("Begin size %d is not equal to fixed size %d", var1, this.array.length));
         } else {
            this.curSize = 0;
         }
      }

      public void accept(int var1) {
         if (this.curSize < this.array.length) {
            this.array[this.curSize++] = var1;
         } else {
            throw new IllegalStateException(String.format("Accept exceeded fixed size of %d", this.array.length));
         }
      }

      public void end() {
         if (this.curSize < this.array.length) {
            throw new IllegalStateException(String.format("End size %d is less than fixed size %d", this.curSize, this.array.length));
         }
      }

      public String toString() {
         return String.format("IntFixedNodeBuilder[%d][%s]", this.array.length - this.curSize, Arrays.toString(this.array));
      }
   }

   private static class DoubleArrayNode implements Node.OfDouble {
      final double[] array;
      int curSize;

      DoubleArrayNode(long var1) {
         if (var1 >= 2147483639L) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
         } else {
            this.array = new double[(int)var1];
            this.curSize = 0;
         }
      }

      DoubleArrayNode(double[] var1) {
         this.array = var1;
         this.curSize = var1.length;
      }

      public Spliterator.OfDouble spliterator() {
         return Arrays.spliterator((double[])this.array, 0, this.curSize);
      }

      public double[] asPrimitiveArray() {
         return this.array.length == this.curSize ? this.array : Arrays.copyOf(this.array, this.curSize);
      }

      public void copyInto(double[] var1, int var2) {
         System.arraycopy(this.array, 0, var1, var2, this.curSize);
      }

      public long count() {
         return (long)this.curSize;
      }

      public void forEach(DoubleConsumer var1) {
         for(int var2 = 0; var2 < this.curSize; ++var2) {
            var1.accept(this.array[var2]);
         }

      }

      public String toString() {
         return String.format("DoubleArrayNode[%d][%s]", this.array.length - this.curSize, Arrays.toString(this.array));
      }
   }

   private static class LongArrayNode implements Node.OfLong {
      final long[] array;
      int curSize;

      LongArrayNode(long var1) {
         if (var1 >= 2147483639L) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
         } else {
            this.array = new long[(int)var1];
            this.curSize = 0;
         }
      }

      LongArrayNode(long[] var1) {
         this.array = var1;
         this.curSize = var1.length;
      }

      public Spliterator.OfLong spliterator() {
         return Arrays.spliterator((long[])this.array, 0, this.curSize);
      }

      public long[] asPrimitiveArray() {
         return this.array.length == this.curSize ? this.array : Arrays.copyOf(this.array, this.curSize);
      }

      public void copyInto(long[] var1, int var2) {
         System.arraycopy(this.array, 0, var1, var2, this.curSize);
      }

      public long count() {
         return (long)this.curSize;
      }

      public void forEach(LongConsumer var1) {
         for(int var2 = 0; var2 < this.curSize; ++var2) {
            var1.accept(this.array[var2]);
         }

      }

      public String toString() {
         return String.format("LongArrayNode[%d][%s]", this.array.length - this.curSize, Arrays.toString(this.array));
      }
   }

   private static class IntArrayNode implements Node.OfInt {
      final int[] array;
      int curSize;

      IntArrayNode(long var1) {
         if (var1 >= 2147483639L) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
         } else {
            this.array = new int[(int)var1];
            this.curSize = 0;
         }
      }

      IntArrayNode(int[] var1) {
         this.array = var1;
         this.curSize = var1.length;
      }

      public Spliterator.OfInt spliterator() {
         return Arrays.spliterator((int[])this.array, 0, this.curSize);
      }

      public int[] asPrimitiveArray() {
         return this.array.length == this.curSize ? this.array : Arrays.copyOf(this.array, this.curSize);
      }

      public void copyInto(int[] var1, int var2) {
         System.arraycopy(this.array, 0, var1, var2, this.curSize);
      }

      public long count() {
         return (long)this.curSize;
      }

      public void forEach(IntConsumer var1) {
         for(int var2 = 0; var2 < this.curSize; ++var2) {
            var1.accept(this.array[var2]);
         }

      }

      public String toString() {
         return String.format("IntArrayNode[%d][%s]", this.array.length - this.curSize, Arrays.toString(this.array));
      }
   }

   private static final class SpinedNodeBuilder<T> extends SpinedBuffer<T> implements Node<T>, Node.Builder<T> {
      private boolean building = false;

      SpinedNodeBuilder() {
      }

      public Spliterator<T> spliterator() {
         assert !this.building : "during building";

         return super.spliterator();
      }

      public void forEach(Consumer<? super T> var1) {
         assert !this.building : "during building";

         super.forEach(var1);
      }

      public void begin(long var1) {
         assert !this.building : "was already building";

         this.building = true;
         this.clear();
         this.ensureCapacity(var1);
      }

      public void accept(T var1) {
         assert this.building : "not building";

         super.accept(var1);
      }

      public void end() {
         assert this.building : "was not building";

         this.building = false;
      }

      public void copyInto(T[] var1, int var2) {
         assert !this.building : "during building";

         super.copyInto(var1, var2);
      }

      public T[] asArray(IntFunction<T[]> var1) {
         assert !this.building : "during building";

         return super.asArray(var1);
      }

      public Node<T> build() {
         assert !this.building : "during building";

         return this;
      }
   }

   private static final class FixedNodeBuilder<T> extends Nodes.ArrayNode<T> implements Node.Builder<T> {
      FixedNodeBuilder(long var1, IntFunction<T[]> var3) {
         super(var1, var3);

         assert var1 < 2147483639L;

      }

      public Node<T> build() {
         if (this.curSize < this.array.length) {
            throw new IllegalStateException(String.format("Current size %d is less than fixed size %d", this.curSize, this.array.length));
         } else {
            return this;
         }
      }

      public void begin(long var1) {
         if (var1 != (long)this.array.length) {
            throw new IllegalStateException(String.format("Begin size %d is not equal to fixed size %d", var1, this.array.length));
         } else {
            this.curSize = 0;
         }
      }

      public void accept(T var1) {
         if (this.curSize < this.array.length) {
            this.array[this.curSize++] = var1;
         } else {
            throw new IllegalStateException(String.format("Accept exceeded fixed size of %d", this.array.length));
         }
      }

      public void end() {
         if (this.curSize < this.array.length) {
            throw new IllegalStateException(String.format("End size %d is less than fixed size %d", this.curSize, this.array.length));
         }
      }

      public String toString() {
         return String.format("FixedNodeBuilder[%d][%s]", this.array.length - this.curSize, Arrays.toString(this.array));
      }
   }

   private abstract static class InternalNodeSpliterator<T, S extends Spliterator<T>, N extends Node<T>> implements Spliterator<T> {
      N curNode;
      int curChildIndex;
      S lastNodeSpliterator;
      S tryAdvanceSpliterator;
      Deque<N> tryAdvanceStack;

      InternalNodeSpliterator(N var1) {
         this.curNode = var1;
      }

      protected final Deque<N> initStack() {
         ArrayDeque var1 = new ArrayDeque(8);

         for(int var2 = this.curNode.getChildCount() - 1; var2 >= this.curChildIndex; --var2) {
            var1.addFirst(this.curNode.getChild(var2));
         }

         return var1;
      }

      protected final N findNextLeafNode(Deque<N> var1) {
         Node var2 = null;

         label26:
         do {
            while((var2 = (Node)var1.pollFirst()) != null) {
               if (var2.getChildCount() == 0) {
                  continue label26;
               }

               for(int var3 = var2.getChildCount() - 1; var3 >= 0; --var3) {
                  var1.addFirst(var2.getChild(var3));
               }
            }

            return null;
         } while(var2.count() <= 0L);

         return var2;
      }

      protected final boolean initTryAdvance() {
         if (this.curNode == null) {
            return false;
         } else {
            if (this.tryAdvanceSpliterator == null) {
               if (this.lastNodeSpliterator == null) {
                  this.tryAdvanceStack = this.initStack();
                  Node var1 = this.findNextLeafNode(this.tryAdvanceStack);
                  if (var1 == null) {
                     this.curNode = null;
                     return false;
                  }

                  this.tryAdvanceSpliterator = var1.spliterator();
               } else {
                  this.tryAdvanceSpliterator = this.lastNodeSpliterator;
               }
            }

            return true;
         }
      }

      public final S trySplit() {
         if (this.curNode != null && this.tryAdvanceSpliterator == null) {
            if (this.lastNodeSpliterator != null) {
               return this.lastNodeSpliterator.trySplit();
            } else if (this.curChildIndex < this.curNode.getChildCount() - 1) {
               return this.curNode.getChild(this.curChildIndex++).spliterator();
            } else {
               this.curNode = this.curNode.getChild(this.curChildIndex);
               if (this.curNode.getChildCount() == 0) {
                  this.lastNodeSpliterator = this.curNode.spliterator();
                  return this.lastNodeSpliterator.trySplit();
               } else {
                  this.curChildIndex = 0;
                  return this.curNode.getChild(this.curChildIndex++).spliterator();
               }
            }
         } else {
            return null;
         }
      }

      public final long estimateSize() {
         if (this.curNode == null) {
            return 0L;
         } else if (this.lastNodeSpliterator != null) {
            return this.lastNodeSpliterator.estimateSize();
         } else {
            long var1 = 0L;

            for(int var3 = this.curChildIndex; var3 < this.curNode.getChildCount(); ++var3) {
               var1 += this.curNode.getChild(var3).count();
            }

            return var1;
         }
      }

      public final int characteristics() {
         return 64;
      }

      private static final class OfDouble extends Nodes.InternalNodeSpliterator.OfPrimitive<Double, DoubleConsumer, double[], Spliterator.OfDouble, Node.OfDouble> implements Spliterator.OfDouble {
         OfDouble(Node.OfDouble var1) {
            super(var1);
         }
      }

      private static final class OfLong extends Nodes.InternalNodeSpliterator.OfPrimitive<Long, LongConsumer, long[], Spliterator.OfLong, Node.OfLong> implements Spliterator.OfLong {
         OfLong(Node.OfLong var1) {
            super(var1);
         }
      }

      private static final class OfInt extends Nodes.InternalNodeSpliterator.OfPrimitive<Integer, IntConsumer, int[], Spliterator.OfInt, Node.OfInt> implements Spliterator.OfInt {
         OfInt(Node.OfInt var1) {
            super(var1);
         }
      }

      private abstract static class OfPrimitive<T, T_CONS, T_ARR, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>, N extends Node.OfPrimitive<T, T_CONS, T_ARR, T_SPLITR, N>> extends Nodes.InternalNodeSpliterator<T, T_SPLITR, N> implements Spliterator.OfPrimitive<T, T_CONS, T_SPLITR> {
         OfPrimitive(N var1) {
            super(var1);
         }

         public boolean tryAdvance(T_CONS var1) {
            if (!this.initTryAdvance()) {
               return false;
            } else {
               boolean var2 = ((Spliterator.OfPrimitive)this.tryAdvanceSpliterator).tryAdvance(var1);
               if (!var2) {
                  if (this.lastNodeSpliterator == null) {
                     Node.OfPrimitive var3 = (Node.OfPrimitive)this.findNextLeafNode(this.tryAdvanceStack);
                     if (var3 != null) {
                        this.tryAdvanceSpliterator = var3.spliterator();
                        return ((Spliterator.OfPrimitive)this.tryAdvanceSpliterator).tryAdvance(var1);
                     }
                  }

                  this.curNode = null;
               }

               return var2;
            }
         }

         public void forEachRemaining(T_CONS var1) {
            if (this.curNode != null) {
               if (this.tryAdvanceSpliterator == null) {
                  if (this.lastNodeSpliterator == null) {
                     Deque var2 = this.initStack();

                     Node.OfPrimitive var3;
                     while((var3 = (Node.OfPrimitive)this.findNextLeafNode(var2)) != null) {
                        var3.forEach(var1);
                     }

                     this.curNode = null;
                  } else {
                     ((Spliterator.OfPrimitive)this.lastNodeSpliterator).forEachRemaining(var1);
                  }
               } else {
                  while(true) {
                     if (this.tryAdvance(var1)) {
                        continue;
                     }
                  }
               }

            }
         }
      }

      private static final class OfRef<T> extends Nodes.InternalNodeSpliterator<T, Spliterator<T>, Node<T>> {
         OfRef(Node<T> var1) {
            super(var1);
         }

         public boolean tryAdvance(Consumer<? super T> var1) {
            if (!this.initTryAdvance()) {
               return false;
            } else {
               boolean var2 = this.tryAdvanceSpliterator.tryAdvance(var1);
               if (!var2) {
                  if (this.lastNodeSpliterator == null) {
                     Node var3 = this.findNextLeafNode(this.tryAdvanceStack);
                     if (var3 != null) {
                        this.tryAdvanceSpliterator = var3.spliterator();
                        return this.tryAdvanceSpliterator.tryAdvance(var1);
                     }
                  }

                  this.curNode = null;
               }

               return var2;
            }
         }

         public void forEachRemaining(Consumer<? super T> var1) {
            if (this.curNode != null) {
               if (this.tryAdvanceSpliterator == null) {
                  if (this.lastNodeSpliterator == null) {
                     Deque var2 = this.initStack();

                     Node var3;
                     while((var3 = this.findNextLeafNode(var2)) != null) {
                        var3.forEach(var1);
                     }

                     this.curNode = null;
                  } else {
                     this.lastNodeSpliterator.forEachRemaining(var1);
                  }
               } else {
                  while(true) {
                     if (this.tryAdvance(var1)) {
                        continue;
                     }
                  }
               }

            }
         }
      }
   }

   static final class ConcNode<T> extends Nodes.AbstractConcNode<T, Node<T>> implements Node<T> {
      ConcNode(Node<T> var1, Node<T> var2) {
         super(var1, var2);
      }

      public Spliterator<T> spliterator() {
         return new Nodes.InternalNodeSpliterator.OfRef(this);
      }

      public void copyInto(T[] var1, int var2) {
         Objects.requireNonNull(var1);
         this.left.copyInto(var1, var2);
         this.right.copyInto(var1, var2 + (int)this.left.count());
      }

      public T[] asArray(IntFunction<T[]> var1) {
         long var2 = this.count();
         if (var2 >= 2147483639L) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
         } else {
            Object[] var4 = (Object[])var1.apply((int)var2);
            this.copyInto(var4, 0);
            return var4;
         }
      }

      public void forEach(Consumer<? super T> var1) {
         this.left.forEach(var1);
         this.right.forEach(var1);
      }

      public Node<T> truncate(long var1, long var3, IntFunction<T[]> var5) {
         if (var1 == 0L && var3 == this.count()) {
            return this;
         } else {
            long var6 = this.left.count();
            if (var1 >= var6) {
               return this.right.truncate(var1 - var6, var3 - var6, var5);
            } else {
               return var3 <= var6 ? this.left.truncate(var1, var3, var5) : Nodes.conc(this.getShape(), this.left.truncate(var1, var6, var5), this.right.truncate(0L, var3 - var6, var5));
            }
         }
      }

      public String toString() {
         return this.count() < 32L ? String.format("ConcNode[%s.%s]", this.left, this.right) : String.format("ConcNode[size=%d]", this.count());
      }

      static final class OfDouble extends Nodes.ConcNode.OfPrimitive<Double, DoubleConsumer, double[], Spliterator.OfDouble, Node.OfDouble> implements Node.OfDouble {
         OfDouble(Node.OfDouble var1, Node.OfDouble var2) {
            super(var1, var2);
         }

         public Spliterator.OfDouble spliterator() {
            return new Nodes.InternalNodeSpliterator.OfDouble(this);
         }
      }

      static final class OfLong extends Nodes.ConcNode.OfPrimitive<Long, LongConsumer, long[], Spliterator.OfLong, Node.OfLong> implements Node.OfLong {
         OfLong(Node.OfLong var1, Node.OfLong var2) {
            super(var1, var2);
         }

         public Spliterator.OfLong spliterator() {
            return new Nodes.InternalNodeSpliterator.OfLong(this);
         }
      }

      static final class OfInt extends Nodes.ConcNode.OfPrimitive<Integer, IntConsumer, int[], Spliterator.OfInt, Node.OfInt> implements Node.OfInt {
         OfInt(Node.OfInt var1, Node.OfInt var2) {
            super(var1, var2);
         }

         public Spliterator.OfInt spliterator() {
            return new Nodes.InternalNodeSpliterator.OfInt(this);
         }
      }

      private abstract static class OfPrimitive<E, T_CONS, T_ARR, T_SPLITR extends Spliterator.OfPrimitive<E, T_CONS, T_SPLITR>, T_NODE extends Node.OfPrimitive<E, T_CONS, T_ARR, T_SPLITR, T_NODE>> extends Nodes.AbstractConcNode<E, T_NODE> implements Node.OfPrimitive<E, T_CONS, T_ARR, T_SPLITR, T_NODE> {
         OfPrimitive(T_NODE var1, T_NODE var2) {
            super(var1, var2);
         }

         public void forEach(T_CONS var1) {
            ((Node.OfPrimitive)this.left).forEach(var1);
            ((Node.OfPrimitive)this.right).forEach(var1);
         }

         public void copyInto(T_ARR var1, int var2) {
            ((Node.OfPrimitive)this.left).copyInto(var1, var2);
            ((Node.OfPrimitive)this.right).copyInto(var1, var2 + (int)((Node.OfPrimitive)this.left).count());
         }

         public T_ARR asPrimitiveArray() {
            long var1 = this.count();
            if (var1 >= 2147483639L) {
               throw new IllegalArgumentException("Stream size exceeds max array size");
            } else {
               Object var3 = this.newArray((int)var1);
               this.copyInto(var3, 0);
               return var3;
            }
         }

         public String toString() {
            return this.count() < 32L ? String.format("%s[%s.%s]", this.getClass().getName(), this.left, this.right) : String.format("%s[size=%d]", this.getClass().getName(), this.count());
         }
      }
   }

   private abstract static class AbstractConcNode<T, T_NODE extends Node<T>> implements Node<T> {
      protected final T_NODE left;
      protected final T_NODE right;
      private final long size;

      AbstractConcNode(T_NODE var1, T_NODE var2) {
         this.left = var1;
         this.right = var2;
         this.size = var1.count() + var2.count();
      }

      public int getChildCount() {
         return 2;
      }

      public T_NODE getChild(int var1) {
         if (var1 == 0) {
            return this.left;
         } else if (var1 == 1) {
            return this.right;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public long count() {
         return this.size;
      }
   }

   private static final class CollectionNode<T> implements Node<T> {
      private final Collection<T> c;

      CollectionNode(Collection<T> var1) {
         this.c = var1;
      }

      public Spliterator<T> spliterator() {
         return this.c.stream().spliterator();
      }

      public void copyInto(T[] var1, int var2) {
         Object var4;
         for(Iterator var3 = this.c.iterator(); var3.hasNext(); var1[var2++] = var4) {
            var4 = var3.next();
         }

      }

      public T[] asArray(IntFunction<T[]> var1) {
         return this.c.toArray((Object[])var1.apply(this.c.size()));
      }

      public long count() {
         return (long)this.c.size();
      }

      public void forEach(Consumer<? super T> var1) {
         this.c.forEach(var1);
      }

      public String toString() {
         return String.format("CollectionNode[%d][%s]", this.c.size(), this.c);
      }
   }

   private static class ArrayNode<T> implements Node<T> {
      final T[] array;
      int curSize;

      ArrayNode(long var1, IntFunction<T[]> var3) {
         if (var1 >= 2147483639L) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
         } else {
            this.array = (Object[])var3.apply((int)var1);
            this.curSize = 0;
         }
      }

      ArrayNode(T[] var1) {
         this.array = var1;
         this.curSize = var1.length;
      }

      public Spliterator<T> spliterator() {
         return Arrays.spliterator((Object[])this.array, 0, this.curSize);
      }

      public void copyInto(T[] var1, int var2) {
         System.arraycopy(this.array, 0, var1, var2, this.curSize);
      }

      public T[] asArray(IntFunction<T[]> var1) {
         if (this.array.length == this.curSize) {
            return this.array;
         } else {
            throw new IllegalStateException();
         }
      }

      public long count() {
         return (long)this.curSize;
      }

      public void forEach(Consumer<? super T> var1) {
         for(int var2 = 0; var2 < this.curSize; ++var2) {
            var1.accept(this.array[var2]);
         }

      }

      public String toString() {
         return String.format("ArrayNode[%d][%s]", this.array.length - this.curSize, Arrays.toString(this.array));
      }
   }

   private abstract static class EmptyNode<T, T_ARR, T_CONS> implements Node<T> {
      EmptyNode() {
      }

      public T[] asArray(IntFunction<T[]> var1) {
         return (Object[])var1.apply(0);
      }

      public void copyInto(T_ARR var1, int var2) {
      }

      public long count() {
         return 0L;
      }

      public void forEach(T_CONS var1) {
      }

      private static final class OfDouble extends Nodes.EmptyNode<Double, double[], DoubleConsumer> implements Node.OfDouble {
         OfDouble() {
         }

         public Spliterator.OfDouble spliterator() {
            return Spliterators.emptyDoubleSpliterator();
         }

         public double[] asPrimitiveArray() {
            return Nodes.EMPTY_DOUBLE_ARRAY;
         }
      }

      private static final class OfLong extends Nodes.EmptyNode<Long, long[], LongConsumer> implements Node.OfLong {
         OfLong() {
         }

         public Spliterator.OfLong spliterator() {
            return Spliterators.emptyLongSpliterator();
         }

         public long[] asPrimitiveArray() {
            return Nodes.EMPTY_LONG_ARRAY;
         }
      }

      private static final class OfInt extends Nodes.EmptyNode<Integer, int[], IntConsumer> implements Node.OfInt {
         OfInt() {
         }

         public Spliterator.OfInt spliterator() {
            return Spliterators.emptyIntSpliterator();
         }

         public int[] asPrimitiveArray() {
            return Nodes.EMPTY_INT_ARRAY;
         }
      }

      private static class OfRef<T> extends Nodes.EmptyNode<T, T[], Consumer<? super T>> {
         private OfRef() {
         }

         public Spliterator<T> spliterator() {
            return Spliterators.emptySpliterator();
         }

         // $FF: synthetic method
         OfRef(Object var1) {
            this();
         }
      }
   }
}
