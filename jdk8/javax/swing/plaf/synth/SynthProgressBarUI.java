package javax.swing.plaf.synth;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;
import sun.swing.SwingUtilities2;

public class SynthProgressBarUI extends BasicProgressBarUI implements SynthUI, PropertyChangeListener {
   private SynthStyle style;
   private int progressPadding;
   private boolean rotateText;
   private boolean paintOutsideClip;
   private boolean tileWhenIndeterminate;
   private int tileWidth;
   private Dimension minBarSize;
   private int glowWidth;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthProgressBarUI();
   }

   protected void installListeners() {
      super.installListeners();
      this.progressBar.addPropertyChangeListener(this);
   }

   protected void uninstallListeners() {
      super.uninstallListeners();
      this.progressBar.removePropertyChangeListener(this);
   }

   protected void installDefaults() {
      this.updateStyle(this.progressBar);
   }

   private void updateStyle(JProgressBar var1) {
      SynthContext var2 = this.getContext(var1, 1);
      SynthStyle var3 = this.style;
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      this.setCellLength(this.style.getInt(var2, "ProgressBar.cellLength", 1));
      this.setCellSpacing(this.style.getInt(var2, "ProgressBar.cellSpacing", 0));
      this.progressPadding = this.style.getInt(var2, "ProgressBar.progressPadding", 0);
      this.paintOutsideClip = this.style.getBoolean(var2, "ProgressBar.paintOutsideClip", false);
      this.rotateText = this.style.getBoolean(var2, "ProgressBar.rotateText", false);
      this.tileWhenIndeterminate = this.style.getBoolean(var2, "ProgressBar.tileWhenIndeterminate", false);
      this.tileWidth = this.style.getInt(var2, "ProgressBar.tileWidth", 15);
      String var4 = (String)this.progressBar.getClientProperty("JComponent.sizeVariant");
      if (var4 != null) {
         if ("large".equals(var4)) {
            this.tileWidth = (int)((double)this.tileWidth * 1.15D);
         } else if ("small".equals(var4)) {
            this.tileWidth = (int)((double)this.tileWidth * 0.857D);
         } else if ("mini".equals(var4)) {
            this.tileWidth = (int)((double)this.tileWidth * 0.784D);
         }
      }

      this.minBarSize = (Dimension)this.style.get(var2, "ProgressBar.minBarSize");
      this.glowWidth = this.style.getInt(var2, "ProgressBar.glowWidth", 0);
      var2.dispose();
   }

   protected void uninstallDefaults() {
      SynthContext var1 = this.getContext(this.progressBar, 1);
      this.style.uninstallDefaults(var1);
      var1.dispose();
      this.style = null;
   }

   public SynthContext getContext(JComponent var1) {
      return this.getContext(var1, this.getComponentState(var1));
   }

   private SynthContext getContext(JComponent var1, int var2) {
      return SynthContext.getContext(var1, this.style, var2);
   }

   private int getComponentState(JComponent var1) {
      return SynthLookAndFeel.getComponentState(var1);
   }

   public int getBaseline(JComponent var1, int var2, int var3) {
      super.getBaseline(var1, var2, var3);
      if (this.progressBar.isStringPainted() && this.progressBar.getOrientation() == 0) {
         SynthContext var4 = this.getContext(var1);
         Font var5 = var4.getStyle().getFont(var4);
         FontMetrics var6 = this.progressBar.getFontMetrics(var5);
         var4.dispose();
         return (var3 - var6.getAscent() - var6.getDescent()) / 2 + var6.getAscent();
      } else {
         return -1;
      }
   }

   protected Rectangle getBox(Rectangle var1) {
      return this.tileWhenIndeterminate ? SwingUtilities.calculateInnerArea(this.progressBar, var1) : super.getBox(var1);
   }

   protected void setAnimationIndex(int var1) {
      if (this.paintOutsideClip) {
         if (this.getAnimationIndex() == var1) {
            return;
         }

         super.setAnimationIndex(var1);
         this.progressBar.repaint();
      } else {
         super.setAnimationIndex(var1);
      }

   }

   public void update(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      SynthLookAndFeel.update(var3, var1);
      var3.getPainter().paintProgressBarBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight(), this.progressBar.getOrientation());
      this.paint(var3, var1);
      var3.dispose();
   }

   public void paint(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      this.paint(var3, var1);
      var3.dispose();
   }

   protected void paint(SynthContext var1, Graphics var2) {
      JProgressBar var3 = (JProgressBar)var1.getComponent();
      int var4 = 0;
      int var5 = 0;
      int var6 = 0;
      int var7 = 0;
      if (!var3.isIndeterminate()) {
         Insets var8 = var3.getInsets();
         double var9 = var3.getPercentComplete();
         if (var9 != 0.0D) {
            if (var3.getOrientation() == 0) {
               var4 = var8.left + this.progressPadding;
               var5 = var8.top + this.progressPadding;
               var6 = (int)(var9 * (double)(var3.getWidth() - (var8.left + this.progressPadding + var8.right + this.progressPadding)));
               var7 = var3.getHeight() - (var8.top + this.progressPadding + var8.bottom + this.progressPadding);
               if (!SynthLookAndFeel.isLeftToRight(var3)) {
                  var4 = var3.getWidth() - var8.right - var6 - this.progressPadding - this.glowWidth;
               }
            } else {
               var4 = var8.left + this.progressPadding;
               var6 = var3.getWidth() - (var8.left + this.progressPadding + var8.right + this.progressPadding);
               var7 = (int)(var9 * (double)(var3.getHeight() - (var8.top + this.progressPadding + var8.bottom + this.progressPadding)));
               var5 = var3.getHeight() - var8.bottom - var7 - this.progressPadding;
               if (SynthLookAndFeel.isLeftToRight(var3)) {
                  var5 -= this.glowWidth;
               }
            }
         }
      } else {
         this.boxRect = this.getBox(this.boxRect);
         var4 = this.boxRect.x + this.progressPadding;
         var5 = this.boxRect.y + this.progressPadding;
         var6 = this.boxRect.width - this.progressPadding - this.progressPadding;
         var7 = this.boxRect.height - this.progressPadding - this.progressPadding;
      }

      if (this.tileWhenIndeterminate && var3.isIndeterminate()) {
         double var13 = (double)this.getAnimationIndex() / (double)this.getFrameCount();
         int var10 = (int)(var13 * (double)this.tileWidth);
         Shape var11 = var2.getClip();
         var2.clipRect(var4, var5, var6, var7);
         int var12;
         if (var3.getOrientation() == 0) {
            for(var12 = var4 - this.tileWidth + var10; var12 <= var6; var12 += this.tileWidth) {
               var1.getPainter().paintProgressBarForeground(var1, var2, var12, var5, this.tileWidth, var7, var3.getOrientation());
            }
         } else {
            for(var12 = var5 - var10; var12 < var7 + this.tileWidth; var12 += this.tileWidth) {
               var1.getPainter().paintProgressBarForeground(var1, var2, var4, var12, var6, this.tileWidth, var3.getOrientation());
            }
         }

         var2.setClip(var11);
      } else if (this.minBarSize == null || var6 >= this.minBarSize.width && var7 >= this.minBarSize.height) {
         var1.getPainter().paintProgressBarForeground(var1, var2, var4, var5, var6, var7, var3.getOrientation());
      }

      if (var3.isStringPainted()) {
         this.paintText(var1, var2, var3.getString());
      }

   }

   protected void paintText(SynthContext var1, Graphics var2, String var3) {
      if (this.progressBar.isStringPainted()) {
         SynthStyle var4 = var1.getStyle();
         Font var5 = var4.getFont(var1);
         FontMetrics var6 = SwingUtilities2.getFontMetrics(this.progressBar, var2, var5);
         int var7 = var4.getGraphicsUtils(var1).computeStringWidth(var1, var5, var6, var3);
         Rectangle var8 = this.progressBar.getBounds();
         if (this.rotateText && this.progressBar.getOrientation() == 1) {
            Graphics2D var12 = (Graphics2D)var2;
            Point var10;
            AffineTransform var11;
            if (this.progressBar.getComponentOrientation().isLeftToRight()) {
               var11 = AffineTransform.getRotateInstance(-1.5707963267948966D);
               var10 = new Point((var8.width + var6.getAscent() - var6.getDescent()) / 2, (var8.height + var7) / 2);
            } else {
               var11 = AffineTransform.getRotateInstance(1.5707963267948966D);
               var10 = new Point((var8.width - var6.getAscent() + var6.getDescent()) / 2, (var8.height - var7) / 2);
            }

            if (var10.x < 0) {
               return;
            }

            var5 = var5.deriveFont(var11);
            var12.setFont(var5);
            var12.setColor(var4.getColor(var1, ColorType.TEXT_FOREGROUND));
            var4.getGraphicsUtils(var1).paintText(var1, var2, var3, var10.x, var10.y, -1);
         } else {
            Rectangle var9 = new Rectangle(var8.width / 2 - var7 / 2, (var8.height - (var6.getAscent() + var6.getDescent())) / 2, 0, 0);
            if (var9.y < 0) {
               return;
            }

            var2.setColor(var4.getColor(var1, ColorType.TEXT_FOREGROUND));
            var2.setFont(var5);
            var4.getGraphicsUtils(var1).paintText(var1, var2, var3, var9.x, var9.y, -1);
         }
      }

   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintProgressBarBorder(var1, var2, var3, var4, var5, var6, this.progressBar.getOrientation());
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1) || "indeterminate".equals(var1.getPropertyName())) {
         this.updateStyle((JProgressBar)var1.getSource());
      }

   }

   public Dimension getPreferredSize(JComponent var1) {
      Dimension var2 = null;
      Insets var3 = this.progressBar.getInsets();
      FontMetrics var4 = this.progressBar.getFontMetrics(this.progressBar.getFont());
      String var5 = this.progressBar.getString();
      int var6 = var4.getHeight() + var4.getDescent();
      int var7;
      if (this.progressBar.getOrientation() == 0) {
         var2 = new Dimension(this.getPreferredInnerHorizontal());
         if (this.progressBar.isStringPainted()) {
            if (var6 > var2.height) {
               var2.height = var6;
            }

            var7 = SwingUtilities2.stringWidth(this.progressBar, var4, var5);
            if (var7 > var2.width) {
               var2.width = var7;
            }
         }
      } else {
         var2 = new Dimension(this.getPreferredInnerVertical());
         if (this.progressBar.isStringPainted()) {
            if (var6 > var2.width) {
               var2.width = var6;
            }

            var7 = SwingUtilities2.stringWidth(this.progressBar, var4, var5);
            if (var7 > var2.height) {
               var2.height = var7;
            }
         }
      }

      String var8 = (String)this.progressBar.getClientProperty("JComponent.sizeVariant");
      if (var8 != null) {
         if ("large".equals(var8)) {
            var2.width = (int)((float)var2.width * 1.15F);
            var2.height = (int)((float)var2.height * 1.15F);
         } else if ("small".equals(var8)) {
            var2.width = (int)((float)var2.width * 0.9F);
            var2.height = (int)((float)var2.height * 0.9F);
         } else if ("mini".equals(var8)) {
            var2.width = (int)((float)var2.width * 0.784F);
            var2.height = (int)((float)var2.height * 0.784F);
         }
      }

      var2.width += var3.left + var3.right;
      var2.height += var3.top + var3.bottom;
      return var2;
   }
}
