package java.awt.event;

import java.util.EventListener;

public interface FocusListener extends EventListener {
   void focusGained(FocusEvent var1);

   void focusLost(FocusEvent var1);
}
