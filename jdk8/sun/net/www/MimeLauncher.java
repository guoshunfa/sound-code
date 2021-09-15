package sun.net.www;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.StringTokenizer;
import sun.security.action.GetPropertyAction;

class MimeLauncher extends Thread {
   java.net.URLConnection uc;
   MimeEntry m;
   String genericTempFileTemplate;
   InputStream is;
   String execPath;

   MimeLauncher(MimeEntry var1, java.net.URLConnection var2, InputStream var3, String var4, String var5) throws ApplicationLaunchException {
      super(var5);
      this.m = var1;
      this.uc = var2;
      this.is = var3;
      this.genericTempFileTemplate = var4;
      String var6 = this.m.getLaunchString();
      if (!this.findExecutablePath(var6)) {
         int var8 = var6.indexOf(32);
         String var7;
         if (var8 != -1) {
            var7 = var6.substring(0, var8);
         } else {
            var7 = var6;
         }

         throw new ApplicationLaunchException(var7);
      }
   }

   protected String getTempFileName(URL var1, String var2) {
      int var4 = var2.lastIndexOf("%s");
      String var5 = var2.substring(0, var4);
      String var6 = "";
      if (var4 < var2.length() - 2) {
         var6 = var2.substring(var4 + 2);
      }

      long var7 = System.currentTimeMillis() / 1000L;

      int var13;
      for(boolean var9 = false; (var13 = var5.indexOf("%s")) >= 0; var5 = var5.substring(0, var13) + var7 + var5.substring(var13 + 2)) {
      }

      String var10 = var1.getFile();
      String var11 = "";
      int var12 = var10.lastIndexOf(46);
      if (var12 >= 0 && var12 > var10.lastIndexOf(47)) {
         var11 = var10.substring(var12);
      }

      var10 = "HJ" + var1.hashCode();
      String var3 = var5 + var10 + var7 + var11 + var6;
      return var3;
   }

   public void run() {
      try {
         String var1 = this.m.getTempFileTemplate();
         if (var1 == null) {
            var1 = this.genericTempFileTemplate;
         }

         var1 = this.getTempFileName(this.uc.getURL(), var1);

         boolean var4;
         try {
            FileOutputStream var2 = new FileOutputStream(var1);
            byte[] var3 = new byte[2048];
            var4 = false;

            try {
               int var18;
               try {
                  while((var18 = this.is.read(var3)) >= 0) {
                     var2.write(var3, 0, var18);
                  }
               } catch (IOException var11) {
               }
            } finally {
               var2.close();
               this.is.close();
            }
         } catch (IOException var13) {
         }

         boolean var15 = false;

         int var16;
         String var17;
         for(var17 = this.execPath; (var16 = var17.indexOf("%t")) >= 0; var17 = var17.substring(0, var16) + this.uc.getContentType() + var17.substring(var16 + 2)) {
         }

         for(var4 = false; (var16 = var17.indexOf("%s")) >= 0; var4 = true) {
            var17 = var17.substring(0, var16) + var1 + var17.substring(var16 + 2);
         }

         if (!var4) {
            var17 = var17 + " <" + var1;
         }

         Runtime.getRuntime().exec(var17);
      } catch (IOException var14) {
      }

   }

   private boolean findExecutablePath(String var1) {
      if (var1 != null && var1.length() != 0) {
         int var3 = var1.indexOf(32);
         String var2;
         if (var3 != -1) {
            var2 = var1.substring(0, var3);
         } else {
            var2 = var1;
         }

         File var4 = new File(var2);
         if (var4.isFile()) {
            this.execPath = var1;
            return true;
         } else {
            String var5 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("exec.path")));
            if (var5 == null) {
               return false;
            } else {
               StringTokenizer var6 = new StringTokenizer(var5, "|");

               String var7;
               do {
                  if (!var6.hasMoreElements()) {
                     return false;
                  }

                  var7 = (String)var6.nextElement();
                  String var8 = var7 + File.separator + var2;
                  var4 = new File(var8);
               } while(!var4.isFile());

               this.execPath = var7 + File.separator + var1;
               return true;
            }
         }
      } else {
         return false;
      }
   }
}
