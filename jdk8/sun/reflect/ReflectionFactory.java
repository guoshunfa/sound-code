package sun.reflect;

import java.io.Externalizable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.Objects;
import sun.reflect.misc.ReflectUtil;

public class ReflectionFactory {
   private static boolean initted = false;
   private static final Permission reflectionFactoryAccessPerm = new RuntimePermission("reflectionFactoryAccess");
   private static final ReflectionFactory soleInstance = new ReflectionFactory();
   private static volatile LangReflectAccess langReflectAccess;
   private static volatile Method hasStaticInitializerMethod;
   private static boolean noInflation = false;
   private static int inflationThreshold = 15;

   private ReflectionFactory() {
   }

   public static ReflectionFactory getReflectionFactory() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(reflectionFactoryAccessPerm);
      }

      return soleInstance;
   }

   public void setLangReflectAccess(LangReflectAccess var1) {
      langReflectAccess = var1;
   }

   public FieldAccessor newFieldAccessor(Field var1, boolean var2) {
      checkInitted();
      return UnsafeFieldAccessorFactory.newFieldAccessor(var1, var2);
   }

   public MethodAccessor newMethodAccessor(Method var1) {
      checkInitted();
      if (noInflation && !ReflectUtil.isVMAnonymousClass(var1.getDeclaringClass())) {
         return (new MethodAccessorGenerator()).generateMethod(var1.getDeclaringClass(), var1.getName(), var1.getParameterTypes(), var1.getReturnType(), var1.getExceptionTypes(), var1.getModifiers());
      } else {
         NativeMethodAccessorImpl var2 = new NativeMethodAccessorImpl(var1);
         DelegatingMethodAccessorImpl var3 = new DelegatingMethodAccessorImpl(var2);
         var2.setParent(var3);
         return var3;
      }
   }

   public ConstructorAccessor newConstructorAccessor(Constructor<?> var1) {
      checkInitted();
      Class var2 = var1.getDeclaringClass();
      if (Modifier.isAbstract(var2.getModifiers())) {
         return new InstantiationExceptionConstructorAccessorImpl((String)null);
      } else if (var2 == Class.class) {
         return new InstantiationExceptionConstructorAccessorImpl("Can not instantiate java.lang.Class");
      } else if (Reflection.isSubclassOf(var2, ConstructorAccessorImpl.class)) {
         return new BootstrapConstructorAccessorImpl(var1);
      } else if (noInflation && !ReflectUtil.isVMAnonymousClass(var1.getDeclaringClass())) {
         return (new MethodAccessorGenerator()).generateConstructor(var1.getDeclaringClass(), var1.getParameterTypes(), var1.getExceptionTypes(), var1.getModifiers());
      } else {
         NativeConstructorAccessorImpl var3 = new NativeConstructorAccessorImpl(var1);
         DelegatingConstructorAccessorImpl var4 = new DelegatingConstructorAccessorImpl(var3);
         var3.setParent(var4);
         return var4;
      }
   }

   public Field newField(Class<?> var1, String var2, Class<?> var3, int var4, int var5, String var6, byte[] var7) {
      return langReflectAccess().newField(var1, var2, var3, var4, var5, var6, var7);
   }

   public Method newMethod(Class<?> var1, String var2, Class<?>[] var3, Class<?> var4, Class<?>[] var5, int var6, int var7, String var8, byte[] var9, byte[] var10, byte[] var11) {
      return langReflectAccess().newMethod(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public Constructor<?> newConstructor(Class<?> var1, Class<?>[] var2, Class<?>[] var3, int var4, int var5, String var6, byte[] var7, byte[] var8) {
      return langReflectAccess().newConstructor(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public MethodAccessor getMethodAccessor(Method var1) {
      return langReflectAccess().getMethodAccessor(var1);
   }

   public void setMethodAccessor(Method var1, MethodAccessor var2) {
      langReflectAccess().setMethodAccessor(var1, var2);
   }

   public ConstructorAccessor getConstructorAccessor(Constructor<?> var1) {
      return langReflectAccess().getConstructorAccessor(var1);
   }

   public void setConstructorAccessor(Constructor<?> var1, ConstructorAccessor var2) {
      langReflectAccess().setConstructorAccessor(var1, var2);
   }

   public Method copyMethod(Method var1) {
      return langReflectAccess().copyMethod(var1);
   }

   public Field copyField(Field var1) {
      return langReflectAccess().copyField(var1);
   }

   public <T> Constructor<T> copyConstructor(Constructor<T> var1) {
      return langReflectAccess().copyConstructor(var1);
   }

   public byte[] getExecutableTypeAnnotationBytes(Executable var1) {
      return langReflectAccess().getExecutableTypeAnnotationBytes(var1);
   }

   public Constructor<?> newConstructorForSerialization(Class<?> var1, Constructor<?> var2) {
      return var2.getDeclaringClass() == var1 ? var2 : this.generateConstructor(var1, var2);
   }

   public final Constructor<?> newConstructorForSerialization(Class<?> var1) {
      Class var2 = var1;

      while(Serializable.class.isAssignableFrom(var2)) {
         if ((var2 = var2.getSuperclass()) == null) {
            return null;
         }
      }

      Constructor var3;
      try {
         var3 = var2.getDeclaredConstructor();
         int var4 = var3.getModifiers();
         if ((var4 & 2) != 0 || (var4 & 5) == 0 && !packageEquals(var1, var2)) {
            return null;
         }
      } catch (NoSuchMethodException var5) {
         return null;
      }

      return this.generateConstructor(var1, var3);
   }

   private final Constructor<?> generateConstructor(Class<?> var1, Constructor<?> var2) {
      SerializationConstructorAccessorImpl var3 = (new MethodAccessorGenerator()).generateSerializationConstructor(var1, var2.getParameterTypes(), var2.getExceptionTypes(), var2.getModifiers(), var2.getDeclaringClass());
      Constructor var4 = this.newConstructor(var2.getDeclaringClass(), var2.getParameterTypes(), var2.getExceptionTypes(), var2.getModifiers(), langReflectAccess().getConstructorSlot(var2), langReflectAccess().getConstructorSignature(var2), langReflectAccess().getConstructorAnnotations(var2), langReflectAccess().getConstructorParameterAnnotations(var2));
      this.setConstructorAccessor(var4, var3);
      var4.setAccessible(true);
      return var4;
   }

   public final Constructor<?> newConstructorForExternalization(Class<?> var1) {
      if (!Externalizable.class.isAssignableFrom(var1)) {
         return null;
      } else {
         try {
            Constructor var2 = var1.getConstructor();
            var2.setAccessible(true);
            return var2;
         } catch (NoSuchMethodException var3) {
            return null;
         }
      }
   }

   public final MethodHandle readObjectForSerialization(Class<?> var1) {
      return this.findReadWriteObjectForSerialization(var1, "readObject", ObjectInputStream.class);
   }

   public final MethodHandle readObjectNoDataForSerialization(Class<?> var1) {
      return this.findReadWriteObjectForSerialization(var1, "readObjectNoData", ObjectInputStream.class);
   }

   public final MethodHandle writeObjectForSerialization(Class<?> var1) {
      return this.findReadWriteObjectForSerialization(var1, "writeObject", ObjectOutputStream.class);
   }

   private final MethodHandle findReadWriteObjectForSerialization(Class<?> var1, String var2, Class<?> var3) {
      if (!Serializable.class.isAssignableFrom(var1)) {
         return null;
      } else {
         try {
            Method var4 = var1.getDeclaredMethod(var2, var3);
            int var5 = var4.getModifiers();
            if (var4.getReturnType() == Void.TYPE && !Modifier.isStatic(var5) && Modifier.isPrivate(var5)) {
               var4.setAccessible(true);
               return MethodHandles.lookup().unreflect(var4);
            } else {
               return null;
            }
         } catch (NoSuchMethodException var6) {
            return null;
         } catch (IllegalAccessException var7) {
            throw new InternalError("Error", var7);
         }
      }
   }

   public final MethodHandle readResolveForSerialization(Class<?> var1) {
      return this.getReplaceResolveForSerialization(var1, "readResolve");
   }

   public final MethodHandle writeReplaceForSerialization(Class<?> var1) {
      return this.getReplaceResolveForSerialization(var1, "writeReplace");
   }

   private MethodHandle getReplaceResolveForSerialization(Class<?> var1, String var2) {
      if (!Serializable.class.isAssignableFrom(var1)) {
         return null;
      } else {
         Class var3 = var1;

         while(var3 != null) {
            try {
               Method var4 = var3.getDeclaredMethod(var2);
               if (var4.getReturnType() != Object.class) {
                  return null;
               }

               int var5 = var4.getModifiers();
               if (Modifier.isStatic(var5) | Modifier.isAbstract(var5)) {
                  return null;
               }

               if (!(Modifier.isPublic(var5) | Modifier.isProtected(var5))) {
                  if (Modifier.isPrivate(var5) && var1 != var3) {
                     return null;
                  }

                  if (!packageEquals(var1, var3)) {
                     return null;
                  }
               }

               try {
                  var4.setAccessible(true);
                  return MethodHandles.lookup().unreflect(var4);
               } catch (IllegalAccessException var7) {
                  throw new InternalError("Error", var7);
               }
            } catch (NoSuchMethodException var8) {
               var3 = var3.getSuperclass();
            }
         }

         return null;
      }
   }

   public final boolean hasStaticInitializerForSerialization(Class<?> var1) {
      Method var2 = hasStaticInitializerMethod;
      if (var2 == null) {
         try {
            var2 = ObjectStreamClass.class.getDeclaredMethod("hasStaticInitializer", Class.class);
            var2.setAccessible(true);
            hasStaticInitializerMethod = var2;
         } catch (NoSuchMethodException var5) {
            throw new InternalError("No such method hasStaticInitializer on " + ObjectStreamClass.class, var5);
         }
      }

      try {
         return (Boolean)var2.invoke((Object)null, var1);
      } catch (IllegalAccessException | InvocationTargetException var4) {
         throw new InternalError("Exception invoking hasStaticInitializer", var4);
      }
   }

   public final OptionalDataException newOptionalDataExceptionForSerialization(boolean var1) {
      try {
         Constructor var2 = OptionalDataException.class.getDeclaredConstructor(Boolean.TYPE);
         var2.setAccessible(true);
         return (OptionalDataException)var2.newInstance(var1);
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException var3) {
         throw new InternalError("unable to create OptionalDataException", var3);
      }
   }

   static int inflationThreshold() {
      return inflationThreshold;
   }

   private static void checkInitted() {
      if (!initted) {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               if (System.out == null) {
                  return null;
               } else {
                  String var1 = System.getProperty("sun.reflect.noInflation");
                  if (var1 != null && var1.equals("true")) {
                     ReflectionFactory.noInflation = true;
                  }

                  var1 = System.getProperty("sun.reflect.inflationThreshold");
                  if (var1 != null) {
                     try {
                        ReflectionFactory.inflationThreshold = Integer.parseInt(var1);
                     } catch (NumberFormatException var3) {
                        throw new RuntimeException("Unable to parse property sun.reflect.inflationThreshold", var3);
                     }
                  }

                  ReflectionFactory.initted = true;
                  return null;
               }
            }
         });
      }
   }

   private static LangReflectAccess langReflectAccess() {
      if (langReflectAccess == null) {
         Modifier.isPublic(1);
      }

      return langReflectAccess;
   }

   private static boolean packageEquals(Class<?> var0, Class<?> var1) {
      return var0.getClassLoader() == var1.getClassLoader() && Objects.equals(var0.getPackage(), var1.getPackage());
   }

   public static final class GetReflectionFactoryAction implements PrivilegedAction<ReflectionFactory> {
      public ReflectionFactory run() {
         return ReflectionFactory.getReflectionFactory();
      }
   }
}
