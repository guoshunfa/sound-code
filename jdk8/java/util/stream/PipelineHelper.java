package java.util.stream;

import java.util.Spliterator;
import java.util.function.IntFunction;

abstract class PipelineHelper<P_OUT> {
   abstract StreamShape getSourceShape();

   abstract int getStreamAndOpFlags();

   abstract <P_IN> long exactOutputSizeIfKnown(Spliterator<P_IN> var1);

   abstract <P_IN, S extends Sink<P_OUT>> S wrapAndCopyInto(S var1, Spliterator<P_IN> var2);

   abstract <P_IN> void copyInto(Sink<P_IN> var1, Spliterator<P_IN> var2);

   abstract <P_IN> void copyIntoWithCancel(Sink<P_IN> var1, Spliterator<P_IN> var2);

   abstract <P_IN> Sink<P_IN> wrapSink(Sink<P_OUT> var1);

   abstract <P_IN> Spliterator<P_OUT> wrapSpliterator(Spliterator<P_IN> var1);

   abstract Node.Builder<P_OUT> makeNodeBuilder(long var1, IntFunction<P_OUT[]> var3);

   abstract <P_IN> Node<P_OUT> evaluate(Spliterator<P_IN> var1, boolean var2, IntFunction<P_OUT[]> var3);
}
