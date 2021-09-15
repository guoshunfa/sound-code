package com.sun.rowset.internal;

import com.sun.rowset.JdbcRowSetResourceBundle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.BitSet;
import javax.sql.RowSetMetaData;

public class InsertRow extends BaseRow implements Serializable, Cloneable {
   private BitSet colsInserted;
   private int cols;
   private JdbcRowSetResourceBundle resBundle;
   static final long serialVersionUID = 1066099658102869344L;

   public InsertRow(int var1) {
      this.origVals = new Object[var1];
      this.colsInserted = new BitSet(var1);
      this.cols = var1;

      try {
         this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   protected void markColInserted(int var1) {
      this.colsInserted.set(var1);
   }

   public boolean isCompleteRow(RowSetMetaData var1) throws SQLException {
      for(int var2 = 0; var2 < this.cols; ++var2) {
         if (!this.colsInserted.get(var2) && var1.isNullable(var2 + 1) == 0) {
            return false;
         }
      }

      return true;
   }

   public void initInsertRow() {
      for(int var1 = 0; var1 < this.cols; ++var1) {
         this.colsInserted.clear(var1);
      }

   }

   public Object getColumnObject(int var1) throws SQLException {
      if (!this.colsInserted.get(var1 - 1)) {
         throw new SQLException(this.resBundle.handleGetObject("insertrow.novalue").toString());
      } else {
         return this.origVals[var1 - 1];
      }
   }

   public void setColumnObject(int var1, Object var2) {
      this.origVals[var1 - 1] = var2;
      this.markColInserted(var1 - 1);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();

      try {
         this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }
}
