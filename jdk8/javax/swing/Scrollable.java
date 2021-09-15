package javax.swing;

import java.awt.Dimension;
import java.awt.Rectangle;

public interface Scrollable {
   Dimension getPreferredScrollableViewportSize();

   int getScrollableUnitIncrement(Rectangle var1, int var2, int var3);

   int getScrollableBlockIncrement(Rectangle var1, int var2, int var3);

   boolean getScrollableTracksViewportWidth();

   boolean getScrollableTracksViewportHeight();
}
