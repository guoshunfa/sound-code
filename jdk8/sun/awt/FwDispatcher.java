package sun.awt;

import java.awt.SecondaryLoop;

public interface FwDispatcher {
   boolean isDispatchThread();

   void scheduleDispatch(Runnable var1);

   SecondaryLoop createSecondaryLoop();
}
