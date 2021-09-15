package javax.swing.plaf.synth;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

public class SynthButtonUI extends BasicButtonUI implements PropertyChangeListener, SynthUI {
   private SynthStyle style;

   public static ComponentUI createUI(JComponent var0) {
      return new SynthButtonUI();
   }

   protected void installDefaults(AbstractButton var1) {
      this.updateStyle(var1);
      LookAndFeel.installProperty(var1, "rolloverEnabled", Boolean.TRUE);
   }

   protected void installListeners(AbstractButton var1) {
      super.installListeners(var1);
      var1.addPropertyChangeListener(this);
   }

   void updateStyle(AbstractButton var1) {
      SynthContext var2 = this.getContext(var1, 1);
      SynthStyle var3 = this.style;
      this.style = SynthLookAndFeel.updateStyle(var2, this);
      if (this.style != var3) {
         if (var1.getMargin() == null || var1.getMargin() instanceof UIResource) {
            Insets var4 = (Insets)this.style.get(var2, this.getPropertyPrefix() + "margin");
            if (var4 == null) {
               var4 = SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS;
            }

            var1.setMargin(var4);
         }

         Object var5 = this.style.get(var2, this.getPropertyPrefix() + "iconTextGap");
         if (var5 != null) {
            LookAndFeel.installProperty(var1, "iconTextGap", var5);
         }

         var5 = this.style.get(var2, this.getPropertyPrefix() + "contentAreaFilled");
         LookAndFeel.installProperty(var1, "contentAreaFilled", var5 != null ? var5 : Boolean.TRUE);
         if (var3 != null) {
            this.uninstallKeyboardActions(var1);
            this.installKeyboardActions(var1);
         }
      }

      var2.dispose();
   }

   protected void uninstallListeners(AbstractButton var1) {
      super.uninstallListeners(var1);
      var1.removePropertyChangeListener(this);
   }

   protected void uninstallDefaults(AbstractButton var1) {
      SynthContext var2 = this.getContext(var1, 1);
      this.style.uninstallDefaults(var2);
      var2.dispose();
      this.style = null;
   }

   public SynthContext getContext(JComponent var1) {
      return this.getContext(var1, this.getComponentState(var1));
   }

   SynthContext getContext(JComponent var1, int var2) {
      return SynthContext.getContext(var1, this.style, var2);
   }

   private int getComponentState(JComponent var1) {
      int var2 = 1;
      if (!var1.isEnabled()) {
         var2 = 8;
      }

      if (SynthLookAndFeel.getSelectedUI() == this) {
         return SynthLookAndFeel.getSelectedUIState() | 1;
      } else {
         AbstractButton var3 = (AbstractButton)var1;
         ButtonModel var4 = var3.getModel();
         if (var4.isPressed()) {
            if (var4.isArmed()) {
               var2 = 4;
            } else {
               var2 = 2;
            }
         }

         if (var4.isRollover()) {
            var2 |= 2;
         }

         if (var4.isSelected()) {
            var2 |= 512;
         }

         if (var1.isFocusOwner() && var3.isFocusPainted()) {
            var2 |= 256;
         }

         if (var1 instanceof JButton && ((JButton)var1).isDefaultButton()) {
            var2 |= 1024;
         }

         return var2;
      }
   }

   public int getBaseline(JComponent var1, int var2, int var3) {
      if (var1 == null) {
         throw new NullPointerException("Component must be non-null");
      } else if (var2 >= 0 && var3 >= 0) {
         AbstractButton var4 = (AbstractButton)var1;
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
      this.paintBackground(var3, var1, var2);
      this.paint(var3, var1);
      var3.dispose();
   }

   public void paint(Graphics var1, JComponent var2) {
      SynthContext var3 = this.getContext(var2);
      this.paint(var3, var1);
      var3.dispose();
   }

   protected void paint(SynthContext var1, Graphics var2) {
      AbstractButton var3 = (AbstractButton)var1.getComponent();
      var2.setColor(var1.getStyle().getColor(var1, ColorType.TEXT_FOREGROUND));
      var2.setFont(this.style.getFont(var1));
      var1.getStyle().getGraphicsUtils(var1).paintText(var1, var2, var3.getText(), this.getIcon(var3), var3.getHorizontalAlignment(), var3.getVerticalAlignment(), var3.getHorizontalTextPosition(), var3.getVerticalTextPosition(), var3.getIconTextGap(), var3.getDisplayedMnemonicIndex(), this.getTextShiftOffset(var1));
   }

   void paintBackground(SynthContext var1, Graphics var2, JComponent var3) {
      if (((AbstractButton)var3).isContentAreaFilled()) {
         var1.getPainter().paintButtonBackground(var1, var2, 0, 0, var3.getWidth(), var3.getHeight());
      }

   }

   public void paintBorder(SynthContext var1, Graphics var2, int var3, int var4, int var5, int var6) {
      var1.getPainter().paintButtonBorder(var1, var2, var3, var4, var5, var6);
   }

   protected Icon getDefaultIcon(AbstractButton var1) {
      SynthContext var2 = this.getContext(var1);
      Icon var3 = var2.getStyle().getIcon(var2, this.getPropertyPrefix() + "icon");
      var2.dispose();
      return var3;
   }

   protected Icon getIcon(AbstractButton var1) {
      Icon var2 = var1.getIcon();
      ButtonModel var3 = var1.getModel();
      if (!var3.isEnabled()) {
         var2 = this.getSynthDisabledIcon(var1, var2);
      } else if (var3.isPressed() && var3.isArmed()) {
         var2 = this.getPressedIcon(var1, this.getSelectedIcon(var1, var2));
      } else if (var1.isRolloverEnabled() && var3.isRollover()) {
         var2 = this.getRolloverIcon(var1, this.getSelectedIcon(var1, var2));
      } else if (var3.isSelected()) {
         var2 = this.getSelectedIcon(var1, var2);
      } else {
         var2 = this.getEnabledIcon(var1, var2);
      }

      return var2 == null ? this.getDefaultIcon(var1) : var2;
   }

   private Icon getIcon(AbstractButton var1, Icon var2, Icon var3, int var4) {
      Icon var5 = var2;
      if (var2 == null) {
         if (var3 instanceof UIResource) {
            var5 = this.getSynthIcon(var1, var4);
            if (var5 == null) {
               var5 = var3;
            }
         } else {
            var5 = var3;
         }
      }

      return var5;
   }

   private Icon getSynthIcon(AbstractButton var1, int var2) {
      return this.style.getIcon(this.getContext(var1, var2), this.getPropertyPrefix() + "icon");
   }

   private Icon getEnabledIcon(AbstractButton var1, Icon var2) {
      if (var2 == null) {
         var2 = this.getSynthIcon(var1, 1);
      }

      return var2;
   }

   private Icon getSelectedIcon(AbstractButton var1, Icon var2) {
      return this.getIcon(var1, var1.getSelectedIcon(), var2, 512);
   }

   private Icon getRolloverIcon(AbstractButton var1, Icon var2) {
      ButtonModel var3 = var1.getModel();
      Icon var4;
      if (var3.isSelected()) {
         var4 = this.getIcon(var1, var1.getRolloverSelectedIcon(), var2, 514);
      } else {
         var4 = this.getIcon(var1, var1.getRolloverIcon(), var2, 2);
      }

      return var4;
   }

   private Icon getPressedIcon(AbstractButton var1, Icon var2) {
      return this.getIcon(var1, var1.getPressedIcon(), var2, 4);
   }

   private Icon getSynthDisabledIcon(AbstractButton var1, Icon var2) {
      ButtonModel var3 = var1.getModel();
      Icon var4;
      if (var3.isSelected()) {
         var4 = this.getIcon(var1, var1.getDisabledSelectedIcon(), var2, 520);
      } else {
         var4 = this.getIcon(var1, var1.getDisabledIcon(), var2, 8);
      }

      return var4;
   }

   private int getTextShiftOffset(SynthContext var1) {
      AbstractButton var2 = (AbstractButton)var1.getComponent();
      ButtonModel var3 = var2.getModel();
      return var3.isArmed() && var3.isPressed() && var2.getPressedIcon() == null ? var1.getStyle().getInt(var1, this.getPropertyPrefix() + "textShiftOffset", 0) : 0;
   }

   public Dimension getMinimumSize(JComponent var1) {
      if (var1.getComponentCount() > 0 && var1.getLayout() != null) {
         return null;
      } else {
         AbstractButton var2 = (AbstractButton)var1;
         SynthContext var3 = this.getContext(var1);
         Dimension var4 = var3.getStyle().getGraphicsUtils(var3).getMinimumSize(var3, var3.getStyle().getFont(var3), var2.getText(), this.getSizingIcon(var2), var2.getHorizontalAlignment(), var2.getVerticalAlignment(), var2.getHorizontalTextPosition(), var2.getVerticalTextPosition(), var2.getIconTextGap(), var2.getDisplayedMnemonicIndex());
         var3.dispose();
         return var4;
      }
   }

   public Dimension getPreferredSize(JComponent var1) {
      if (var1.getComponentCount() > 0 && var1.getLayout() != null) {
         return null;
      } else {
         AbstractButton var2 = (AbstractButton)var1;
         SynthContext var3 = this.getContext(var1);
         Dimension var4 = var3.getStyle().getGraphicsUtils(var3).getPreferredSize(var3, var3.getStyle().getFont(var3), var2.getText(), this.getSizingIcon(var2), var2.getHorizontalAlignment(), var2.getVerticalAlignment(), var2.getHorizontalTextPosition(), var2.getVerticalTextPosition(), var2.getIconTextGap(), var2.getDisplayedMnemonicIndex());
         var3.dispose();
         return var4;
      }
   }

   public Dimension getMaximumSize(JComponent var1) {
      if (var1.getComponentCount() > 0 && var1.getLayout() != null) {
         return null;
      } else {
         AbstractButton var2 = (AbstractButton)var1;
         SynthContext var3 = this.getContext(var1);
         Dimension var4 = var3.getStyle().getGraphicsUtils(var3).getMaximumSize(var3, var3.getStyle().getFont(var3), var2.getText(), this.getSizingIcon(var2), var2.getHorizontalAlignment(), var2.getVerticalAlignment(), var2.getHorizontalTextPosition(), var2.getVerticalTextPosition(), var2.getIconTextGap(), var2.getDisplayedMnemonicIndex());
         var3.dispose();
         return var4;
      }
   }

   protected Icon getSizingIcon(AbstractButton var1) {
      Icon var2 = this.getEnabledIcon(var1, var1.getIcon());
      if (var2 == null) {
         var2 = this.getDefaultIcon(var1);
      }

      return var2;
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (SynthLookAndFeel.shouldUpdateStyle(var1)) {
         this.updateStyle((AbstractButton)var1.getSource());
      }

   }
}
