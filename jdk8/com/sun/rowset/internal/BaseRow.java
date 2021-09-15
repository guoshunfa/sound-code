package com.sun.rowset.internal;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;

public abstract class BaseRow implements Serializable, Cloneable {
   private static final long serialVersionUID = 4152013523511412238L;
   protected Object[] origVals;

   public Object[] getOrigRow() {
      Object[] var1 = this.origVals;
      return var1 == null ? null : Arrays.copyOf(var1, var1.length);
   }

   public abstract Object getColumnObject(int var1) throws SQLException;

   public abstract void setColumnObject(int var1, Object var2) throws SQLException;
}
