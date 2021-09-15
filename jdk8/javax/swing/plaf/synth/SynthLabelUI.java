package javax.swing.plaf.synth;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.text.View;

public class SynthLabelUI extends BasicLabelUI implements SynthUI {
   private SynthStyle style;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthLabelUI();
   }

   protected void installDefaults(JLabel var1) {
      this.updateStyle(var1);
   }

   void updateStyle(JLabel var1) {
      SynthContext var2 = this.getContext(var1, 1);
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      var2.dispose();
   }

   protected void uninstallDefaults(JLabel var1) {
      SynthContext var2 = this.getContext(var1, 1);
      this.style.uninstallDefaults(var2);
      var2.dispose();
      this.style = null;
   }

   public SynthContext getContext(JComponent var1) {
      return this.getContext(var1, this.getComponentState(var1));
   }

   private SynthContext getContext(JComponent var1, int var2) {
      return SynthContext.getContext(var1, this.style, var2);
   }

   private int getComponentState(JComponent var1) {
      int var2 = SynthLookAndFeel.getComponentState(var1);
      if (SynthLookAndFeel.getSelectedUI() == this && var2 == 1) {
         var2 = SynthLookAndFeel.getSelectedUIState() | 1;
      }

      return var2;
   }

   public int getBaseline(JComponent var1, int var2, int var3) {
      if (var1 == null) {
         throw new NullPointerException("Component must be non-null");
      } else if (var2 >= 0 && var3 >= 0) {
         JLabel var4 = (JLabel)var1;
         String var5 = var4.getText();
         if (var5 != null && !"".equals(var5)) {
            Insets var6 = var4.getInsets();
            Rectangle var7 = new Rectangle();
            Rectangle var8 = new Rectangle();
            Rectangle var9 = new Rectangle();
            var7.x = var6.left;
            var7.y = var6.top;
            var7.width = var2 - (var6.right + var7.x);
            var7.height = var3 - (var6.bottom + var7.y);
            SynthContext var10 = this.getContext(var4);
            FontMetrics var11 = var10.getComponent().getFontMetrics(var10.getStyle().getFont(var10));
            var10.getStyle().getGraphicsUtils(var10).layoutText(var10, var11, var4.getText(), var4.getIcon(), var4.getHorizontalAlignment(), var4.getVerticalAlignment(), var4.getHorizontalTextPosition(), var4.getVerticalTextPosition(), var7, var9, var8, var4.getIconTextGap());
            View var12 = (View)var4.getClientProperty("html");
            int var13;
            if (var12 != null) {
               var13 = BasicHTML.getHTMLBaseline(var12, var8.width, var8.height);
               if (var13 >= 0) {
                  var13 += var8.y;
               }
            } else {
               var13 = var8.y + var11.getAscent();
            }

            var10.dispose();
            return var13;
         } else {
            return -1;
         }
      } else {
         throw new IllegalArgumentException("Width and height must be >= 0");
      }
   }

   public void update(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      SynthLookAndFeel.update(var3, var1);
      var3.getPainter().paintLabelBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight());
      this.paint(var3, var1);
      var3.dispose();
   }

   public void paint(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      this.paint(var3, var1);
      var3.dispose();
   }

   protected void paint(SynthContext var1, Graphics var2) {
      JLabel var3 = (JLabel)var1.getComponent();
      Icon var4 = var3.isEnabled() ? var3.getIcon() : var3.getDisabledIcon();
      var2.setColor(var1.getStyle().getColor(var1, ColorType.TEXT_FOREGROUND));
      var2.setFont(this.style.getFont(var1));
      var1.getStyle().getGraphicsUtils(var1).paintText(var1, var2, var3.getText(), var4, var3.getHorizontalAlignment(), var3.getVerticalAlignment(), var3.getHorizontalTextPosition(), var3.getVerticalTextPosition(), var3.getIconTextGap(), var3.getDisplayedMnemonicIndex(), 0);
   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintLabelBorder(var1, var2, var3, var4, var5, var6);
   }

   public Dimension getPreferredSize(JComponent var1) {
      JLabel var2 = (JLabel)var1;
      Icon var3 = var2.isEnabled() ? var2.getIcon() : var2.getDisabledIcon();
      SynthContext var4 = this.getContext(var1);
      Dimension var5 = var4.getStyle().getGraphicsUtils(var4).getPreferredSize(var4, var4.getStyle().getFont(var4), var2.getText(), var3, var2.getHorizontalAlignment(), var2.getVerticalAlignment(), var2.getHorizontalTextPosition(), var2.getVerticalTextPosition(), var2.getIconTextGap(), var2.getDisplayedMnemonicIndex());
      var4.dispose();
      return var5;
   }

   public Dimension getMinimumSize(JComponent var1) {
      JLabel var2 = (JLabel)var1;
      Icon var3 = var2.isEnabled() ? var2.getIcon() : var2.getDisabledIcon();
      SynthContext var4 = this.getContext(var1);
      Dimension var5 = var4.getStyle().getGraphicsUtils(var4).getMinimumSize(var4, var4.getStyle().getFont(var4), var2.getText(), var3, var2.getHorizontalAlignment(), var2.getVerticalAlignment(), var2.getHorizontalTextPosition(), var2.getVerticalTextPosition(), var2.getIconTextGap(), var2.getDisplayedMnemonicIndex());
      var4.dispose();
      return var5;
   }

   public Dimension getMaximumSize(JComponent var1) {
      JLabel var2 = (JLabel)var1;
      Icon var3 = var2.isEnabled() ? var2.getIcon() : var2.getDisabledIcon();
      SynthContext var4 = this.getContext(var1);
      Dimension var5 = var4.getStyle().getGraphicsUtils(var4).getMaximumSize(var4, var4.getStyle().getFont(var4), var2.getText(), var3, var2.getHorizontalAlignment(), var2.getVerticalAlignment(), var2.getHorizontalTextPosition(), var2.getVerticalTextPosition(), var2.getIconTextGap(), var2.getDisplayedMnemonicIndex());
      var4.dispose();
      return var5;
   }

   public void propertyChange(PropertyChangeEvent var1) {
      super.propertyChange(var1);
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle((JLabel)var1.getSource());
      }

   }
}
