package jdk.management.resource.internal.inst;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import jdk.internal.instrumentation.ClassInstrumentation;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.internal.instrumentation.Logger;

public final class StaticInstrumentation {
   public static void main(String[] var0) throws Exception {
      instrumentClassesForResourceManagement(new File(var0[0]), new File(var0[1]));
   }

   public static void instrumentClassesForResourceManagement(File var0, File var1) throws Exception {
      if (!var0.isDirectory()) {
         throw new Exception(var0 + " is not a directory");
      } else if (!var1.isDirectory()) {
         throw new Exception(var0 + " is not a directory");
      } else {
         StaticInstrumentation.InstrumentationLogger var2 = new StaticInstrumentation.InstrumentationLogger();
         System.out.println();
         System.out.println("Reading from " + var0);
         System.out.println("Output to " + var1);
         Set var3 = findAllJarFiles(var0);
         HashMap var4 = new HashMap();
         System.out.println();
         System.out.println("Searching for classes");
         int var5 = 0;
         Class[] var6 = InitInstrumentation.hooks;
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Class var9 = var6[var8];
            String var10 = findTargetClassName(var9);
            System.out.println(var5 + ":");
            ++var5;
            System.out.println("   Instrumentation: " + var9.getName());
            System.out.println("   Target         : " + var10);
            boolean var11 = false;
            Iterator var12 = var3.iterator();

            while(var12.hasNext()) {
               File var13 = (File)var12.next();
               JarEntry var14 = getJarEntry(var10, var13);
               if (var14 != null) {
                  System.out.println("   Found in jar  : " + var13);
                  if (var14.getCodeSigners() != null) {
                     throw new Exception("The target class '" + var10 + "' was found in a signed jar: " + var13);
                  }

                  addNewTask(var4, var13, var9);
                  var11 = true;
                  break;
               }
            }

            if (!var11) {
               throw new Exception("The target class '" + var10 + " was not found in any jar");
            }
         }

         System.out.println();
         System.out.println("Instrumenting");
         Iterator var16 = var4.keySet().iterator();

         while(var16.hasNext()) {
            File var17 = (File)var16.next();
            File var18 = new File(var1, var17.getName());
            Files.copy(var17.toPath(), var18.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("   Jar     : " + var17);
            System.out.println("   Jar copy: " + var18);
            ArrayList var19 = new ArrayList();
            Iterator var20 = ((List)var4.get(var17)).iterator();

            while(var20.hasNext()) {
               Class var21 = (Class)var20.next();
               String var22 = findTargetClassName(var21);
               System.out.println("      Class: " + var22);
               byte[] var23 = findSourceBytesFor(var22, var18);
               byte[] var24 = (new ClassInstrumentation(var21, var22, var23, var2)).getNewBytes();
               File var15 = createOutputFile(var1, var22);
               writeOutputClass(var15, var24);
               var19.add(var15);
            }

            System.out.println("   Updating jar");
            updateJar(var1, var18, var19);
            System.out.println();
         }

      }
   }

   private static void updateJar(File var0, File var1, List<File> var2) throws InterruptedException, IOException {
      String var3 = System.getProperty("java.home") + File.separator + "bin" + File.separator + "jar";
      ProcessBuilder var4 = new ProcessBuilder(new String[]{var3, "uvf", var1.getAbsolutePath()});
      Iterator var5 = var2.iterator();

      while(var5.hasNext()) {
         File var6 = (File)var5.next();
         String var7 = var0.toPath().relativize(var6.toPath()).toString();
         var4.command().add(var7);
      }

      var4.directory(var0);
      var4.redirectOutput(ProcessBuilder.Redirect.INHERIT);
      var4.redirectError(ProcessBuilder.Redirect.INHERIT);
      System.out.println("Executing: " + (String)var4.command().stream().collect(Collectors.joining(" ")));
      Process var8 = var4.start();
      var8.waitFor();
   }

   private static void addNewTask(HashMap<File, List<Class<?>>> var0, File var1, Class<?> var2) {
      Object var3 = (List)var0.get(var1);
      if (var3 == null) {
         var3 = new ArrayList();
         var0.put(var1, var3);
      }

      ((List)var3).add(var2);
   }

   private static Set<File> findAllJarFiles(File var0) throws IOException {
      HashSet var1 = new HashSet();
      LinkedBlockingDeque var2 = new LinkedBlockingDeque();
      var2.add(var0);

      File var3;
      while((var3 = (File)var2.poll()) != null) {
         File[] var4 = var3.listFiles();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            File var7 = var4[var6];
            if (var7.isDirectory()) {
               var2.add(var7);
            } else if (var7.getName().endsWith(".jar")) {
               var1.add(var7);
            }
         }
      }

      return var1;
   }

   private static File createOutputFile(File var0, String var1) {
      File var2 = new File(var0, var1.replace(".", File.separator) + ".class");
      var2.getParentFile().mkdirs();
      return var2;
   }

   private static void writeOutputClass(File var0, byte[] var1) throws FileNotFoundException, IOException {
      FileOutputStream var2 = new FileOutputStream(var0);
      Throwable var3 = null;

      try {
         var2.write(var1);
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               var2.close();
            }
         }

      }

   }

   private static String findTargetClassName(Class<?> var0) {
      return ((InstrumentationTarget)var0.getAnnotation(InstrumentationTarget.class)).value();
   }

   private static JarEntry getJarEntry(String var0, File var1) throws Exception {
      JarFile var2 = new JarFile(var1);
      Throwable var3 = null;

      JarEntry var6;
      try {
         String var4 = var0.replace(".", "/") + ".class";
         JarEntry var5 = var2.getJarEntry(var4);
         var6 = var5;
      } catch (Throwable var15) {
         var3 = var15;
         throw var15;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var14) {
                  var3.addSuppressed(var14);
               }
            } else {
               var2.close();
            }
         }

      }

      return var6;
   }

   private static byte[] findSourceBytesFor(String var0, File var1) throws Exception {
      JarFile var2 = new JarFile(var1);
      Throwable var3 = null;

      Object var6;
      try {
         String var4 = var0.replace(".", "/") + ".class";
         ZipEntry var5 = var2.getEntry(var4);
         if (var5 != null) {
            byte[] var19 = readBytes(var2.getInputStream(var5));
            byte[] var7 = var19;
            return var7;
         }

         var6 = null;
      } catch (Throwable var17) {
         var3 = var17;
         throw var17;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var16) {
                  var3.addSuppressed(var16);
               }
            } else {
               var2.close();
            }
         }

      }

      return (byte[])var6;
   }

   private static byte[] readBytes(InputStream var0) throws IOException {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      byte[] var2 = new byte[1024];

      int var3;
      while((var3 = var0.read(var2)) != -1) {
         var1.write(var2, 0, var3);
      }

      return var1.toByteArray();
   }

   static class InstrumentationLogger implements Logger {
      public void error(String var1) {
         System.err.println("StaticInstrumentation error: " + var1);
      }

      public void warn(String var1) {
         System.err.println("StaticInstrumentation warning: " + var1);
      }

      public void info(String var1) {
         System.err.println("StaticInstrumentation info: " + var1);
      }

      public void debug(String var1) {
      }

      public void trace(String var1) {
      }

      public void error(String var1, Throwable var2) {
         System.err.println("StaticInstrumentation error: " + var1 + ": " + var2);
      }
   }
}
