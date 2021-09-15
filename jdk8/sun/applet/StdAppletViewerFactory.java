package sun.applet;

import java.awt.MenuBar;
import java.net.URL;
import java.util.Hashtable;

final class StdAppletViewerFactory implements AppletViewerFactory {
   public AppletViewer createAppletViewer(int var1, int var2, URL var3, Hashtable var4) {
      return new AppletViewer(var1, var2, var3, var4, System.out, this);
   }

   public MenuBar getBaseMenuBar() {
      return new MenuBar();
   }

   public boolean isStandalone() {
      return true;
   }
}
