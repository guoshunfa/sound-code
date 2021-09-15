package javax.swing.table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Vector;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

public class DefaultTableColumnModel implements TableColumnModel, PropertyChangeListener, ListSelectionListener, Serializable {
   protected Vector<TableColumn> tableColumns = new Vector();
   protected ListSelectionModel selectionModel;
   protected int columnMargin;
   protected EventListenerList listenerList = new EventListenerList();
   protected transient ChangeEvent changeEvent = null;
   protected boolean columnSelectionAllowed;
   protected int totalColumnWidth;

   public DefaultTableColumnModel() {
      this.setSelectionModel(this.createSelectionModel());
      this.setColumnMargin(1);
      this.invalidateWidthCache();
      this.setColumnSelectionAllowed(false);
   }

   public void addColumn(TableColumn var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Object is null");
      } else {
         this.tableColumns.addElement(var1);
         var1.addPropertyChangeListener(this);
         this.invalidateWidthCache();
         this.fireColumnAdded(new TableColumnModelEvent(this, 0, this.getColumnCount() - 1));
      }
   }

   public void removeColumn(TableColumn var1) {
      int var2 = this.tableColumns.indexOf(var1);
      if (var2 != -1) {
         if (this.selectionModel != null) {
            this.selectionModel.removeIndexInterval(var2, var2);
         }

         var1.removePropertyChangeListener(this);
         this.tableColumns.removeElementAt(var2);
         this.invalidateWidthCache();
         this.fireColumnRemoved(new TableColumnModelEvent(this, var2, 0));
      }

   }

   public void moveColumn(int var1, int var2) {
      if (var1 >= 0 && var1 < this.getColumnCount() && var2 >= 0 && var2 < this.getColumnCount()) {
         if (var1 == var2) {
            this.fireColumnMoved(new TableColumnModelEvent(this, var1, var2));
         } else {
            TableColumn var3 = (TableColumn)this.tableColumns.elementAt(var1);
            this.tableColumns.removeElementAt(var1);
            boolean var4 = this.selectionModel.isSelectedIndex(var1);
            this.selectionModel.removeIndexInterval(var1, var1);
            this.tableColumns.insertElementAt(var3, var2);
            this.selectionModel.insertIndexInterval(var2, 1, true);
            if (var4) {
               this.selectionModel.addSelectionInterval(var2, var2);
            } else {
               this.selectionModel.removeSelectionInterval(var2, var2);
            }

            this.fireColumnMoved(new TableColumnModelEvent(this, var1, var2));
         }
      } else {
         throw new IllegalArgumentException("moveColumn() - Index out of range");
      }
   }

   public void setColumnMargin(int var1) {
      if (var1 != this.columnMargin) {
         this.columnMargin = var1;
         this.fireColumnMarginChanged();
      }

   }

   public int getColumnCount() {
      return this.tableColumns.size();
   }

   public Enumeration<TableColumn> getColumns() {
      return this.tableColumns.elements();
   }

   public int getColumnIndex(Object var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Identifier is null");
      } else {
         Enumeration var2 = this.getColumns();

         for(int var4 = 0; var2.hasMoreElements(); ++var4) {
            TableColumn var3 = (TableColumn)var2.nextElement();
            if (var1.equals(var3.getIdentifier())) {
               return var4;
            }
         }

         throw new IllegalArgumentException("Identifier not found");
      }
   }

   public TableColumn getColumn(int var1) {
      return (TableColumn)this.tableColumns.elementAt(var1);
   }

   public int getColumnMargin() {
      return this.columnMargin;
   }

   public int getColumnIndexAtX(int var1) {
      if (var1 < 0) {
         return -1;
      } else {
         int var2 = this.getColumnCount();

         for(int var3 = 0; var3 < var2; ++var3) {
            var1 -= this.getColumn(var3).getWidth();
            if (var1 < 0) {
               return var3;
            }
         }

         return -1;
      }
   }

   public int getTotalColumnWidth() {
      if (this.totalColumnWidth == -1) {
         this.recalcWidthCache();
      }

      return this.totalColumnWidth;
   }

   public void setSelectionModel(ListSelectionModel var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Cannot set a null SelectionModel");
      } else {
         ListSelectionModel var2 = this.selectionModel;
         if (var1 != var2) {
            if (var2 != null) {
               var2.removeListSelectionListener(this);
            }

            this.selectionModel = var1;
            var1.addListSelectionListener(this);
         }

      }
   }

   public ListSelectionModel getSelectionModel() {
      return this.selectionModel;
   }

   public void setColumnSelectionAllowed(boolean var1) {
      this.columnSelectionAllowed = var1;
   }

   public boolean getColumnSelectionAllowed() {
      return this.columnSelectionAllowed;
   }

   public int[] getSelectedColumns() {
      if (this.selectionModel != null) {
         int var1 = this.selectionModel.getMinSelectionIndex();
         int var2 = this.selectionModel.getMaxSelectionIndex();
         if (var1 != -1 && var2 != -1) {
            int[] var3 = new int[1 + (var2 - var1)];
            int var4 = 0;

            for(int var5 = var1; var5 <= var2; ++var5) {
               if (this.selectionModel.isSelectedIndex(var5)) {
                  var3[var4++] = var5;
               }
            }

            int[] var6 = new int[var4];
            System.arraycopy(var3, 0, var6, 0, var4);
            return var6;
         } else {
            return new int[0];
         }
      } else {
         return new int[0];
      }
   }

   public int getSelectedColumnCount() {
      if (this.selectionModel != null) {
         int var1 = this.selectionModel.getMinSelectionIndex();
         int var2 = this.selectionModel.getMaxSelectionIndex();
         int var3 = 0;

         for(int var4 = var1; var4 <= var2; ++var4) {
            if (this.selectionModel.isSelectedIndex(var4)) {
               ++var3;
            }
         }

         return var3;
      } else {
         return 0;
      }
   }

   public void addColumnModelListener(TableColumnModelListener var1) {
      this.listenerList.add(TableColumnModelListener.class, var1);
   }

   public void removeColumnModelListener(TableColumnModelListener var1) {
      this.listenerList.remove(TableColumnModelListener.class, var1);
   }

   public TableColumnModelListener[] getColumnModelListeners() {
      return (TableColumnModelListener[])this.listenerList.getListeners(TableColumnModelListener.class);
   }

   protected void fireColumnAdded(TableColumnModelEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();

      for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
         if (var2[var3] == TableColumnModelListener.class) {
            ((TableColumnModelListener)var2[var3 + 1]).columnAdded(var1);
         }
      }

   }

   protected void fireColumnRemoved(TableColumnModelEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();

      for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
         if (var2[var3] == TableColumnModelListener.class) {
            ((TableColumnModelListener)var2[var3 + 1]).columnRemoved(var1);
         }
      }

   }

   protected void fireColumnMoved(TableColumnModelEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();

      for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
         if (var2[var3] == TableColumnModelListener.class) {
            ((TableColumnModelListener)var2[var3 + 1]).columnMoved(var1);
         }
      }

   }

   protected void fireColumnSelectionChanged(ListSelectionEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();

      for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
         if (var2[var3] == TableColumnModelListener.class) {
            ((TableColumnModelListener)var2[var3 + 1]).columnSelectionChanged(var1);
         }
      }

   }

   protected void fireColumnMarginChanged() {
      Object[] var1 = this.listenerList.getListenerList();

      for(int var2 = var1.length - 2; var2 >= 0; var2 -= 2) {
         if (var1[var2] == TableColumnModelListener.class) {
            if (this.changeEvent == null) {
               this.changeEvent = new ChangeEvent(this);
            }

            ((TableColumnModelListener)var1[var2 + 1]).columnMarginChanged(this.changeEvent);
         }
      }

   }

   public <T extends EventListener> T[] getListeners(Class<T> var1) {
      return this.listenerList.getListeners(var1);
   }

   public void propertyChange(PropertyChangeEvent var1) {
      String var2 = var1.getPropertyName();
      if (var2 == "width" || var2 == "preferredWidth") {
         this.invalidateWidthCache();
         this.fireColumnMarginChanged();
      }

   }

   public void valueChanged(ListSelectionEvent var1) {
      this.fireColumnSelectionChanged(var1);
   }

   protected ListSelectionModel createSelectionModel() {
      return new DefaultListSelectionModel();
   }

   protected void recalcWidthCache() {
      Enumeration var1 = this.getColumns();

      for(this.totalColumnWidth = 0; var1.hasMoreElements(); this.totalColumnWidth += ((TableColumn)var1.nextElement()).getWidth()) {
      }

   }

   private void invalidateWidthCache() {
      this.totalColumnWidth = -1;
   }
}
