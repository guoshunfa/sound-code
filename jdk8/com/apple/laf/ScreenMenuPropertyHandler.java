package com.apple.laf;

import java.awt.Font;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

interface ScreenMenuPropertyHandler {
   void setEnabled(boolean var1);

   void setFont(Font var1);

   void setLabel(String var1);

   void setIcon(Icon var1);

   void setAccelerator(KeyStroke var1);

   void setToolTipText(String var1);

   void setChildVisible(JMenuItem var1, boolean var2);

   void setIndeterminate(boolean var1);
}
