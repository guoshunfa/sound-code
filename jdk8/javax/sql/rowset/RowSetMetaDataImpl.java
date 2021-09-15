package javax.sql.rowset;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import javax.sql.RowSetMetaData;

public class RowSetMetaDataImpl implements RowSetMetaData, Serializable {
   private int colCount;
   private RowSetMetaDataImpl.ColInfo[] colInfo;
   static final long serialVersionUID = 6893806403181801867L;

   private void checkColRange(int var1) throws SQLException {
      if (var1 <= 0 || var1 > this.colCount) {
         throw new SQLException("Invalid column index :" + var1);
      }
   }

   private void checkColType(int var1) throws SQLException {
      try {
         Class var2 = Types.class;
         Field[] var3 = var2.getFields();
         boolean var4 = false;

         for(int var5 = 0; var5 < var3.length; ++var5) {
            int var7 = var3[var5].getInt(var2);
            if (var7 == var1) {
               return;
            }
         }
      } catch (Exception var6) {
         throw new SQLException(var6.getMessage());
      }

      throw new SQLException("Invalid SQL type for column");
   }

   public void setColumnCount(int var1) throws SQLException {
      if (var1 <= 0) {
         throw new SQLException("Invalid column count. Cannot be less or equal to zero");
      } else {
         this.colCount = var1;
         if (this.colCount != Integer.MAX_VALUE) {
            this.colInfo = new RowSetMetaDataImpl.ColInfo[this.colCount + 1];

            for(int var2 = 1; var2 <= this.colCount; ++var2) {
               this.colInfo[var2] = new RowSetMetaDataImpl.ColInfo();
            }
         }

      }
   }

   public void setAutoIncrement(int var1, boolean var2) throws SQLException {
      this.checkColRange(var1);
      this.colInfo[var1].autoIncrement = var2;
   }

   public void setCaseSensitive(int var1, boolean var2) throws SQLException {
      this.checkColRange(var1);
      this.colInfo[var1].caseSensitive = var2;
   }

   public void setSearchable(int var1, boolean var2) throws SQLException {
      this.checkColRange(var1);
      this.colInfo[var1].searchable = var2;
   }

   public void setCurrency(int var1, boolean var2) throws SQLException {
      this.checkColRange(var1);
      this.colInfo[var1].currency = var2;
   }

   public void setNullable(int var1, int var2) throws SQLException {
      if (var2 >= 0 && var2 <= 2) {
         this.checkColRange(var1);
         this.colInfo[var1].nullable = var2;
      } else {
         throw new SQLException("Invalid nullable constant set. Must be either columnNoNulls, columnNullable or columnNullableUnknown");
      }
   }

   public void setSigned(int var1, boolean var2) throws SQLException {
      this.checkColRange(var1);
      this.colInfo[var1].signed = var2;
   }

   public void setColumnDisplaySize(int var1, int var2) throws SQLException {
      if (var2 < 0) {
         throw new SQLException("Invalid column display size. Cannot be less than zero");
      } else {
         this.checkColRange(var1);
         this.colInfo[var1].columnDisplaySize = var2;
      }
   }

   public void setColumnLabel(int var1, String var2) throws SQLException {
      this.checkColRange(var1);
      if (var2 != null) {
         this.colInfo[var1].columnLabel = var2;
      } else {
         this.colInfo[var1].columnLabel = "";
      }

   }

   public void setColumnName(int var1, String var2) throws SQLException {
      this.checkColRange(var1);
      if (var2 != null) {
         this.colInfo[var1].columnName = var2;
      } else {
         this.colInfo[var1].columnName = "";
      }

   }

   public void setSchemaName(int var1, String var2) throws SQLException {
      this.checkColRange(var1);
      if (var2 != null) {
         this.colInfo[var1].schemaName = var2;
      } else {
         this.colInfo[var1].schemaName = "";
      }

   }

   public void setPrecision(int var1, int var2) throws SQLException {
      if (var2 < 0) {
         throw new SQLException("Invalid precision value. Cannot be less than zero");
      } else {
         this.checkColRange(var1);
         this.colInfo[var1].colPrecision = var2;
      }
   }

   public void setScale(int var1, int var2) throws SQLException {
      if (var2 < 0) {
         throw new SQLException("Invalid scale size. Cannot be less than zero");
      } else {
         this.checkColRange(var1);
         this.colInfo[var1].colScale = var2;
      }
   }

   public void setTableName(int var1, String var2) throws SQLException {
      this.checkColRange(var1);
      if (var2 != null) {
         this.colInfo[var1].tableName = var2;
      } else {
         this.colInfo[var1].tableName = "";
      }

   }

   public void setCatalogName(int var1, String var2) throws SQLException {
      this.checkColRange(var1);
      if (var2 != null) {
         this.colInfo[var1].catName = var2;
      } else {
         this.colInfo[var1].catName = "";
      }

   }

   public void setColumnType(int var1, int var2) throws SQLException {
      this.checkColType(var2);
      this.checkColRange(var1);
      this.colInfo[var1].colType = var2;
   }

   public void setColumnTypeName(int var1, String var2) throws SQLException {
      this.checkColRange(var1);
      if (var2 != null) {
         this.colInfo[var1].colTypeName = var2;
      } else {
         this.colInfo[var1].colTypeName = "";
      }

   }

   public int getColumnCount() throws SQLException {
      return this.colCount;
   }

   public boolean isAutoIncrement(int var1) throws SQLException {
      this.checkColRange(var1);
      return this.colInfo[var1].autoIncrement;
   }

   public boolean isCaseSensitive(int var1) throws SQLException {
      this.checkColRange(var1);
      return this.colInfo[var1].caseSensitive;
   }

   public boolean isSearchable(int var1) throws SQLException {
      this.checkColRange(var1);
      return this.colInfo[var1].searchable;
   }

   public boolean isCurrency(int var1) throws SQLException {
      this.checkColRange(var1);
      return this.colInfo[var1].currency;
   }

   public int isNullable(int var1) throws SQLException {
      this.checkColRange(var1);
      return this.colInfo[var1].nullable;
   }

   public boolean isSigned(int var1) throws SQLException {
      this.checkColRange(var1);
      return this.colInfo[var1].signed;
   }

   public int getColumnDisplaySize(int var1) throws SQLException {
      this.checkColRange(var1);
      return this.colInfo[var1].columnDisplaySize;
   }

   public String getColumnLabel(int var1) throws SQLException {
      this.checkColRange(var1);
      return this.colInfo[var1].columnLabel;
   }

   public String getColumnName(int var1) throws SQLException {
      this.checkColRange(var1);
      return this.colInfo[var1].columnName;
   }

   public String getSchemaName(int var1) throws SQLException {
      this.checkColRange(var1);
      String var2 = "";
      if (this.colInfo[var1].schemaName != null) {
         var2 = this.colInfo[var1].schemaName;
      }

      return var2;
   }

   public int getPrecision(int var1) throws SQLException {
      this.checkColRange(var1);
      return this.colInfo[var1].colPrecision;
   }

   public int getScale(int var1) throws SQLException {
      this.checkColRange(var1);
      return this.colInfo[var1].colScale;
   }

   public String getTableName(int var1) throws SQLException {
      this.checkColRange(var1);
      return this.colInfo[var1].tableName;
   }

   public String getCatalogName(int var1) throws SQLException {
      this.checkColRange(var1);
      String var2 = "";
      if (this.colInfo[var1].catName != null) {
         var2 = this.colInfo[var1].catName;
      }

      return var2;
   }

   public int getColumnType(int var1) throws SQLException {
      this.checkColRange(var1);
      return this.colInfo[var1].colType;
   }

   public String getColumnTypeName(int var1) throws SQLException {
      this.checkColRange(var1);
      return this.colInfo[var1].colTypeName;
   }

   public boolean isReadOnly(int var1) throws SQLException {
      this.checkColRange(var1);
      return this.colInfo[var1].readOnly;
   }

   public boolean isWritable(int var1) throws SQLException {
      this.checkColRange(var1);
      return this.colInfo[var1].writable;
   }

   public boolean isDefinitelyWritable(int var1) throws SQLException {
      this.checkColRange(var1);
      return true;
   }

   public String getColumnClassName(int var1) throws SQLException {
      String var2 = String.class.getName();
      int var3 = this.getColumnType(var1);
      switch(var3) {
      case -7:
         var2 = Boolean.class.getName();
         break;
      case -6:
         var2 = Byte.class.getName();
         break;
      case -5:
         var2 = Long.class.getName();
         break;
      case -4:
      case -3:
      case -2:
         var2 = "byte[]";
         break;
      case 2:
      case 3:
         var2 = BigDecimal.class.getName();
         break;
      case 4:
         var2 = Integer.class.getName();
         break;
      case 5:
         var2 = Short.class.getName();
         break;
      case 6:
      case 8:
         var2 = Double.class.getName();
         break;
      case 7:
         var2 = Float.class.getName();
         break;
      case 91:
         var2 = Date.class.getName();
         break;
      case 92:
         var2 = Time.class.getName();
         break;
      case 93:
         var2 = Timestamp.class.getName();
         break;
      case 2004:
         var2 = Blob.class.getName();
         break;
      case 2005:
         var2 = Clob.class.getName();
      }

      return var2;
   }

   public <T> T unwrap(Class<T> var1) throws SQLException {
      if (this.isWrapperFor(var1)) {
         return var1.cast(this);
      } else {
         throw new SQLException("unwrap failed for:" + var1);
      }
   }

   public boolean isWrapperFor(Class<?> var1) throws SQLException {
      return var1.isInstance(this);
   }

   private class ColInfo implements Serializable {
      public boolean autoIncrement;
      public boolean caseSensitive;
      public boolean currency;
      public int nullable;
      public boolean signed;
      public boolean searchable;
      public int columnDisplaySize;
      public String columnLabel;
      public String columnName;
      public String schemaName;
      public int colPrecision;
      public int colScale;
      public String tableName;
      public String catName;
      public int colType;
      public String colTypeName;
      public boolean readOnly;
      public boolean writable;
      static final long serialVersionUID = 5490834817919311283L;

      private ColInfo() {
         this.tableName = "";
         this.readOnly = false;
         this.writable = true;
      }

      // $FF: synthetic method
      ColInfo(Object var2) {
         this();
      }
   }
}
