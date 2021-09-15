package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import sun.swing.SwingUtilities2;

public class MetalScrollBarUI extends BasicScrollBarUI {
   private static Color shadowColor;
   private static Color highlightColor;
   private static Color darkShadowColor;
   private static Color thumbColor;
   private static Color thumbShadow;
   private static Color thumbHighlightColor;
   protected MetalBumps bumps;
   protected MetalScrollButton increaseButton;
   protected MetalScrollButton decreaseButton;
   protected int scrollBarWidth;
   public static final String FREE_STANDING_PROP = "JScrollBar.isFreeStanding";
   protected boolean isFreeStanding = true;

   public static ComponentUI createUI(JComponent var0) {
      return new MetalScrollBarUI();
   }

   protected void installDefaults() {
      this.scrollBarWidth = (Integer)((Integer)UIManager.get("ScrollBar.width"));
      super.installDefaults();
      this.bumps = new MetalBumps(10, 10, thumbHighlightColor, thumbShadow, thumbColor);
   }

   protected void installListeners() {
      super.installListeners();
      ((MetalScrollBarUI.ScrollBarListener)this.propertyChangeListener).handlePropertyChange(this.scrollbar.getClientProperty("JScrollBar.isFreeStanding"));
   }

   protected PropertyChangeListener createPropertyChangeListener() {
      return new MetalScrollBarUI.ScrollBarListener();
   }

   protected void configureScrollBarColors() {
      super.configureScrollBarColors();
      shadowColor = UIManager.getColor("ScrollBar.shadow");
      highlightColor = UIManager.getColor("ScrollBar.highlight");
      darkShadowColor = UIManager.getColor("ScrollBar.darkShadow");
      thumbColor = UIManager.getColor("ScrollBar.thumb");
      thumbShadow = UIManager.getColor("ScrollBar.thumbShadow");
      thumbHighlightColor = UIManager.getColor("ScrollBar.thumbHighlight");
   }

   public Dimension getPreferredSize(JComponent var1) {
      return this.scrollbar.getOrientation() == 1 ? new Dimension(this.scrollBarWidth, this.scrollBarWidth * 3 + 10) : new Dimension(this.scrollBarWidth * 3 + 10, this.scrollBarWidth);
   }

   protected JButton createDecreaseButton(int var1) {
      this.decreaseButton = new MetalScrollButton(var1, this.scrollBarWidth, this.isFreeStanding);
      return this.decreaseButton;
   }

   protected JButton createIncreaseButton(int var1) {
      this.increaseButton = new MetalScrollButton(var1, this.scrollBarWidth, this.isFreeStanding);
      return this.increaseButton;
   }

   protected void paintTrack(Graphics var1, JComponent var2, Rectangle var3) {
      var1.translate(var3.x, var3.y);
      boolean var4 = MetalUtils.isLeftToRight(var2);
      int var5;
      if (this.scrollbar.getOrientation() == 1) {
         if (!this.isFreeStanding) {
            var3.width += 2;
            if (!var4) {
               var1.translate(-1, 0);
            }
         }

         if (var2.isEnabled()) {
            var1.setColor(darkShadowColor);
            SwingUtilities2.drawVLine(var1, 0, 0, var3.height - 1);
            SwingUtilities2.drawVLine(var1, var3.width - 2, 0, var3.height - 1);
            SwingUtilities2.drawHLine(var1, 2, var3.width - 1, var3.height - 1);
            SwingUtilities2.drawHLine(var1, 2, var3.width - 2, 0);
            var1.setColor(shadowColor);
            SwingUtilities2.drawVLine(var1, 1, 1, var3.height - 2);
            SwingUtilities2.drawHLine(var1, 1, var3.width - 3, 1);
            if (this.scrollbar.getValue() != this.scrollbar.getMaximum()) {
               var5 = this.thumbRect.y + this.thumbRect.height - var3.y;
               SwingUtilities2.drawHLine(var1, 1, var3.width - 1, var5);
            }

            var1.setColor(highlightColor);
            SwingUtilities2.drawVLine(var1, var3.width - 1, 0, var3.height - 1);
         } else {
            MetalUtils.drawDisabledBorder(var1, 0, 0, var3.width, var3.height);
         }

         if (!this.isFreeStanding) {
            var3.width -= 2;
            if (!var4) {
               var1.translate(1, 0);
            }
         }
      } else {
         if (!this.isFreeStanding) {
            var3.height += 2;
         }

         if (var2.isEnabled()) {
            var1.setColor(darkShadowColor);
            SwingUtilities2.drawHLine(var1, 0, var3.width - 1, 0);
            SwingUtilities2.drawVLine(var1, 0, 2, var3.height - 2);
            SwingUtilities2.drawHLine(var1, 0, var3.width - 1, var3.height - 2);
            SwingUtilities2.drawVLine(var1, var3.width - 1, 2, var3.height - 1);
            var1.setColor(shadowColor);
            SwingUtilities2.drawHLine(var1, 1, var3.width - 2, 1);
            SwingUtilities2.drawVLine(var1, 1, 1, var3.height - 3);
            SwingUtilities2.drawHLine(var1, 0, var3.width - 1, var3.height - 1);
            if (this.scrollbar.getValue() != this.scrollbar.getMaximum()) {
               var5 = this.thumbRect.x + this.thumbRect.width - var3.x;
               SwingUtilities2.drawVLine(var1, var5, 1, var3.height - 1);
            }
         } else {
            MetalUtils.drawDisabledBorder(var1, 0, 0, var3.width, var3.height);
         }

         if (!this.isFreeStanding) {
            var3.height -= 2;
         }
      }

      var1.translate(-var3.x, -var3.y);
   }

   protected void paintThumb(Graphics var1, JComponent var2, Rectangle var3) {
      if (var2.isEnabled()) {
         if (MetalLookAndFeel.usingOcean()) {
            this.oceanPaintThumb(var1, var2, var3);
         } else {
            boolean var4 = MetalUtils.isLeftToRight(var2);
            var1.translate(var3.x, var3.y);
            if (this.scrollbar.getOrientation() == 1) {
               if (!this.isFreeStanding) {
                  var3.width += 2;
                  if (!var4) {
                     var1.translate(-1, 0);
                  }
               }

               var1.setColor(thumbColor);
               var1.fillRect(0, 0, var3.width - 2, var3.height - 1);
               var1.setColor(thumbShadow);
               SwingUtilities2.drawRect(var1, 0, 0, var3.width - 2, var3.height - 1);
               var1.setColor(thumbHighlightColor);
               SwingUtilities2.drawHLine(var1, 1, var3.width - 3, 1);
               SwingUtilities2.drawVLine(var1, 1, 1, var3.height - 2);
               this.bumps.setBumpArea(var3.width - 6, var3.height - 7);
               this.bumps.paintIcon(var2, var1, 3, 4);
               if (!this.isFreeStanding) {
                  var3.width -= 2;
                  if (!var4) {
                     var1.translate(1, 0);
                  }
               }
            } else {
               if (!this.isFreeStanding) {
                  var3.height += 2;
               }

               var1.setColor(thumbColor);
               var1.fillRect(0, 0, var3.width - 1, var3.height - 2);
               var1.setColor(thumbShadow);
               SwingUtilities2.drawRect(var1, 0, 0, var3.width - 1, var3.height - 2);
               var1.setColor(thumbHighlightColor);
               SwingUtilities2.drawHLine(var1, 1, var3.width - 3, 1);
               SwingUtilities2.drawVLine(var1, 1, 1, var3.height - 3);
               this.bumps.setBumpArea(var3.width - 7, var3.height - 6);
               this.bumps.paintIcon(var2, var1, 4, 3);
               if (!this.isFreeStanding) {
                  var3.height -= 2;
               }
            }

            var1.translate(-var3.x, -var3.y);
         }
      }
   }

   private void oceanPaintThumb(Graphics var1, JComponent var2, Rectangle var3) {
      boolean var4 = MetalUtils.isLeftToRight(var2);
      var1.translate(var3.x, var3.y);
      int var5;
      int var6;
      int var7;
      if (this.scrollbar.getOrientation() == 1) {
         if (!this.isFreeStanding) {
            var3.width += 2;
            if (!var4) {
               var1.translate(-1, 0);
            }
         }

         if (thumbColor != null) {
            var1.setColor(thumbColor);
            var1.fillRect(0, 0, var3.width - 2, var3.height - 1);
         }

         var1.setColor(thumbShadow);
         SwingUtilities2.drawRect(var1, 0, 0, var3.width - 2, var3.height - 1);
         var1.setColor(thumbHighlightColor);
         SwingUtilities2.drawHLine(var1, 1, var3.width - 3, 1);
         SwingUtilities2.drawVLine(var1, 1, 1, var3.height - 2);
         MetalUtils.drawGradient(var2, var1, "ScrollBar.gradient", 2, 2, var3.width - 4, var3.height - 3, false);
         var5 = var3.width - 8;
         if (var5 > 2 && var3.height >= 10) {
            var1.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
            var6 = var3.height / 2 - 2;

            for(var7 = 0; var7 < 6; var7 += 2) {
               var1.fillRect(4, var7 + var6, var5, 1);
            }

            var1.setColor(MetalLookAndFeel.getWhite());
            ++var6;

            for(var7 = 0; var7 < 6; var7 += 2) {
               var1.fillRect(5, var7 + var6, var5, 1);
            }
         }

         if (!this.isFreeStanding) {
            var3.width -= 2;
            if (!var4) {
               var1.translate(1, 0);
            }
         }
      } else {
         if (!this.isFreeStanding) {
            var3.height += 2;
         }

         if (thumbColor != null) {
            var1.setColor(thumbColor);
            var1.fillRect(0, 0, var3.width - 1, var3.height - 2);
         }

         var1.setColor(thumbShadow);
         SwingUtilities2.drawRect(var1, 0, 0, var3.width - 1, var3.height - 2);
         var1.setColor(thumbHighlightColor);
         SwingUtilities2.drawHLine(var1, 1, var3.width - 2, 1);
         SwingUtilities2.drawVLine(var1, 1, 1, var3.height - 3);
         MetalUtils.drawGradient(var2, var1, "ScrollBar.gradient", 2, 2, var3.width - 3, var3.height - 4, true);
         var5 = var3.height - 8;
         if (var5 > 2 && var3.width >= 10) {
            var1.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
            var6 = var3.width / 2 - 2;

            for(var7 = 0; var7 < 6; var7 += 2) {
               var1.fillRect(var6 + var7, 4, 1, var5);
            }

            var1.setColor(MetalLookAndFeel.getWhite());
            ++var6;

            for(var7 = 0; var7 < 6; var7 += 2) {
               var1.fillRect(var6 + var7, 5, 1, var5);
            }
         }

         if (!this.isFreeStanding) {
            var3.height -= 2;
         }
      }

      var1.translate(-var3.x, -var3.y);
   }

   protected Dimension getMinimumThumbSize() {
      return new Dimension(this.scrollBarWidth, this.scrollBarWidth);
   }

   protected void setThumbBounds(int var1, int var2, int var3, int var4) {
      if (this.thumbRect.x != var1 || this.thumbRect.y != var2 || this.thumbRect.width != var3 || this.thumbRect.height != var4) {
         int var5 = Math.min(var1, this.thumbRect.x);
         int var6 = Math.min(var2, this.thumbRect.y);
         int var7 = Math.max(var1 + var3, this.thumbRect.x + this.thumbRect.width);
         int var8 = Math.max(var2 + var4, this.thumbRect.y + this.thumbRect.height);
         this.thumbRect.setBounds(var1, var2, var3, var4);
         this.scrollbar.repaint(var5, var6, var7 - var5 + 1, var8 - var6 + 1);
      }
   }

   class ScrollBarListener extends BasicScrollBarUI.PropertyChangeHandler {
      ScrollBarListener() {
         super();
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if (var2.equals("JScrollBar.isFreeStanding")) {
            this.handlePropertyChange(var1.getNewValue());
         } else {
            super.propertyChange(var1);
         }

      }

      public void handlePropertyChange(Object var1) {
         if (var1 != null) {
            boolean var2 = (Boolean)var1;
            boolean var3 = !var2 && MetalScrollBarUI.this.isFreeStanding;
            boolean var4 = var2 && !MetalScrollBarUI.this.isFreeStanding;
            MetalScrollBarUI.this.isFreeStanding = var2;
            if (var3) {
               this.toFlush();
            } else if (var4) {
               this.toFreeStanding();
            }
         } else if (!MetalScrollBarUI.this.isFreeStanding) {
            MetalScrollBarUI.this.isFreeStanding = true;
            this.toFreeStanding();
         }

         if (MetalScrollBarUI.this.increaseButton != null) {
            MetalScrollBarUI.this.increaseButton.setFreeStanding(MetalScrollBarUI.this.isFreeStanding);
         }

         if (MetalScrollBarUI.this.decreaseButton != null) {
            MetalScrollBarUI.this.decreaseButton.setFreeStanding(MetalScrollBarUI.this.isFreeStanding);
         }

      }

      protected void toFlush() {
         MetalScrollBarUI var10000 = MetalScrollBarUI.this;
         var10000.scrollBarWidth -= 2;
      }

      protected void toFreeStanding() {
         MetalScrollBarUI var10000 = MetalScrollBarUI.this;
         var10000.scrollBarWidth += 2;
      }
   }
}
