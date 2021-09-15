package java.awt.event;

import java.util.EventListener;

public interface InputMethodListener extends EventListener {
   void inputMethodTextChanged(InputMethodEvent var1);

   void caretPositionChanged(InputMethodEvent var1);
}
