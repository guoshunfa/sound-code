package java.awt.event;

import java.util.EventListener;

public interface ContainerListener extends EventListener {
   void componentAdded(ContainerEvent var1);

   void componentRemoved(ContainerEvent var1);
}
