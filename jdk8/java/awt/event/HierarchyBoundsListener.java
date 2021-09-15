package java.awt.event;

import java.util.EventListener;

public interface HierarchyBoundsListener extends EventListener {
   void ancestorMoved(HierarchyEvent var1);

   void ancestorResized(HierarchyEvent var1);
}
