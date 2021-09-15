package java.awt;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.peer.CheckboxPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleValue;

public class Checkbox extends Component implements ItemSelectable, Accessible {
   String label;
   boolean state;
   CheckboxGroup group;
   transient ItemListener itemListener;
   private static final String base = "checkbox";
   private static int nameCounter;
   private static final long serialVersionUID = 7270714317450821763L;
   private int checkboxSerializedDataVersion;

   void setStateInternal(boolean var1) {
      this.state = var1;
      CheckboxPeer var2 = (CheckboxPeer)this.peer;
      if (var2 != null) {
         var2.setState(var1);
      }

   }

   public Checkbox() throws HeadlessException {
      this("", false, (CheckboxGroup)null);
   }

   public Checkbox(String var1) throws HeadlessException {
      this(var1, false, (CheckboxGroup)null);
   }

   public Checkbox(String var1, boolean var2) throws HeadlessException {
      this(var1, var2, (CheckboxGroup)null);
   }

   public Checkbox(String var1, boolean var2, CheckboxGroup var3) throws HeadlessException {
      this.checkboxSerializedDataVersion = 1;
      GraphicsEnvironment.checkHeadless();
      this.label = var1;
      this.state = var2;
      this.group = var3;
      if (var2 && var3 != null) {
         var3.setSelectedCheckbox(this);
      }

   }

   public Checkbox(String var1, CheckboxGroup var2, boolean var3) throws HeadlessException {
      this(var1, var3, var2);
   }

   String constructComponentName() {
      Class var1 = Checkbox.class;
      synchronized(Checkbox.class) {
         return "checkbox" + nameCounter++;
      }
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         if (this.peer == null) {
            this.peer = this.getToolkit().createCheckbox(this);
         }

         super.addNotify();
      }
   }

   public String getLabel() {
      return this.label;
   }

   public void setLabel(String var1) {
      boolean var2 = false;
      synchronized(this) {
         if (var1 != this.label && (this.label == null || !this.label.equals(var1))) {
            this.label = var1;
            CheckboxPeer var4 = (CheckboxPeer)this.peer;
            if (var4 != null) {
               var4.setLabel(var1);
            }

            var2 = true;
         }
      }

      if (var2) {
         this.invalidateIfValid();
      }

   }

   public boolean getState() {
      return this.state;
   }

   public void setState(boolean var1) {
      CheckboxGroup var2 = this.group;
      if (var2 != null) {
         if (var1) {
            var2.setSelectedCheckbox(this);
         } else if (var2.getSelectedCheckbox() == this) {
            var1 = true;
         }
      }

      this.setStateInternal(var1);
   }

   public Object[] getSelectedObjects() {
      if (this.state) {
         Object[] var1 = new Object[]{this.label};
         return var1;
      } else {
         return null;
      }
   }

   public CheckboxGroup getCheckboxGroup() {
      return this.group;
   }

   public void setCheckboxGroup(CheckboxGroup var1) {
      if (this.group != var1) {
         CheckboxGroup var2;
         boolean var3;
         synchronized(this) {
            var2 = this.group;
            var3 = this.getState();
            this.group = var1;
            CheckboxPeer var5 = (CheckboxPeer)this.peer;
            if (var5 != null) {
               var5.setCheckboxGroup(var1);
            }

            if (this.group != null && this.getState()) {
               if (this.group.getSelectedCheckbox() != null) {
                  this.setState(false);
               } else {
                  this.group.setSelectedCheckbox(this);
               }
            }
         }

         if (var2 != null && var3) {
            var2.setSelectedCheckbox((Checkbox)null);
         }

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
      String var1 = super.paramString();
      String var2 = this.label;
      if (var2 != null) {
         var1 = var1 + ",label=" + var2;
      }

      return var1 + ",state=" + this.state;
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
         this.accessibleContext = new Checkbox.AccessibleAWTCheckbox();
      }

      return this.accessibleContext;
   }

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

      nameCounter = 0;
   }

   protected class AccessibleAWTCheckbox extends Component.AccessibleAWTComponent implements ItemListener, AccessibleAction, AccessibleValue {
      private static final long serialVersionUID = 7881579233144754107L;

      public AccessibleAWTCheckbox() {
         super();
         Checkbox.this.addItemListener(this);
      }

      public void itemStateChanged(ItemEvent var1) {
         Checkbox var2 = (Checkbox)var1.getSource();
         if (Checkbox.this.accessibleContext != null) {
            if (var2.getState()) {
               Checkbox.this.accessibleContext.firePropertyChange("AccessibleState", (Object)null, AccessibleState.CHECKED);
            } else {
               Checkbox.this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.CHECKED, (Object)null);
            }
         }

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

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         if (Checkbox.this.getState()) {
            var1.add(AccessibleState.CHECKED);
         }

         return var1;
      }
   }
}
