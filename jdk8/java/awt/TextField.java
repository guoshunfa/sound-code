package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.peer.TextFieldPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;

public class TextField extends TextComponent {
   int columns;
   char echoChar;
   transient ActionListener actionListener;
   private static final String base = "textfield";
   private static int nameCounter = 0;
   private static final long serialVersionUID = -2966288784432217853L;
   private int textFieldSerializedDataVersion;

   private static native void initIDs();

   public TextField() throws HeadlessException {
      this("", 0);
   }

   public TextField(String var1) throws HeadlessException {
      this(var1, var1 != null ? var1.length() : 0);
   }

   public TextField(int var1) throws HeadlessException {
      this("", var1);
   }

   public TextField(String var1, int var2) throws HeadlessException {
      super(var1);
      this.textFieldSerializedDataVersion = 1;
      this.columns = var2 >= 0 ? var2 : 0;
   }

   String constructComponentName() {
      Class var1 = TextField.class;
      synchronized(TextField.class) {
         return "textfield" + nameCounter++;
      }
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         if (this.peer == null) {
            this.peer = this.getToolkit().createTextField(this);
         }

         super.addNotify();
      }
   }

   public char getEchoChar() {
      return this.echoChar;
   }

   public void setEchoChar(char var1) {
      this.setEchoCharacter(var1);
   }

   /** @deprecated */
   @Deprecated
   public synchronized void setEchoCharacter(char var1) {
      if (this.echoChar != var1) {
         this.echoChar = var1;
         TextFieldPeer var2 = (TextFieldPeer)this.peer;
         if (var2 != null) {
            var2.setEchoChar(var1);
         }
      }

   }

   public void setText(String var1) {
      super.setText(var1);
      this.invalidateIfValid();
   }

   public boolean echoCharIsSet() {
      return this.echoChar != 0;
   }

   public int getColumns() {
      return this.columns;
   }

   public void setColumns(int var1) {
      int var2;
      synchronized(this) {
         var2 = this.columns;
         if (var1 < 0) {
            throw new IllegalArgumentException("columns less than zero.");
         }

         if (var1 != var2) {
            this.columns = var1;
         }
      }

      if (var1 != var2) {
         this.invalidate();
      }

   }

   public Dimension getPreferredSize(int var1) {
      return this.preferredSize(var1);
   }

   /** @deprecated */
   @Deprecated
   public Dimension preferredSize(int var1) {
      synchronized(this.getTreeLock()) {
         TextFieldPeer var3 = (TextFieldPeer)this.peer;
         return var3 != null ? var3.getPreferredSize(var1) : super.preferredSize();
      }
   }

   public Dimension getPreferredSize() {
      return this.preferredSize();
   }

   /** @deprecated */
   @Deprecated
   public Dimension preferredSize() {
      synchronized(this.getTreeLock()) {
         return this.columns > 0 ? this.preferredSize(this.columns) : super.preferredSize();
      }
   }

   public Dimension getMinimumSize(int var1) {
      return this.minimumSize(var1);
   }

   /** @deprecated */
   @Deprecated
   public Dimension minimumSize(int var1) {
      synchronized(this.getTreeLock()) {
         TextFieldPeer var3 = (TextFieldPeer)this.peer;
         return var3 != null ? var3.getMinimumSize(var1) : super.minimumSize();
      }
   }

   public Dimension getMinimumSize() {
      return this.minimumSize();
   }

   /** @deprecated */
   @Deprecated
   public Dimension minimumSize() {
      synchronized(this.getTreeLock()) {
         return this.columns > 0 ? this.minimumSize(this.columns) : super.minimumSize();
      }
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
      String var1 = super.paramString();
      if (this.echoChar != 0) {
         var1 = var1 + ",echo=" + this.echoChar;
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      AWTEventMulticaster.save(var1, "actionL", this.actionListener);
      var1.writeObject((Object)null);
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException, HeadlessException {
      var1.defaultReadObject();
      if (this.columns < 0) {
         this.columns = 0;
      }

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
         this.accessibleContext = new TextField.AccessibleAWTTextField();
      }

      return this.accessibleContext;
   }

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

   }

   protected class AccessibleAWTTextField extends TextComponent.AccessibleAWTTextComponent {
      private static final long serialVersionUID = 6219164359235943158L;

      protected AccessibleAWTTextField() {
         super();
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         var1.add(AccessibleState.SINGLE_LINE);
         return var1;
      }
   }
}
