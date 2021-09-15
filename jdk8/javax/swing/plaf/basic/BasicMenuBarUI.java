package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.LookAndFeel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.MenuBarUI;
import javax.swing.plaf.UIResource;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicMenuBarUI extends MenuBarUI {
   protected JMenuBar menuBar = null;
   protected ContainerListener containerListener;
   protected ChangeListener changeListener;
   private BasicMenuBarUI.Handler handler;

   public static ComponentUI createUI(JComponent var0) {
      return new BasicMenuBarUI();
   }

   static void loadActionMap(LazyActionMap var0) {
      var0.put(new BasicMenuBarUI.Actions("takeFocus"));
   }

   public void installUI(JComponent var1) {
      this.menuBar = (JMenuBar)var1;
      this.installDefaults();
      this.installListeners();
      this.installKeyboardActions();
   }

   protected void installDefaults() {
      if (this.menuBar.getLayout() == null || this.menuBar.getLayout() instanceof UIResource) {
         this.menuBar.setLayout(new DefaultMenuLayout(this.menuBar, 2));
      }

      LookAndFeel.installProperty(this.menuBar, "opaque", Boolean.TRUE);
      LookAndFeel.installBorder(this.menuBar, "MenuBar.border");
      LookAndFeel.installColorsAndFont(this.menuBar, "MenuBar.background", "MenuBar.foreground", "MenuBar.font");
   }

   protected void installListeners() {
      this.containerListener = this.createContainerListener();
      this.changeListener = this.createChangeListener();

      for(int var1 = 0; var1 < this.menuBar.getMenuCount(); ++var1) {
         JMenu var2 = this.menuBar.getMenu(var1);
         if (var2 != null) {
            var2.getModel().addChangeListener(this.changeListener);
         }
      }

      this.menuBar.addContainerListener(this.containerListener);
   }

   protected void installKeyboardActions() {
      InputMap var1 = this.getInputMap(2);
      SwingUtilities.replaceUIInputMap(this.menuBar, 2, var1);
      LazyActionMap.installLazyActionMap(this.menuBar, BasicMenuBarUI.class, "MenuBar.actionMap");
   }

   InputMap getInputMap(int var1) {
      if (var1 == 2) {
         Object[] var2 = (Object[])((Object[])DefaultLookup.get(this.menuBar, this, "MenuBar.windowBindings"));
         if (var2 != null) {
            return LookAndFeel.makeComponentInputMap(this.menuBar, var2);
         }
      }

      return null;
   }

   public void uninstallUI(JComponent var1) {
      this.uninstallDefaults();
      this.uninstallListeners();
      this.uninstallKeyboardActions();
      this.menuBar = null;
   }

   protected void uninstallDefaults() {
      if (this.menuBar != null) {
         LookAndFeel.uninstallBorder(this.menuBar);
      }

   }

   protected void uninstallListeners() {
      this.menuBar.removeContainerListener(this.containerListener);

      for(int var1 = 0; var1 < this.menuBar.getMenuCount(); ++var1) {
         JMenu var2 = this.menuBar.getMenu(var1);
         if (var2 != null) {
            var2.getModel().removeChangeListener(this.changeListener);
         }
      }

      this.containerListener = null;
      this.changeListener = null;
      this.handler = null;
   }

   protected void uninstallKeyboardActions() {
      SwingUtilities.replaceUIInputMap(this.menuBar, 2, (InputMap)null);
      SwingUtilities.replaceUIActionMap(this.menuBar, (ActionMap)null);
   }

   protected ContainerListener createContainerListener() {
      return this.getHandler();
   }

   protected ChangeListener createChangeListener() {
      return this.getHandler();
   }

   private BasicMenuBarUI.Handler getHandler() {
      if (this.handler == null) {
         this.handler = new BasicMenuBarUI.Handler();
      }

      return this.handler;
   }

   public Dimension getMinimumSize(JComponent var1) {
      return null;
   }

   public Dimension getMaximumSize(JComponent var1) {
      return null;
   }

   private static class Actions extends UIAction {
      private static final String TAKE_FOCUS = "takeFocus";

      Actions(String var1) {
         super(var1);
      }

      public void actionPerformed(ActionEvent var1) {
         JMenuBar var2 = (JMenuBar)var1.getSource();
         MenuSelectionManager var3 = MenuSelectionManager.defaultManager();
         JMenu var6 = var2.getMenu(0);
         if (var6 != null) {
            MenuElement[] var4 = new MenuElement[]{var2, var6, var6.getPopupMenu()};
            var3.setSelectedPath(var4);
         }

      }
   }

   private class Handler implements ChangeListener, ContainerListener {
      private Handler() {
      }

      public void stateChanged(ChangeEvent var1) {
         int var2 = 0;

         for(int var3 = BasicMenuBarUI.this.menuBar.getMenuCount(); var2 < var3; ++var2) {
            JMenu var4 = BasicMenuBarUI.this.menuBar.getMenu(var2);
            if (var4 != null && var4.isSelected()) {
               BasicMenuBarUI.this.menuBar.getSelectionModel().setSelectedIndex(var2);
               break;
            }
         }

      }

      public void componentAdded(ContainerEvent var1) {
         Component var2 = var1.getChild();
         if (var2 instanceof JMenu) {
            ((JMenu)var2).getModel().addChangeListener(BasicMenuBarUI.this.changeListener);
         }

      }

      public void componentRemoved(ContainerEvent var1) {
         Component var2 = var1.getChild();
         if (var2 instanceof JMenu) {
            ((JMenu)var2).getModel().removeChangeListener(BasicMenuBarUI.this.changeListener);
         }

      }

      // $FF: synthetic method
      Handler(Object var2) {
         this();
      }
   }
}
