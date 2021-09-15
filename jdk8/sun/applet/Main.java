package sun.applet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import sun.net.www.ParseUtil;

public class Main {
   static File theUserPropertiesFile;
   static final String[][] avDefaultUserProps = new String[][]{{"http.proxyHost", ""}, {"http.proxyPort", "80"}, {"package.restrict.access.sun", "true"}};
   private static AppletMessageHandler amh;
   private boolean debugFlag = false;
   private boolean helpFlag = false;
   private String encoding = null;
   private boolean noSecurityFlag = false;
   private static boolean cmdLineTestFlag;
   private static Vector urlList;
   public static final String theVersion;

   public static void main(String[] var0) {
      Main var1 = new Main();
      int var2 = var1.run(var0);
      if (var2 != 0 || cmdLineTestFlag) {
         System.exit(var2);
      }

   }

   private int run(String[] var1) {
      int var2;
      try {
         if (var1.length == 0) {
            usage();
            return 0;
         }

         int var3;
         for(var2 = 0; var2 < var1.length; var2 += var3) {
            var3 = this.decodeArg(var1, var2);
            if (var3 == 0) {
               throw new Main.ParseException(lookup("main.err.unrecognizedarg", var1[var2]));
            }
         }
      } catch (Main.ParseException var5) {
         System.err.println(var5.getMessage());
         return 1;
      }

      if (this.helpFlag) {
         usage();
         return 0;
      } else if (urlList.size() == 0) {
         System.err.println(lookup("main.err.inputfile"));
         return 1;
      } else if (this.debugFlag) {
         return this.invokeDebugger(var1);
      } else {
         if (!this.noSecurityFlag && System.getSecurityManager() == null) {
            this.init();
         }

         for(var2 = 0; var2 < urlList.size(); ++var2) {
            try {
               AppletViewer.parse((URL)urlList.elementAt(var2), this.encoding);
            } catch (IOException var4) {
               System.err.println(lookup("main.err.io", var4.getMessage()));
               return 1;
            }
         }

         return 0;
      }
   }

   private static void usage() {
      System.out.println(lookup("usage"));
   }

   private int decodeArg(String[] var1, int var2) throws Main.ParseException {
      String var3 = var1[var2];
      int var4 = var1.length;
      if (!"-help".equalsIgnoreCase(var3) && !"-?".equals(var3)) {
         if ("-encoding".equals(var3) && var2 < var4 - 1) {
            if (this.encoding != null) {
               throw new Main.ParseException(lookup("main.err.dupoption", var3));
            } else {
               ++var2;
               this.encoding = var1[var2];
               return 2;
            }
         } else if ("-debug".equals(var3)) {
            this.debugFlag = true;
            return 1;
         } else if ("-Xnosecurity".equals(var3)) {
            System.err.println();
            System.err.println(lookup("main.warn.nosecmgr"));
            System.err.println();
            this.noSecurityFlag = true;
            return 1;
         } else if ("-XcmdLineTest".equals(var3)) {
            cmdLineTestFlag = true;
            return 1;
         } else if (var3.startsWith("-")) {
            throw new Main.ParseException(lookup("main.err.unsupportedopt", var3));
         } else {
            URL var5 = this.parseURL(var3);
            if (var5 != null) {
               urlList.addElement(var5);
               return 1;
            } else {
               return 0;
            }
         }
      } else {
         this.helpFlag = true;
         return 1;
      }
   }

   private URL parseURL(String var1) throws Main.ParseException {
      URL var2 = null;
      String var3 = "file:";

      try {
         if (var1.indexOf(58) <= 1) {
            var2 = ParseUtil.fileToEncodedURL(new File(var1));
         } else if (var1.startsWith(var3) && var1.length() != var3.length() && !(new File(var1.substring(var3.length()))).isAbsolute()) {
            String var4 = ParseUtil.fileToEncodedURL(new File(System.getProperty("user.dir"))).getPath() + var1.substring(var3.length());
            var2 = new URL("file", "", var4);
         } else {
            var2 = new URL(var1);
         }

         return var2;
      } catch (MalformedURLException var5) {
         throw new Main.ParseException(lookup("main.err.badurl", var1, var5.getMessage()));
      }
   }

   private int invokeDebugger(String[] var1) {
      String[] var2 = new String[var1.length + 1];
      byte var3 = 0;
      String var4 = System.getProperty("java.home") + File.separator + "phony";
      int var11 = var3 + 1;
      var2[var3] = "-Djava.class.path=" + var4;
      var2[var11++] = "sun.applet.Main";

      for(int var5 = 0; var5 < var1.length; ++var5) {
         if (!"-debug".equals(var1[var5])) {
            var2[var11++] = var1[var5];
         }
      }

      try {
         Class var12 = Class.forName("com.sun.tools.example.debug.tty.TTY", true, ClassLoader.getSystemClassLoader());
         Method var6 = var12.getDeclaredMethod("main", String[].class);
         var6.invoke((Object)null, var2);
         return 0;
      } catch (ClassNotFoundException var7) {
         System.err.println(lookup("main.debug.cantfinddebug"));
         return 1;
      } catch (NoSuchMethodException var8) {
         System.err.println(lookup("main.debug.cantfindmain"));
         return 1;
      } catch (InvocationTargetException var9) {
         System.err.println(lookup("main.debug.exceptionindebug"));
         return 1;
      } catch (IllegalAccessException var10) {
         System.err.println(lookup("main.debug.cantaccess"));
         return 1;
      }
   }

   private void init() {
      Properties var1 = this.getAVProps();
      var1.put("browser", "sun.applet.AppletViewer");
      var1.put("browser.version", "1.06");
      var1.put("browser.vendor", "Oracle Corporation");
      var1.put("http.agent", "Java(tm) 2 SDK, Standard Edition v" + theVersion);
      var1.put("package.restrict.definition.java", "true");
      var1.put("package.restrict.definition.sun", "true");
      var1.put("java.version.applet", "true");
      var1.put("java.vendor.applet", "true");
      var1.put("java.vendor.url.applet", "true");
      var1.put("java.class.version.applet", "true");
      var1.put("os.name.applet", "true");
      var1.put("os.version.applet", "true");
      var1.put("os.arch.applet", "true");
      var1.put("file.separator.applet", "true");
      var1.put("path.separator.applet", "true");
      var1.put("line.separator.applet", "true");
      Properties var2 = System.getProperties();
      Enumeration var3 = var2.propertyNames();

      while(var3.hasMoreElements()) {
         String var4 = (String)var3.nextElement();
         String var5 = var2.getProperty(var4);
         String var6;
         if ((var6 = (String)var1.setProperty(var4, var5)) != null) {
            System.err.println(lookup("main.warn.prop.overwrite", var4, var6, var5));
         }
      }

      System.setProperties(var1);
      if (!this.noSecurityFlag) {
         System.setSecurityManager(new AppletSecurity());
      } else {
         System.err.println(lookup("main.nosecmgr"));
      }

   }

   private Properties getAVProps() {
      new Properties();
      File var2 = theUserPropertiesFile;
      Properties var1;
      if (var2.exists()) {
         if (var2.canRead()) {
            var1 = this.getAVProps(var2);
         } else {
            System.err.println(lookup("main.warn.cantreadprops", var2.toString()));
            var1 = this.setDefaultAVProps();
         }
      } else {
         File var3 = new File(System.getProperty("user.home"));
         File var4 = new File(var3, ".hotjava");
         var4 = new File(var4, "properties");
         if (var4.exists()) {
            var1 = this.getAVProps(var4);
         } else {
            System.err.println(lookup("main.warn.cantreadprops", var4.toString()));
            var1 = this.setDefaultAVProps();
         }

         try {
            FileOutputStream var5 = new FileOutputStream(var2);
            Throwable var6 = null;

            try {
               var1.store((OutputStream)var5, lookup("main.prop.store"));
            } catch (Throwable var16) {
               var6 = var16;
               throw var16;
            } finally {
               if (var5 != null) {
                  if (var6 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var15) {
                        var6.addSuppressed(var15);
                     }
                  } else {
                     var5.close();
                  }
               }

            }
         } catch (IOException var18) {
            System.err.println(lookup("main.err.prop.cantsave", var2.toString()));
         }
      }

      return var1;
   }

   private Properties setDefaultAVProps() {
      Properties var1 = new Properties();

      for(int var2 = 0; var2 < avDefaultUserProps.length; ++var2) {
         var1.setProperty(avDefaultUserProps[var2][0], avDefaultUserProps[var2][1]);
      }

      return var1;
   }

   private Properties getAVProps(File var1) {
      Properties var2 = new Properties();
      Properties var3 = new Properties();

      try {
         FileInputStream var4 = new FileInputStream(var1);
         Throwable var5 = null;

         try {
            var3.load((InputStream)(new BufferedInputStream(var4)));
         } catch (Throwable var15) {
            var5 = var15;
            throw var15;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var14) {
                     var5.addSuppressed(var14);
                  }
               } else {
                  var4.close();
               }
            }

         }
      } catch (IOException var17) {
         System.err.println(lookup("main.err.prop.cantread", var1.toString()));
      }

      for(int var18 = 0; var18 < avDefaultUserProps.length; ++var18) {
         String var19 = var3.getProperty(avDefaultUserProps[var18][0]);
         if (var19 != null) {
            var2.setProperty(avDefaultUserProps[var18][0], var19);
         } else {
            var2.setProperty(avDefaultUserProps[var18][0], avDefaultUserProps[var18][1]);
         }
      }

      return var2;
   }

   private static String lookup(String var0) {
      return amh.getMessage(var0);
   }

   private static String lookup(String var0, String var1) {
      return amh.getMessage(var0, (Object)var1);
   }

   private static String lookup(String var0, String var1, String var2) {
      return amh.getMessage(var0, var1, var2);
   }

   private static String lookup(String var0, String var1, String var2, String var3) {
      return amh.getMessage(var0, var1, var2, var3);
   }

   static {
      File var0 = new File(System.getProperty("user.home"));
      var0.canWrite();
      theUserPropertiesFile = new File(var0, ".appletviewer");
      amh = new AppletMessageHandler("appletviewer");
      cmdLineTestFlag = false;
      urlList = new Vector(1);
      theVersion = System.getProperty("java.version");
   }

   class ParseException extends RuntimeException {
      Throwable t = null;

      public ParseException(String var2) {
         super(var2);
      }

      public ParseException(Throwable var2) {
         super(var2.getMessage());
         this.t = var2;
      }
   }
}
