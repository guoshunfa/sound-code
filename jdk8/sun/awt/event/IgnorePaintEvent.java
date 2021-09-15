package sun.awt.event;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.PaintEvent;

public class IgnorePaintEvent extends PaintEvent {
   public IgnorePaintEvent(Component var1, int var2, Rectangle var3) {
      super(var1, var2, var3);
   }
}
