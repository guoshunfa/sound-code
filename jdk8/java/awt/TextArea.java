package java.awt;

import java.awt.peer.TextAreaPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Set;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;

public class TextArea extends TextComponent {
   int rows;
   int columns;
   private static final String base = "text";
   private static int nameCounter = 0;
   public static final int SCROLLBARS_BOTH = 0;
   public static final int SCROLLBARS_VERTICAL_ONLY = 1;
   public static final int SCROLLBARS_HORIZONTAL_ONLY = 2;
   public static final int SCROLLBARS_NONE = 3;
   private int scrollbarVisibility;
   private static Set<AWTKeyStroke> forwardTraversalKeys;
   private static Set<AWTKeyStroke> backwardTraversalKeys;
   private static final long serialVersionUID = 3692302836626095722L;
   private int textAreaSerializedDataVersion;

   private static native void initIDs();

   public TextArea() throws HeadlessException {
      this("", 0, 0, 0);
   }

   public TextArea(String var1) throws HeadlessException {
      this(var1, 0, 0, 0);
   }

   public TextArea(int var1, int var2) throws HeadlessException {
      this("", var1, var2, 0);
   }

   public TextArea(String var1, int var2, int var3) throws HeadlessException {
      this(var1, var2, var3, 0);
   }

   public TextArea(String var1, int var2, int var3, int var4) throws HeadlessException {
      super(var1);
      this.textAreaSerializedDataVersion = 2;
      this.rows = var2 >= 0 ? var2 : 0;
      this.columns = var3 >= 0 ? var3 : 0;
      if (var4 >= 0 && var4 <= 3) {
         this.scrollbarVisibility = var4;
      } else {
         this.scrollbarVisibility = 0;
      }

      this.setFocusTraversalKeys(0, forwardTraversalKeys);
      this.setFocusTraversalKeys(1, backwardTraversalKeys);
   }

   String constructComponentName() {
      Class var1 = TextArea.class;
      synchronized(TextArea.class) {
         return "text" + nameCounter++;
      }
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         if (this.peer == null) {
            this.peer = this.getToolkit().createTextArea(this);
         }

         super.addNotify();
      }
   }

   public void insert(String var1, int var2) {
      this.insertText(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public synchronized void insertText(String var1, int var2) {
      TextAreaPeer var3 = (TextAreaPeer)this.peer;
      if (var3 != null) {
         var3.insert(var1, var2);
      }

      this.text = this.text.substring(0, var2) + var1 + this.text.substring(var2);
   }

   public void append(String var1) {
      this.appendText(var1);
   }

   /** @deprecated */
   @Deprecated
   public synchronized void appendText(String var1) {
      this.insertText(var1, this.getText().length());
   }

   public void replaceRange(String var1, int var2, int var3) {
      this.replaceText(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   public synchronized void replaceText(String var1, int var2, int var3) {
      TextAreaPeer var4 = (TextAreaPeer)this.peer;
      if (var4 != null) {
         var4.replaceRange(var1, var2, var3);
      }

      this.text = this.text.substring(0, var2) + var1 + this.text.substring(var3);
   }

   public int getRows() {
      return this.rows;
   }

   public void setRows(int var1) {
      int var2 = this.rows;
      if (var1 < 0) {
         throw new IllegalArgumentException("rows less than zero.");
      } else {
         if (var1 != var2) {
            this.rows = var1;
            this.invalidate();
         }

      }
   }

   public int getColumns() {
      return this.columns;
   }

   public void setColumns(int var1) {
      int var2 = this.columns;
      if (var1 < 0) {
         throw new IllegalArgumentException("columns less than zero.");
      } else {
         if (var1 != var2) {
            this.columns = var1;
            this.invalidate();
         }

      }
   }

   public int getScrollbarVisibility() {
      return this.scrollbarVisibility;
   }

   public Dimension getPreferredSize(int var1, int var2) {
      return this.preferredSize(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public Dimension preferredSize(int var1, int var2) {
      synchronized(this.getTreeLock()) {
         TextAreaPeer var4 = (TextAreaPeer)this.peer;
         return var4 != null ? var4.getPreferredSize(var1, var2) : super.preferredSize();
      }
   }

   public Dimension getPreferredSize() {
      return this.preferredSize();
   }

   /** @deprecated */
   @Deprecated
   public Dimension preferredSize() {
      synchronized(this.getTreeLock()) {
         return this.rows > 0 && this.columns > 0 ? this.preferredSize(this.rows, this.columns) : super.preferredSize();
      }
   }

   public Dimension getMinimumSize(int var1, int var2) {
      return this.minimumSize(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public Dimension minimumSize(int var1, int var2) {
      synchronized(this.getTreeLock()) {
         TextAreaPeer var4 = (TextAreaPeer)this.peer;
         return var4 != null ? var4.getMinimumSize(var1, var2) : super.minimumSize();
      }
   }

   public Dimension getMinimumSize() {
      return this.minimumSize();
   }

   /** @deprecated */
   @Deprecated
   public Dimension minimumSize() {
      synchronized(this.getTreeLock()) {
         return this.rows > 0 && this.columns > 0 ? this.minimumSize(this.rows, this.columns) : super.minimumSize();
      }
   }

   protected String paramString() {
      String var1;
      switch(this.scrollbarVisibility) {
      case 0:
         var1 = "both";
         break;
      case 1:
         var1 = "vertical-only";
         break;
      case 2:
         var1 = "horizontal-only";
         break;
      case 3:
         var1 = "none";
         break;
      default:
         var1 = "invalid display policy";
      }

      return super.paramString() + ",rows=" + this.rows + ",columns=" + this.columns + ",scrollbarVisibility=" + var1;
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException, HeadlessException {
      var1.defaultReadObject();
      if (this.columns < 0) {
         this.columns = 0;
      }

      if (this.rows < 0) {
         this.rows = 0;
      }

      if (this.scrollbarVisibility < 0 || this.scrollbarVisibility > 3) {
         this.scrollbarVisibility = 0;
      }

      if (this.textAreaSerializedDataVersion < 2) {
         this.setFocusTraversalKeys(0, forwardTraversalKeys);
         this.setFocusTraversalKeys(1, backwardTraversalKeys);
      }

   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new TextArea.AccessibleAWTTextArea();
      }

      return this.accessibleContext;
   }

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

      forwardTraversalKeys = KeyboardFocusManager.initFocusTraversalKeysSet("ctrl TAB", new HashSet());
      backwardTraversalKeys = KeyboardFocusManager.initFocusTraversalKeysSet("ctrl shift TAB", new HashSet());
   }

   protected class AccessibleAWTTextArea extends TextComponent.AccessibleAWTTextComponent {
      private static final long serialVersionUID = 3472827823632144419L;

      protected AccessibleAWTTextArea() {
         super();
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         var1.add(AccessibleState.MULTI_LINE);
         return var1;
      }
   }
}
