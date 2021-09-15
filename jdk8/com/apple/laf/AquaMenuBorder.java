package com.apple.laf;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.Box;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;

public class AquaMenuBorder implements Border, UIResource {
   public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
   }

   public boolean isBorderOpaque() {
      return false;
   }

   protected static Insets getItemInsets() {
      return new Insets(1, 5, 1, 5);
   }

   protected static Insets getEmptyInsets() {
      return new Insets(0, 0, 0, 0);
   }

   protected static Insets getPopupInsets() {
      return new Insets(4, 0, 4, 0);
   }

   public Insets getBorderInsets(Component var1) {
      if (!(var1 instanceof JPopupMenu)) {
         return getItemInsets();
      } else {
         JPopupMenu var2 = (JPopupMenu)var1;
         int var3 = var2.getComponentCount();
         if (var3 > 0) {
            Component var4 = var2.getComponent(0);
            if (var4 instanceof Box.Filler) {
               return getEmptyInsets();
            }

            if (var4 instanceof JScrollPane) {
               return getEmptyInsets();
            }
         }

         return getPopupInsets();
      }
   }
}
