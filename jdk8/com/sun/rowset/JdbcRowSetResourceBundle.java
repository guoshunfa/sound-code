package com.sun.rowset;

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class JdbcRowSetResourceBundle implements Serializable {
   private static String fileName;
   private transient PropertyResourceBundle propResBundle;
   private static volatile JdbcRowSetResourceBundle jpResBundle;
   private static final String PROPERTIES = "properties";
   private static final String UNDERSCORE = "_";
   private static final String DOT = ".";
   private static final String SLASH = "/";
   private static final String PATH = "com/sun/rowset/RowSetResourceBundle";
   static final long serialVersionUID = 436199386225359954L;

   private JdbcRowSetResourceBundle() throws IOException {
      Locale var1 = Locale.getDefault();
      this.propResBundle = (PropertyResourceBundle)ResourceBundle.getBundle("com/sun/rowset/RowSetResourceBundle", var1, Thread.currentThread().getContextClassLoader());
   }

   public static JdbcRowSetResourceBundle getJdbcRowSetResourceBundle() throws IOException {
      if (jpResBundle == null) {
         Class var0 = JdbcRowSetResourceBundle.class;
         synchronized(JdbcRowSetResourceBundle.class) {
            if (jpResBundle == null) {
               jpResBundle = new JdbcRowSetResourceBundle();
            }
         }
      }

      return jpResBundle;
   }

   public Enumeration getKeys() {
      return this.propResBundle.getKeys();
   }

   public Object handleGetObject(String var1) {
      return this.propResBundle.handleGetObject(var1);
   }
}
