package sun.net.www;

import java.io.File;
import java.io.InputStream;
import java.util.StringTokenizer;

public class MimeEntry implements Cloneable {
   private String typeName;
   private String tempFileNameTemplate;
   private int action;
   private String command;
   private String description;
   private String imageFileName;
   private String[] fileExtensions;
   boolean starred;
   public static final int UNKNOWN = 0;
   public static final int LOAD_INTO_BROWSER = 1;
   public static final int SAVE_TO_FILE = 2;
   public static final int LAUNCH_APPLICATION = 3;
   static final String[] actionKeywords = new String[]{"unknown", "browser", "save", "application"};

   public MimeEntry(String var1) {
      this(var1, 0, (String)null, (String)null, (String[])null);
   }

   MimeEntry(String var1, String var2, String var3) {
      this.typeName = var1.toLowerCase();
      this.action = 0;
      this.command = null;
      this.imageFileName = var2;
      this.setExtensions(var3);
      this.starred = this.isStarred(this.typeName);
   }

   MimeEntry(String var1, int var2, String var3, String var4) {
      this.typeName = var1.toLowerCase();
      this.action = var2;
      this.command = var3;
      this.imageFileName = null;
      this.fileExtensions = null;
      this.tempFileNameTemplate = var4;
   }

   MimeEntry(String var1, int var2, String var3, String var4, String[] var5) {
      this.typeName = var1.toLowerCase();
      this.action = var2;
      this.command = var3;
      this.imageFileName = var4;
      this.fileExtensions = var5;
      this.starred = this.isStarred(var1);
   }

   public synchronized String getType() {
      return this.typeName;
   }

   public synchronized void setType(String var1) {
      this.typeName = var1.toLowerCase();
   }

   public synchronized int getAction() {
      return this.action;
   }

   public synchronized void setAction(int var1, String var2) {
      this.action = var1;
      this.command = var2;
   }

   public synchronized void setAction(int var1) {
      this.action = var1;
   }

   public synchronized String getLaunchString() {
      return this.command;
   }

   public synchronized void setCommand(String var1) {
      this.command = var1;
   }

   public synchronized String getDescription() {
      return this.description != null ? this.description : this.typeName;
   }

   public synchronized void setDescription(String var1) {
      this.description = var1;
   }

   public String getImageFileName() {
      return this.imageFileName;
   }

   public synchronized void setImageFileName(String var1) {
      File var2 = new File(var1);
      if (var2.getParent() == null) {
         this.imageFileName = System.getProperty("java.net.ftp.imagepath." + var1);
      } else {
         this.imageFileName = var1;
      }

      if (var1.lastIndexOf(46) < 0) {
         this.imageFileName = this.imageFileName + ".gif";
      }

   }

   public String getTempFileTemplate() {
      return this.tempFileNameTemplate;
   }

   public synchronized String[] getExtensions() {
      return this.fileExtensions;
   }

   public synchronized String getExtensionsAsList() {
      String var1 = "";
      if (this.fileExtensions != null) {
         for(int var2 = 0; var2 < this.fileExtensions.length; ++var2) {
            var1 = var1 + this.fileExtensions[var2];
            if (var2 < this.fileExtensions.length - 1) {
               var1 = var1 + ",";
            }
         }
      }

      return var1;
   }

   public synchronized void setExtensions(String var1) {
      StringTokenizer var2 = new StringTokenizer(var1, ",");
      int var3 = var2.countTokens();
      String[] var4 = new String[var3];

      for(int var5 = 0; var5 < var3; ++var5) {
         String var6 = (String)var2.nextElement();
         var4[var5] = var6.trim();
      }

      this.fileExtensions = var4;
   }

   private boolean isStarred(String var1) {
      return var1 != null && var1.length() > 0 && var1.endsWith("/*");
   }

   public Object launch(java.net.URLConnection var1, InputStream var2, MimeTable var3) throws ApplicationLaunchException {
      switch(this.action) {
      case 0:
         return null;
      case 1:
         try {
            return var1.getContent();
         } catch (Exception var7) {
            return null;
         }
      case 2:
         try {
            return var2;
         } catch (Exception var6) {
            return "Load to file failed:\n" + var6;
         }
      case 3:
         String var4 = this.command;
         int var5 = var4.indexOf(32);
         if (var5 > 0) {
            var4 = var4.substring(0, var5);
         }

         return new MimeLauncher(this, var1, var2, var3.getTempFileTemplate(), var4);
      default:
         return null;
      }
   }

   public boolean matches(String var1) {
      return this.starred ? var1.startsWith(this.typeName) : var1.equals(this.typeName);
   }

   public Object clone() {
      MimeEntry var1 = new MimeEntry(this.typeName);
      var1.action = this.action;
      var1.command = this.command;
      var1.description = this.description;
      var1.imageFileName = this.imageFileName;
      var1.tempFileNameTemplate = this.tempFileNameTemplate;
      var1.fileExtensions = this.fileExtensions;
      return var1;
   }

   public synchronized String toProperty() {
      StringBuffer var1 = new StringBuffer();
      String var2 = "; ";
      boolean var3 = false;
      int var4 = this.getAction();
      if (var4 != 0) {
         var1.append("action=" + actionKeywords[var4]);
         var3 = true;
      }

      String var5 = this.getLaunchString();
      if (var5 != null && var5.length() > 0) {
         if (var3) {
            var1.append(var2);
         }

         var1.append("application=" + var5);
         var3 = true;
      }

      if (this.getImageFileName() != null) {
         if (var3) {
            var1.append(var2);
         }

         var1.append("icon=" + this.getImageFileName());
         var3 = true;
      }

      String var6 = this.getExtensionsAsList();
      if (var6.length() > 0) {
         if (var3) {
            var1.append(var2);
         }

         var1.append("file_extensions=" + var6);
         var3 = true;
      }

      String var7 = this.getDescription();
      if (var7 != null && !var7.equals(this.getType())) {
         if (var3) {
            var1.append(var2);
         }

         var1.append("description=" + var7);
      }

      return var1.toString();
   }

   public String toString() {
      return "MimeEntry[contentType=" + this.typeName + ", image=" + this.imageFileName + ", action=" + this.action + ", command=" + this.command + ", extensions=" + this.getExtensionsAsList() + "]";
   }
}
