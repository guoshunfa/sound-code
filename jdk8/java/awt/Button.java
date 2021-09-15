package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.peer.ButtonPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleValue;

public class Button extends Component implements Accessible {
   String label;
   String actionCommand;
   transient ActionListener actionListener;
   private static final String base = "button";
   private static int nameCounter = 0;
   private static final long serialVersionUID = -8774683716313001058L;
   private int buttonSerializedDataVersion;

   private static native void initIDs();

   public Button() throws HeadlessException {
      this("");
   }

   public Button(String var1) throws HeadlessException {
      this.buttonSerializedDataVersion = 1;
      GraphicsEnvironment.checkHeadless();
      this.label = var1;
   }

   String constructComponentName() {
      Class var1 = Button.class;
      synchronized(Button.class) {
         return "button" + nameCounter++;
      }
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         if (this.peer == null) {
            this.peer = this.getToolkit().createButton(this);
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
            ButtonPeer var4 = (ButtonPeer)this.peer;
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

   public void setActionCommand(String var1) {
      this.actionCommand = var1;
   }

   public String getActionCommand() {
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
         return AWTEventMulticaster.getListeners(var2, var1);
      } else {
         return super.getListeners(var1);
      }
   }

   boolean eventEnabled(AWTEvent var1) {
      if (var1.id == 1001) {
         return (this.eventMask & 128L) != 0L || this.actionListener != null;
      } else {
         return super.eventEnabled(var1);
      }
   }

   protected void processEvent(AWTEvent var1) {
      if (var1 instanceof ActionEvent) {
         this.processActionEvent((ActionEvent)var1);
      } else {
         super.processEvent(var1);
      }
   }

   protected void processActionEvent(ActionEvent var1) {
      ActionListener var2 = this.actionListener;
      if (var2 != null) {
         var2.actionPerformed(var1);
      }

   }

   protected String paramString() {
      return super.paramString() + ",label=" + this.label;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      AWTEventMulticaster.save(var1, "actionL", this.actionListener);
      var1.writeObject((Object)null);
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException, HeadlessException {
      GraphicsEnvironment.checkHeadless();
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

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new Button.AccessibleAWTButton();
      }

      return this.accessibleContext;
   }

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

   }

   protected class AccessibleAWTButton extends Component.AccessibleAWTComponent implements AccessibleAction, AccessibleValue {
      private static final long serialVersionUID = -5932203980244017102L;

      protected AccessibleAWTButton() {
         super();
      }

      public String getAccessibleName() {
         if (this.accessibleName != null) {
            return this.accessibleName;
         } else {
            return Button.this.getLabel() == null ? super.getAccessibleName() : Button.this.getLabel();
         }
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
            Toolkit.getEventQueue().postEvent(new ActionEvent(Button.this, 1001, Button.this.getActionCommand()));
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

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.PUSH_BUTTON;
      }
   }
}
