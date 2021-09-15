package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ActionMap;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.LookAndFeel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.RootPaneUI;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicRootPaneUI extends RootPaneUI implements PropertyChangeListener {
   private static RootPaneUI rootPaneUI = new BasicRootPaneUI();

   public static ComponentUI createUI(JComponent var0) {
      return rootPaneUI;
   }

   public void installUI(JComponent var1) {
      this.installDefaults((JRootPane)var1);
      this.installComponents((JRootPane)var1);
      this.installListeners((JRootPane)var1);
      this.installKeyboardActions((JRootPane)var1);
   }

   public void uninstallUI(JComponent var1) {
      this.uninstallDefaults((JRootPane)var1);
      this.uninstallComponents((JRootPane)var1);
      this.uninstallListeners((JRootPane)var1);
      this.uninstallKeyboardActions((JRootPane)var1);
   }

   protected void installDefaults(JRootPane var1) {
      LookAndFeel.installProperty(var1, "opaque", Boolean.FALSE);
   }

   protected void installComponents(JRootPane var1) {
   }

   protected void installListeners(JRootPane var1) {
      var1.addPropertyChangeListener(this);
   }

   protected void installKeyboardActions(JRootPane var1) {
      InputMap var2 = this.getInputMap(2, var1);
      SwingUtilities.replaceUIInputMap(var1, 2, var2);
      var2 = this.getInputMap(1, var1);
      SwingUtilities.replaceUIInputMap(var1, 1, var2);
      LazyActionMap.installLazyActionMap(var1, BasicRootPaneUI.class, "RootPane.actionMap");
      this.updateDefaultButtonBindings(var1);
   }

   protected void uninstallDefaults(JRootPane var1) {
   }

   protected void uninstallComponents(JRootPane var1) {
   }

   protected void uninstallListeners(JRootPane var1) {
      var1.removePropertyChangeListener(this);
   }

   protected void uninstallKeyboardActions(JRootPane var1) {
      SwingUtilities.replaceUIInputMap(var1, 2, (InputMap)null);
      SwingUtilities.replaceUIActionMap(var1, (ActionMap)null);
   }

   InputMap getInputMap(int var1, JComponent var2) {
      if (var1 == 1) {
         return (InputMap)DefaultLookup.get(var2, this, "RootPane.ancestorInputMap");
      } else {
         return var1 == 2 ? this.createInputMap(var1, var2) : null;
      }
   }

   ComponentInputMap createInputMap(int var1, JComponent var2) {
      return new BasicRootPaneUI.RootPaneInputMap(var2);
   }

   static void loadActionMap(LazyActionMap var0) {
      var0.put(new BasicRootPaneUI.Actions("press"));
      var0.put(new BasicRootPaneUI.Actions("release"));
      var0.put(new BasicRootPaneUI.Actions("postPopup"));
   }

   void updateDefaultButtonBindings(JRootPane var1) {
      InputMap var2;
      for(var2 = SwingUtilities.getUIInputMap(var1, 2); var2 != null && !(var2 instanceof BasicRootPaneUI.RootPaneInputMap); var2 = var2.getParent()) {
      }

      if (var2 != null) {
         var2.clear();
         if (var1.getDefaultButton() != null) {
            Object[] var3 = (Object[])((Object[])DefaultLookup.get(var1, this, "RootPane.defaultButtonWindowKeyBindings"));
            if (var3 != null) {
               LookAndFeel.loadKeyBindings(var2, var3);
            }
         }
      }

   }

   public void propertyChange(PropertyChangeEvent var1) {
      if (var1.getPropertyName().equals("defaultButton")) {
         JRootPane var2 = (JRootPane)var1.getSource();
         this.updateDefaultButtonBindings(var2);
         if (var2.getClientProperty("temporaryDefaultButton") == null) {
            var2.putClientProperty("initialDefaultButton", var1.getNewValue());
         }
      }

   }

   private static class RootPaneInputMap extends ComponentInputMapUIResource {
      public RootPaneInputMap(JComponent var1) {
         super(var1);
      }
   }

   static class Actions extends UIAction {
      public static final String PRESS = "press";
      public static final String RELEASE = "release";
      public static final String POST_POPUP = "postPopup";

      Actions(String var1) {
         super(var1);
      }

      public void actionPerformed(ActionEvent var1) {
         JRootPane var2 = (JRootPane)var1.getSource();
         JButton var3 = var2.getDefaultButton();
         String var4 = this.getName();
         if (var4 == "postPopup") {
            Component var5 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            if (var5 instanceof JComponent) {
               JComponent var6 = (JComponent)var5;
               JPopupMenu var7 = var6.getComponentPopupMenu();
               if (var7 != null) {
                  Point var8 = var6.getPopupLocation((MouseEvent)null);
                  if (var8 == null) {
                     Rectangle var9 = var6.getVisibleRect();
                     var8 = new Point(var9.x + var9.width / 2, var9.y + var9.height / 2);
                  }

                  var7.show(var5, var8.x, var8.y);
               }
            }
         } else if (var3 != null && SwingUtilities.getRootPane(var3) == var2 && var4 == "press") {
            var3.doClick(20);
         }

      }

      public boolean isEnabled(Object var1) {
         String var2 = this.getName();
         if (var2 == "postPopup") {
            MenuElement[] var6 = MenuSelectionManager.defaultManager().getSelectedPath();
            if (var6 != null && var6.length != 0) {
               return false;
            } else {
               Component var4 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
               if (var4 instanceof JComponent) {
                  JComponent var5 = (JComponent)var4;
                  return var5.getComponentPopupMenu() != null;
               } else {
                  return false;
               }
            }
         } else if (var1 != null && var1 instanceof JRootPane) {
            JButton var3 = ((JRootPane)var1).getDefaultButton();
            return var3 != null && var3.getModel().isEnabled();
         } else {
            return true;
         }
      }
   }
}
