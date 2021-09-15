package com.sun.rowset.internal;

import com.sun.rowset.CachedRowSetImpl;
import com.sun.rowset.JdbcRowSetResourceBundle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import javax.sql.RowSetInternal;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.serial.SQLInputImpl;
import javax.sql.rowset.serial.SerialArray;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialStruct;
import javax.sql.rowset.spi.SyncProviderException;
import javax.sql.rowset.spi.TransactionalWriter;
import sun.reflect.misc.ReflectUtil;

public class CachedRowSetWriter implements TransactionalWriter, Serializable {
   private transient Connection con;
   private String selectCmd;
   private String updateCmd;
   private String updateWhere;
   private String deleteCmd;
   private String deleteWhere;
   private String insertCmd;
   private int[] keyCols;
   private Object[] params;
   private CachedRowSetReader reader;
   private ResultSetMetaData callerMd;
   private int callerColumnCount;
   private CachedRowSetImpl crsResolve;
   private ArrayList<Integer> status;
   private int iChangedValsInDbAndCRS;
   private int iChangedValsinDbOnly;
   private JdbcRowSetResourceBundle resBundle;
   static final long serialVersionUID = -8506030970299413976L;

   public CachedRowSetWriter() {
      try {
         this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   public boolean writeData(RowSetInternal var1) throws SQLException {
      long var2 = 0L;
      boolean var4 = false;
      PreparedStatement var5 = null;
      this.iChangedValsInDbAndCRS = 0;
      this.iChangedValsinDbOnly = 0;
      CachedRowSetImpl var6 = (CachedRowSetImpl)var1;
      this.crsResolve = new CachedRowSetImpl();
      this.con = this.reader.connect(var1);
      if (this.con == null) {
         throw new SQLException(this.resBundle.handleGetObject("crswriter.connect").toString());
      } else {
         this.initSQLStatements(var6);
         RowSetMetaDataImpl var8 = (RowSetMetaDataImpl)var6.getMetaData();
         RowSetMetaDataImpl var9 = new RowSetMetaDataImpl();
         int var7 = var8.getColumnCount();
         int var10 = var6.size() + 1;
         this.status = new ArrayList(var10);
         this.status.add(0, (Object)null);
         var9.setColumnCount(var7);

         int var11;
         for(var11 = 1; var11 <= var7; ++var11) {
            var9.setColumnType(var11, var8.getColumnType(var11));
            var9.setColumnName(var11, var8.getColumnName(var11));
            var9.setNullable(var11, 2);
         }

         this.crsResolve.setMetaData(var9);
         if (this.callerColumnCount < 1) {
            if (this.reader.getCloseConnection()) {
               this.con.close();
            }

            return true;
         } else {
            var4 = var6.getShowDeleted();
            var6.setShowDeleted(true);
            var6.beforeFirst();

            for(var11 = 1; var6.next(); ++var11) {
               if (var6.rowDeleted()) {
                  if (this.deleteOriginalRow(var6, this.crsResolve)) {
                     this.status.add(var11, 1);
                     ++var2;
                  } else {
                     this.status.add(var11, 3);
                  }
               } else if (var6.rowInserted()) {
                  var5 = this.con.prepareStatement(this.insertCmd);
                  if (this.insertNewRow(var6, var5, this.crsResolve)) {
                     this.status.add(var11, 2);
                     ++var2;
                  } else {
                     this.status.add(var11, 3);
                  }
               } else if (var6.rowUpdated()) {
                  if (this.updateOriginalRow(var6)) {
                     this.status.add(var11, 0);
                     ++var2;
                  } else {
                     this.status.add(var11, 3);
                  }
               } else {
                  int var12 = var6.getMetaData().getColumnCount();
                  this.status.add(var11, 3);
                  this.crsResolve.moveToInsertRow();

                  for(int var13 = 0; var13 < var7; ++var13) {
                     this.crsResolve.updateNull(var13 + 1);
                  }

                  this.crsResolve.insertRow();
                  this.crsResolve.moveToCurrentRow();
               }
            }

            if (var5 != null) {
               var5.close();
            }

            var6.setShowDeleted(var4);
            var6.beforeFirst();
            this.crsResolve.beforeFirst();
            if (var2 != 0L) {
               SyncProviderException var14 = new SyncProviderException(var2 + " " + this.resBundle.handleGetObject("crswriter.conflictsno").toString());
               SyncResolverImpl var15 = (SyncResolverImpl)var14.getSyncResolver();
               var15.setCachedRowSet(var6);
               var15.setCachedRowSetResolver(this.crsResolve);
               var15.setStatus(this.status);
               var15.setCachedRowSetWriter(this);
               throw var14;
            } else {
               return true;
            }
         }
      }
   }

   private boolean updateOriginalRow(CachedRowSet var1) throws SQLException {
      boolean var3 = false;
      int var4 = 0;
      ResultSet var5 = var1.getOriginalRow();
      var5.next();

      int var29;
      try {
         this.updateWhere = this.buildWhereClause(this.updateWhere, var5);
         String var6 = this.selectCmd.toLowerCase();
         int var7 = var6.indexOf("where");
         String var8;
         if (var7 != -1) {
            var8 = this.selectCmd.substring(0, var7);
            this.selectCmd = var8;
         }

         PreparedStatement var2 = this.con.prepareStatement(this.selectCmd + this.updateWhere, 1005, 1007);

         for(var29 = 0; var29 < this.keyCols.length; ++var29) {
            if (this.params[var29] != null) {
               ++var4;
               var2.setObject(var4, this.params[var29]);
            }
         }

         try {
            var2.setMaxRows(var1.getMaxRows());
            var2.setMaxFieldSize(var1.getMaxFieldSize());
            var2.setEscapeProcessing(var1.getEscapeProcessing());
            var2.setQueryTimeout(var1.getQueryTimeout());
         } catch (Exception var27) {
         }

         var8 = null;
         ResultSet var30 = var2.executeQuery();
         ResultSetMetaData var9 = var30.getMetaData();
         if (!var30.next()) {
            return true;
         } else if (var30.next()) {
            return true;
         } else {
            var30.first();
            int var10 = 0;
            Vector var11 = new Vector();
            String var12 = this.updateCmd;
            boolean var16 = true;
            Object var17 = null;
            boolean var18 = true;
            boolean var19 = true;
            this.crsResolve.moveToInsertRow();

            for(var29 = 1; var29 <= this.callerColumnCount; ++var29) {
               Object var13 = var5.getObject(var29);
               Object var14 = var1.getObject(var29);
               Object var15 = var30.getObject(var29);
               Map var20 = var1.getTypeMap() == null ? this.con.getTypeMap() : var1.getTypeMap();
               if (var15 instanceof Struct) {
                  Struct var21 = (Struct)var15;
                  Class var22 = null;
                  var22 = (Class)var20.get(var21.getSQLTypeName());
                  if (var22 != null) {
                     SQLData var23 = null;

                     try {
                        var23 = (SQLData)ReflectUtil.newInstance(var22);
                     } catch (Exception var26) {
                        throw new SQLException("Unable to Instantiate: ", var26);
                     }

                     Object[] var24 = var21.getAttributes(var20);
                     SQLInputImpl var25 = new SQLInputImpl(var24, var20);
                     var23.readSQL(var25, var21.getSQLTypeName());
                     var15 = var23;
                  }
               } else if (var15 instanceof SQLData) {
                  var15 = new SerialStruct((SQLData)var15, var20);
               } else if (var15 instanceof Blob) {
                  var15 = new SerialBlob((Blob)var15);
               } else if (var15 instanceof Clob) {
                  var15 = new SerialClob((Clob)var15);
               } else if (var15 instanceof Array) {
                  var15 = new SerialArray((Array)var15, var20);
               }

               var16 = true;
               if (var15 == null && var13 != null) {
                  ++this.iChangedValsinDbOnly;
                  var16 = false;
                  var17 = var15;
               } else if (var15 != null && !var15.equals(var13)) {
                  ++this.iChangedValsinDbOnly;
                  var16 = false;
                  var17 = var15;
               } else if (var13 != null && var14 != null) {
                  if (var13.equals(var14)) {
                     ++var10;
                  } else if (!var13.equals(var14) && var1.columnUpdated(var29)) {
                     if (var15.equals(var13)) {
                        if (!var19 || !var18) {
                           var12 = var12 + ", ";
                        }

                        var12 = var12 + var1.getMetaData().getColumnName(var29);
                        var11.add(var29);
                        var12 = var12 + " = ? ";
                        var19 = false;
                     } else {
                        var16 = false;
                        var17 = var15;
                        ++this.iChangedValsInDbAndCRS;
                     }
                  }
               } else {
                  if (!var18 || !var19) {
                     var12 = var12 + ", ";
                  }

                  var12 = var12 + var1.getMetaData().getColumnName(var29);
                  var11.add(var29);
                  var12 = var12 + " = ? ";
                  var18 = false;
               }

               if (!var16) {
                  this.crsResolve.updateObject(var29, var17);
               } else {
                  this.crsResolve.updateNull(var29);
               }
            }

            var30.close();
            var2.close();
            this.crsResolve.insertRow();
            this.crsResolve.moveToCurrentRow();
            if ((var18 || var11.size() != 0) && var10 != this.callerColumnCount) {
               if (this.iChangedValsInDbAndCRS == 0 && this.iChangedValsinDbOnly == 0) {
                  var12 = var12 + this.updateWhere;
                  var2 = this.con.prepareStatement(var12);

                  for(var29 = 0; var29 < var11.size(); ++var29) {
                     Object var31 = var1.getObject((Integer)var11.get(var29));
                     if (var31 != null) {
                        var2.setObject(var29 + 1, var31);
                     } else {
                        var2.setNull(var29 + 1, var1.getMetaData().getColumnType(var29 + 1));
                     }
                  }

                  var4 = var29;

                  for(var29 = 0; var29 < this.keyCols.length; ++var29) {
                     if (this.params[var29] != null) {
                        ++var4;
                        var2.setObject(var4, this.params[var29]);
                     }
                  }

                  var29 = var2.executeUpdate();
                  return false;
               } else {
                  return true;
               }
            } else {
               return false;
            }
         }
      } catch (SQLException var28) {
         var28.printStackTrace();
         this.crsResolve.moveToInsertRow();

         for(var29 = 1; var29 <= this.callerColumnCount; ++var29) {
            this.crsResolve.updateNull(var29);
         }

         this.crsResolve.insertRow();
         this.crsResolve.moveToCurrentRow();
         return true;
      }
   }

   private boolean insertNewRow(CachedRowSet var1, PreparedStatement var2, CachedRowSetImpl var3) throws SQLException {
      Object var4 = false;
      PreparedStatement var5 = this.con.prepareStatement(this.selectCmd, 1005, 1007);
      Throwable var6 = null;

      SQLException var87;
      try {
         ResultSet var7 = var5.executeQuery();
         Throwable var8 = null;

         try {
            ResultSet var9 = this.con.getMetaData().getPrimaryKeys((String)null, (String)null, var1.getTableName());
            Throwable var10 = null;

            try {
               ResultSetMetaData var11 = var1.getMetaData();
               int var12 = var11.getColumnCount();
               String[] var13 = new String[var12];

               for(int var14 = 0; var9.next(); ++var14) {
                  var13[var14] = var9.getString("COLUMN_NAME");
               }

               Object var16;
               if (var7.next()) {
                  String[] var15 = var13;
                  var16 = var13.length;

                  for(int var17 = 0; var17 < var16; ++var17) {
                     String var18 = var15[var17];
                     if (this.isPKNameValid(var18, var11)) {
                        Object var19 = var1.getObject(var18);
                        if (var19 == null) {
                           break;
                        }

                        String var20 = var7.getObject(var18).toString();
                        if (var19.toString().equals(var20)) {
                           var4 = true;
                           this.crsResolve.moveToInsertRow();

                           for(int var21 = 1; var21 <= var12; ++var21) {
                              String var22 = var7.getMetaData().getColumnName(var21);
                              if (var22.equals(var18)) {
                                 this.crsResolve.updateObject(var21, var20);
                              } else {
                                 this.crsResolve.updateNull(var21);
                              }
                           }

                           this.crsResolve.insertRow();
                           this.crsResolve.moveToCurrentRow();
                        }
                     }
                  }
               }

               if (var4 == false) {
                  try {
                     for(int var88 = 1; var88 <= var12; ++var88) {
                        var16 = var1.getObject(var88);
                        if (var16 != null) {
                           var2.setObject(var88, var16);
                        } else {
                           var2.setNull(var88, var1.getMetaData().getColumnType(var88));
                        }
                     }

                     var2.executeUpdate();
                     var87 = false;
                     return (boolean)var87;
                  } catch (SQLException var80) {
                     var87 = var80;
                     this.crsResolve.moveToInsertRow();

                     for(int var89 = 1; var89 <= var12; ++var89) {
                        this.crsResolve.updateNull(var89);
                     }

                     this.crsResolve.insertRow();
                     this.crsResolve.moveToCurrentRow();
                     var16 = true;
                     return (boolean)var16;
                  }
               }

               var87 = (SQLException)var4;
            } catch (Throwable var81) {
               var10 = var81;
               throw var81;
            } finally {
               if (var9 != null) {
                  if (var10 != null) {
                     try {
                        var9.close();
                     } catch (Throwable var79) {
                        var10.addSuppressed(var79);
                     }
                  } else {
                     var9.close();
                  }
               }

            }
         } catch (Throwable var83) {
            var8 = var83;
            throw var83;
         } finally {
            if (var7 != null) {
               if (var8 != null) {
                  try {
                     var7.close();
                  } catch (Throwable var78) {
                     var8.addSuppressed(var78);
                  }
               } else {
                  var7.close();
               }
            }

         }
      } catch (Throwable var85) {
         var6 = var85;
         throw var85;
      } finally {
         if (var5 != null) {
            if (var6 != null) {
               try {
                  var5.close();
               } catch (Throwable var77) {
                  var6.addSuppressed(var77);
               }
            } else {
               var5.close();
            }
         }

      }

      return (boolean)var87;
   }

   private boolean deleteOriginalRow(CachedRowSet var1, CachedRowSetImpl var2) throws SQLException {
      int var5 = 0;
      ResultSet var7 = var1.getOriginalRow();
      var7.next();
      this.deleteWhere = this.buildWhereClause(this.deleteWhere, var7);
      PreparedStatement var3 = this.con.prepareStatement(this.selectCmd + this.deleteWhere, 1005, 1007);

      int var4;
      for(var4 = 0; var4 < this.keyCols.length; ++var4) {
         if (this.params[var4] != null) {
            ++var5;
            var3.setObject(var5, this.params[var4]);
         }
      }

      try {
         var3.setMaxRows(var1.getMaxRows());
         var3.setMaxFieldSize(var1.getMaxFieldSize());
         var3.setEscapeProcessing(var1.getEscapeProcessing());
         var3.setQueryTimeout(var1.getQueryTimeout());
      } catch (Exception var12) {
      }

      ResultSet var8 = var3.executeQuery();
      if (!var8.next()) {
         return true;
      } else if (var8.next()) {
         return true;
      } else {
         var8.first();
         boolean var9 = false;
         var2.moveToInsertRow();

         for(var4 = 1; var4 <= var1.getMetaData().getColumnCount(); ++var4) {
            Object var10 = var7.getObject(var4);
            Object var11 = var8.getObject(var4);
            if (var10 != null && var11 != null) {
               if (!var10.toString().equals(var11.toString())) {
                  var9 = true;
                  var2.updateObject(var4, var7.getObject(var4));
               }
            } else {
               var2.updateNull(var4);
            }
         }

         var2.insertRow();
         var2.moveToCurrentRow();
         if (var9) {
            return true;
         } else {
            String var13 = this.deleteCmd + this.deleteWhere;
            var3 = this.con.prepareStatement(var13);
            var5 = 0;

            for(var4 = 0; var4 < this.keyCols.length; ++var4) {
               if (this.params[var4] != null) {
                  ++var5;
                  var3.setObject(var5, this.params[var4]);
               }
            }

            if (var3.executeUpdate() != 1) {
               return true;
            } else {
               var3.close();
               return false;
            }
         }
      }
   }

   public void setReader(CachedRowSetReader var1) throws SQLException {
      this.reader = var1;
   }

   public CachedRowSetReader getReader() throws SQLException {
      return this.reader;
   }

   private void initSQLStatements(CachedRowSet var1) throws SQLException {
      this.callerMd = var1.getMetaData();
      this.callerColumnCount = this.callerMd.getColumnCount();
      if (this.callerColumnCount >= 1) {
         String var3 = var1.getTableName();
         if (var3 == null) {
            var3 = this.callerMd.getTableName(1);
            if (var3 == null || var3.length() == 0) {
               throw new SQLException(this.resBundle.handleGetObject("crswriter.tname").toString());
            }
         }

         String var4 = this.callerMd.getCatalogName(1);
         String var5 = this.callerMd.getSchemaName(1);
         DatabaseMetaData var6 = this.con.getMetaData();
         this.selectCmd = "SELECT ";

         int var2;
         for(var2 = 1; var2 <= this.callerColumnCount; ++var2) {
            this.selectCmd = this.selectCmd + this.callerMd.getColumnName(var2);
            if (var2 < this.callerMd.getColumnCount()) {
               this.selectCmd = this.selectCmd + ", ";
            } else {
               this.selectCmd = this.selectCmd + " ";
            }
         }

         this.selectCmd = this.selectCmd + "FROM " + this.buildTableName(var6, var4, var5, var3);
         this.updateCmd = "UPDATE " + this.buildTableName(var6, var4, var5, var3);
         String var7 = this.updateCmd.toLowerCase();
         int var8 = var7.indexOf("where");
         if (var8 != -1) {
            this.updateCmd = this.updateCmd.substring(0, var8);
         }

         this.updateCmd = this.updateCmd + "SET ";
         this.insertCmd = "INSERT INTO " + this.buildTableName(var6, var4, var5, var3);
         this.insertCmd = this.insertCmd + "(";

         for(var2 = 1; var2 <= this.callerColumnCount; ++var2) {
            this.insertCmd = this.insertCmd + this.callerMd.getColumnName(var2);
            if (var2 < this.callerMd.getColumnCount()) {
               this.insertCmd = this.insertCmd + ", ";
            } else {
               this.insertCmd = this.insertCmd + ") VALUES (";
            }
         }

         for(var2 = 1; var2 <= this.callerColumnCount; ++var2) {
            this.insertCmd = this.insertCmd + "?";
            if (var2 < this.callerColumnCount) {
               this.insertCmd = this.insertCmd + ", ";
            } else {
               this.insertCmd = this.insertCmd + ")";
            }
         }

         this.deleteCmd = "DELETE FROM " + this.buildTableName(var6, var4, var5, var3);
         this.buildKeyDesc(var1);
      }
   }

   private String buildTableName(DatabaseMetaData var1, String var2, String var3, String var4) throws SQLException {
      String var5 = "";
      var2 = var2.trim();
      var3 = var3.trim();
      var4 = var4.trim();
      if (var1.isCatalogAtStart()) {
         if (var2 != null && var2.length() > 0) {
            var5 = var5 + var2 + var1.getCatalogSeparator();
         }

         if (var3 != null && var3.length() > 0) {
            var5 = var5 + var3 + ".";
         }

         var5 = var5 + var4;
      } else {
         if (var3 != null && var3.length() > 0) {
            var5 = var5 + var3 + ".";
         }

         var5 = var5 + var4;
         if (var2 != null && var2.length() > 0) {
            var5 = var5 + var1.getCatalogSeparator() + var2;
         }
      }

      var5 = var5 + " ";
      return var5;
   }

   private void buildKeyDesc(CachedRowSet var1) throws SQLException {
      this.keyCols = var1.getKeyColumns();
      ResultSetMetaData var2 = var1.getMetaData();
      if (this.keyCols == null || this.keyCols.length == 0) {
         ArrayList var3 = new ArrayList();

         int var4;
         for(var4 = 0; var4 < this.callerColumnCount; ++var4) {
            if (var2.getColumnType(var4 + 1) != 2005 && var2.getColumnType(var4 + 1) != 2002 && var2.getColumnType(var4 + 1) != 2009 && var2.getColumnType(var4 + 1) != 2004 && var2.getColumnType(var4 + 1) != 2003 && var2.getColumnType(var4 + 1) != 1111) {
               var3.add(var4 + 1);
            }
         }

         this.keyCols = new int[var3.size()];

         for(var4 = 0; var4 < var3.size(); ++var4) {
            this.keyCols[var4] = (Integer)var3.get(var4);
         }
      }

      this.params = new Object[this.keyCols.length];
   }

   private String buildWhereClause(String var1, ResultSet var2) throws SQLException {
      var1 = "WHERE ";

      for(int var3 = 0; var3 < this.keyCols.length; ++var3) {
         if (var3 > 0) {
            var1 = var1 + "AND ";
         }

         var1 = var1 + this.callerMd.getColumnName(this.keyCols[var3]);
         this.params[var3] = var2.getObject(this.keyCols[var3]);
         if (var2.wasNull()) {
            var1 = var1 + " IS NULL ";
         } else {
            var1 = var1 + " = ? ";
         }
      }

      return var1;
   }

   void updateResolvedConflictToDB(CachedRowSet var1, Connection var2) throws SQLException {
      String var4 = "WHERE ";
      String var5 = " ";
      String var6 = "UPDATE ";
      int var7 = var1.getMetaData().getColumnCount();
      int[] var8 = var1.getKeyColumns();
      String var10 = "";
      this.buildWhereClause(var4, var1);
      if (var8 == null || var8.length == 0) {
         var8 = new int[var7];

         for(int var11 = 0; var11 < var8.length; var8[var11++] = var11) {
         }
      }

      Object[] var9 = new Object[var8.length];
      var6 = "UPDATE " + this.buildTableName(var2.getMetaData(), var1.getMetaData().getCatalogName(1), var1.getMetaData().getSchemaName(1), var1.getTableName());
      var6 = var6 + "SET ";
      boolean var15 = true;

      int var12;
      for(var12 = 1; var12 <= var7; ++var12) {
         if (var1.columnUpdated(var12)) {
            if (!var15) {
               var10 = var10 + ", ";
            }

            var10 = var10 + var1.getMetaData().getColumnName(var12);
            var10 = var10 + " = ? ";
            var15 = false;
         }
      }

      var6 = var6 + var10;
      var4 = "WHERE ";

      for(var12 = 0; var12 < var8.length; ++var12) {
         if (var12 > 0) {
            var4 = var4 + "AND ";
         }

         var4 = var4 + var1.getMetaData().getColumnName(var8[var12]);
         var9[var12] = var1.getObject(var8[var12]);
         if (var1.wasNull()) {
            var4 = var4 + " IS NULL ";
         } else {
            var4 = var4 + " = ? ";
         }
      }

      var6 = var6 + var4;
      PreparedStatement var3 = var2.prepareStatement(var6);
      var12 = 0;

      int var13;
      for(var13 = 0; var13 < var7; ++var13) {
         if (var1.columnUpdated(var13 + 1)) {
            Object var14 = var1.getObject(var13 + 1);
            if (var14 != null) {
               ++var12;
               var3.setObject(var12, var14);
            } else {
               var3.setNull(var13 + 1, var1.getMetaData().getColumnType(var13 + 1));
            }
         }
      }

      for(var13 = 0; var13 < var8.length; ++var13) {
         if (var9[var13] != null) {
            ++var12;
            var3.setObject(var12, var9[var13]);
         }
      }

      var13 = var3.executeUpdate();
   }

   public void commit() throws SQLException {
      this.con.commit();
      if (this.reader.getCloseConnection()) {
         this.con.close();
      }

   }

   public void commit(CachedRowSetImpl var1, boolean var2) throws SQLException {
      this.con.commit();
      if (var2 && var1.getCommand() != null) {
         var1.execute(this.con);
      }

      if (this.reader.getCloseConnection()) {
         this.con.close();
      }

   }

   public void rollback() throws SQLException {
      this.con.rollback();
      if (this.reader.getCloseConnection()) {
         this.con.close();
      }

   }

   public void rollback(Savepoint var1) throws SQLException {
      this.con.rollback(var1);
      if (this.reader.getCloseConnection()) {
         this.con.close();
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();

      try {
         this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   private boolean isPKNameValid(String var1, ResultSetMetaData var2) throws SQLException {
      boolean var3 = false;
      int var4 = var2.getColumnCount();

      for(int var5 = 1; var5 <= var4; ++var5) {
         String var6 = var2.getColumnClassName(var5);
         if (var6.equalsIgnoreCase(var1)) {
            var3 = true;
            break;
         }
      }

      return var3;
   }
}
