package javax.swing.plaf.basic;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractButton;
import javax.swing.ActionMap;
import javax.swing.ButtonModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentInputMapUIResource;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicButtonListener implements MouseListener, MouseMotionListener, FocusListener, ChangeListener, PropertyChangeListener {
   private long lastPressedTimestamp = -1L;
   private boolean shouldDiscardRelease = false;

   static void loadActionMap(LazyActionMap var0) {
      var0.put(new BasicButtonListener.Actions("pressed"));
      var0.put(new BasicButtonListener.Actions("released"));
   }

   public BasicButtonListener(AbstractButton var1) {
   }

   public void propertyChange(PropertyChangeEvent var1) {
      String var2 = var1.getPropertyName();
      if (var2 == "mnemonic") {
         this.updateMnemonicBinding((AbstractButton)var1.getSource());
      } else if (var2 == "contentAreaFilled") {
         this.checkOpacity((AbstractButton)var1.getSource());
      } else if (var2 == "text" || "font" == var2 || "foreground" == var2) {
         AbstractButton var3 = (AbstractButton)var1.getSource();
         BasicHTML.updateRenderer(var3, var3.getText());
      }

   }

   protected void checkOpacity(AbstractButton var1) {
      var1.setOpaque(var1.isContentAreaFilled());
   }

   public void installKeyboardActions(JComponent var1) {
      AbstractButton var2 = (AbstractButton)var1;
      this.updateMnemonicBinding(var2);
      LazyActionMap.installLazyActionMap(var1, BasicButtonListener.class, "Button.actionMap");
      InputMap var3 = this.getInputMap(0, var1);
      SwingUtilities.replaceUIInputMap(var1, 0, var3);
   }

   public void uninstallKeyboardActions(JComponent var1) {
      SwingUtilities.replaceUIInputMap(var1, 2, (InputMap)null);
      SwingUtilities.replaceUIInputMap(var1, 0, (InputMap)null);
      SwingUtilities.replaceUIActionMap(var1, (ActionMap)null);
   }

   InputMap getInputMap(int var1, JComponent var2) {
      if (var1 == 0) {
         BasicButtonUI var3 = (BasicButtonUI)BasicLookAndFeel.getUIOfType(((AbstractButton)var2).getUI(), BasicButtonUI.class);
         if (var3 != null) {
            return (InputMap)DefaultLookup.get(var2, var3, var3.getPropertyPrefix() + "focusInputMap");
         }
      }

      return null;
   }

   void updateMnemonicBinding(AbstractButton var1) {
      int var2 = var1.getMnemonic();
      if (var2 != 0) {
         Object var3 = SwingUtilities.getUIInputMap(var1, 2);
         if (var3 == null) {
            var3 = new ComponentInputMapUIResource(var1);
            SwingUtilities.replaceUIInputMap(var1, 2, (InputMap)var3);
         }

         ((InputMap)var3).clear();
         ((InputMap)var3).put(KeyStroke.getKeyStroke(var2, BasicLookAndFeel.getFocusAcceleratorKeyMask(), false), "pressed");
         ((InputMap)var3).put(KeyStroke.getKeyStroke(var2, BasicLookAndFeel.getFocusAcceleratorKeyMask(), true), "released");
         ((InputMap)var3).put(KeyStroke.getKeyStroke(var2, 0, true), "released");
      } else {
         InputMap var4 = SwingUtilities.getUIInputMap(var1, 2);
         if (var4 != null) {
            var4.clear();
         }
      }

   }

   public void stateChanged(ChangeEvent var1) {
      AbstractButton var2 = (AbstractButton)var1.getSource();
      var2.repaint();
   }

   public void focusGained(FocusEvent var1) {
      AbstractButton var2 = (AbstractButton)var1.getSource();
      if (var2 instanceof JButton && ((JButton)var2).isDefaultCapable()) {
         JRootPane var3 = var2.getRootPane();
         if (var3 != null) {
            BasicButtonUI var4 = (BasicButtonUI)BasicLookAndFeel.getUIOfType(var2.getUI(), BasicButtonUI.class);
            if (var4 != null && DefaultLookup.getBoolean(var2, var4, var4.getPropertyPrefix() + "defaultButtonFollowsFocus", true)) {
               var3.putClientProperty("temporaryDefaultButton", var2);
               var3.setDefaultButton((JButton)var2);
               var3.putClientProperty("temporaryDefaultButton", (Object)null);
            }
         }
      }

      var2.repaint();
   }

   public void focusLost(FocusEvent var1) {
      AbstractButton var2 = (AbstractButton)var1.getSource();
      JRootPane var3 = var2.getRootPane();
      if (var3 != null) {
         JButton var4 = (JButton)var3.getClientProperty("initialDefaultButton");
         if (var2 != var4) {
            BasicButtonUI var5 = (BasicButtonUI)BasicLookAndFeel.getUIOfType(var2.getUI(), BasicButtonUI.class);
            if (var5 != null && DefaultLookup.getBoolean(var2, var5, var5.getPropertyPrefix() + "defaultButtonFollowsFocus", true)) {
               var3.setDefaultButton(var4);
            }
         }
      }

      ButtonModel var6 = var2.getModel();
      var6.setPressed(false);
      var6.setArmed(false);
      var2.repaint();
   }

   public void mouseMoved(MouseEvent var1) {
   }

   public void mouseDragged(MouseEvent var1) {
   }

   public void mouseClicked(MouseEvent var1) {
   }

   public void mousePressed(MouseEvent var1) {
      if (SwingUtilities.isLeftMouseButton(var1)) {
         AbstractButton var2 = (AbstractButton)var1.getSource();
         if (var2.contains(var1.getX(), var1.getY())) {
            long var3 = var2.getMultiClickThreshhold();
            long var5 = this.lastPressedTimestamp;
            long var7 = this.lastPressedTimestamp = var1.getWhen();
            if (var5 != -1L && var7 - var5 < var3) {
               this.shouldDiscardRelease = true;
               return;
            }

            ButtonModel var9 = var2.getModel();
            if (!var9.isEnabled()) {
               return;
            }

            if (!var9.isArmed()) {
               var9.setArmed(true);
            }

            var9.setPressed(true);
            if (!var2.hasFocus() && var2.isRequestFocusEnabled()) {
               var2.requestFocus();
            }
         }
      }

   }

   public void mouseReleased(MouseEvent var1) {
      if (SwingUtilities.isLeftMouseButton(var1)) {
         if (this.shouldDiscardRelease) {
            this.shouldDiscardRelease = false;
            return;
         }

         AbstractButton var2 = (AbstractButton)var1.getSource();
         ButtonModel var3 = var2.getModel();
         var3.setPressed(false);
         var3.setArmed(false);
      }

   }

   public void mouseEntered(MouseEvent var1) {
      AbstractButton var2 = (AbstractButton)var1.getSource();
      ButtonModel var3 = var2.getModel();
      if (var2.isRolloverEnabled() && !SwingUtilities.isLeftMouseButton(var1)) {
         var3.setRollover(true);
      }

      if (var3.isPressed()) {
         var3.setArmed(true);
      }

   }

   public void mouseExited(MouseEvent var1) {
      AbstractButton var2 = (AbstractButton)var1.getSource();
      ButtonModel var3 = var2.getModel();
      if (var2.isRolloverEnabled()) {
         var3.setRollover(false);
      }

      var3.setArmed(false);
   }

   private static class Actions extends UIAction {
      private static final String PRESS = "pressed";
      private static final String RELEASE = "released";

      Actions(String var1) {
         super(var1);
      }

      public void actionPerformed(ActionEvent var1) {
         AbstractButton var2 = (AbstractButton)var1.getSource();
         String var3 = this.getName();
         ButtonModel var4;
         if (var3 == "pressed") {
            var4 = var2.getModel();
            var4.setArmed(true);
            var4.setPressed(true);
            if (!var2.hasFocus()) {
               var2.requestFocus();
            }
         } else if (var3 == "released") {
            var4 = var2.getModel();
            var4.setPressed(false);
            var4.setArmed(false);
         }

      }

      public boolean isEnabled(Object var1) {
         return var1 == null || !(var1 instanceof AbstractButton) || ((AbstractButton)var1).getModel().isEnabled();
      }
   }
}
