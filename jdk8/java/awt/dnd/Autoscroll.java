package java.awt.dnd;

import java.awt.Insets;
import java.awt.Point;

public interface Autoscroll {
   Insets getAutoscrollInsets();

   void autoscroll(Point var1);
}
