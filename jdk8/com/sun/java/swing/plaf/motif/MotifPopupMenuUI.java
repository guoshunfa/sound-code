package com.sun.java.swing.plaf.motif;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;
import sun.swing.SwingUtilities2;

public class MotifPopupMenuUI extends BasicPopupMenuUI {
   private static Border border = null;
   private Font titleFont = null;

   public static ComponentUI createUI(JComponent var0) {
      return new MotifPopupMenuUI();
   }

   public Dimension getPreferredSize(JComponent var1) {
      LayoutManager var2 = var1.getLayout();
      Dimension var3 = var2.preferredLayoutSize(var1);
      String var4 = ((JPopupMenu)var1).getLabel();
      if (this.titleFont == null) {
         UIDefaults var5 = UIManager.getLookAndFeelDefaults();
         this.titleFont = var5.getFont("PopupMenu.font");
      }

      FontMetrics var8 = var1.getFontMetrics(this.titleFont);
      int var6 = 0;
      if (var4 != null) {
         var6 += SwingUtilities2.stringWidth(var1, var8, var4);
      }

      if (var3.width < var6) {
         var3.width = var6 + 8;
         Insets var7 = var1.getInsets();
         if (var7 != null) {
            var3.width += var7.left + var7.right;
         }

         if (border != null) {
            var7 = border.getBorderInsets(var1);
            var3.width += var7.left + var7.right;
         }

         return var3;
      } else {
         return null;
      }
   }

   protected ChangeListener createChangeListener(JPopupMenu var1) {
      return new ChangeListener() {
         public void stateChanged(ChangeEvent var1) {
         }
      };
   }

   public boolean isPopupTrigger(MouseEvent var1) {
      return var1.getID() == 501 && (var1.getModifiers() & 4) != 0;
   }
}
