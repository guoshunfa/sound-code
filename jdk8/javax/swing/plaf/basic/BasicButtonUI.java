package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseMotionListener;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.View;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;

public class BasicButtonUI extends ButtonUI {
   protected int defaultTextIconGap;
   private int shiftOffset = 0;
   protected int defaultTextShiftOffset;
   private static final String propertyPrefix = "Button.";
   private static final Object BASIC_BUTTON_UI_KEY = new Object();
   private static Rectangle viewRect = new Rectangle();
   private static Rectangle textRect = new Rectangle();
   private static Rectangle iconRect = new Rectangle();

   public static ComponentUI createUI(JComponent var0) {
      AppContext var1 = AppContext.getAppContext();
      BasicButtonUI var2 = (BasicButtonUI)var1.get(BASIC_BUTTON_UI_KEY);
      if (var2 == null) {
         var2 = new BasicButtonUI();
         var1.put(BASIC_BUTTON_UI_KEY, var2);
      }

      return var2;
   }

   protected String getPropertyPrefix() {
      return "Button.";
   }

   public void installUI(JComponent var1) {
      this.installDefaults((AbstractButton)var1);
      this.installListeners((AbstractButton)var1);
      this.installKeyboardActions((AbstractButton)var1);
      BasicHTML.updateRenderer(var1, ((AbstractButton)var1).getText());
   }

   protected void installDefaults(AbstractButton var1) {
      String var2 = this.getPropertyPrefix();
      this.defaultTextShiftOffset = UIManager.getInt(var2 + "textShiftOffset");
      if (var1.isContentAreaFilled()) {
         LookAndFeel.installProperty(var1, "opaque", Boolean.TRUE);
      } else {
         LookAndFeel.installProperty(var1, "opaque", Boolean.FALSE);
      }

      if (var1.getMargin() == null || var1.getMargin() instanceof UIResource) {
         var1.setMargin(UIManager.getInsets(var2 + "margin"));
      }

      LookAndFeel.installColorsAndFont(var1, var2 + "background", var2 + "foreground", var2 + "font");
      LookAndFeel.installBorder(var1, var2 + "border");
      Object var3 = UIManager.get(var2 + "rollover");
      if (var3 != null) {
         LookAndFeel.installProperty(var1, "rolloverEnabled", var3);
      }

      LookAndFeel.installProperty(var1, "iconTextGap", 4);
   }

   protected void installListeners(AbstractButton var1) {
      BasicButtonListener var2 = this.createButtonListener(var1);
      if (var2 != null) {
         var1.addMouseListener(var2);
         var1.addMouseMotionListener(var2);
         var1.addFocusListener(var2);
         var1.addPropertyChangeListener(var2);
         var1.addChangeListener(var2);
      }

   }

   protected void installKeyboardActions(AbstractButton var1) {
      BasicButtonListener var2 = this.getButtonListener(var1);
      if (var2 != null) {
         var2.installKeyboardActions(var1);
      }

   }

   public void uninstallUI(JComponent var1) {
      this.uninstallKeyboardActions((AbstractButton)var1);
      this.uninstallListeners((AbstractButton)var1);
      this.uninstallDefaults((AbstractButton)var1);
      BasicHTML.updateRenderer(var1, "");
   }

   protected void uninstallKeyboardActions(AbstractButton var1) {
      BasicButtonListener var2 = this.getButtonListener(var1);
      if (var2 != null) {
         var2.uninstallKeyboardActions(var1);
      }

   }

   protected void uninstallListeners(AbstractButton var1) {
      BasicButtonListener var2 = this.getButtonListener(var1);
      if (var2 != null) {
         var1.removeMouseListener(var2);
         var1.removeMouseMotionListener(var2);
         var1.removeFocusListener(var2);
         var1.removeChangeListener(var2);
         var1.removePropertyChangeListener(var2);
      }

   }

   protected void uninstallDefaults(AbstractButton var1) {
      LookAndFeel.uninstallBorder(var1);
   }

   protected BasicButtonListener createButtonListener(AbstractButton var1) {
      return new BasicButtonListener(var1);
   }

   public int getDefaultTextIconGap(AbstractButton var1) {
      return this.defaultTextIconGap;
   }

   public void paint(Graphics var1, JComponent var2) {
      AbstractButton var3 = (AbstractButton)var2;
      ButtonModel var4 = var3.getModel();
      String var5 = this.layout(var3, SwingUtilities2.getFontMetrics(var3, (Graphics)var1), var3.getWidth(), var3.getHeight());
      this.clearTextShiftOffset();
      if (var4.isArmed() && var4.isPressed()) {
         this.paintButtonPressed(var1, var3);
      }

      if (var3.getIcon() != null) {
         this.paintIcon(var1, var2, iconRect);
      }

      if (var5 != null && !var5.equals("")) {
         View var6 = (View)var2.getClientProperty("html");
         if (var6 != null) {
            var6.paint(var1, textRect);
         } else {
            this.paintText(var1, var3, textRect, var5);
         }
      }

      if (var3.isFocusPainted() && var3.hasFocus()) {
         this.paintFocus(var1, var3, viewRect, textRect, iconRect);
      }

   }

   protected void paintIcon(Graphics var1, JComponent var2, Rectangle var3) {
      AbstractButton var4 = (AbstractButton)var2;
      ButtonModel var5 = var4.getModel();
      Icon var6 = var4.getIcon();
      Icon var7 = null;
      if (var6 != null) {
         Icon var8 = null;
         if (var5.isSelected()) {
            var8 = var4.getSelectedIcon();
            if (var8 != null) {
               var6 = var8;
            }
         }

         if (!var5.isEnabled()) {
            if (var5.isSelected()) {
               var7 = var4.getDisabledSelectedIcon();
               if (var7 == null) {
                  var7 = var8;
               }
            }

            if (var7 == null) {
               var7 = var4.getDisabledIcon();
            }
         } else if (var5.isPressed() && var5.isArmed()) {
            var7 = var4.getPressedIcon();
            if (var7 != null) {
               this.clearTextShiftOffset();
            }
         } else if (var4.isRolloverEnabled() && var5.isRollover()) {
            if (var5.isSelected()) {
               var7 = var4.getRolloverSelectedIcon();
               if (var7 == null) {
                  var7 = var8;
               }
            }

            if (var7 == null) {
               var7 = var4.getRolloverIcon();
            }
         }

         if (var7 != null) {
            var6 = var7;
         }

         if (var5.isPressed() && var5.isArmed()) {
            var6.paintIcon(var2, var1, var3.x + this.getTextShiftOffset(), var3.y + this.getTextShiftOffset());
         } else {
            var6.paintIcon(var2, var1, var3.x, var3.y);
         }

      }
   }

   protected void paintText(Graphics var1, JComponent var2, Rectangle var3, String var4) {
      AbstractButton var5 = (AbstractButton)var2;
      ButtonModel var6 = var5.getModel();
      FontMetrics var7 = SwingUtilities2.getFontMetrics(var2, var1);
      int var8 = var5.getDisplayedMnemonicIndex();
      if (var6.isEnabled()) {
         var1.setColor(var5.getForeground());
         SwingUtilities2.drawStringUnderlineCharAt(var2, var1, var4, var8, var3.x + this.getTextShiftOffset(), var3.y + var7.getAscent() + this.getTextShiftOffset());
      } else {
         var1.setColor(var5.getBackground().brighter());
         SwingUtilities2.drawStringUnderlineCharAt(var2, var1, var4, var8, var3.x, var3.y + var7.getAscent());
         var1.setColor(var5.getBackground().darker());
         SwingUtilities2.drawStringUnderlineCharAt(var2, var1, var4, var8, var3.x - 1, var3.y + var7.getAscent() - 1);
      }

   }

   protected void paintText(Graphics var1, AbstractButton var2, Rectangle var3, String var4) {
      this.paintText(var1, (JComponent)var2, var3, var4);
   }

   protected void paintFocus(Graphics var1, AbstractButton var2, Rectangle var3, Rectangle var4, Rectangle var5) {
   }

   protected void paintButtonPressed(Graphics var1, AbstractButton var2) {
   }

   protected void clearTextShiftOffset() {
      this.shiftOffset = 0;
   }

   protected void setTextShiftOffset() {
      this.shiftOffset = this.defaultTextShiftOffset;
   }

   protected int getTextShiftOffset() {
      return this.shiftOffset;
   }

   public Dimension getMinimumSize(JComponent var1) {
      Dimension var2 = this.getPreferredSize(var1);
      View var3 = (View)var1.getClientProperty("html");
      if (var3 != null) {
         var2.width = (int)((float)var2.width - (var3.getPreferredSpan(0) - var3.getMinimumSpan(0)));
      }

      return var2;
   }

   public Dimension getPreferredSize(JComponent var1) {
      AbstractButton var2 = (AbstractButton)var1;
      return BasicGraphicsUtils.getPreferredButtonSize(var2, var2.getIconTextGap());
   }

   public Dimension getMaximumSize(JComponent var1) {
      Dimension var2 = this.getPreferredSize(var1);
      View var3 = (View)var1.getClientProperty("html");
      if (var3 != null) {
         var2.width = (int)((float)var2.width + (var3.getMaximumSpan(0) - var3.getPreferredSpan(0)));
      }

      return var2;
   }

   public int getBaseline(JComponent var1, int var2, int var3) {
      super.getBaseline(var1, var2, var3);
      AbstractButton var4 = (AbstractButton)var1;
      String var5 = var4.getText();
      if (var5 != null && !"".equals(var5)) {
         FontMetrics var6 = var4.getFontMetrics(var4.getFont());
         this.layout(var4, var6, var2, var3);
         return BasicHTML.getBaseline(var4, textRect.y, var6.getAscent(), textRect.width, textRect.height);
      } else {
         return -1;
      }
   }

   public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent var1) {
      super.getBaselineResizeBehavior(var1);
      if (var1.getClientProperty("html") != null) {
         return Component.BaselineResizeBehavior.OTHER;
      } else {
         switch(((AbstractButton)var1).getVerticalAlignment()) {
         case 0:
            return Component.BaselineResizeBehavior.CENTER_OFFSET;
         case 1:
            return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
         case 2:
         default:
            return Component.BaselineResizeBehavior.OTHER;
         case 3:
            return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
         }
      }
   }

   private String layout(AbstractButton var1, FontMetrics var2, int var3, int var4) {
      Insets var5 = var1.getInsets();
      viewRect.x = var5.left;
      viewRect.y = var5.top;
      viewRect.width = var3 - (var5.right + viewRect.x);
      viewRect.height = var4 - (var5.bottom + viewRect.y);
      textRect.x = textRect.y = textRect.width = textRect.height = 0;
      iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
      return SwingUtilities.layoutCompoundLabel(var1, var2, var1.getText(), var1.getIcon(), var1.getVerticalAlignment(), var1.getHorizontalAlignment(), var1.getVerticalTextPosition(), var1.getHorizontalTextPosition(), viewRect, iconRect, textRect, var1.getText() == null ? 0 : var1.getIconTextGap());
   }

   private BasicButtonListener getButtonListener(AbstractButton var1) {
      MouseMotionListener[] var2 = var1.getMouseMotionListeners();
      if (var2 != null) {
         MouseMotionListener[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            MouseMotionListener var6 = var3[var5];
            if (var6 instanceof BasicButtonListener) {
               return (BasicButtonListener)var6;
            }
         }
      }

      return null;
   }
}
