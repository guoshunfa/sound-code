package jdk.internal.instrumentation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.PropertyPermission;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.util.CheckClassAdapter;

public final class ClassInstrumentation {
   private final Class<?> instrumentor;
   private final Logger logger;
   private final String targetName;
   private final String instrumentorName;
   private byte[] newBytes;
   private final ClassReader targetClassReader;
   private final ClassReader instrClassReader;
   private static final String JAVA_HOME = (String)AccessController.doPrivileged((PrivilegedAction)(new PrivilegedAction<String>() {
      public String run() {
         return System.getProperty("java.home");
      }
   }), (AccessControlContext)null, new PropertyPermission("java.home", "read"));

   public ClassInstrumentation(Class<?> var1, String var2, byte[] var3, Logger var4) throws ClassNotFoundException, IOException {
      this.instrumentorName = var1.getName();
      this.targetName = var2;
      this.instrumentor = var1;
      this.logger = var4;
      this.targetClassReader = new ClassReader(var3);
      this.instrClassReader = new ClassReader(this.getInstrumentationInputStream(this.instrumentorName));
      this.instrument();
      this.saveGeneratedInstrumentation();
   }

   private InputStream getInstrumentationInputStream(final String var1) throws IOException {
      try {
         return (InputStream)AccessController.doPrivileged((PrivilegedExceptionAction)(new PrivilegedExceptionAction<InputStream>() {
            public InputStream run() throws IOException {
               return Tracer.class.getResourceAsStream("/" + var1.replace(".", "/") + ".class");
            }
         }), (AccessControlContext)null, new FilePermission(JAVA_HOME + File.separator + "-", "read"));
      } catch (PrivilegedActionException var4) {
         Exception var3 = var4.getException();
         if (var3 instanceof IOException) {
            throw (IOException)var3;
         } else {
            throw (RuntimeException)var3;
         }
      }
   }

   private void instrument() throws IOException, ClassNotFoundException {
      ArrayList var1 = new ArrayList();
      Method[] var2 = this.instrumentor.getDeclaredMethods();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Method var5 = var2[var4];
         InstrumentationMethod var6 = (InstrumentationMethod)var5.getAnnotation(InstrumentationMethod.class);
         if (var6 != null) {
            var1.add(var5);
         }
      }

      MaxLocalsTracker var7 = new MaxLocalsTracker();
      this.instrClassReader.accept(var7, 0);
      ClassNode var8 = new ClassNode();
      Inliner var9 = new Inliner(327680, var8, this.instrumentorName, this.targetClassReader, var1, var7, this.logger);
      this.instrClassReader.accept(var9, 8);
      ClassWriter var10 = new ClassWriter(2);
      MethodMergeAdapter var11 = new MethodMergeAdapter(var10, var8, var1, (TypeMapping[])this.instrumentor.getAnnotationsByType(TypeMapping.class), this.logger);
      this.targetClassReader.accept(var11, 8);
      this.newBytes = var10.toByteArray();
   }

   public byte[] getNewBytes() {
      return (byte[])this.newBytes.clone();
   }

   private void saveGeneratedInstrumentation() {
      boolean var1 = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            return Boolean.getBoolean("jfr.savegenerated");
         }
      });
      if (var1) {
         try {
            this.writeGeneratedDebugInstrumentation();
         } catch (ClassNotFoundException | IOException var3) {
            this.logger.info("Unable to create debug instrumentation");
         }
      }

   }

   private void writeGeneratedDebugInstrumentation() throws IOException, ClassNotFoundException {
      FileOutputStream var1 = new FileOutputStream(this.targetName + ".class");
      Throwable var2 = null;

      try {
         var1.write(this.newBytes);
      } catch (Throwable var47) {
         var2 = var47;
         throw var47;
      } finally {
         if (var1 != null) {
            if (var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var43) {
                  var2.addSuppressed(var43);
               }
            } else {
               var1.close();
            }
         }

      }

      FileWriter var52 = new FileWriter(this.targetName + ".asm");
      var2 = null;

      try {
         PrintWriter var3 = new PrintWriter(var52);
         Throwable var4 = null;

         try {
            ClassReader var5 = new ClassReader(this.getNewBytes());
            CheckClassAdapter.verify(var5, true, var3);
         } catch (Throwable var46) {
            var4 = var46;
            throw var46;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var45) {
                     var4.addSuppressed(var45);
                  }
               } else {
                  var3.close();
               }
            }

         }
      } catch (Throwable var50) {
         var2 = var50;
         throw var50;
      } finally {
         if (var52 != null) {
            if (var2 != null) {
               try {
                  var52.close();
               } catch (Throwable var44) {
                  var2.addSuppressed(var44);
               }
            } else {
               var52.close();
            }
         }

      }

      this.logger.info("Instrumented code saved to " + this.targetName + ".class and .asm");
   }
}
