package java.lang.reflect;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.reflect.ReflectionFactory;

public class Modifier {
   public static final int PUBLIC = 1;
   public static final int PRIVATE = 2;
   public static final int PROTECTED = 4;
   public static final int STATIC = 8;
   public static final int FINAL = 16;
   public static final int SYNCHRONIZED = 32;
   public static final int VOLATILE = 64;
   public static final int TRANSIENT = 128;
   public static final int NATIVE = 256;
   public static final int INTERFACE = 512;
   public static final int ABSTRACT = 1024;
   public static final int STRICT = 2048;
   static final int BRIDGE = 64;
   static final int VARARGS = 128;
   static final int SYNTHETIC = 4096;
   static final int ANNOTATION = 8192;
   static final int ENUM = 16384;
   static final int MANDATED = 32768;
   private static final int CLASS_MODIFIERS = 3103;
   private static final int INTERFACE_MODIFIERS = 3087;
   private static final int CONSTRUCTOR_MODIFIERS = 7;
   private static final int METHOD_MODIFIERS = 3391;
   private static final int FIELD_MODIFIERS = 223;
   private static final int PARAMETER_MODIFIERS = 16;
   static final int ACCESS_MODIFIERS = 7;

   public static boolean isPublic(int var0) {
      return (var0 & 1) != 0;
   }

   public static boolean isPrivate(int var0) {
      return (var0 & 2) != 0;
   }

   public static boolean isProtected(int var0) {
      return (var0 & 4) != 0;
   }

   public static boolean isStatic(int var0) {
      return (var0 & 8) != 0;
   }

   public static boolean isFinal(int var0) {
      return (var0 & 16) != 0;
   }

   public static boolean isSynchronized(int var0) {
      return (var0 & 32) != 0;
   }

   public static boolean isVolatile(int var0) {
      return (var0 & 64) != 0;
   }

   public static boolean isTransient(int var0) {
      return (var0 & 128) != 0;
   }

   public static boolean isNative(int var0) {
      return (var0 & 256) != 0;
   }

   public static boolean isInterface(int var0) {
      return (var0 & 512) != 0;
   }

   public static boolean isAbstract(int var0) {
      return (var0 & 1024) != 0;
   }

   public static boolean isStrict(int var0) {
      return (var0 & 2048) != 0;
   }

   public static String toString(int var0) {
      StringBuilder var1 = new StringBuilder();
      if ((var0 & 1) != 0) {
         var1.append("public ");
      }

      if ((var0 & 4) != 0) {
         var1.append("protected ");
      }

      if ((var0 & 2) != 0) {
         var1.append("private ");
      }

      if ((var0 & 1024) != 0) {
         var1.append("abstract ");
      }

      if ((var0 & 8) != 0) {
         var1.append("static ");
      }

      if ((var0 & 16) != 0) {
         var1.append("final ");
      }

      if ((var0 & 128) != 0) {
         var1.append("transient ");
      }

      if ((var0 & 64) != 0) {
         var1.append("volatile ");
      }

      if ((var0 & 32) != 0) {
         var1.append("synchronized ");
      }

      if ((var0 & 256) != 0) {
         var1.append("native ");
      }

      if ((var0 & 2048) != 0) {
         var1.append("strictfp ");
      }

      if ((var0 & 512) != 0) {
         var1.append("interface ");
      }

      int var2;
      return (var2 = var1.length()) > 0 ? var1.toString().substring(0, var2 - 1) : "";
   }

   static boolean isSynthetic(int var0) {
      return (var0 & 4096) != 0;
   }

   static boolean isMandated(int var0) {
      return (var0 & '耀') != 0;
   }

   public static int classModifiers() {
      return 3103;
   }

   public static int interfaceModifiers() {
      return 3087;
   }

   public static int constructorModifiers() {
      return 7;
   }

   public static int methodModifiers() {
      return 3391;
   }

   public static int fieldModifiers() {
      return 223;
   }

   public static int parameterModifiers() {
      return 16;
   }

   static {
      ReflectionFactory var0 = (ReflectionFactory)AccessController.doPrivileged((PrivilegedAction)(new ReflectionFactory.GetReflectionFactoryAction()));
      var0.setLangReflectAccess(new ReflectAccess());
   }
}
