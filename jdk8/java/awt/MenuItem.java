package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.peer.MenuItemPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleValue;
import sun.awt.AWTAccessor;

public class MenuItem extends MenuComponent implements Accessible {
   boolean enabled;
   String label;
   String actionCommand;
   long eventMask;
   transient ActionListener actionListener;
   private MenuShortcut shortcut;
   private static final String base = "menuitem";
   private static int nameCounter;
   private static final long serialVersionUID = -21757335363267194L;
   private int menuItemSerializedDataVersion;

   public MenuItem() throws HeadlessException {
      this("", (MenuShortcut)null);
   }

   public MenuItem(String var1) throws HeadlessException {
      this(var1, (MenuShortcut)null);
   }

   public MenuItem(String var1, MenuShortcut var2) throws HeadlessException {
      this.enabled = true;
      this.shortcut = null;
      this.menuItemSerializedDataVersion = 1;
      this.label = var1;
      this.shortcut = var2;
   }

   String constructComponentName() {
      Class var1 = MenuItem.class;
      synchronized(MenuItem.class) {
         return "menuitem" + nameCounter++;
      }
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         if (this.peer == null) {
            this.peer = Toolkit.getDefaultToolkit().createMenuItem(this);
         }

      }
   }

   public String getLabel() {
      return this.label;
   }

   public synchronized void setLabel(String var1) {
      this.label = var1;
      MenuItemPeer var2 = (MenuItemPeer)this.peer;
      if (var2 != null) {
         var2.setLabel(var1);
      }

   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public synchronized void setEnabled(boolean var1) {
      this.enable(var1);
   }

   /** @deprecated */
   @Deprecated
   public synchronized void enable() {
      this.enabled = true;
      MenuItemPeer var1 = (MenuItemPeer)this.peer;
      if (var1 != null) {
         var1.setEnabled(true);
      }

   }

   /** @deprecated */
   @Deprecated
   public void enable(boolean var1) {
      if (var1) {
         this.enable();
      } else {
         this.disable();
      }

   }

   /** @deprecated */
   @Deprecated
   public synchronized void disable() {
      this.enabled = false;
      MenuItemPeer var1 = (MenuItemPeer)this.peer;
      if (var1 != null) {
         var1.setEnabled(false);
      }

   }

   public MenuShortcut getShortcut() {
      return this.shortcut;
   }

   public void setShortcut(MenuShortcut var1) {
      this.shortcut = var1;
      MenuItemPeer var2 = (MenuItemPeer)this.peer;
      if (var2 != null) {
         var2.setLabel(this.label);
      }

   }

   public void deleteShortcut() {
      this.shortcut = null;
      MenuItemPeer var1 = (MenuItemPeer)this.peer;
      if (var1 != null) {
         var1.setLabel(this.label);
      }

   }

   void deleteShortcut(MenuShortcut var1) {
      if (var1.equals(this.shortcut)) {
         this.shortcut = null;
         MenuItemPeer var2 = (MenuItemPeer)this.peer;
         if (var2 != null) {
            var2.setLabel(this.label);
         }
      }

   }

   void doMenuEvent(long var1, int var3) {
      Toolkit.getEventQueue().postEvent(new ActionEvent(this, 1001, this.getActionCommand(), var1, var3));
   }

   private final boolean isItemEnabled() {
      if (!this.isEnabled()) {
         return false;
      } else {
         MenuContainer var1 = this.getParent_NoClientCode();

         while(var1 instanceof Menu) {
            Menu var2 = (Menu)var1;
            if (!var2.isEnabled()) {
               return false;
            }

            var1 = var2.getParent_NoClientCode();
            if (var1 == null) {
               return true;
            }
         }

         return true;
      }
   }

   boolean handleShortcut(KeyEvent var1) {
      MenuShortcut var2 = new MenuShortcut(var1.getKeyCode(), (var1.getModifiers() & 1) > 0);
      MenuShortcut var3 = new MenuShortcut(var1.getExtendedKeyCode(), (var1.getModifiers() & 1) > 0);
      if ((var2.equals(this.shortcut) || var3.equals(this.shortcut)) && this.isItemEnabled()) {
         if (var1.getID() == 401) {
            this.doMenuEvent(var1.getWhen(), var1.getModifiers());
         }

         return true;
      } else {
         return false;
      }
   }

   MenuItem getShortcutMenuItem(MenuShortcut var1) {
      return var1.equals(this.shortcut) ? this : null;
   }

   protected final void enableEvents(long var1) {
      this.eventMask |= var1;
      this.newEventsOnly = true;
   }

   protected final void disableEvents(long var1) {
      this.eventMask &= ~var1;
   }

   public void setActionCommand(String var1) {
      this.actionCommand = var1;
   }

   public String getActionCommand() {
      return this.getActionCommandImpl();
   }

   final String getActionCommandImpl() {
      return this.actionCommand == null ? this.label : this.actionCommand;
   }

   public synchronized void addActionListener(ActionListener var1) {
      if (var1 != null) {
         this.actionListener = AWTEventMulticaster.add(this.actionListener, var1);
         this.newEventsOnly = true;
      }
   }

   public synchronized void removeActionListener(ActionListener var1) {
      if (var1 != null) {
         this.actionListener = AWTEventMulticaster.remove(this.actionListener, var1);
      }
   }

   public synchronized ActionListener[] getActionListeners() {
      return (ActionListener[])this.getListeners(ActionListener.class);
   }

   public <T extends EventListener> T[] getListeners(Class<T> var1) {
      ActionListener var2 = null;
      if (var1 == ActionListener.class) {
         var2 = this.actionListener;
      }

      return AWTEventMulticaster.getListeners(var2, var1);
   }

   protected void processEvent(AWTEvent var1) {
      if (var1 instanceof ActionEvent) {
         this.processActionEvent((ActionEvent)var1);
      }

   }

   boolean eventEnabled(AWTEvent var1) {
      if (var1.id == 1001) {
         return (this.eventMask & 128L) != 0L || this.actionListener != null;
      } else {
         return super.eventEnabled(var1);
      }
   }

   protected void processActionEvent(ActionEvent var1) {
      ActionListener var2 = this.actionListener;
      if (var2 != null) {
         var2.actionPerformed(var1);
      }

   }

   public String paramString() {
      String var1 = ",label=" + this.label;
      if (this.shortcut != null) {
         var1 = var1 + ",shortcut=" + this.shortcut;
      }

      return super.paramString() + var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      AWTEventMulticaster.save(var1, "actionL", this.actionListener);
      var1.writeObject((Object)null);
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException, HeadlessException {
      var1.defaultReadObject();

      Object var2;
      while(null != (var2 = var1.readObject())) {
         String var3 = ((String)var2).intern();
         if ("actionL" == var3) {
            this.addActionListener((ActionListener)((ActionListener)var1.readObject()));
         } else {
            var1.readObject();
         }
      }

   }

   private static native void initIDs();

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new MenuItem.AccessibleAWTMenuItem();
      }

      return this.accessibleContext;
   }

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

      AWTAccessor.setMenuItemAccessor(new AWTAccessor.MenuItemAccessor() {
         public boolean isEnabled(MenuItem var1) {
            return var1.enabled;
         }

         public String getLabel(MenuItem var1) {
            return var1.label;
         }

         public MenuShortcut getShortcut(MenuItem var1) {
            return var1.shortcut;
         }

         public String getActionCommandImpl(MenuItem var1) {
            return var1.getActionCommandImpl();
         }

         public boolean isItemEnabled(MenuItem var1) {
            return var1.isItemEnabled();
         }
      });
      nameCounter = 0;
   }

   protected class AccessibleAWTMenuItem extends MenuComponent.AccessibleAWTMenuComponent implements AccessibleAction, AccessibleValue {
      private static final long serialVersionUID = -217847831945965825L;

      protected AccessibleAWTMenuItem() {
         super();
      }

      public String getAccessibleName() {
         if (this.accessibleName != null) {
            return this.accessibleName;
         } else {
            return MenuItem.this.getLabel() == null ? super.getAccessibleName() : MenuItem.this.getLabel();
         }
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.MENU_ITEM;
      }

      public AccessibleAction getAccessibleAction() {
         return this;
      }

      public AccessibleValue getAccessibleValue() {
         return this;
      }

      public int getAccessibleActionCount() {
         return 1;
      }

      public String getAccessibleActionDescription(int var1) {
         return var1 == 0 ? "click" : null;
      }

      public boolean doAccessibleAction(int var1) {
         if (var1 == 0) {
            Toolkit.getEventQueue().postEvent(new ActionEvent(MenuItem.this, 1001, MenuItem.this.getActionCommand(), EventQueue.getMostRecentEventTime(), 0));
            return true;
         } else {
            return false;
         }
      }

      public Number getCurrentAccessibleValue() {
         return 0;
      }

      public boolean setCurrentAccessibleValue(Number var1) {
         return false;
      }

      public Number getMinimumAccessibleValue() {
         return 0;
      }

      public Number getMaximumAccessibleValue() {
         return 0;
      }
   }
}
