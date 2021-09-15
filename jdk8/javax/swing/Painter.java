package javax.swing;

import java.awt.Graphics2D;

public interface Painter<T> {
   void paint(Graphics2D var1, T var2, int var3, int var4);
}
