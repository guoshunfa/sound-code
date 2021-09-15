package javax.accessibility;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusListener;

public interface AccessibleComponent {
   Color getBackground();

   void setBackground(Color var1);

   Color getForeground();

   void setForeground(Color var1);

   Cursor getCursor();

   void setCursor(Cursor var1);

   Font getFont();

   void setFont(Font var1);

   FontMetrics getFontMetrics(Font var1);

   boolean isEnabled();

   void setEnabled(boolean var1);

   boolean isVisible();

   void setVisible(boolean var1);

   boolean isShowing();

   boolean contains(Point var1);

   Point getLocationOnScreen();

   Point getLocation();

   void setLocation(Point var1);

   Rectangle getBounds();

   void setBounds(Rectangle var1);

   Dimension getSize();

   void setSize(Dimension var1);

   Accessible getAccessibleAt(Point var1);

   boolean isFocusTraversable();

   void requestFocus();

   void addFocusListener(FocusListener var1);

   void removeFocusListener(FocusListener var1);
}
