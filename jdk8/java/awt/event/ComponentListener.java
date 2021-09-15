package java.awt.event;

import java.util.EventListener;

public interface ComponentListener extends EventListener {
   void componentResized(ComponentEvent var1);

   void componentMoved(ComponentEvent var1);

   void componentShown(ComponentEvent var1);

   void componentHidden(ComponentEvent var1);
}
