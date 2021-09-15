package javax.swing;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public interface MenuElement {
   void processMouseEvent(MouseEvent var1, MenuElement[] var2, MenuSelectionManager var3);

   void processKeyEvent(KeyEvent var1, MenuElement[] var2, MenuSelectionManager var3);

   void menuSelectionChanged(boolean var1);

   MenuElement[] getSubElements();

   Component getComponent();
}
