package javax.swing.table;

import java.io.Serializable;
import java.util.Vector;
import javax.swing.event.TableModelEvent;

public class DefaultTableModel extends AbstractTableModel implements Serializable {
   protected Vector dataVector;
   protected Vector columnIdentifiers;

   public DefaultTableModel() {
      this(0, 0);
   }

   private static Vector newVector(int var0) {
      Vector var1 = new Vector(var0);
      var1.setSize(var0);
      return var1;
   }

   public DefaultTableModel(int var1, int var2) {
      this(newVector(var2), var1);
   }

   public DefaultTableModel(Vector var1, int var2) {
      this.setDataVector(newVector(var2), var1);
   }

   public DefaultTableModel(Object[] var1, int var2) {
      this(convertToVector(var1), var2);
   }

   public DefaultTableModel(Vector var1, Vector var2) {
      this.setDataVector(var1, var2);
   }

   public DefaultTableModel(Object[][] var1, Object[] var2) {
      this.setDataVector(var1, var2);
   }

   public Vector getDataVector() {
      return this.dataVector;
   }

   private static Vector nonNullVector(Vector var0) {
      return var0 != null ? var0 : new Vector();
   }

   public void setDataVector(Vector var1, Vector var2) {
      this.dataVector = nonNullVector(var1);
      this.columnIdentifiers = nonNullVector(var2);
      this.justifyRows(0, this.getRowCount());
      this.fireTableStructureChanged();
   }

   public void setDataVector(Object[][] var1, Object[] var2) {
      this.setDataVector(convertToVector(var1), convertToVector(var2));
   }

   public void newDataAvailable(TableModelEvent var1) {
      this.fireTableChanged(var1);
   }

   private void justifyRows(int var1, int var2) {
      this.dataVector.setSize(this.getRowCount());

      for(int var3 = var1; var3 < var2; ++var3) {
         if (this.dataVector.elementAt(var3) == null) {
            this.dataVector.setElementAt(new Vector(), var3);
         }

         ((Vector)this.dataVector.elementAt(var3)).setSize(this.getColumnCount());
      }

   }

   public void newRowsAdded(TableModelEvent var1) {
      this.justifyRows(var1.getFirstRow(), var1.getLastRow() + 1);
      this.fireTableChanged(var1);
   }

   public void rowsRemoved(TableModelEvent var1) {
      this.fireTableChanged(var1);
   }

   public void setNumRows(int var1) {
      int var2 = this.getRowCount();
      if (var2 != var1) {
         this.dataVector.setSize(var1);
         if (var1 <= var2) {
            this.fireTableRowsDeleted(var1, var2 - 1);
         } else {
            this.justifyRows(var2, var1);
            this.fireTableRowsInserted(var2, var1 - 1);
         }

      }
   }

   public void setRowCount(int var1) {
      this.setNumRows(var1);
   }

   public void addRow(Vector var1) {
      this.insertRow(this.getRowCount(), var1);
   }

   public void addRow(Object[] var1) {
      this.addRow(convertToVector(var1));
   }

   public void insertRow(int var1, Vector var2) {
      this.dataVector.insertElementAt(var2, var1);
      this.justifyRows(var1, var1 + 1);
      this.fireTableRowsInserted(var1, var1);
   }

   public void insertRow(int var1, Object[] var2) {
      this.insertRow(var1, convertToVector(var2));
   }

   private static int gcd(int var0, int var1) {
      return var1 == 0 ? var0 : gcd(var1, var0 % var1);
   }

   private static void rotate(Vector var0, int var1, int var2, int var3) {
      int var4 = var2 - var1;
      int var5 = var4 - var3;
      int var6 = gcd(var4, var5);

      for(int var7 = 0; var7 < var6; ++var7) {
         int var8 = var7;
         Object var9 = var0.elementAt(var1 + var7);

         for(int var10 = (var7 + var5) % var4; var10 != var7; var10 = (var10 + var5) % var4) {
            var0.setElementAt(var0.elementAt(var1 + var10), var1 + var8);
            var8 = var10;
         }

         var0.setElementAt(var9, var1 + var8);
      }

   }

   public void moveRow(int var1, int var2, int var3) {
      int var4 = var3 - var1;
      int var5;
      int var6;
      if (var4 < 0) {
         var5 = var3;
         var6 = var2;
      } else {
         var5 = var1;
         var6 = var3 + var2 - var1;
      }

      rotate(this.dataVector, var5, var6 + 1, var4);
      this.fireTableRowsUpdated(var5, var6);
   }

   public void removeRow(int var1) {
      this.dataVector.removeElementAt(var1);
      this.fireTableRowsDeleted(var1, var1);
   }

   public void setColumnIdentifiers(Vector var1) {
      this.setDataVector(this.dataVector, var1);
   }

   public void setColumnIdentifiers(Object[] var1) {
      this.setColumnIdentifiers(convertToVector(var1));
   }

   public void setColumnCount(int var1) {
      this.columnIdentifiers.setSize(var1);
      this.justifyRows(0, this.getRowCount());
      this.fireTableStructureChanged();
   }

   public void addColumn(Object var1) {
      this.addColumn(var1, (Vector)null);
   }

   public void addColumn(Object var1, Vector var2) {
      this.columnIdentifiers.addElement(var1);
      if (var2 != null) {
         int var3 = var2.size();
         if (var3 > this.getRowCount()) {
            this.dataVector.setSize(var3);
         }

         this.justifyRows(0, this.getRowCount());
         int var4 = this.getColumnCount() - 1;

         for(int var5 = 0; var5 < var3; ++var5) {
            Vector var6 = (Vector)this.dataVector.elementAt(var5);
            var6.setElementAt(var2.elementAt(var5), var4);
         }
      } else {
         this.justifyRows(0, this.getRowCount());
      }

      this.fireTableStructureChanged();
   }

   public void addColumn(Object var1, Object[] var2) {
      this.addColumn(var1, convertToVector(var2));
   }

   public int getRowCount() {
      return this.dataVector.size();
   }

   public int getColumnCount() {
      return this.columnIdentifiers.size();
   }

   public String getColumnName(int var1) {
      Object var2 = null;
      if (var1 < this.columnIdentifiers.size() && var1 >= 0) {
         var2 = this.columnIdentifiers.elementAt(var1);
      }

      return var2 == null ? super.getColumnName(var1) : var2.toString();
   }

   public boolean isCellEditable(int var1, int var2) {
      return true;
   }

   public Object getValueAt(int var1, int var2) {
      Vector var3 = (Vector)this.dataVector.elementAt(var1);
      return var3.elementAt(var2);
   }

   public void setValueAt(Object var1, int var2, int var3) {
      Vector var4 = (Vector)this.dataVector.elementAt(var2);
      var4.setElementAt(var1, var3);
      this.fireTableCellUpdated(var2, var3);
   }

   protected static Vector convertToVector(Object[] var0) {
      if (var0 == null) {
         return null;
      } else {
         Vector var1 = new Vector(var0.length);
         Object[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Object var5 = var2[var4];
            var1.addElement(var5);
         }

         return var1;
      }
   }

   protected static Vector convertToVector(Object[][] var0) {
      if (var0 == null) {
         return null;
      } else {
         Vector var1 = new Vector(var0.length);
         Object[][] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Object[] var5 = var2[var4];
            var1.addElement(convertToVector(var5));
         }

         return var1;
      }
   }
}
