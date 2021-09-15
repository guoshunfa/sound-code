package javax.swing.plaf.nimbus;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JToolBar;
import javax.swing.Painter;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.synth.SynthContext;
import sun.swing.plaf.synth.SynthIcon;

class NimbusIcon extends SynthIcon {
   private int width;
   private int height;
   private String prefix;
   private String key;

   NimbusIcon(String var1, String var2, int var3, int var4) {
      this.width = var3;
      this.height = var4;
      this.prefix = var1;
      this.key = var2;
   }

   public void paintIcon(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      Painter var7 = null;
      if (var1 != null) {
         var7 = (Painter)var1.getStyle().get(var1, this.key);
      }

      if (var7 == null) {
         var7 = (Painter)UIManager.get(this.prefix + "[Enabled]." + this.key);
      }

      if (var7 != null && var1 != null) {
         JComponent var8 = var1.getComponent();
         boolean var9 = false;
         boolean var10 = false;
         byte var11 = 0;
         byte var12 = 0;
         JToolBar var13;
         if (var8 instanceof JToolBar) {
            var13 = (JToolBar)var8;
            var9 = var13.getOrientation() == 1;
            var10 = !var13.getComponentOrientation().isLeftToRight();
            Object var14 = NimbusLookAndFeel.resolveToolbarConstraint(var13);
            if (var13.getBorder() instanceof UIResource) {
               if (var14 == "South") {
                  var12 = 1;
               } else if (var14 == "East") {
                  var11 = 1;
               }
            }
         } else if (var8 instanceof JMenu) {
            var10 = !var8.getComponentOrientation().isLeftToRight();
         }

         if (var2 instanceof Graphics2D) {
            Graphics2D var15 = (Graphics2D)var2;
            var15.translate(var3, var4);
            var15.translate(var11, var12);
            if (var9) {
               var15.rotate(Math.toRadians(90.0D));
               var15.translate(0, -var5);
               var7.paint(var15, var1.getComponent(), var6, var5);
               var15.translate(0, var5);
               var15.rotate(Math.toRadians(-90.0D));
            } else if (var10) {
               var15.scale(-1.0D, 1.0D);
               var15.translate(-var5, 0);
               var7.paint(var15, var1.getComponent(), var5, var6);
               var15.translate(var5, 0);
               var15.scale(-1.0D, 1.0D);
            } else {
               var7.paint(var15, var1.getComponent(), var5, var6);
            }

            var15.translate(-var11, -var12);
            var15.translate(-var3, -var4);
         } else {
            BufferedImage var16 = new BufferedImage(var5, var6, 2);
            Graphics2D var17 = var16.createGraphics();
            if (var9) {
               var17.rotate(Math.toRadians(90.0D));
               var17.translate(0, -var5);
               var7.paint(var17, var1.getComponent(), var6, var5);
            } else if (var10) {
               var17.scale(-1.0D, 1.0D);
               var17.translate(-var5, 0);
               var7.paint(var17, var1.getComponent(), var5, var6);
            } else {
               var7.paint(var17, var1.getComponent(), var5, var6);
            }

            var17.dispose();
            var2.drawImage(var16, var3, var4, (ImageObserver)null);
            var13 = null;
         }
      }

   }

   public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
      Painter var5 = (Painter)UIManager.get(this.prefix + "[Enabled]." + this.key);
      if (var5 != null) {
         JComponent var6 = var1 instanceof JComponent ? (JComponent)var1 : null;
         Graphics2D var7 = (Graphics2D)var2;
         var7.translate(var3, var4);
         var5.paint(var7, var6, this.width, this.height);
         var7.translate(-var3, -var4);
      }

   }

   public int getIconWidth(SynthContext var1) {
      if (var1 == null) {
         return this.width;
      } else {
         JComponent var2 = var1.getComponent();
         if (var2 instanceof JToolBar && ((JToolBar)var2).getOrientation() == 1) {
            return var2.getBorder() instanceof UIResource ? var2.getWidth() - 1 : var2.getWidth();
         } else {
            return this.scale(var1, this.width);
         }
      }
   }

   public int getIconHeight(SynthContext var1) {
      if (var1 == null) {
         return this.height;
      } else {
         JComponent var2 = var1.getComponent();
         if (var2 instanceof JToolBar) {
            JToolBar var3 = (JToolBar)var2;
            if (var3.getOrientation() == 0) {
               return var3.getBorder() instanceof UIResource ? var2.getHeight() - 1 : var2.getHeight();
            } else {
               return this.scale(var1, this.width);
            }
         } else {
            return this.scale(var1, this.height);
         }
      }
   }

   private int scale(SynthContext var1, int var2) {
      if (var1 != null && var1.getComponent() != null) {
         String var3 = (String)var1.getComponent().getClientProperty("JComponent.sizeVariant");
         if (var3 != null) {
            if ("large".equals(var3)) {
               var2 = (int)((double)var2 * 1.15D);
            } else if ("small".equals(var3)) {
               var2 = (int)((double)var2 * 0.857D);
            } else if ("mini".equals(var3)) {
               var2 = (int)((double)var2 * 0.784D);
            }
         }

         return var2;
      } else {
         return var2;
      }
   }
}
