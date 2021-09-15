package sun.misc;

public interface SignalHandler {
   SignalHandler SIG_DFL = new NativeSignalHandler(0L);
   SignalHandler SIG_IGN = new NativeSignalHandler(1L);

   void handle(Signal var1);
}
