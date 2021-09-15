package java.awt.event;

import java.util.EventListener;

public interface WindowFocusListener extends EventListener {
   void windowGainedFocus(WindowEvent var1);

   void windowLostFocus(WindowEvent var1);
}
