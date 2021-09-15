package java.awt;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.peer.CheckboxMenuItemPeer;
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

public class CheckboxMenuItem extends MenuItem implements ItemSelectable, Accessible {
   boolean state;
   transient ItemListener itemListener;
   private static final String base = "chkmenuitem";
   private static int nameCounter;
   private static final long serialVersionUID = 6190621106981774043L;
   private int checkboxMenuItemSerializedDataVersion;

   public CheckboxMenuItem() throws HeadlessException {
      this("", false);
   }

   public CheckboxMenuItem(String var1) throws HeadlessException {
      this(var1, false);
   }

   public CheckboxMenuItem(String var1, boolean var2) throws HeadlessException {
      super(var1);
      this.state = false;
      this.checkboxMenuItemSerializedDataVersion = 1;
      this.state = var2;
   }

   String constructComponentName() {
      Class var1 = CheckboxMenuItem.class;
      synchronized(CheckboxMenuItem.class) {
         return "chkmenuitem" + nameCounter++;
      }
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         if (this.peer == null) {
            this.peer = Toolkit.getDefaultToolkit().createCheckboxMenuItem(this);
         }

         super.addNotify();
      }
   }

   public boolean getState() {
      return this.state;
   }

   public synchronized void setState(boolean var1) {
      this.state = var1;
      CheckboxMenuItemPeer var2 = (CheckboxMenuItemPeer)this.peer;
      if (var2 != null) {
         var2.setState(var1);
      }

   }

   public synchronized Object[] getSelectedObjects() {
      if (this.state) {
         Object[] var1 = new Object[]{this.label};
         return var1;
      } else {
         return null;
      }
   }

   public synchronized void addItemListener(ItemListener var1) {
      if (var1 != null) {
         this.itemListener = AWTEventMulticaster.add(this.itemListener, var1);
         this.newEventsOnly = true;
      }
   }

   public synchronized void removeItemListener(ItemListener var1) {
      if (var1 != null) {
         this.itemListener = AWTEventMulticaster.remove(this.itemListener, var1);
      }
   }

   public synchronized ItemListener[] getItemListeners() {
      return (ItemListener[])this.getListeners(ItemListener.class);
   }

   public <T extends EventListener> T[] getListeners(Class<T> var1) {
      ItemListener var2 = null;
      if (var1 == ItemListener.class) {
         var2 = this.itemListener;
         return AWTEventMulticaster.getListeners(var2, var1);
      } else {
         return super.getListeners(var1);
      }
   }

   boolean eventEnabled(AWTEvent var1) {
      if (var1.id == 701) {
         return (this.eventMask & 512L) != 0L || this.itemListener != null;
      } else {
         return super.eventEnabled(var1);
      }
   }

   protected void processEvent(AWTEvent var1) {
      if (var1 instanceof ItemEvent) {
         this.processItemEvent((ItemEvent)var1);
      } else {
         super.processEvent(var1);
      }
   }

   protected void processItemEvent(ItemEvent var1) {
      ItemListener var2 = this.itemListener;
      if (var2 != null) {
         var2.itemStateChanged(var1);
      }

   }

   void doMenuEvent(long var1, int var3) {
      this.setState(!this.state);
      Toolkit.getEventQueue().postEvent(new ItemEvent(this, 701, this.getLabel(), this.state ? 1 : 2));
   }

   public String paramString() {
      return super.paramString() + ",state=" + this.state;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      AWTEventMulticaster.save(var1, "itemL", this.itemListener);
      var1.writeObject((Object)null);
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      var1.defaultReadObject();

      Object var2;
      while(null != (var2 = var1.readObject())) {
         String var3 = ((String)var2).intern();
         if ("itemL" == var3) {
            this.addItemListener((ItemListener)((ItemListener)var1.readObject()));
         } else {
            var1.readObject();
         }
      }

   }

   private static native void initIDs();

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new CheckboxMenuItem.AccessibleAWTCheckboxMenuItem();
      }

      return this.accessibleContext;
   }

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

      AWTAccessor.setCheckboxMenuItemAccessor(new AWTAccessor.CheckboxMenuItemAccessor() {
         public boolean getState(CheckboxMenuItem var1) {
            return var1.state;
         }
      });
      nameCounter = 0;
   }

   protected class AccessibleAWTCheckboxMenuItem extends MenuItem.AccessibleAWTMenuItem implements AccessibleAction, AccessibleValue {
      private static final long serialVersionUID = -1122642964303476L;

      protected AccessibleAWTCheckboxMenuItem() {
         super();
      }

      public AccessibleAction getAccessibleAction() {
         return this;
      }

      public AccessibleValue getAccessibleValue() {
         return this;
      }

      public int getAccessibleActionCount() {
         return 0;
      }

      public String getAccessibleActionDescription(int var1) {
         return null;
      }

      public boolean doAccessibleAction(int var1) {
         return false;
      }

      public Number getCurrentAccessibleValue() {
         return null;
      }

      public boolean setCurrentAccessibleValue(Number var1) {
         return false;
      }

      public Number getMinimumAccessibleValue() {
         return null;
      }

      public Number getMaximumAccessibleValue() {
         return null;
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.CHECK_BOX;
      }
   }
}
