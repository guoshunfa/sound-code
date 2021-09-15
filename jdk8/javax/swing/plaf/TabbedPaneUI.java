package javax.swing.plaf;

import java.awt.Rectangle;
import javax.swing.JTabbedPane;

public abstract class TabbedPaneUI extends ComponentUI {
   public abstract int tabForCoordinate(JTabbedPane var1, int var2, int var3);

   public abstract Rectangle getTabBounds(JTabbedPane var1, int var2);

   public abstract int getTabRunCount(JTabbedPane var1);
}
