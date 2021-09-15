package javax.swing.plaf.synth;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicToolTipUI;
import javax.swing.text.View;

public class SynthToolTipUI extends BasicToolTipUI implements PropertyChangeListener, SynthUI {
   private SynthStyle style;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthToolTipUI();
   }

   protected void installDefaults(JComponent var1) {
      this.updateStyle(var1);
   }

   private void updateStyle(JComponent var1) {
      SynthContext var2 = this.getContext(var1, 1);
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      var2.dispose();
   }

   protected void uninstallDefaults(JComponent var1) {
      SynthContext var2 = this.getContext(var1, 1);
      this.style.uninstallDefaults(var2);
      var2.dispose();
      this.style = null;
   }

   protected void installListeners(JComponent var1) {
      var1.addPropertyChangeListener(this);
   }

   protected void uninstallListeners(JComponent var1) {
      var1.removePropertyChangeListener(this);
   }

   public SynthContext getContext(JComponent var1) {
      return this.getContext(var1, this.getComponentState(var1));
   }

   private SynthContext getContext(JComponent var1, int var2) {
      return SynthContext.getContext(var1, this.style, var2);
   }

   private int getComponentState(JComponent var1) {
      JComponent var2 = ((JToolTip)var1).getComponent();
      return var2 != null && !var2.isEnabled() ? 8 : SynthLookAndFeel.getComponentState(var1);
   }

   public void update(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      SynthLookAndFeel.update(var3, var1);
      var3.getPainter().paintToolTipBackground(var3, var1, 0, 0, var2.getWidth(), var2.getHeight());
      this.paint(var3, var1);
      var3.dispose();
   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintToolTipBorder(var1, var2, var3, var4, var5, var6);
   }

   public void paint(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      this.paint(var3, var1);
      var3.dispose();
   }

   protected void paint(SynthContext var1, Graphics var2) {
      JToolTip var3 = (JToolTip)var1.getComponent();
      Insets var4 = var3.getInsets();
      View var5 = (View)var3.getClientProperty("html");
      if (var5 != null) {
         Rectangle var6 = new Rectangle(var4.left, var4.top, var3.getWidth() - (var4.left + var4.right), var3.getHeight() - (var4.top + var4.bottom));
         var5.paint(var2, var6);
      } else {
         var2.setColor(var1.getStyle().getColor(var1, ColorType.TEXT_FOREGROUND));
         var2.setFont(this.style.getFont(var1));
         var1.getStyle().getGraphicsUtils(var1).paintText(var1, var2, var3.getTipText(), var4.left, var4.top, -1);
      }

   }

   public Dimension getPreferredSize(JComponent var1) {
      SynthContext var2 = this.getContext(var1);
      Insets var3 = var1.getInsets();
      Dimension var4 = new Dimension(var3.left + var3.right, var3.top + var3.bottom);
      String var5 = ((JToolTip)var1).getTipText();
      if (var5 != null) {
         View var6 = var1 != null ? (View)var1.getClientProperty("html") : null;
         if (var6 != null) {
            var4.width += (int)var6.getPreferredSpan(0);
            var4.height += (int)var6.getPreferredSpan(1);
         } else {
            Font var7 = var2.getStyle().getFont(var2);
            FontMetrics var8 = var1.getFontMetrics(var7);
            var4.width += var2.getStyle().getGraphicsUtils(var2).computeStringWidth(var2, var7, var8, var5);
            var4.height += var8.getHeight();
         }
      }

      var2.dispose();
      return var4;
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle((JToolTip)var1.getSource());
      }

      String var2 = var1.getPropertyName();
      if (var2.equals("tiptext") || "font".equals(var2) || "foreground".equals(var2)) {
         JToolTip var3 = (JToolTip)var1.getSource();
         String var4 = var3.getTipText();
         BasicHTML.updateRenderer(var3, var4);
      }

   }
}
