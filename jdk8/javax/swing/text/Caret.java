package javax.swing.text;

import java.awt.Graphics;
import java.awt.Point;
import javax.swing.event.ChangeListener;

public interface Caret {
   void install(JTextComponent var1);

   void deinstall(JTextComponent var1);

   void paint(Graphics var1);

   void addChangeListener(ChangeListener var1);

   void removeChangeListener(ChangeListener var1);

   boolean isVisible();

   void setVisible(boolean var1);

   boolean isSelectionVisible();

   void setSelectionVisible(boolean var1);

   void setMagicCaretPosition(Point var1);

   Point getMagicCaretPosition();

   void setBlinkRate(int var1);

   int getBlinkRate();

   int getDot();

   int getMark();

   void setDot(int var1);

   void moveDot(int var1);
}
