package javax.swing;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuDragMouseEvent;
import javax.swing.event.MenuDragMouseListener;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.plaf.MenuItemUI;

public class JMenuItem extends AbstractButton implements Accessible, MenuElement {
   private static final String uiClassID = "MenuItemUI";
   private static final boolean TRACE = false;
   private static final boolean VERBOSE = false;
   private static final boolean DEBUG = false;
   private boolean isMouseDragged;
   private KeyStroke accelerator;

   public JMenuItem() {
      this((String)null, (Icon)null);
   }

   public JMenuItem(Icon var1) {
      this((String)null, var1);
   }

   public JMenuItem(String var1) {
      this(var1, (Icon)null);
   }

   public JMenuItem(Action var1) {
      this();
      this.setAction(var1);
   }

   public JMenuItem(String var1, Icon var2) {
      this.isMouseDragged = false;
      this.setModel(new DefaultButtonModel());
      this.init(var1, var2);
      this.initFocusability();
   }

   public JMenuItem(String var1, int var2) {
      this.isMouseDragged = false;
      this.setModel(new DefaultButtonModel());
      this.init(var1, (Icon)null);
      this.setMnemonic(var2);
      this.initFocusability();
   }

   public void setModel(ButtonModel var1) {
      super.setModel(var1);
      if (var1 instanceof DefaultButtonModel) {
         ((DefaultButtonModel)var1).setMenuItem(true);
      }

   }

   void initFocusability() {
      this.setFocusable(false);
   }

   protected void init(String var1, Icon var2) {
      if (var1 != null) {
         this.setText(var1);
      }

      if (var2 != null) {
         this.setIcon(var2);
      }

      this.addFocusListener(new JMenuItem.MenuItemFocusListener());
      this.setUIProperty("borderPainted", Boolean.FALSE);
      this.setFocusPainted(false);
      this.setHorizontalTextPosition(11);
      this.setHorizontalAlignment(10);
      this.updateUI();
   }

   public void setUI(MenuItemUI var1) {
      super.setUI(var1);
   }

   public void updateUI() {
      this.setUI((MenuItemUI)UIManager.getUI(this));
   }

   public String getUIClassID() {
      return "MenuItemUI";
   }

   public void setArmed(boolean var1) {
      ButtonModel var2 = this.getModel();
      boolean var3 = var2.isArmed();
      if (var2.isArmed() != var1) {
         var2.setArmed(var1);
      }

   }

   public boolean isArmed() {
      ButtonModel var1 = this.getModel();
      return var1.isArmed();
   }

   public void setEnabled(boolean var1) {
      if (!var1 && !UIManager.getBoolean("MenuItem.disabledAreNavigable")) {
         this.setArmed(false);
      }

      super.setEnabled(var1);
   }

   boolean alwaysOnTop() {
      return SwingUtilities.getAncestorOfClass(JInternalFrame.class, this) == null;
   }

   public void setAccelerator(KeyStroke var1) {
      KeyStroke var2 = this.accelerator;
      this.accelerator = var1;
      this.repaint();
      this.revalidate();
      this.firePropertyChange("accelerator", var2, this.accelerator);
   }

   public KeyStroke getAccelerator() {
      return this.accelerator;
   }

   protected void configurePropertiesFromAction(Action var1) {
      super.configurePropertiesFromAction(var1);
      this.configureAcceleratorFromAction(var1);
   }

   void setIconFromAction(Action var1) {
      Icon var2 = null;
      if (var1 != null) {
         var2 = (Icon)var1.getValue("SmallIcon");
      }

      this.setIcon(var2);
   }

   void largeIconChanged(Action var1) {
   }

   void smallIconChanged(Action var1) {
      this.setIconFromAction(var1);
   }

   void configureAcceleratorFromAction(Action var1) {
      KeyStroke var2 = var1 == null ? null : (KeyStroke)var1.getValue("AcceleratorKey");
      this.setAccelerator(var2);
   }

   protected void actionPropertyChanged(Action var1, String var2) {
      if (var2 == "AcceleratorKey") {
         this.configureAcceleratorFromAction(var1);
      } else {
         super.actionPropertyChanged(var1, var2);
      }

   }

   public void processMouseEvent(MouseEvent var1, MenuElement[] var2, MenuSelectionManager var3) {
      this.processMenuDragMouseEvent(new MenuDragMouseEvent(var1.getComponent(), var1.getID(), var1.getWhen(), var1.getModifiers(), var1.getX(), var1.getY(), var1.getXOnScreen(), var1.getYOnScreen(), var1.getClickCount(), var1.isPopupTrigger(), var2, var3));
   }

   public void processKeyEvent(KeyEvent var1, MenuElement[] var2, MenuSelectionManager var3) {
      MenuKeyEvent var4 = new MenuKeyEvent(var1.getComponent(), var1.getID(), var1.getWhen(), var1.getModifiers(), var1.getKeyCode(), var1.getKeyChar(), var2, var3);
      this.processMenuKeyEvent(var4);
      if (var4.isConsumed()) {
         var1.consume();
      }

   }

   public void processMenuDragMouseEvent(MenuDragMouseEvent var1) {
      switch(var1.getID()) {
      case 502:
         if (this.isMouseDragged) {
            this.fireMenuDragMouseReleased(var1);
         }
      case 503:
      default:
         break;
      case 504:
         this.isMouseDragged = false;
         this.fireMenuDragMouseEntered(var1);
         break;
      case 505:
         this.isMouseDragged = false;
         this.fireMenuDragMouseExited(var1);
         break;
      case 506:
         this.isMouseDragged = true;
         this.fireMenuDragMouseDragged(var1);
      }

   }

   public void processMenuKeyEvent(MenuKeyEvent var1) {
      switch(var1.getID()) {
      case 400:
         this.fireMenuKeyTyped(var1);
         break;
      case 401:
         this.fireMenuKeyPressed(var1);
         break;
      case 402:
         this.fireMenuKeyReleased(var1);
      }

   }

   protected void fireMenuDragMouseEntered(MenuDragMouseEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();

      for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
         if (var2[var3] == MenuDragMouseListener.class) {
            ((MenuDragMouseListener)var2[var3 + 1]).menuDragMouseEntered(var1);
         }
      }

   }

   protected void fireMenuDragMouseExited(MenuDragMouseEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();

      for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
         if (var2[var3] == MenuDragMouseListener.class) {
            ((MenuDragMouseListener)var2[var3 + 1]).menuDragMouseExited(var1);
         }
      }

   }

   protected void fireMenuDragMouseDragged(MenuDragMouseEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();

      for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
         if (var2[var3] == MenuDragMouseListener.class) {
            ((MenuDragMouseListener)var2[var3 + 1]).menuDragMouseDragged(var1);
         }
      }

   }

   protected void fireMenuDragMouseReleased(MenuDragMouseEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();

      for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
         if (var2[var3] == MenuDragMouseListener.class) {
            ((MenuDragMouseListener)var2[var3 + 1]).menuDragMouseReleased(var1);
         }
      }

   }

   protected void fireMenuKeyPressed(MenuKeyEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();

      for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
         if (var2[var3] == MenuKeyListener.class) {
            ((MenuKeyListener)var2[var3 + 1]).menuKeyPressed(var1);
         }
      }

   }

   protected void fireMenuKeyReleased(MenuKeyEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();

      for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
         if (var2[var3] == MenuKeyListener.class) {
            ((MenuKeyListener)var2[var3 + 1]).menuKeyReleased(var1);
         }
      }

   }

   protected void fireMenuKeyTyped(MenuKeyEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();

      for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
         if (var2[var3] == MenuKeyListener.class) {
            ((MenuKeyListener)var2[var3 + 1]).menuKeyTyped(var1);
         }
      }

   }

   public void menuSelectionChanged(boolean var1) {
      this.setArmed(var1);
   }

   public MenuElement[] getSubElements() {
      return new MenuElement[0];
   }

   public Component getComponent() {
      return this;
   }

   public void addMenuDragMouseListener(MenuDragMouseListener var1) {
      this.listenerList.add(MenuDragMouseListener.class, var1);
   }

   public void removeMenuDragMouseListener(MenuDragMouseListener var1) {
      this.listenerList.remove(MenuDragMouseListener.class, var1);
   }

   public MenuDragMouseListener[] getMenuDragMouseListeners() {
      return (MenuDragMouseListener[])this.listenerList.getListeners(MenuDragMouseListener.class);
   }

   public void addMenuKeyListener(MenuKeyListener var1) {
      this.listenerList.add(MenuKeyListener.class, var1);
   }

   public void removeMenuKeyListener(MenuKeyListener var1) {
      this.listenerList.remove(MenuKeyListener.class, var1);
   }

   public MenuKeyListener[] getMenuKeyListeners() {
      return (MenuKeyListener[])this.listenerList.getListeners(MenuKeyListener.class);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      if (this.getUIClassID().equals("MenuItemUI")) {
         this.updateUI();
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("MenuItemUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   protected String paramString() {
      return super.paramString();
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JMenuItem.AccessibleJMenuItem();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJMenuItem extends AbstractButton.AccessibleAbstractButton implements ChangeListener {
      private boolean isArmed = false;
      private boolean hasFocus = false;
      private boolean isPressed = false;
      private boolean isSelected = false;

      AccessibleJMenuItem() {
         super();
         JMenuItem.this.addChangeListener(this);
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.MENU_ITEM;
      }

      private void fireAccessibilityFocusedEvent(JMenuItem var1) {
         MenuElement[] var2 = MenuSelectionManager.defaultManager().getSelectedPath();
         if (var2.length > 0) {
            MenuElement var3 = var2[var2.length - 1];
            if (var1 == var3) {
               this.firePropertyChange("AccessibleState", (Object)null, AccessibleState.FOCUSED);
            }
         }

      }

      public void stateChanged(ChangeEvent var1) {
         this.firePropertyChange("AccessibleVisibleData", false, true);
         if (JMenuItem.this.getModel().isArmed()) {
            if (!this.isArmed) {
               this.isArmed = true;
               this.firePropertyChange("AccessibleState", (Object)null, AccessibleState.ARMED);
               this.fireAccessibilityFocusedEvent(JMenuItem.this);
            }
         } else if (this.isArmed) {
            this.isArmed = false;
            this.firePropertyChange("AccessibleState", AccessibleState.ARMED, (Object)null);
         }

         if (JMenuItem.this.isFocusOwner()) {
            if (!this.hasFocus) {
               this.hasFocus = true;
               this.firePropertyChange("AccessibleState", (Object)null, AccessibleState.FOCUSED);
            }
         } else if (this.hasFocus) {
            this.hasFocus = false;
            this.firePropertyChange("AccessibleState", AccessibleState.FOCUSED, (Object)null);
         }

         if (JMenuItem.this.getModel().isPressed()) {
            if (!this.isPressed) {
               this.isPressed = true;
               this.firePropertyChange("AccessibleState", (Object)null, AccessibleState.PRESSED);
            }
         } else if (this.isPressed) {
            this.isPressed = false;
            this.firePropertyChange("AccessibleState", AccessibleState.PRESSED, (Object)null);
         }

         if (JMenuItem.this.getModel().isSelected()) {
            if (!this.isSelected) {
               this.isSelected = true;
               this.firePropertyChange("AccessibleState", (Object)null, AccessibleState.CHECKED);
               this.fireAccessibilityFocusedEvent(JMenuItem.this);
            }
         } else if (this.isSelected) {
            this.isSelected = false;
            this.firePropertyChange("AccessibleState", AccessibleState.CHECKED, (Object)null);
         }

      }
   }

   private static class MenuItemFocusListener implements FocusListener, Serializable {
      private MenuItemFocusListener() {
      }

      public void focusGained(FocusEvent var1) {
      }

      public void focusLost(FocusEvent var1) {
         JMenuItem var2 = (JMenuItem)var1.getSource();
         if (var2.isFocusPainted()) {
            var2.repaint();
         }

      }

      // $FF: synthetic method
      MenuItemFocusListener(Object var1) {
         this();
      }
   }
}
