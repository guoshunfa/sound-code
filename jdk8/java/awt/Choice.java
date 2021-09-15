package java.awt;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.peer.ChoicePeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;

public class Choice extends Component implements ItemSelectable, Accessible {
   Vector<String> pItems;
   int selectedIndex = -1;
   transient ItemListener itemListener;
   private static final String base = "choice";
   private static int nameCounter = 0;
   private static final long serialVersionUID = -4075310674757313071L;
   private int choiceSerializedDataVersion = 1;

   public Choice() throws HeadlessException {
      GraphicsEnvironment.checkHeadless();
      this.pItems = new Vector();
   }

   String constructComponentName() {
      Class var1 = Choice.class;
      synchronized(Choice.class) {
         return "choice" + nameCounter++;
      }
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         if (this.peer == null) {
            this.peer = this.getToolkit().createChoice(this);
         }

         super.addNotify();
      }
   }

   public int getItemCount() {
      return this.countItems();
   }

   /** @deprecated */
   @Deprecated
   public int countItems() {
      return this.pItems.size();
   }

   public String getItem(int var1) {
      return this.getItemImpl(var1);
   }

   final String getItemImpl(int var1) {
      return (String)this.pItems.elementAt(var1);
   }

   public void add(String var1) {
      this.addItem(var1);
   }

   public void addItem(String var1) {
      synchronized(this) {
         this.insertNoInvalidate(var1, this.pItems.size());
      }

      this.invalidateIfValid();
   }

   private void insertNoInvalidate(String var1, int var2) {
      if (var1 == null) {
         throw new NullPointerException("cannot add null item to Choice");
      } else {
         this.pItems.insertElementAt(var1, var2);
         ChoicePeer var3 = (ChoicePeer)this.peer;
         if (var3 != null) {
            var3.add(var1, var2);
         }

         if (this.selectedIndex < 0 || this.selectedIndex >= var2) {
            this.select(0);
         }

      }
   }

   public void insert(String var1, int var2) {
      synchronized(this) {
         if (var2 < 0) {
            throw new IllegalArgumentException("index less than zero.");
         }

         var2 = Math.min(var2, this.pItems.size());
         this.insertNoInvalidate(var1, var2);
      }

      this.invalidateIfValid();
   }

   public void remove(String var1) {
      synchronized(this) {
         int var3 = this.pItems.indexOf(var1);
         if (var3 < 0) {
            throw new IllegalArgumentException("item " + var1 + " not found in choice");
         }

         this.removeNoInvalidate(var3);
      }

      this.invalidateIfValid();
   }

   public void remove(int var1) {
      synchronized(this) {
         this.removeNoInvalidate(var1);
      }

      this.invalidateIfValid();
   }

   private void removeNoInvalidate(int var1) {
      this.pItems.removeElementAt(var1);
      ChoicePeer var2 = (ChoicePeer)this.peer;
      if (var2 != null) {
         var2.remove(var1);
      }

      if (this.pItems.size() == 0) {
         this.selectedIndex = -1;
      } else if (this.selectedIndex == var1) {
         this.select(0);
      } else if (this.selectedIndex > var1) {
         this.select(this.selectedIndex - 1);
      }

   }

   public void removeAll() {
      synchronized(this) {
         if (this.peer != null) {
            ((ChoicePeer)this.peer).removeAll();
         }

         this.pItems.removeAllElements();
         this.selectedIndex = -1;
      }

      this.invalidateIfValid();
   }

   public synchronized String getSelectedItem() {
      return this.selectedIndex >= 0 ? this.getItem(this.selectedIndex) : null;
   }

   public synchronized Object[] getSelectedObjects() {
      if (this.selectedIndex >= 0) {
         Object[] var1 = new Object[]{this.getItem(this.selectedIndex)};
         return var1;
      } else {
         return null;
      }
   }

   public int getSelectedIndex() {
      return this.selectedIndex;
   }

   public synchronized void select(int var1) {
      if (var1 < this.pItems.size() && var1 >= 0) {
         if (this.pItems.size() > 0) {
            this.selectedIndex = var1;
            ChoicePeer var2 = (ChoicePeer)this.peer;
            if (var2 != null) {
               var2.select(var1);
            }
         }

      } else {
         throw new IllegalArgumentException("illegal Choice item position: " + var1);
      }
   }

   public synchronized void select(String var1) {
      int var2 = this.pItems.indexOf(var1);
      if (var2 >= 0) {
         this.select(var2);
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

   protected String paramString() {
      return super.paramString() + ",current=" + this.getSelectedItem();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      AWTEventMulticaster.save(var1, "itemL", this.itemListener);
      var1.writeObject((Object)null);
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException, HeadlessException {
      GraphicsEnvironment.checkHeadless();
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
         this.accessibleContext = new Choice.AccessibleAWTChoice();
      }

      return this.accessibleContext;
   }

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

   }

   protected class AccessibleAWTChoice extends Component.AccessibleAWTComponent implements AccessibleAction {
      private static final long serialVersionUID = 7175603582428509322L;

      public AccessibleAWTChoice() {
         super();
      }

      public AccessibleAction getAccessibleAction() {
         return this;
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.COMBO_BOX;
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
   }
}
