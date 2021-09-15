package javax.rmi;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

class GetORBPropertiesFileAction implements PrivilegedAction {
   private boolean debug = false;

   public GetORBPropertiesFileAction() {
   }

   private String getSystemProperty(final String var1) {
      String var2 = (String)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return System.getProperty(var1);
         }
      });
      return var2;
   }

   private void getPropertiesFromFile(Properties var1, String var2) {
      try {
         File var3 = new File(var2);
         if (!var3.exists()) {
            return;
         }

         FileInputStream var4 = new FileInputStream(var3);

         try {
            var1.load((InputStream)var4);
         } finally {
            var4.close();
         }
      } catch (Exception var9) {
         if (this.debug) {
            System.out.println("ORB properties file " + var2 + " not found: " + var9);
         }
      }

   }

   public Object run() {
      Properties var1 = new Properties();
      String var2 = this.getSystemProperty("java.home");
      String var3 = var2 + File.separator + "lib" + File.separator + "orb.properties";
      this.getPropertiesFromFile(var1, var3);
      Properties var4 = new Properties(var1);
      String var5 = this.getSystemProperty("user.home");
      var3 = var5 + File.separator + "orb.properties";
      this.getPropertiesFromFile(var4, var3);
      return var4;
   }
}
