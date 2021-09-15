package java.awt.peer;

import java.awt.MenuBar;
import java.awt.Rectangle;

public interface FramePeer extends WindowPeer {
   void setTitle(String var1);

   void setMenuBar(MenuBar var1);

   void setResizable(boolean var1);

   void setState(int var1);

   int getState();

   void setMaximizedBounds(Rectangle var1);

   void setBoundsPrivate(int var1, int var2, int var3, int var4);

   Rectangle getBoundsPrivate();

   void emulateActivation(boolean var1);
}
