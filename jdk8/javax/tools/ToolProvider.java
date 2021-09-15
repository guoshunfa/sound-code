package javax.tools;

import java.io.File;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ToolProvider {
   private static final String propertyName = "sun.tools.ToolProvider";
   private static final String loggerName = "javax.tools";
   private static final String defaultJavaCompilerName = "com.sun.tools.javac.api.JavacTool";
   private static final String defaultDocumentationToolName = "com.sun.tools.javadoc.api.JavadocTool";
   private static ToolProvider instance;
   private Map<String, Reference<Class<?>>> toolClasses = new HashMap();
   private Reference<ClassLoader> refToolClassLoader = null;
   private static final String[] defaultToolsLocation = new String[]{"lib", "tools.jar"};

   static <T> T trace(Level var0, Object var1) {
      try {
         if (System.getProperty("sun.tools.ToolProvider") != null) {
            StackTraceElement[] var2 = Thread.currentThread().getStackTrace();
            String var3 = "???";
            String var4 = ToolProvider.class.getName();
            if (var2.length > 2) {
               StackTraceElement var5 = var2[2];
               var3 = String.format((Locale)null, "%s(%s:%s)", var5.getMethodName(), var5.getFileName(), var5.getLineNumber());
               var4 = var5.getClassName();
            }

            Logger var7 = Logger.getLogger("javax.tools");
            if (var1 instanceof Throwable) {
               var7.logp(var0, var4, var3, var1.getClass().getName(), (Throwable)var1);
            } else {
               var7.logp(var0, var4, var3, String.valueOf(var1));
            }
         }
      } catch (SecurityException var6) {
         System.err.format((Locale)null, "%s: %s; %s%n", ToolProvider.class.getName(), var1, var6.getLocalizedMessage());
      }

      return null;
   }

   public static JavaCompiler getSystemJavaCompiler() {
      return (JavaCompiler)instance().getSystemTool(JavaCompiler.class, "com.sun.tools.javac.api.JavacTool");
   }

   public static DocumentationTool getSystemDocumentationTool() {
      return (DocumentationTool)instance().getSystemTool(DocumentationTool.class, "com.sun.tools.javadoc.api.JavadocTool");
   }

   public static ClassLoader getSystemToolClassLoader() {
      try {
         Class var0 = instance().getSystemToolClass(JavaCompiler.class, "com.sun.tools.javac.api.JavacTool");
         return var0.getClassLoader();
      } catch (Throwable var1) {
         return (ClassLoader)trace(Level.WARNING, var1);
      }
   }

   private static synchronized ToolProvider instance() {
      if (instance == null) {
         instance = new ToolProvider();
      }

      return instance;
   }

   private ToolProvider() {
   }

   private <T> T getSystemTool(Class<T> var1, String var2) {
      Class var3 = this.getSystemToolClass(var1, var2);

      try {
         return var3.asSubclass(var1).newInstance();
      } catch (Throwable var5) {
         trace(Level.WARNING, var5);
         return null;
      }
   }

   private <T> Class<? extends T> getSystemToolClass(Class<T> var1, String var2) {
      Reference var3 = (Reference)this.toolClasses.get(var2);
      Class var4 = var3 == null ? null : (Class)var3.get();
      if (var4 == null) {
         try {
            var4 = this.findSystemToolClass(var2);
         } catch (Throwable var6) {
            return (Class)trace(Level.WARNING, var6);
         }

         this.toolClasses.put(var2, new WeakReference(var4));
      }

      return var4.asSubclass(var1);
   }

   private Class<?> findSystemToolClass(String var1) throws MalformedURLException, ClassNotFoundException {
      try {
         return Class.forName(var1, false, (ClassLoader)null);
      } catch (ClassNotFoundException var9) {
         trace(Level.FINE, var9);
         Object var3 = this.refToolClassLoader == null ? null : (ClassLoader)this.refToolClassLoader.get();
         if (var3 == null) {
            File var4 = new File(System.getProperty("java.home"));
            if (var4.getName().equalsIgnoreCase("jre")) {
               var4 = var4.getParentFile();
            }

            String[] var5 = defaultToolsLocation;
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               String var8 = var5[var7];
               var4 = new File(var4, var8);
            }

            if (!var4.exists()) {
               throw var9;
            }

            URL[] var10 = new URL[]{var4.toURI().toURL()};
            trace(Level.FINE, var10[0].toString());
            var3 = URLClassLoader.newInstance(var10);
            this.refToolClassLoader = new WeakReference(var3);
         }

         return Class.forName(var1, false, (ClassLoader)var3);
      }
   }
}
