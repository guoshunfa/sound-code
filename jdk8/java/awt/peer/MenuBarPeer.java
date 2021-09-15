package java.awt.peer;

import java.awt.Menu;

public interface MenuBarPeer extends MenuComponentPeer {
   void addMenu(Menu var1);

   void delMenu(int var1);

   void addHelpMenu(Menu var1);
}
