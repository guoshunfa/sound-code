package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.InputMapUIResource;
import javax.swing.plaf.LabelUI;
import javax.swing.text.View;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicLabelUI extends LabelUI implements PropertyChangeListener {
   protected static BasicLabelUI labelUI = new BasicLabelUI();
   private static final Object BASIC_LABEL_UI_KEY = new Object();
   private Rectangle paintIconR = new Rectangle();
   private Rectangle paintTextR = new Rectangle();

   static void loadActionMap(LazyActionMap var0) {
      var0.put(new BasicLabelUI.Actions("press"));
      var0.put(new BasicLabelUI.Actions("release"));
   }

   protected String layoutCL(JLabel var1, FontMetrics var2, String var3, Icon var4, Rectangle var5, Rectangle var6, Rectangle var7) {
      return SwingUtilities.layoutCompoundLabel(var1, var2, var3, var4, var1.getVerticalAlignment(), var1.getHorizontalAlignment(), var1.getVerticalTextPosition(), var1.getHorizontalTextPosition(), var5, var6, var7, var1.getIconTextGap());
   }

   protected void paintEnabledText(JLabel var1, Graphics var2, String var3, int var4, int var5) {
      int var6 = var1.getDisplayedMnemonicIndex();
      var2.setColor(var1.getForeground());
      SwingUtilities2.drawStringUnderlineCharAt(var1, var2, var3, var6, var4, var5);
   }

   protected void paintDisabledText(JLabel var1, Graphics var2, String var3, int var4, int var5) {
      int var6 = var1.getDisplayedMnemonicIndex();
      Color var7 = var1.getBackground();
      var2.setColor(var7.brighter());
      SwingUtilities2.drawStringUnderlineCharAt(var1, var2, var3, var6, var4 + 1, var5 + 1);
      var2.setColor(var7.darker());
      SwingUtilities2.drawStringUnderlineCharAt(var1, var2, var3, var6, var4, var5);
   }

   public void paint(Graphics var1, JComponent var2) {
      JLabel var3 = (JLabel)var2;
      String var4 = var3.getText();
      Icon var5 = var3.isEnabled() ? var3.getIcon() : var3.getDisabledIcon();
      if (var5 != null || var4 != null) {
         FontMetrics var6 = SwingUtilities2.getFontMetrics(var3, (Graphics)var1);
         String var7 = this.layout(var3, var6, var2.getWidth(), var2.getHeight());
         if (var5 != null) {
            var5.paintIcon(var2, var1, this.paintIconR.x, this.paintIconR.y);
         }

         if (var4 != null) {
            View var8 = (View)var2.getClientProperty("html");
            if (var8 != null) {
               var8.paint(var1, this.paintTextR);
            } else {
               int var9 = this.paintTextR.x;
               int var10 = this.paintTextR.y + var6.getAscent();
               if (var3.isEnabled()) {
                  this.paintEnabledText(var3, var1, var7, var9, var10);
               } else {
                  this.paintDisabledText(var3, var1, var7, var9, var10);
               }
            }
         }

      }
   }

   private String layout(JLabel var1, FontMetrics var2, int var3, int var4) {
      Insets var5 = var1.getInsets((Insets)null);
      String var6 = var1.getText();
      Icon var7 = var1.isEnabled() ? var1.getIcon() : var1.getDisabledIcon();
      Rectangle var8 = new Rectangle();
      var8.x = var5.left;
      var8.y = var5.top;
      var8.width = var3 - (var5.left + var5.right);
      var8.height = var4 - (var5.top + var5.bottom);
      this.paintIconR.x = this.paintIconR.y = this.paintIconR.width = this.paintIconR.height = 0;
      this.paintTextR.x = this.paintTextR.y = this.paintTextR.width = this.paintTextR.height = 0;
      return this.layoutCL(var1, var2, var6, var7, var8, this.paintIconR, this.paintTextR);
   }

   public Dimension getPreferredSize(JComponent var1) {
      JLabel var2 = (JLabel)var1;
      String var3 = var2.getText();
      Icon var4 = var2.isEnabled() ? var2.getIcon() : var2.getDisabledIcon();
      Insets var5 = var2.getInsets((Insets)null);
      Font var6 = var2.getFont();
      int var7 = var5.left + var5.right;
      int var8 = var5.top + var5.bottom;
      if (var4 != null || var3 != null && (var3 == null || var6 != null)) {
         if (var3 != null && (var4 == null || var6 != null)) {
            FontMetrics var9 = var2.getFontMetrics(var6);
            Rectangle var10 = new Rectangle();
            Rectangle var11 = new Rectangle();
            Rectangle var12 = new Rectangle();
            var10.x = var10.y = var10.width = var10.height = 0;
            var11.x = var11.y = var11.width = var11.height = 0;
            var12.x = var7;
            var12.y = var8;
            var12.width = var12.height = 32767;
            this.layoutCL(var2, var9, var3, var4, var12, var10, var11);
            int var13 = Math.min(var10.x, var11.x);
            int var14 = Math.max(var10.x + var10.width, var11.x + var11.width);
            int var15 = Math.min(var10.y, var11.y);
            int var16 = Math.max(var10.y + var10.height, var11.y + var11.height);
            Dimension var17 = new Dimension(var14 - var13, var16 - var15);
            var17.width += var7;
            var17.height += var8;
            return var17;
         } else {
            return new Dimension(var4.getIconWidth() + var7, var4.getIconHeight() + var8);
         }
      } else {
         return new Dimension(var7, var8);
      }
   }

   public Dimension getMinimumSize(JComponent var1) {
      Dimension var2 = this.getPreferredSize(var1);
      View var3 = (View)var1.getClientProperty("html");
      if (var3 != null) {
         var2.width = (int)((float)var2.width - (var3.getPreferredSpan(0) - var3.getMinimumSpan(0)));
      }

      return var2;
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
      JLabel var4 = (JLabel)var1;
      String var5 = var4.getText();
      if (var5 != null && !"".equals(var5) && var4.getFont() != null) {
         FontMetrics var6 = var4.getFontMetrics(var4.getFont());
         this.layout(var4, var6, var2, var3);
         return BasicHTML.getBaseline(var4, this.paintTextR.y, var6.getAscent(), this.paintTextR.width, this.paintTextR.height);
      } else {
         return -1;
      }
   }

   public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent var1) {
      super.getBaselineResizeBehavior(var1);
      if (var1.getClientProperty("html") != null) {
         return Component.BaselineResizeBehavior.OTHER;
      } else {
         switch(((JLabel)var1).getVerticalAlignment()) {
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

   public void installUI(JComponent var1) {
      this.installDefaults((JLabel)var1);
      this.installComponents((JLabel)var1);
      this.installListeners((JLabel)var1);
      this.installKeyboardActions((JLabel)var1);
   }

   public void uninstallUI(JComponent var1) {
      this.uninstallDefaults((JLabel)var1);
      this.uninstallComponents((JLabel)var1);
      this.uninstallListeners((JLabel)var1);
      this.uninstallKeyboardActions((JLabel)var1);
   }

   protected void installDefaults(JLabel var1) {
      LookAndFeel.installColorsAndFont(var1, "Label.background", "Label.foreground", "Label.font");
      LookAndFeel.installProperty(var1, "opaque", Boolean.FALSE);
   }

   protected void installListeners(JLabel var1) {
      var1.addPropertyChangeListener(this);
   }

   protected void installComponents(JLabel var1) {
      BasicHTML.updateRenderer(var1, var1.getText());
      var1.setInheritsPopupMenu(true);
   }

   protected void installKeyboardActions(JLabel var1) {
      int var2 = var1.getDisplayedMnemonic();
      Component var3 = var1.getLabelFor();
      if (var2 != 0 && var3 != null) {
         LazyActionMap.installLazyActionMap(var1, BasicLabelUI.class, "Label.actionMap");
         Object var5 = SwingUtilities.getUIInputMap(var1, 2);
         if (var5 == null) {
            var5 = new ComponentInputMapUIResource(var1);
            SwingUtilities.replaceUIInputMap(var1, 2, (InputMap)var5);
         }

         ((InputMap)var5).clear();
         ((InputMap)var5).put(KeyStroke.getKeyStroke(var2, BasicLookAndFeel.getFocusAcceleratorKeyMask(), false), "press");
      } else {
         InputMap var4 = SwingUtilities.getUIInputMap(var1, 2);
         if (var4 != null) {
            var4.clear();
         }
      }

   }

   protected void uninstallDefaults(JLabel var1) {
   }

   protected void uninstallListeners(JLabel var1) {
      var1.removePropertyChangeListener(this);
   }

   protected void uninstallComponents(JLabel var1) {
      BasicHTML.updateRenderer(var1, "");
   }

   protected void uninstallKeyboardActions(JLabel var1) {
      SwingUtilities.replaceUIInputMap(var1, 0, (InputMap)null);
      SwingUtilities.replaceUIInputMap(var1, 2, (InputMap)null);
      SwingUtilities.replaceUIActionMap(var1, (ActionMap)null);
   }

   public static ComponentUI createUI(JComponent var0) {
      if (System.getSecurityManager() != null) {
         AppContext var1 = AppContext.getAppContext();
         BasicLabelUI var2 = (BasicLabelUI)var1.get(BASIC_LABEL_UI_KEY);
         if (var2 == null) {
            var2 = new BasicLabelUI();
            var1.put(BASIC_LABEL_UI_KEY, var2);
         }

         return var2;
      } else {
         return labelUI;
      }
   }

   public void propertyChange(PropertyChangeEvent var1) {
      String var2 = var1.getPropertyName();
      if (var2 != "text" && "font" != var2 && "foreground" != var2) {
         if (var2 == "labelFor" || var2 == "displayedMnemonic") {
            this.installKeyboardActions((JLabel)var1.getSource());
         }
      } else {
         JLabel var3 = (JLabel)var1.getSource();
         String var4 = var3.getText();
         BasicHTML.updateRenderer(var3, var4);
      }

   }

   private static class Actions extends UIAction {
      private static final String PRESS = "press";
      private static final String RELEASE = "release";

      Actions(String var1) {
         super(var1);
      }

      public void actionPerformed(ActionEvent var1) {
         JLabel var2 = (JLabel)var1.getSource();
         String var3 = this.getName();
         if (var3 == "press") {
            this.doPress(var2);
         } else if (var3 == "release") {
            this.doRelease(var2, var1.getActionCommand() != null);
         }

      }

      private void doPress(JLabel var1) {
         Component var2 = var1.getLabelFor();
         if (var2 != null && var2.isEnabled()) {
            Object var3 = SwingUtilities.getUIInputMap(var1, 0);
            if (var3 == null) {
               var3 = new InputMapUIResource();
               SwingUtilities.replaceUIInputMap(var1, 0, (InputMap)var3);
            }

            int var4 = var1.getDisplayedMnemonic();
            this.putOnRelease((InputMap)var3, var4, BasicLookAndFeel.getFocusAcceleratorKeyMask());
            this.putOnRelease((InputMap)var3, var4, 0);
            this.putOnRelease((InputMap)var3, 18, 0);
            var1.requestFocus();
         }

      }

      private void doRelease(JLabel var1, boolean var2) {
         Component var3 = var1.getLabelFor();
         if (var3 != null && var3.isEnabled()) {
            InputMap var4;
            int var5;
            if (var1.hasFocus()) {
               var4 = SwingUtilities.getUIInputMap(var1, 0);
               if (var4 != null) {
                  var5 = var1.getDisplayedMnemonic();
                  this.removeOnRelease(var4, var5, BasicLookAndFeel.getFocusAcceleratorKeyMask());
                  this.removeOnRelease(var4, var5, 0);
                  this.removeOnRelease(var4, 18, 0);
               }

               Object var6 = SwingUtilities.getUIInputMap(var1, 2);
               if (var6 == null) {
                  var6 = new InputMapUIResource();
                  SwingUtilities.replaceUIInputMap(var1, 2, (InputMap)var6);
               }

               var5 = var1.getDisplayedMnemonic();
               if (var2) {
                  this.putOnRelease((InputMap)var6, 18, 0);
               } else {
                  this.putOnRelease((InputMap)var6, var5, BasicLookAndFeel.getFocusAcceleratorKeyMask());
                  this.putOnRelease((InputMap)var6, var5, 0);
               }

               if (var3 instanceof Container && ((Container)var3).isFocusCycleRoot()) {
                  var3.requestFocus();
               } else {
                  SwingUtilities2.compositeRequestFocus(var3);
               }
            } else {
               var4 = SwingUtilities.getUIInputMap(var1, 2);
               var5 = var1.getDisplayedMnemonic();
               if (var4 != null) {
                  if (var2) {
                     this.removeOnRelease(var4, var5, BasicLookAndFeel.getFocusAcceleratorKeyMask());
                     this.removeOnRelease(var4, var5, 0);
                  } else {
                     this.removeOnRelease(var4, 18, 0);
                  }
               }
            }
         }

      }

      private void putOnRelease(InputMap var1, int var2, int var3) {
         var1.put(KeyStroke.getKeyStroke(var2, var3, true), "release");
      }

      private void removeOnRelease(InputMap var1, int var2, int var3) {
         var1.remove(KeyStroke.getKeyStroke(var2, var3, true));
      }
   }
}
