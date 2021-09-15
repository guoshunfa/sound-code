package com.sun.rowset.internal;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.BitSet;

public class Row extends BaseRow implements Serializable, Cloneable {
   static final long serialVersionUID = 5047859032611314762L;
   private Object[] currentVals;
   private BitSet colsChanged;
   private boolean deleted;
   private boolean updated;
   private boolean inserted;
   private int numCols;

   public Row(int var1) {
      this.origVals = new Object[var1];
      this.currentVals = new Object[var1];
      this.colsChanged = new BitSet(var1);
      this.numCols = var1;
   }

   public Row(int var1, Object[] var2) {
      this.origVals = new Object[var1];
      System.arraycopy(var2, 0, this.origVals, 0, var1);
      this.currentVals = new Object[var1];
      this.colsChanged = new BitSet(var1);
      this.numCols = var1;
   }

   public void initColumnObject(int var1, Object var2) {
      this.origVals[var1 - 1] = var2;
   }

   public void setColumnObject(int var1, Object var2) {
      this.currentVals[var1 - 1] = var2;
      this.setColUpdated(var1 - 1);
   }

   public Object getColumnObject(int var1) throws SQLException {
      return this.getColUpdated(var1 - 1) ? this.currentVals[var1 - 1] : this.origVals[var1 - 1];
   }

   public boolean getColUpdated(int var1) {
      return this.colsChanged.get(var1);
   }

   public void setDeleted() {
      this.deleted = true;
   }

   public boolean getDeleted() {
      return this.deleted;
   }

   public void clearDeleted() {
      this.deleted = false;
   }

   public void setInserted() {
      this.inserted = true;
   }

   public boolean getInserted() {
      return this.inserted;
   }

   public void clearInserted() {
      this.inserted = false;
   }

   public boolean getUpdated() {
      return this.updated;
   }

   public void setUpdated() {
      for(int var1 = 0; var1 < this.numCols; ++var1) {
         if (this.getColUpdated(var1)) {
            this.updated = true;
            return;
         }
      }

   }

   private void setColUpdated(int var1) {
      this.colsChanged.set(var1);
   }

   public void clearUpdated() {
      this.updated = false;

      for(int var1 = 0; var1 < this.numCols; ++var1) {
         this.currentVals[var1] = null;
         this.colsChanged.clear(var1);
      }

   }

   public void moveCurrentToOrig() {
      for(int var1 = 0; var1 < this.numCols; ++var1) {
         if (this.getColUpdated(var1)) {
            this.origVals[var1] = this.currentVals[var1];
            this.currentVals[var1] = null;
            this.colsChanged.clear(var1);
         }
      }

      this.updated = false;
   }

   public BaseRow getCurrentRow() {
      return null;
   }
}
