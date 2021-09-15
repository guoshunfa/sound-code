package javax.swing;

import java.awt.Component;
import java.awt.Container;

public interface RootPaneContainer {
   JRootPane getRootPane();

   void setContentPane(Container var1);

   Container getContentPane();

   void setLayeredPane(JLayeredPane var1);

   JLayeredPane getLayeredPane();

   void setGlassPane(Component var1);

   Component getGlassPane();
}
