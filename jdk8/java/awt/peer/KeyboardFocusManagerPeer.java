package java.awt.peer;

import java.awt.Component;
import java.awt.Window;

public interface KeyboardFocusManagerPeer {
   void setCurrentFocusedWindow(Window var1);

   Window getCurrentFocusedWindow();

   void setCurrentFocusOwner(Component var1);

   Component getCurrentFocusOwner();

   void clearGlobalFocusOwner(Window var1);
}
