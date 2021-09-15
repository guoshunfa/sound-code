package javax.swing.plaf;

import java.awt.Graphics;
import javax.swing.JSplitPane;

public abstract class SplitPaneUI extends ComponentUI {
   public abstract void resetToPreferredSizes(JSplitPane var1);

   public abstract void setDividerLocation(JSplitPane var1, int var2);

   public abstract int getDividerLocation(JSplitPane var1);

   public abstract int getMinimumDividerLocation(JSplitPane var1);

   public abstract int getMaximumDividerLocation(JSplitPane var1);

   public abstract void finishedPaintingChildren(JSplitPane var1, Graphics var2);
}
