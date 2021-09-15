package com.apple.laf;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.PopupFactory;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;

public class AquaPopupMenuUI extends BasicPopupMenuUI {
   static final int OVERLAP_SLACK = 10;

   public static ComponentUI createUI(JComponent var0) {
      return new AquaPopupMenuUI();
   }

   public boolean isPopupTrigger(MouseEvent var1) {
      return var1.isPopupTrigger();
   }

   public void paint(Graphics var1, JComponent var2) {
      if (!(var1 instanceof Graphics2D)) {
         super.paint(var1, var2);
      } else if (!(PopupFactory.getSharedInstance() instanceof ScreenPopupFactory)) {
         super.paint(var1, var2);
      } else {
         Graphics2D var3 = (Graphics2D)var1.create();
         Rectangle var4 = this.popupMenu.getBounds();
         this.paintRoundRect(var3, var4);
         this.clipEdges(var3, var4);
         var3.dispose();
         super.paint(var1, var2);
      }
   }

   protected void paintRoundRect(Graphics2D var1, Rectangle var2) {
      var1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      var1.setComposite(AlphaComposite.Clear);
      var1.setStroke(new BasicStroke(3.0F));
      var1.drawRoundRect(-2, -2, var2.width + 3, var2.height + 3, 12, 12);
   }

   protected void clipEdges(Graphics2D var1, Rectangle var2) {
      Component var3 = this.popupMenu.getInvoker();
      if (var3 instanceof JMenu) {
         Rectangle var4 = var3.getBounds();
         var4.setLocation(var3.getLocationOnScreen());
         var2.setLocation(this.popupMenu.getLocationOnScreen());
         Point var5 = new Point((int)var4.getCenterX(), (int)var4.getCenterY());
         if (!var2.contains(var5)) {
            var1.setComposite(AlphaComposite.SrcOver);
            var1.setColor(this.popupMenu.getBackground());
            Point var6 = new Point((int)var2.getCenterX(), (int)var2.getCenterY());
            boolean var7 = var5.y <= var6.y;
            if (var4.x + var4.width < var2.x + 10) {
               if (var7) {
                  var1.fillRect(-2, -2, 8, 8);
               } else {
                  var1.fillRect(-2, var2.height - 6, 8, 8);
               }
            } else if (var2.x + var2.width < var4.x + 10) {
               if (var7) {
                  var1.fillRect(var2.width - 6, -2, 8, 8);
               } else {
                  var1.fillRect(var2.width - 6, var2.height - 6, 8, 8);
               }
            } else if (var4.y + var4.height < var2.y + 10) {
               var1.fillRect(-2, -2, var2.width + 4, 8);
            }
         }
      }
   }
}
