package com.sun.rowset.internal;

import com.sun.rowset.JdbcRowSetResourceBundle;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.RowSet;
import javax.sql.RowSetInternal;
import javax.sql.RowSetReader;
import javax.sql.rowset.CachedRowSet;

public class CachedRowSetReader implements RowSetReader, Serializable {
   private int writerCalls = 0;
   private boolean userCon = false;
   private int startPosition;
   private JdbcRowSetResourceBundle resBundle;
   static final long serialVersionUID = 5049738185801363801L;

   public CachedRowSetReader() {
      try {
         this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   public void readData(RowSetInternal var1) throws SQLException {
      Connection var2 = null;

      try {
         CachedRowSet var3 = (CachedRowSet)var1;
         if (var3.getPageSize() == 0 && var3.size() > 0) {
            var3.close();
         }

         this.writerCalls = 0;
         this.userCon = false;
         var2 = this.connect(var1);
         if (var2 == null || var3.getCommand() == null) {
            throw new SQLException(this.resBundle.handleGetObject("crsreader.connecterr").toString());
         }

         try {
            var2.setTransactionIsolation(var3.getTransactionIsolation());
         } catch (Exception var24) {
         }

         PreparedStatement var4 = var2.prepareStatement(var3.getCommand());
         this.decodeParams(var1.getParams(), var4);

         try {
            var4.setMaxRows(var3.getMaxRows());
            var4.setMaxFieldSize(var3.getMaxFieldSize());
            var4.setEscapeProcessing(var3.getEscapeProcessing());
            var4.setQueryTimeout(var3.getQueryTimeout());
         } catch (Exception var23) {
            throw new SQLException(var23.getMessage());
         }

         if (var3.getCommand().toLowerCase().indexOf("select") != -1) {
            ResultSet var5 = var4.executeQuery();
            if (var3.getPageSize() == 0) {
               var3.populate(var5);
            } else {
               var4 = var2.prepareStatement(var3.getCommand(), 1004, 1008);
               this.decodeParams(var1.getParams(), var4);

               try {
                  var4.setMaxRows(var3.getMaxRows());
                  var4.setMaxFieldSize(var3.getMaxFieldSize());
                  var4.setEscapeProcessing(var3.getEscapeProcessing());
                  var4.setQueryTimeout(var3.getQueryTimeout());
               } catch (Exception var22) {
                  throw new SQLException(var22.getMessage());
               }

               var5 = var4.executeQuery();
               var3.populate(var5, this.startPosition);
            }

            var5.close();
         } else {
            var4.executeUpdate();
         }

         var4.close();

         try {
            var2.commit();
         } catch (SQLException var21) {
         }

         if (this.getCloseConnection()) {
            var2.close();
         }
      } catch (SQLException var25) {
         throw var25;
      } finally {
         try {
            if (var2 != null && this.getCloseConnection()) {
               try {
                  if (!var2.getAutoCommit()) {
                     var2.rollback();
                  }
               } catch (Exception var19) {
               }

               var2.close();
               var2 = null;
            }
         } catch (SQLException var20) {
         }

      }

   }

   public boolean reset() throws SQLException {
      ++this.writerCalls;
      return this.writerCalls == 1;
   }

   public Connection connect(RowSetInternal var1) throws SQLException {
      if (var1.getConnection() != null) {
         this.userCon = true;
         return var1.getConnection();
      } else if (((RowSet)var1).getDataSourceName() != null) {
         try {
            InitialContext var2 = new InitialContext();
            DataSource var5 = (DataSource)var2.lookup(((RowSet)var1).getDataSourceName());
            return ((RowSet)var1).getUsername() != null ? var5.getConnection(((RowSet)var1).getUsername(), ((RowSet)var1).getPassword()) : var5.getConnection();
         } catch (NamingException var4) {
            SQLException var3 = new SQLException(this.resBundle.handleGetObject("crsreader.connect").toString());
            var3.initCause(var4);
            throw var3;
         }
      } else {
         return ((RowSet)var1).getUrl() != null ? DriverManager.getConnection(((RowSet)var1).getUrl(), ((RowSet)var1).getUsername(), ((RowSet)var1).getPassword()) : null;
      }
   }

   private void decodeParams(Object[] var1, PreparedStatement var2) throws SQLException {
      Object[] var4 = null;

      for(int var5 = 0; var5 < var1.length; ++var5) {
         if (var1[var5] instanceof Object[]) {
            var4 = (Object[])((Object[])var1[var5]);
            if (var4.length == 2) {
               if (var4[0] == null) {
                  var2.setNull(var5 + 1, (Integer)var4[1]);
               } else if (!(var4[0] instanceof Date) && !(var4[0] instanceof Time) && !(var4[0] instanceof Timestamp)) {
                  if (var4[0] instanceof Reader) {
                     var2.setCharacterStream(var5 + 1, (Reader)var4[0], (Integer)var4[1]);
                  } else if (var4[1] instanceof Integer) {
                     var2.setObject(var5 + 1, var4[0], (Integer)var4[1]);
                  }
               } else {
                  System.err.println(this.resBundle.handleGetObject("crsreader.datedetected").toString());
                  if (!(var4[1] instanceof Calendar)) {
                     throw new SQLException(this.resBundle.handleGetObject("crsreader.paramtype").toString());
                  }

                  System.err.println(this.resBundle.handleGetObject("crsreader.caldetected").toString());
                  var2.setDate(var5 + 1, (Date)var4[0], (Calendar)var4[1]);
               }
            } else if (var4.length == 3) {
               if (var4[0] == null) {
                  var2.setNull(var5 + 1, (Integer)var4[1], (String)var4[2]);
               } else {
                  if (var4[0] instanceof InputStream) {
                     switch((Integer)var4[2]) {
                     case 0:
                        var2.setUnicodeStream(var5 + 1, (InputStream)var4[0], (Integer)var4[1]);
                        break;
                     case 1:
                        var2.setBinaryStream(var5 + 1, (InputStream)var4[0], (Integer)var4[1]);
                        break;
                     case 2:
                        var2.setAsciiStream(var5 + 1, (InputStream)var4[0], (Integer)var4[1]);
                        break;
                     default:
                        throw new SQLException(this.resBundle.handleGetObject("crsreader.paramtype").toString());
                     }
                  }

                  if (!(var4[1] instanceof Integer) || !(var4[2] instanceof Integer)) {
                     throw new SQLException(this.resBundle.handleGetObject("crsreader.paramtype").toString());
                  }

                  var2.setObject(var5 + 1, var4[0], (Integer)var4[1], (Integer)var4[2]);
               }
            } else {
               var2.setObject(var5 + 1, var1[var5]);
            }
         } else {
            var2.setObject(var5 + 1, var1[var5]);
         }
      }

   }

   protected boolean getCloseConnection() {
      return !this.userCon;
   }

   public void setStartPosition(int var1) {
      this.startPosition = var1;
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
