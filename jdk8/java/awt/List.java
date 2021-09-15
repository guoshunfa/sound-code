package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.peer.ListPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import java.util.Locale;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;

public class List extends Component implements ItemSelectable, Accessible {
   Vector<String> items;
   int rows;
   boolean multipleMode;
   int[] selected;
   int visibleIndex;
   transient ActionListener actionListener;
   transient ItemListener itemListener;
   private static final String base = "list";
   private static int nameCounter = 0;
   private static final long serialVersionUID = -3304312411574666869L;
   static final int DEFAULT_VISIBLE_ROWS = 4;
   private int listSerializedDataVersion;

   public List() throws HeadlessException {
      this(0, false);
   }

   public List(int var1) throws HeadlessException {
      this(var1, false);
   }

   public List(int var1, boolean var2) throws HeadlessException {
      this.items = new Vector();
      this.rows = 0;
      this.multipleMode = false;
      this.selected = new int[0];
      this.visibleIndex = -1;
      this.listSerializedDataVersion = 1;
      GraphicsEnvironment.checkHeadless();
      this.rows = var1 != 0 ? var1 : 4;
      this.multipleMode = var2;
   }

   String constructComponentName() {
      Class var1 = List.class;
      synchronized(List.class) {
         return "list" + nameCounter++;
      }
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         if (this.peer == null) {
            this.peer = this.getToolkit().createList(this);
         }

         super.addNotify();
      }
   }

   public void removeNotify() {
      synchronized(this.getTreeLock()) {
         ListPeer var2 = (ListPeer)this.peer;
         if (var2 != null) {
            this.selected = var2.getSelectedIndexes();
         }

         super.removeNotify();
      }
   }

   public int getItemCount() {
      return this.countItems();
   }

   /** @deprecated */
   @Deprecated
   public int countItems() {
      return this.items.size();
   }

   public String getItem(int var1) {
      return this.getItemImpl(var1);
   }

   final String getItemImpl(int var1) {
      return (String)this.items.elementAt(var1);
   }

   public synchronized String[] getItems() {
      String[] var1 = new String[this.items.size()];
      this.items.copyInto(var1);
      return var1;
   }

   public void add(String var1) {
      this.addItem(var1);
   }

   /** @deprecated */
   @Deprecated
   public void addItem(String var1) {
      this.addItem(var1, -1);
   }

   public void add(String var1, int var2) {
      this.addItem(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public synchronized void addItem(String var1, int var2) {
      if (var2 < -1 || var2 >= this.items.size()) {
         var2 = -1;
      }

      if (var1 == null) {
         var1 = "";
      }

      if (var2 == -1) {
         this.items.addElement(var1);
      } else {
         this.items.insertElementAt(var1, var2);
      }

      ListPeer var3 = (ListPeer)this.peer;
      if (var3 != null) {
         var3.add(var1, var2);
      }

   }

   public synchronized void replaceItem(String var1, int var2) {
      this.remove(var2);
      this.add(var1, var2);
   }

   public void removeAll() {
      this.clear();
   }

   /** @deprecated */
   @Deprecated
   public synchronized void clear() {
      ListPeer var1 = (ListPeer)this.peer;
      if (var1 != null) {
         var1.removeAll();
      }

      this.items = new Vector();
      this.selected = new int[0];
   }

   public synchronized void remove(String var1) {
      int var2 = this.items.indexOf(var1);
      if (var2 < 0) {
         throw new IllegalArgumentException("item " + var1 + " not found in list");
      } else {
         this.remove(var2);
      }
   }

   public void remove(int var1) {
      this.delItem(var1);
   }

   /** @deprecated */
   @Deprecated
   public void delItem(int var1) {
      this.delItems(var1, var1);
   }

   public synchronized int getSelectedIndex() {
      int[] var1 = this.getSelectedIndexes();
      return var1.length == 1 ? var1[0] : -1;
   }

   public synchronized int[] getSelectedIndexes() {
      ListPeer var1 = (ListPeer)this.peer;
      if (var1 != null) {
         this.selected = var1.getSelectedIndexes();
      }

      return (int[])this.selected.clone();
   }

   public synchronized String getSelectedItem() {
      int var1 = this.getSelectedIndex();
      return var1 < 0 ? null : this.getItem(var1);
   }

   public synchronized String[] getSelectedItems() {
      int[] var1 = this.getSelectedIndexes();
      String[] var2 = new String[var1.length];

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2[var3] = this.getItem(var1[var3]);
      }

      return var2;
   }

   public Object[] getSelectedObjects() {
      return this.getSelectedItems();
   }

   public void select(int var1) {
      ListPeer var2;
      do {
         var2 = (ListPeer)this.peer;
         if (var2 != null) {
            var2.select(var1);
            return;
         }

         synchronized(this) {
            boolean var4 = false;
            int var5 = 0;

            while(true) {
               if (var5 < this.selected.length) {
                  if (this.selected[var5] != var1) {
                     ++var5;
                     continue;
                  }

                  var4 = true;
               }

               if (!var4) {
                  if (!this.multipleMode) {
                     this.selected = new int[1];
                     this.selected[0] = var1;
                  } else {
                     int[] var8 = new int[this.selected.length + 1];
                     System.arraycopy(this.selected, 0, var8, 0, this.selected.length);
                     var8[this.selected.length] = var1;
                     this.selected = var8;
                  }
               }
               break;
            }
         }
      } while(var2 != this.peer);

   }

   public synchronized void deselect(int var1) {
      ListPeer var2 = (ListPeer)this.peer;
      if (var2 != null && (this.isMultipleMode() || this.getSelectedIndex() == var1)) {
         var2.deselect(var1);
      }

      for(int var3 = 0; var3 < this.selected.length; ++var3) {
         if (this.selected[var3] == var1) {
            int[] var4 = new int[this.selected.length - 1];
            System.arraycopy(this.selected, 0, var4, 0, var3);
            System.arraycopy(this.selected, var3 + 1, var4, var3, this.selected.length - (var3 + 1));
            this.selected = var4;
            return;
         }
      }

   }

   public boolean isIndexSelected(int var1) {
      return this.isSelected(var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean isSelected(int var1) {
      int[] var2 = this.getSelectedIndexes();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var2[var3] == var1) {
            return true;
         }
      }

      return false;
   }

   public int getRows() {
      return this.rows;
   }

   public boolean isMultipleMode() {
      return this.allowsMultipleSelections();
   }

   /** @deprecated */
   @Deprecated
   public boolean allowsMultipleSelections() {
      return this.multipleMode;
   }

   public void setMultipleMode(boolean var1) {
      this.setMultipleSelections(var1);
   }

   /** @deprecated */
   @Deprecated
   public synchronized void setMultipleSelections(boolean var1) {
      if (var1 != this.multipleMode) {
         this.multipleMode = var1;
         ListPeer var2 = (ListPeer)this.peer;
         if (var2 != null) {
            var2.setMultipleMode(var1);
         }
      }

   }

   public int getVisibleIndex() {
      return this.visibleIndex;
   }

   public synchronized void makeVisible(int var1) {
      this.visibleIndex = var1;
      ListPeer var2 = (ListPeer)this.peer;
      if (var2 != null) {
         var2.makeVisible(var1);
      }

   }

   public Dimension getPreferredSize(int var1) {
      return this.preferredSize(var1);
   }

   /** @deprecated */
   @Deprecated
   public Dimension preferredSize(int var1) {
      synchronized(this.getTreeLock()) {
         ListPeer var3 = (ListPeer)this.peer;
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
         return this.rows > 0 ? this.preferredSize(this.rows) : super.preferredSize();
      }
   }

   public Dimension getMinimumSize(int var1) {
      return this.minimumSize(var1);
   }

   /** @deprecated */
   @Deprecated
   public Dimension minimumSize(int var1) {
      synchronized(this.getTreeLock()) {
         ListPeer var3 = (ListPeer)this.peer;
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
         return this.rows > 0 ? this.minimumSize(this.rows) : super.minimumSize();
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
      Object var2 = null;
      if (var1 == ActionListener.class) {
         var2 = this.actionListener;
      } else {
         if (var1 != ItemListener.class) {
            return super.getListeners(var1);
         }

         var2 = this.itemListener;
      }

      return AWTEventMulticaster.getListeners((EventListener)var2, var1);
   }

   boolean eventEnabled(AWTEvent var1) {
      switch(var1.id) {
      case 701:
         if ((this.eventMask & 512L) == 0L && this.itemListener == null) {
            return false;
         }

         return true;
      case 1001:
         if ((this.eventMask & 128L) == 0L && this.actionListener == null) {
            return false;
         }

         return true;
      default:
         return super.eventEnabled(var1);
      }
   }

   protected void processEvent(AWTEvent var1) {
      if (var1 instanceof ItemEvent) {
         this.processItemEvent((ItemEvent)var1);
      } else if (var1 instanceof ActionEvent) {
         this.processActionEvent((ActionEvent)var1);
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

   protected void processActionEvent(ActionEvent var1) {
      ActionListener var2 = this.actionListener;
      if (var2 != null) {
         var2.actionPerformed(var1);
      }

   }

   protected String paramString() {
      return super.paramString() + ",selected=" + this.getSelectedItem();
   }

   /** @deprecated */
   @Deprecated
   public synchronized void delItems(int var1, int var2) {
      for(int var3 = var2; var3 >= var1; --var3) {
         this.items.removeElementAt(var3);
      }

      ListPeer var4 = (ListPeer)this.peer;
      if (var4 != null) {
         var4.delItems(var1, var2);
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      synchronized(this) {
         ListPeer var3 = (ListPeer)this.peer;
         if (var3 != null) {
            this.selected = var3.getSelectedIndexes();
         }
      }

      var1.defaultWriteObject();
      AWTEventMulticaster.save(var1, "itemL", this.itemListener);
      AWTEventMulticaster.save(var1, "actionL", this.actionListener);
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
         } else if ("actionL" == var3) {
            this.addActionListener((ActionListener)((ActionListener)var1.readObject()));
         } else {
            var1.readObject();
         }
      }

   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new List.AccessibleAWTList();
      }

      return this.accessibleContext;
   }

   protected class AccessibleAWTList extends Component.AccessibleAWTComponent implements AccessibleSelection, ItemListener, ActionListener {
      private static final long serialVersionUID = 7924617370136012829L;

      public AccessibleAWTList() {
         super();
         List.this.addActionListener(this);
         List.this.addItemListener(this);
      }

      public void actionPerformed(ActionEvent var1) {
      }

      public void itemStateChanged(ItemEvent var1) {
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         if (List.this.isMultipleMode()) {
            var1.add(AccessibleState.MULTISELECTABLE);
         }

         return var1;
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.LIST;
      }

      public Accessible getAccessibleAt(Point var1) {
         return null;
      }

      public int getAccessibleChildrenCount() {
         return List.this.getItemCount();
      }

      public Accessible getAccessibleChild(int var1) {
         synchronized(List.this) {
            return var1 >= List.this.getItemCount() ? null : new List.AccessibleAWTList.AccessibleAWTListChild(List.this, var1);
         }
      }

      public AccessibleSelection getAccessibleSelection() {
         return this;
      }

      public int getAccessibleSelectionCount() {
         return List.this.getSelectedIndexes().length;
      }

      public Accessible getAccessibleSelection(int var1) {
         synchronized(List.this) {
            int var3 = this.getAccessibleSelectionCount();
            return var1 >= 0 && var1 < var3 ? this.getAccessibleChild(List.this.getSelectedIndexes()[var1]) : null;
         }
      }

      public boolean isAccessibleChildSelected(int var1) {
         return List.this.isIndexSelected(var1);
      }

      public void addAccessibleSelection(int var1) {
         List.this.select(var1);
      }

      public void removeAccessibleSelection(int var1) {
         List.this.deselect(var1);
      }

      public void clearAccessibleSelection() {
         synchronized(List.this) {
            int[] var2 = List.this.getSelectedIndexes();
            if (var2 != null) {
               for(int var3 = var2.length - 1; var3 >= 0; --var3) {
                  List.this.deselect(var2[var3]);
               }

            }
         }
      }

      public void selectAllAccessibleSelection() {
         synchronized(List.this) {
            for(int var2 = List.this.getItemCount() - 1; var2 >= 0; --var2) {
               List.this.select(var2);
            }

         }
      }

      protected class AccessibleAWTListChild extends Component.AccessibleAWTComponent implements Accessible {
         private static final long serialVersionUID = 4412022926028300317L;
         private List parent;
         private int indexInParent;

         public AccessibleAWTListChild(List var2, int var3) {
            super();
            this.parent = var2;
            this.setAccessibleParent(var2);
            this.indexInParent = var3;
         }

         public AccessibleContext getAccessibleContext() {
            return this;
         }

         public AccessibleRole getAccessibleRole() {
            return AccessibleRole.LIST_ITEM;
         }

         public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet var1 = super.getAccessibleStateSet();
            if (this.parent.isIndexSelected(this.indexInParent)) {
               var1.add(AccessibleState.SELECTED);
            }

            return var1;
         }

         public Locale getLocale() {
            return this.parent.getLocale();
         }

         public int getAccessibleIndexInParent() {
            return this.indexInParent;
         }

         public int getAccessibleChildrenCount() {
            return 0;
         }

         public Accessible getAccessibleChild(int var1) {
            return null;
         }

         public Color getBackground() {
            return this.parent.getBackground();
         }

         public void setBackground(Color var1) {
            this.parent.setBackground(var1);
         }

         public Color getForeground() {
            return this.parent.getForeground();
         }

         public void setForeground(Color var1) {
            this.parent.setForeground(var1);
         }

         public Cursor getCursor() {
            return this.parent.getCursor();
         }

         public void setCursor(Cursor var1) {
            this.parent.setCursor(var1);
         }

         public Font getFont() {
            return this.parent.getFont();
         }

         public void setFont(Font var1) {
            this.parent.setFont(var1);
         }

         public FontMetrics getFontMetrics(Font var1) {
            return this.parent.getFontMetrics(var1);
         }

         public boolean isEnabled() {
            return this.parent.isEnabled();
         }

         public void setEnabled(boolean var1) {
            this.parent.setEnabled(var1);
         }

         public boolean isVisible() {
            return false;
         }

         public void setVisible(boolean var1) {
            this.parent.setVisible(var1);
         }

         public boolean isShowing() {
            return false;
         }

         public boolean contains(Point var1) {
            return false;
         }

         public Point getLocationOnScreen() {
            return null;
         }

         public Point getLocation() {
            return null;
         }

         public void setLocation(Point var1) {
         }

         public Rectangle getBounds() {
            return null;
         }

         public void setBounds(Rectangle var1) {
         }

         public Dimension getSize() {
            return null;
         }

         public void setSize(Dimension var1) {
         }

         public Accessible getAccessibleAt(Point var1) {
            return null;
         }

         public boolean isFocusTraversable() {
            return false;
         }

         public void requestFocus() {
         }

         public void addFocusListener(FocusListener var1) {
         }

         public void removeFocusListener(FocusListener var1) {
         }
      }
   }
}
