package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Enumeration;
import java.util.HashSet;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.View;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;

public class BasicRadioButtonUI extends BasicToggleButtonUI {
   private static final Object BASIC_RADIO_BUTTON_UI_KEY = new Object();
   protected Icon icon;
   private boolean defaults_initialized = false;
   private static final String propertyPrefix = "RadioButton.";
   private KeyListener keyListener = null;
   private static Dimension size = new Dimension();
   private static Rectangle viewRect = new Rectangle();
   private static Rectangle iconRect = new Rectangle();
   private static Rectangle textRect = new Rectangle();
   private static Rectangle prefViewRect = new Rectangle();
   private static Rectangle prefIconRect = new Rectangle();
   private static Rectangle prefTextRect = new Rectangle();
   private static Insets prefInsets = new Insets(0, 0, 0, 0);

   public static ComponentUI createUI(JComponent var0) {
      AppContext var1 = AppContext.getAppContext();
      BasicRadioButtonUI var2 = (BasicRadioButtonUI)var1.get(BASIC_RADIO_BUTTON_UI_KEY);
      if (var2 == null) {
         var2 = new BasicRadioButtonUI();
         var1.put(BASIC_RADIO_BUTTON_UI_KEY, var2);
      }

      return var2;
   }

   protected String getPropertyPrefix() {
      return "RadioButton.";
   }

   protected void installDefaults(AbstractButton var1) {
      super.installDefaults(var1);
      if (!this.defaults_initialized) {
         this.icon = UIManager.getIcon(this.getPropertyPrefix() + "icon");
         this.defaults_initialized = true;
      }

   }

   protected void uninstallDefaults(AbstractButton var1) {
      super.uninstallDefaults(var1);
      this.defaults_initialized = false;
   }

   public Icon getDefaultIcon() {
      return this.icon;
   }

   protected void installListeners(AbstractButton var1) {
      super.installListeners(var1);
      if (var1 instanceof JRadioButton) {
         this.keyListener = this.createKeyListener();
         var1.addKeyListener(this.keyListener);
         var1.setFocusTraversalKeysEnabled(false);
         var1.getActionMap().put("Previous", new BasicRadioButtonUI.SelectPreviousBtn());
         var1.getActionMap().put("Next", new BasicRadioButtonUI.SelectNextBtn());
         var1.getInputMap(1).put(KeyStroke.getKeyStroke("UP"), "Previous");
         var1.getInputMap(1).put(KeyStroke.getKeyStroke("DOWN"), "Next");
         var1.getInputMap(1).put(KeyStroke.getKeyStroke("LEFT"), "Previous");
         var1.getInputMap(1).put(KeyStroke.getKeyStroke("RIGHT"), "Next");
      }
   }

   protected void uninstallListeners(AbstractButton var1) {
      super.uninstallListeners(var1);
      if (var1 instanceof JRadioButton) {
         var1.getActionMap().remove("Previous");
         var1.getActionMap().remove("Next");
         var1.getInputMap(1).remove(KeyStroke.getKeyStroke("UP"));
         var1.getInputMap(1).remove(KeyStroke.getKeyStroke("DOWN"));
         var1.getInputMap(1).remove(KeyStroke.getKeyStroke("LEFT"));
         var1.getInputMap(1).remove(KeyStroke.getKeyStroke("RIGHT"));
         if (this.keyListener != null) {
            var1.removeKeyListener(this.keyListener);
            this.keyListener = null;
         }

      }
   }

   public synchronized void paint(Graphics var1, JComponent var2) {
      AbstractButton var3 = (AbstractButton)var2;
      ButtonModel var4 = var3.getModel();
      Font var5 = var2.getFont();
      var1.setFont(var5);
      FontMetrics var6 = SwingUtilities2.getFontMetrics(var2, var1, var5);
      Insets var7 = var2.getInsets();
      size = var3.getSize(size);
      viewRect.x = var7.left;
      viewRect.y = var7.top;
      viewRect.width = size.width - (var7.right + viewRect.x);
      viewRect.height = size.height - (var7.bottom + viewRect.y);
      iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
      textRect.x = textRect.y = textRect.width = textRect.height = 0;
      Icon var8 = var3.getIcon();
      Object var9 = null;
      Object var10 = null;
      String var11 = SwingUtilities.layoutCompoundLabel(var2, var6, var3.getText(), var8 != null ? var8 : this.getDefaultIcon(), var3.getVerticalAlignment(), var3.getHorizontalAlignment(), var3.getVerticalTextPosition(), var3.getHorizontalTextPosition(), viewRect, iconRect, textRect, var3.getText() == null ? 0 : var3.getIconTextGap());
      if (var2.isOpaque()) {
         var1.setColor(var3.getBackground());
         var1.fillRect(0, 0, size.width, size.height);
      }

      if (var8 != null) {
         if (!var4.isEnabled()) {
            if (var4.isSelected()) {
               var8 = var3.getDisabledSelectedIcon();
            } else {
               var8 = var3.getDisabledIcon();
            }
         } else if (var4.isPressed() && var4.isArmed()) {
            var8 = var3.getPressedIcon();
            if (var8 == null) {
               var8 = var3.getSelectedIcon();
            }
         } else if (var4.isSelected()) {
            if (var3.isRolloverEnabled() && var4.isRollover()) {
               var8 = var3.getRolloverSelectedIcon();
               if (var8 == null) {
                  var8 = var3.getSelectedIcon();
               }
            } else {
               var8 = var3.getSelectedIcon();
            }
         } else if (var3.isRolloverEnabled() && var4.isRollover()) {
            var8 = var3.getRolloverIcon();
         }

         if (var8 == null) {
            var8 = var3.getIcon();
         }

         var8.paintIcon(var2, var1, iconRect.x, iconRect.y);
      } else {
         this.getDefaultIcon().paintIcon(var2, var1, iconRect.x, iconRect.y);
      }

      if (var11 != null) {
         View var12 = (View)var2.getClientProperty("html");
         if (var12 != null) {
            var12.paint(var1, textRect);
         } else {
            this.paintText(var1, var3, textRect, var11);
         }

         if (var3.hasFocus() && var3.isFocusPainted() && textRect.width > 0 && textRect.height > 0) {
            this.paintFocus(var1, textRect, size);
         }
      }

   }

   protected void paintFocus(Graphics var1, Rectangle var2, Dimension var3) {
   }

   public Dimension getPreferredSize(JComponent var1) {
      if (var1.getComponentCount() > 0) {
         return null;
      } else {
         AbstractButton var2 = (AbstractButton)var1;
         String var3 = var2.getText();
         Icon var4 = var2.getIcon();
         if (var4 == null) {
            var4 = this.getDefaultIcon();
         }

         Font var5 = var2.getFont();
         FontMetrics var6 = var2.getFontMetrics(var5);
         prefViewRect.x = prefViewRect.y = 0;
         prefViewRect.width = 32767;
         prefViewRect.height = 32767;
         prefIconRect.x = prefIconRect.y = prefIconRect.width = prefIconRect.height = 0;
         prefTextRect.x = prefTextRect.y = prefTextRect.width = prefTextRect.height = 0;
         SwingUtilities.layoutCompoundLabel(var1, var6, var3, var4, var2.getVerticalAlignment(), var2.getHorizontalAlignment(), var2.getVerticalTextPosition(), var2.getHorizontalTextPosition(), prefViewRect, prefIconRect, prefTextRect, var3 == null ? 0 : var2.getIconTextGap());
         int var7 = Math.min(prefIconRect.x, prefTextRect.x);
         int var8 = Math.max(prefIconRect.x + prefIconRect.width, prefTextRect.x + prefTextRect.width);
         int var9 = Math.min(prefIconRect.y, prefTextRect.y);
         int var10 = Math.max(prefIconRect.y + prefIconRect.height, prefTextRect.y + prefTextRect.height);
         int var11 = var8 - var7;
         int var12 = var10 - var9;
         prefInsets = var2.getInsets(prefInsets);
         var11 += prefInsets.left + prefInsets.right;
         var12 += prefInsets.top + prefInsets.bottom;
         return new Dimension(var11, var12);
      }
   }

   private KeyListener createKeyListener() {
      if (this.keyListener == null) {
         this.keyListener = new BasicRadioButtonUI.KeyHandler();
      }

      return this.keyListener;
   }

   private boolean isValidRadioButtonObj(Object var1) {
      return var1 instanceof JRadioButton && ((JRadioButton)var1).isVisible() && ((JRadioButton)var1).isEnabled();
   }

   private void selectRadioButton(ActionEvent var1, boolean var2) {
      Object var3 = var1.getSource();
      if (this.isValidRadioButtonObj(var3)) {
         BasicRadioButtonUI.ButtonGroupInfo var4 = new BasicRadioButtonUI.ButtonGroupInfo((JRadioButton)var3);
         var4.selectNewButton(var2);
      }
   }

   private class KeyHandler implements KeyListener {
      private KeyHandler() {
      }

      public void keyPressed(KeyEvent var1) {
         if (var1.getKeyCode() == 9) {
            Object var2 = var1.getSource();
            if (BasicRadioButtonUI.this.isValidRadioButtonObj(var2)) {
               var1.consume();
               BasicRadioButtonUI.ButtonGroupInfo var3 = BasicRadioButtonUI.this.new ButtonGroupInfo((JRadioButton)var2);
               var3.jumpToNextComponent(!var1.isShiftDown());
            }
         }

      }

      public void keyReleased(KeyEvent var1) {
      }

      public void keyTyped(KeyEvent var1) {
      }

      // $FF: synthetic method
      KeyHandler(Object var2) {
         this();
      }
   }

   private class ButtonGroupInfo {
      JRadioButton activeBtn = null;
      JRadioButton firstBtn = null;
      JRadioButton lastBtn = null;
      JRadioButton previousBtn = null;
      JRadioButton nextBtn = null;
      HashSet<JRadioButton> btnsInGroup = null;
      boolean srcFound = false;

      public ButtonGroupInfo(JRadioButton var2) {
         this.activeBtn = var2;
         this.btnsInGroup = new HashSet();
      }

      boolean containsInGroup(Object var1) {
         return this.btnsInGroup.contains(var1);
      }

      Component getFocusTransferBaseComponent(boolean var1) {
         JRadioButton var2 = this.activeBtn;
         Container var3 = var2.getFocusCycleRootAncestor();
         if (var3 != null) {
            FocusTraversalPolicy var4 = var3.getFocusTraversalPolicy();
            Component var5 = var1 ? var4.getComponentAfter(var3, this.activeBtn) : var4.getComponentBefore(var3, this.activeBtn);
            if (this.containsInGroup(var5)) {
               var2 = var1 ? this.lastBtn : this.firstBtn;
            }
         }

         return var2;
      }

      boolean getButtonGroupInfo() {
         if (this.activeBtn == null) {
            return false;
         } else {
            this.btnsInGroup.clear();
            ButtonModel var1 = this.activeBtn.getModel();
            if (!(var1 instanceof DefaultButtonModel)) {
               return false;
            } else {
               DefaultButtonModel var2 = (DefaultButtonModel)var1;
               ButtonGroup var3 = var2.getGroup();
               if (var3 == null) {
                  return false;
               } else {
                  Enumeration var4 = var3.getElements();
                  if (var4 == null) {
                     return false;
                  } else {
                     while(var4.hasMoreElements()) {
                        AbstractButton var5 = (AbstractButton)var4.nextElement();
                        if (BasicRadioButtonUI.this.isValidRadioButtonObj(var5)) {
                           this.btnsInGroup.add((JRadioButton)var5);
                           if (null == this.firstBtn) {
                              this.firstBtn = (JRadioButton)var5;
                           }

                           if (this.activeBtn == var5) {
                              this.srcFound = true;
                           } else if (!this.srcFound) {
                              this.previousBtn = (JRadioButton)var5;
                           } else if (this.nextBtn == null) {
                              this.nextBtn = (JRadioButton)var5;
                           }

                           this.lastBtn = (JRadioButton)var5;
                        }
                     }

                     return true;
                  }
               }
            }
         }
      }

      void selectNewButton(boolean var1) {
         if (this.getButtonGroupInfo()) {
            if (this.srcFound) {
               JRadioButton var2 = null;
               if (var1) {
                  var2 = null == this.nextBtn ? this.firstBtn : this.nextBtn;
               } else {
                  var2 = null == this.previousBtn ? this.lastBtn : this.previousBtn;
               }

               if (var2 != null && var2 != this.activeBtn) {
                  var2.requestFocusInWindow();
                  var2.setSelected(true);
               }
            }

         }
      }

      void jumpToNextComponent(boolean var1) {
         if (!this.getButtonGroupInfo()) {
            if (this.activeBtn == null) {
               return;
            }

            this.lastBtn = this.activeBtn;
            this.firstBtn = this.activeBtn;
         }

         JRadioButton var2 = this.activeBtn;
         Component var3 = this.getFocusTransferBaseComponent(var1);
         if (var3 != null) {
            if (var1) {
               KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent(var3);
            } else {
               KeyboardFocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent(var3);
            }
         }

      }
   }

   private class SelectNextBtn extends AbstractAction {
      public SelectNextBtn() {
         super("Next");
      }

      public void actionPerformed(ActionEvent var1) {
         BasicRadioButtonUI.this.selectRadioButton(var1, true);
      }
   }

   private class SelectPreviousBtn extends AbstractAction {
      public SelectPreviousBtn() {
         super("Previous");
      }

      public void actionPerformed(ActionEvent var1) {
         BasicRadioButtonUI.this.selectRadioButton(var1, false);
      }
   }
}
