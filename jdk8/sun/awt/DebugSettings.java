package sun.awt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringBufferInputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import sun.util.logging.PlatformLogger;

final class DebugSettings {
   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.debug.DebugSettings");
   static final String PREFIX = "awtdebug";
   static final String PROP_FILE = "properties";
   private static final String[] DEFAULT_PROPS = new String[]{"awtdebug.assert=true", "awtdebug.trace=false", "awtdebug.on=true", "awtdebug.ctrace=false"};
   private static DebugSettings instance = null;
   private Properties props = new Properties();
   private static final String PROP_CTRACE = "ctrace";
   private static final int PROP_CTRACE_LEN = "ctrace".length();

   static void init() {
      if (instance == null) {
         NativeLibLoader.loadLibraries();
         instance = new DebugSettings();
         instance.loadNativeSettings();
      }
   }

   private DebugSettings() {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            DebugSettings.this.loadProperties();
            return null;
         }
      });
   }

   private synchronized void loadProperties() {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            DebugSettings.this.loadDefaultProperties();
            DebugSettings.this.loadFileProperties();
            DebugSettings.this.loadSystemProperties();
            return null;
         }
      });
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
         log.fine("DebugSettings:\n{0}", this);
      }

   }

   public String toString() {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      PrintStream var2 = new PrintStream(var1);
      Iterator var3 = this.props.stringPropertyNames().iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         String var5 = this.props.getProperty(var4, "");
         var2.println(var4 + " = " + var5);
      }

      return new String(var1.toByteArray());
   }

   private void loadDefaultProperties() {
      try {
         for(int var1 = 0; var1 < DEFAULT_PROPS.length; ++var1) {
            StringBufferInputStream var2 = new StringBufferInputStream(DEFAULT_PROPS[var1]);
            this.props.load((InputStream)var2);
            var2.close();
         }
      } catch (IOException var3) {
      }

   }

   private void loadFileProperties() {
      String var1 = System.getProperty("awtdebug.properties", "");
      if (var1.equals("")) {
         var1 = System.getProperty("user.home", "") + File.separator + "awtdebug" + "." + "properties";
      }

      File var3 = new File(var1);

      try {
         this.println("Reading debug settings from '" + var3.getCanonicalPath() + "'...");
         FileInputStream var4 = new FileInputStream(var3);
         this.props.load((InputStream)var4);
         var4.close();
      } catch (FileNotFoundException var5) {
         this.println("Did not find settings file.");
      } catch (IOException var6) {
         this.println("Problem reading settings, IOException: " + var6.getMessage());
      }

   }

   private void loadSystemProperties() {
      Properties var1 = System.getProperties();
      Iterator var2 = var1.stringPropertyNames().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         String var4 = var1.getProperty(var3, "");
         if (var3.startsWith("awtdebug")) {
            this.props.setProperty(var3, var4);
         }
      }

   }

   public synchronized boolean getBoolean(String var1, boolean var2) {
      String var3 = this.getString(var1, String.valueOf(var2));
      return var3.equalsIgnoreCase("true");
   }

   public synchronized int getInt(String var1, int var2) {
      String var3 = this.getString(var1, String.valueOf(var2));
      return Integer.parseInt(var3);
   }

   public synchronized String getString(String var1, String var2) {
      String var3 = "awtdebug." + var1;
      String var4 = this.props.getProperty(var3, var2);
      return var4;
   }

   private synchronized List<String> getPropertyNames() {
      LinkedList var1 = new LinkedList();
      Iterator var2 = this.props.stringPropertyNames().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         var3 = var3.substring("awtdebug".length() + 1);
         var1.add(var3);
      }

      return var1;
   }

   private void println(Object var1) {
      if (log.isLoggable(PlatformLogger.Level.FINER)) {
         log.finer(var1.toString());
      }

   }

   private synchronized native void setCTracingOn(boolean var1);

   private synchronized native void setCTracingOn(boolean var1, String var2);

   private synchronized native void setCTracingOn(boolean var1, String var2, int var3);

   private void loadNativeSettings() {
      boolean var1 = this.getBoolean("ctrace", false);
      this.setCTracingOn(var1);
      LinkedList var2 = new LinkedList();
      Iterator var3 = this.getPropertyNames().iterator();

      String var4;
      while(var3.hasNext()) {
         var4 = (String)var3.next();
         if (var4.startsWith("ctrace") && var4.length() > PROP_CTRACE_LEN) {
            var2.add(var4);
         }
      }

      Collections.sort(var2);
      var3 = var2.iterator();

      while(var3.hasNext()) {
         var4 = (String)var3.next();
         String var5 = var4.substring(PROP_CTRACE_LEN + 1);
         int var8 = var5.indexOf(64);
         String var6 = var8 != -1 ? var5.substring(0, var8) : var5;
         String var7 = var8 != -1 ? var5.substring(var8 + 1) : "";
         boolean var9 = this.getBoolean(var4, false);
         if (var7.length() == 0) {
            this.setCTracingOn(var9, var6);
         } else {
            int var10 = Integer.parseInt(var7, 10);
            this.setCTracingOn(var9, var6, var10);
         }
      }

   }
}
