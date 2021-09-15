package javax.swing.event;

import java.util.EventListener;

public interface AncestorListener extends EventListener {
   void ancestorAdded(AncestorEvent var1);

   void ancestorRemoved(AncestorEvent var1);

   void ancestorMoved(AncestorEvent var1);
}
