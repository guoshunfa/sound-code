package java.awt.event;

import java.util.EventListener;

public interface WindowListener extends EventListener {
   void windowOpened(WindowEvent var1);

   void windowClosing(WindowEvent var1);

   void windowClosed(WindowEvent var1);

   void windowIconified(WindowEvent var1);

   void windowDeiconified(WindowEvent var1);

   void windowActivated(WindowEvent var1);

   void windowDeactivated(WindowEvent var1);
}
