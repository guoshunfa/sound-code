package com.sun.rowset;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Hashtable;
import javax.sql.rowset.FilteredRowSet;
import javax.sql.rowset.Predicate;

public class FilteredRowSetImpl extends WebRowSetImpl implements Serializable, Cloneable, FilteredRowSet {
   private Predicate p;
   private boolean onInsertRow = false;
   static final long serialVersionUID = 6178454588413509360L;

   public FilteredRowSetImpl() throws SQLException {
   }

   public FilteredRowSetImpl(Hashtable var1) throws SQLException {
      super(var1);
   }

   public void setFilter(Predicate var1) throws SQLException {
      this.p = var1;
   }

   public Predicate getFilter() {
      return this.p;
   }

   protected boolean internalNext() throws SQLException {
      boolean var1 = false;
      int var2 = this.getRow();

      while(true) {
         if (var2 <= this.size()) {
            var1 = super.internalNext();
            if (!var1 || this.p == null) {
               return var1;
            }

            if (!this.p.evaluate(this)) {
               ++var2;
               continue;
            }
         }

         return var1;
      }
   }

   protected boolean internalPrevious() throws SQLException {
      boolean var1 = false;

      for(int var2 = this.getRow(); var2 > 0; --var2) {
         var1 = super.internalPrevious();
         if (this.p == null) {
            return var1;
         }

         if (this.p.evaluate(this)) {
            break;
         }
      }

      return var1;
   }

   protected boolean internalFirst() throws SQLException {
      boolean var1 = super.internalFirst();
      if (this.p == null) {
         return var1;
      } else {
         while(var1 && !this.p.evaluate(this)) {
            var1 = super.internalNext();
         }

         return var1;
      }
   }

   protected boolean internalLast() throws SQLException {
      boolean var1 = super.internalLast();
      if (this.p == null) {
         return var1;
      } else {
         while(var1 && !this.p.evaluate(this)) {
            var1 = super.internalPrevious();
         }

         return var1;
      }
   }

   public boolean relative(int var1) throws SQLException {
      boolean var3 = false;
      boolean var4 = false;
      if (this.getType() == 1003) {
         throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.relative").toString());
      } else {
         boolean var2;
         int var5;
         if (var1 > 0) {
            for(var5 = 0; var5 < var1; ++var5) {
               if (this.isAfterLast()) {
                  return false;
               }

               var3 = this.internalNext();
            }

            var2 = var3;
         } else {
            for(var5 = var1; var5 < 0; ++var5) {
               if (this.isBeforeFirst()) {
                  return false;
               }

               var4 = this.internalPrevious();
            }

            var2 = var4;
         }

         if (var1 != 0) {
            this.notifyCursorMoved();
         }

         return var2;
      }
   }

   public boolean absolute(int var1) throws SQLException {
      boolean var3 = false;
      if (var1 != 0 && this.getType() != 1003) {
         boolean var2;
         int var4;
         if (var1 > 0) {
            var3 = this.internalFirst();

            for(var4 = 0; var4 < var1 - 1; ++var4) {
               if (this.isAfterLast()) {
                  return false;
               }

               var3 = this.internalNext();
            }

            var2 = var3;
         } else {
            var3 = this.internalLast();

            for(var4 = var1; var4 + 1 < 0; ++var4) {
               if (this.isBeforeFirst()) {
                  return false;
               }

               var3 = this.internalPrevious();
            }

            var2 = var3;
         }

         this.notifyCursorMoved();
         return var2;
      } else {
         throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.absolute").toString());
      }
   }

   public void moveToInsertRow() throws SQLException {
      this.onInsertRow = true;
      super.moveToInsertRow();
   }

   public void updateInt(int var1, int var2) throws SQLException {
      if (this.onInsertRow && this.p != null) {
         boolean var3 = this.p.evaluate(var2, var1);
         if (!var3) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
         }
      }

      super.updateInt(var1, var2);
   }

   public void updateInt(String var1, int var2) throws SQLException {
      this.updateInt(this.findColumn(var1), var2);
   }

   public void updateBoolean(int var1, boolean var2) throws SQLException {
      if (this.onInsertRow && this.p != null) {
         boolean var3 = this.p.evaluate(var2, var1);
         if (!var3) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
         }
      }

      super.updateBoolean(var1, var2);
   }

   public void updateBoolean(String var1, boolean var2) throws SQLException {
      this.updateBoolean(this.findColumn(var1), var2);
   }

   public void updateByte(int var1, byte var2) throws SQLException {
      if (this.onInsertRow && this.p != null) {
         boolean var3 = this.p.evaluate(var2, var1);
         if (!var3) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
         }
      }

      super.updateByte(var1, var2);
   }

   public void updateByte(String var1, byte var2) throws SQLException {
      this.updateByte(this.findColumn(var1), var2);
   }

   public void updateShort(int var1, short var2) throws SQLException {
      if (this.onInsertRow && this.p != null) {
         boolean var3 = this.p.evaluate(var2, var1);
         if (!var3) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
         }
      }

      super.updateShort(var1, var2);
   }

   public void updateShort(String var1, short var2) throws SQLException {
      this.updateShort(this.findColumn(var1), var2);
   }

   public void updateLong(int var1, long var2) throws SQLException {
      if (this.onInsertRow && this.p != null) {
         boolean var4 = this.p.evaluate(var2, var1);
         if (!var4) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
         }
      }

      super.updateLong(var1, var2);
   }

   public void updateLong(String var1, long var2) throws SQLException {
      this.updateLong(this.findColumn(var1), var2);
   }

   public void updateFloat(int var1, float var2) throws SQLException {
      if (this.onInsertRow && this.p != null) {
         boolean var3 = this.p.evaluate(var2, var1);
         if (!var3) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
         }
      }

      super.updateFloat(var1, var2);
   }

   public void updateFloat(String var1, float var2) throws SQLException {
      this.updateFloat(this.findColumn(var1), var2);
   }

   public void updateDouble(int var1, double var2) throws SQLException {
      if (this.onInsertRow && this.p != null) {
         boolean var4 = this.p.evaluate(var2, var1);
         if (!var4) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
         }
      }

      super.updateDouble(var1, var2);
   }

   public void updateDouble(String var1, double var2) throws SQLException {
      this.updateDouble(this.findColumn(var1), var2);
   }

   public void updateBigDecimal(int var1, BigDecimal var2) throws SQLException {
      if (this.onInsertRow && this.p != null) {
         boolean var3 = this.p.evaluate(var2, var1);
         if (!var3) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
         }
      }

      super.updateBigDecimal(var1, var2);
   }

   public void updateBigDecimal(String var1, BigDecimal var2) throws SQLException {
      this.updateBigDecimal(this.findColumn(var1), var2);
   }

   public void updateString(int var1, String var2) throws SQLException {
      if (this.onInsertRow && this.p != null) {
         boolean var3 = this.p.evaluate(var2, var1);
         if (!var3) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
         }
      }

      super.updateString(var1, var2);
   }

   public void updateString(String var1, String var2) throws SQLException {
      this.updateString(this.findColumn(var1), var2);
   }

   public void updateBytes(int var1, byte[] var2) throws SQLException {
      String var4 = "";
      Byte[] var5 = new Byte[var2.length];

      for(int var6 = 0; var6 < var2.length; ++var6) {
         var5[var6] = var2[var6];
         var4 = var4.concat(var5[var6].toString());
      }

      if (this.onInsertRow && this.p != null) {
         boolean var3 = this.p.evaluate(var4, var1);
         if (!var3) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
         }
      }

      super.updateBytes(var1, var2);
   }

   public void updateBytes(String var1, byte[] var2) throws SQLException {
      this.updateBytes(this.findColumn(var1), var2);
   }

   public void updateDate(int var1, Date var2) throws SQLException {
      if (this.onInsertRow && this.p != null) {
         boolean var3 = this.p.evaluate(var2, var1);
         if (!var3) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
         }
      }

      super.updateDate(var1, var2);
   }

   public void updateDate(String var1, Date var2) throws SQLException {
      this.updateDate(this.findColumn(var1), var2);
   }

   public void updateTime(int var1, Time var2) throws SQLException {
      if (this.onInsertRow && this.p != null) {
         boolean var3 = this.p.evaluate(var2, var1);
         if (!var3) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
         }
      }

      super.updateTime(var1, var2);
   }

   public void updateTime(String var1, Time var2) throws SQLException {
      this.updateTime(this.findColumn(var1), var2);
   }

   public void updateTimestamp(int var1, Timestamp var2) throws SQLException {
      if (this.onInsertRow && this.p != null) {
         boolean var3 = this.p.evaluate(var2, var1);
         if (!var3) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
         }
      }

      super.updateTimestamp(var1, var2);
   }

   public void updateTimestamp(String var1, Timestamp var2) throws SQLException {
      this.updateTimestamp(this.findColumn(var1), var2);
   }

   public void updateAsciiStream(int var1, InputStream var2, int var3) throws SQLException {
      if (this.onInsertRow && this.p != null) {
         boolean var4 = this.p.evaluate(var2, var1);
         if (!var4) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
         }
      }

      super.updateAsciiStream(var1, var2, var3);
   }

   public void updateAsciiStream(String var1, InputStream var2, int var3) throws SQLException {
      this.updateAsciiStream(this.findColumn(var1), var2, var3);
   }

   public void updateCharacterStream(int var1, Reader var2, int var3) throws SQLException {
      if (this.onInsertRow && this.p != null) {
         boolean var4 = this.p.evaluate(var2, var1);
         if (!var4) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
         }
      }

      super.updateCharacterStream(var1, var2, var3);
   }

   public void updateCharacterStream(String var1, Reader var2, int var3) throws SQLException {
      this.updateCharacterStream(this.findColumn(var1), var2, var3);
   }

   public void updateBinaryStream(int var1, InputStream var2, int var3) throws SQLException {
      if (this.onInsertRow && this.p != null) {
         boolean var4 = this.p.evaluate(var2, var1);
         if (!var4) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
         }
      }

      super.updateBinaryStream(var1, var2, var3);
   }

   public void updateBinaryStream(String var1, InputStream var2, int var3) throws SQLException {
      this.updateBinaryStream(this.findColumn(var1), var2, var3);
   }

   public void updateObject(int var1, Object var2) throws SQLException {
      if (this.onInsertRow && this.p != null) {
         boolean var3 = this.p.evaluate(var2, var1);
         if (!var3) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
         }
      }

      super.updateObject(var1, var2);
   }

   public void updateObject(String var1, Object var2) throws SQLException {
      this.updateObject(this.findColumn(var1), var2);
   }

   public void updateObject(int var1, Object var2, int var3) throws SQLException {
      if (this.onInsertRow && this.p != null) {
         boolean var4 = this.p.evaluate(var2, var1);
         if (!var4) {
            throw new SQLException(this.resBundle.handleGetObject("filteredrowsetimpl.notallowed").toString());
         }
      }

      super.updateObject(var1, var2, var3);
   }

   public void updateObject(String var1, Object var2, int var3) throws SQLException {
      this.updateObject(this.findColumn(var1), var2, var3);
   }

   public void insertRow() throws SQLException {
      this.onInsertRow = false;
      super.insertRow();
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
