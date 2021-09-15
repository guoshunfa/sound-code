package com.sun.java.swing.plaf.windows;

import java.awt.Graphics;
import java.awt.Window;
import javax.swing.JWindow;

class WindowsPopupWindow extends JWindow {
   static final int UNDEFINED_WINDOW_TYPE = 0;
   static final int TOOLTIP_WINDOW_TYPE = 1;
   static final int MENU_WINDOW_TYPE = 2;
   static final int SUBMENU_WINDOW_TYPE = 3;
   static final int POPUPMENU_WINDOW_TYPE = 4;
   static final int COMBOBOX_POPUP_WINDOW_TYPE = 5;
   private int windowType;

   WindowsPopupWindow(Window var1) {
      super(var1);
      this.setFocusableWindowState(false);
   }

   void setWindowType(int var1) {
      this.windowType = var1;
   }

   int getWindowType() {
      return this.windowType;
   }

   public void update(Graphics var1) {
      this.paint(var1);
   }

   public void hide() {
      super.hide();
      this.removeNotify();
   }

   public void show() {
      super.show();
      this.pack();
   }
}
