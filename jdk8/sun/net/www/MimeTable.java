package sun.net.www;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

public class MimeTable implements FileNameMap {
   private Hashtable<String, MimeEntry> entries = new Hashtable();
   private Hashtable<String, MimeEntry> extensionMap = new Hashtable();
   private static String tempFileTemplate;
   private static final String filePreamble = "sun.net.www MIME content-types table";
   private static final String fileMagic = "#sun.net.www MIME content-types table";
   protected static String[] mailcapLocations;

   MimeTable() {
      this.load();
   }

   public static MimeTable getDefaultTable() {
      return MimeTable.DefaultInstanceHolder.defaultInstance;
   }

   public static FileNameMap loadTable() {
      MimeTable var0 = getDefaultTable();
      return var0;
   }

   public synchronized int getSize() {
      return this.entries.size();
   }

   public synchronized String getContentTypeFor(String var1) {
      MimeEntry var2 = this.findByFileName(var1);
      return var2 != null ? var2.getType() : null;
   }

   public synchronized void add(MimeEntry var1) {
      this.entries.put(var1.getType(), var1);
      String[] var2 = var1.getExtensions();
      if (var2 != null) {
         for(int var3 = 0; var3 < var2.length; ++var3) {
            this.extensionMap.put(var2[var3], var1);
         }

      }
   }

   public synchronized MimeEntry remove(String var1) {
      MimeEntry var2 = (MimeEntry)this.entries.get(var1);
      return this.remove(var2);
   }

   public synchronized MimeEntry remove(MimeEntry var1) {
      String[] var2 = var1.getExtensions();
      if (var2 != null) {
         for(int var3 = 0; var3 < var2.length; ++var3) {
            this.extensionMap.remove(var2[var3]);
         }
      }

      return (MimeEntry)this.entries.remove(var1.getType());
   }

   public synchronized MimeEntry find(String var1) {
      MimeEntry var2 = (MimeEntry)this.entries.get(var1);
      if (var2 == null) {
         Enumeration var3 = this.entries.elements();

         while(var3.hasMoreElements()) {
            MimeEntry var4 = (MimeEntry)var3.nextElement();
            if (var4.matches(var1)) {
               return var4;
            }
         }
      }

      return var2;
   }

   public MimeEntry findByFileName(String var1) {
      String var2 = "";
      int var3 = var1.lastIndexOf(35);
      if (var3 > 0) {
         var1 = var1.substring(0, var3 - 1);
      }

      var3 = var1.lastIndexOf(46);
      var3 = Math.max(var3, var1.lastIndexOf(47));
      var3 = Math.max(var3, var1.lastIndexOf(63));
      if (var3 != -1 && var1.charAt(var3) == '.') {
         var2 = var1.substring(var3).toLowerCase();
      }

      return this.findByExt(var2);
   }

   public synchronized MimeEntry findByExt(String var1) {
      return (MimeEntry)this.extensionMap.get(var1);
   }

   public synchronized MimeEntry findByDescription(String var1) {
      Enumeration var2 = this.elements();

      MimeEntry var3;
      do {
         if (!var2.hasMoreElements()) {
            return this.find(var1);
         }

         var3 = (MimeEntry)var2.nextElement();
      } while(!var1.equals(var3.getDescription()));

      return var3;
   }

   String getTempFileTemplate() {
      return tempFileTemplate;
   }

   public synchronized Enumeration<MimeEntry> elements() {
      return this.entries.elements();
   }

   public synchronized void load() {
      Properties var1 = new Properties();
      File var2 = null;

      try {
         String var4 = System.getProperty("content.types.user.table");
         if (var4 != null) {
            var2 = new File(var4);
            if (!var2.exists()) {
               var2 = new File(System.getProperty("java.home") + File.separator + "lib" + File.separator + "content-types.properties");
            }
         } else {
            var2 = new File(System.getProperty("java.home") + File.separator + "lib" + File.separator + "content-types.properties");
         }

         BufferedInputStream var3 = new BufferedInputStream(new FileInputStream(var2));
         var1.load((InputStream)var3);
         var3.close();
      } catch (IOException var5) {
         System.err.println("Warning: default mime table not found: " + var2.getPath());
         return;
      }

      this.parse(var1);
   }

   void parse(Properties var1) {
      String var2 = (String)var1.get("temp.file.template");
      if (var2 != null) {
         var1.remove("temp.file.template");
         tempFileTemplate = var2;
      }

      Enumeration var3 = var1.propertyNames();

      while(var3.hasMoreElements()) {
         String var4 = (String)var3.nextElement();
         String var5 = var1.getProperty(var4);
         this.parse(var4, var5);
      }

   }

   void parse(String var1, String var2) {
      MimeEntry var3 = new MimeEntry(var1);
      StringTokenizer var4 = new StringTokenizer(var2, ";");

      while(var4.hasMoreTokens()) {
         String var5 = var4.nextToken();
         this.parse(var5, var3);
      }

      this.add(var3);
   }

   void parse(String var1, MimeEntry var2) {
      String var3 = null;
      String var4 = null;
      boolean var5 = false;
      StringTokenizer var6 = new StringTokenizer(var1, "=");

      while(var6.hasMoreTokens()) {
         if (var5) {
            var4 = var6.nextToken().trim();
         } else {
            var3 = var6.nextToken().trim();
            var5 = true;
         }
      }

      this.fill(var2, var3, var4);
   }

   void fill(MimeEntry var1, String var2, String var3) {
      if ("description".equalsIgnoreCase(var2)) {
         var1.setDescription(var3);
      } else if ("action".equalsIgnoreCase(var2)) {
         var1.setAction(this.getActionCode(var3));
      } else if ("application".equalsIgnoreCase(var2)) {
         var1.setCommand(var3);
      } else if ("icon".equalsIgnoreCase(var2)) {
         var1.setImageFileName(var3);
      } else if ("file_extensions".equalsIgnoreCase(var2)) {
         var1.setExtensions(var3);
      }

   }

   String[] getExtensions(String var1) {
      StringTokenizer var2 = new StringTokenizer(var1, ",");
      int var3 = var2.countTokens();
      String[] var4 = new String[var3];

      for(int var5 = 0; var5 < var3; ++var5) {
         var4[var5] = var2.nextToken();
      }

      return var4;
   }

   int getActionCode(String var1) {
      for(int var2 = 0; var2 < MimeEntry.actionKeywords.length; ++var2) {
         if (var1.equalsIgnoreCase(MimeEntry.actionKeywords[var2])) {
            return var2;
         }
      }

      return 0;
   }

   public synchronized boolean save(String var1) {
      if (var1 == null) {
         var1 = System.getProperty("user.home" + File.separator + "lib" + File.separator + "content-types.properties");
      }

      return this.saveAsProperties(new File(var1));
   }

   public Properties getAsProperties() {
      Properties var1 = new Properties();
      Enumeration var2 = this.elements();

      while(var2.hasMoreElements()) {
         MimeEntry var3 = (MimeEntry)var2.nextElement();
         var1.put(var3.getType(), var3.toProperty());
      }

      return var1;
   }

   protected boolean saveAsProperties(File var1) {
      FileOutputStream var2 = null;

      boolean var4;
      try {
         var2 = new FileOutputStream(var1);
         Properties var3 = this.getAsProperties();
         var3.put("temp.file.template", tempFileTemplate);
         String var5 = System.getProperty("user.name");
         if (var5 != null) {
            String var16 = "; customized for " + var5;
            var3.store((OutputStream)var2, "sun.net.www MIME content-types table" + var16);
         } else {
            var3.store((OutputStream)var2, "sun.net.www MIME content-types table");
         }

         return true;
      } catch (IOException var14) {
         var14.printStackTrace();
         var4 = false;
      } finally {
         if (var2 != null) {
            try {
               var2.close();
            } catch (IOException var13) {
            }
         }

      }

      return var4;
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            MimeTable.tempFileTemplate = System.getProperty("content.types.temp.file.template", "/tmp/%s");
            MimeTable.mailcapLocations = new String[]{System.getProperty("user.mailcap"), System.getProperty("user.home") + "/.mailcap", "/etc/mailcap", "/usr/etc/mailcap", "/usr/local/etc/mailcap", System.getProperty("hotjava.home", "/usr/local/hotjava") + "/lib/mailcap"};
            return null;
         }
      });
   }

   private static class DefaultInstanceHolder {
      static final MimeTable defaultInstance = getDefaultInstance();

      static MimeTable getDefaultInstance() {
         return (MimeTable)AccessController.doPrivileged(new PrivilegedAction<MimeTable>() {
            public MimeTable run() {
               MimeTable var1 = new MimeTable();
               URLConnection.setFileNameMap(var1);
               return var1;
            }
         });
      }
   }
}
