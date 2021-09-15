package java.util.stream;

import java.util.Spliterator;

interface TerminalOp<E_IN, R> {
   default StreamShape inputShape() {
      return StreamShape.REFERENCE;
   }

   default int getOpFlags() {
      return 0;
   }

   default <P_IN> R evaluateParallel(PipelineHelper<E_IN> var1, Spliterator<P_IN> var2) {
      if (Tripwire.ENABLED) {
         Tripwire.trip(this.getClass(), "{0} triggering TerminalOp.evaluateParallel serial default");
      }

      return this.evaluateSequential(var1, var2);
   }

   <P_IN> R evaluateSequential(PipelineHelper<E_IN> var1, Spliterator<P_IN> var2);
}
