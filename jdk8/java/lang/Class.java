package java.lang;

import java.io.InputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import sun.misc.Unsafe;
import sun.misc.VM;
import sun.reflect.CallerSensitive;
import sun.reflect.ConstantPool;
import sun.reflect.Reflection;
import sun.reflect.ReflectionFactory;
import sun.reflect.annotation.AnnotationParser;
import sun.reflect.annotation.AnnotationSupport;
import sun.reflect.annotation.AnnotationType;
import sun.reflect.annotation.TypeAnnotationParser;
import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.repository.ClassRepository;
import sun.reflect.generics.repository.ConstructorRepository;
import sun.reflect.generics.repository.MethodRepository;
import sun.reflect.generics.scope.ClassScope;
import sun.reflect.misc.ReflectUtil;
import sun.security.util.SecurityConstants;

public final class Class<T> implements Serializable, GenericDeclaration, Type, AnnotatedElement {
   private static final int ANNOTATION = 8192;
   private static final int ENUM = 16384;
   private static final int SYNTHETIC = 4096;
   private transient volatile Constructor<T> cachedConstructor;
   private transient volatile Class<?> newInstanceCallerCache;
   private transient String name;
   private final ClassLoader classLoader;
   private static ProtectionDomain allPermDomain;
   private static boolean useCaches;
   private transient volatile SoftReference<Class.ReflectionData<T>> reflectionData;
   private transient volatile int classRedefinedCount = 0;
   private transient volatile ClassRepository genericInfo;
   private static final long serialVersionUID = 3206093459760846163L;
   private static final ObjectStreamField[] serialPersistentFields;
   private static ReflectionFactory reflectionFactory;
   private static boolean initted;
   private transient volatile T[] enumConstants = null;
   private transient volatile Map<String, T> enumConstantDirectory = null;
   private transient volatile Class.AnnotationData annotationData;
   private transient volatile AnnotationType annotationType;
   transient ClassValue.ClassValueMap classValueMap;

   private static native void registerNatives();

   private Class(ClassLoader var1) {
      this.classLoader = var1;
   }

   public String toString() {
      return (this.isInterface() ? "interface " : (this.isPrimitive() ? "" : "class ")) + this.getName();
   }

   public String toGenericString() {
      if (this.isPrimitive()) {
         return this.toString();
      } else {
         StringBuilder var1 = new StringBuilder();
         int var2 = this.getModifiers() & Modifier.classModifiers();
         if (var2 != 0) {
            var1.append(Modifier.toString(var2));
            var1.append(' ');
         }

         if (this.isAnnotation()) {
            var1.append('@');
         }

         if (this.isInterface()) {
            var1.append("interface");
         } else if (this.isEnum()) {
            var1.append("enum");
         } else {
            var1.append("class");
         }

         var1.append(' ');
         var1.append(this.getName());
         TypeVariable[] var3 = this.getTypeParameters();
         if (var3.length > 0) {
            boolean var4 = true;
            var1.append('<');
            TypeVariable[] var5 = var3;
            int var6 = var3.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               TypeVariable var8 = var5[var7];
               if (!var4) {
                  var1.append(',');
               }

               var1.append(var8.getTypeName());
               var4 = false;
            }

            var1.append('>');
         }

         return var1.toString();
      }
   }

   @CallerSensitive
   public static Class<?> forName(String var0) throws ClassNotFoundException {
      Class var1 = Reflection.getCallerClass();
      return forName0(var0, true, ClassLoader.getClassLoader(var1), var1);
   }

   @CallerSensitive
   public static Class<?> forName(String var0, boolean var1, ClassLoader var2) throws ClassNotFoundException {
      Class var3 = null;
      SecurityManager var4 = System.getSecurityManager();
      if (var4 != null) {
         var3 = Reflection.getCallerClass();
         if (VM.isSystemDomainLoader(var2)) {
            ClassLoader var5 = ClassLoader.getClassLoader(var3);
            if (!VM.isSystemDomainLoader(var5)) {
               var4.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
            }
         }
      }

      return forName0(var0, var1, var2, var3);
   }

   private static native Class<?> forName0(String var0, boolean var1, ClassLoader var2, Class<?> var3) throws ClassNotFoundException;

   @CallerSensitive
   public T newInstance() throws InstantiationException, IllegalAccessException {
      if (System.getSecurityManager() != null) {
         this.checkMemberAccess(0, Reflection.getCallerClass(), false);
      }

      if (this.cachedConstructor == null) {
         if (this == Class.class) {
            throw new IllegalAccessException("Can not call newInstance() on the Class for java.lang.Class");
         }

         try {
            Class[] var1 = new Class[0];
            final Constructor var2 = this.getConstructor0(var1, 1);
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
               public Void run() {
                  var2.setAccessible(true);
                  return null;
               }
            });
            this.cachedConstructor = var2;
         } catch (NoSuchMethodException var5) {
            throw (InstantiationException)(new InstantiationException(this.getName())).initCause(var5);
         }
      }

      Constructor var6 = this.cachedConstructor;
      int var7 = var6.getModifiers();
      if (!Reflection.quickCheckMemberAccess(this, var7)) {
         Class var3 = Reflection.getCallerClass();
         if (this.newInstanceCallerCache != var3) {
            Reflection.ensureMemberAccess(var3, this, (Object)null, var7);
            this.newInstanceCallerCache = var3;
         }
      }

      try {
         return var6.newInstance((Object[])null);
      } catch (InvocationTargetException var4) {
         Unsafe.getUnsafe().throwException(var4.getTargetException());
         return null;
      }
   }

   public native boolean isInstance(Object var1);

   public native boolean isAssignableFrom(Class<?> var1);

   public native boolean isInterface();

   public native boolean isArray();

   public native boolean isPrimitive();

   public boolean isAnnotation() {
      return (this.getModifiers() & 8192) != 0;
   }

   public boolean isSynthetic() {
      return (this.getModifiers() & 4096) != 0;
   }

   public String getName() {
      String var1 = this.name;
      if (var1 == null) {
         this.name = var1 = this.getName0();
      }

      return var1;
   }

   private native String getName0();

   @CallerSensitive
   public ClassLoader getClassLoader() {
      ClassLoader var1 = this.getClassLoader0();
      if (var1 == null) {
         return null;
      } else {
         SecurityManager var2 = System.getSecurityManager();
         if (var2 != null) {
            ClassLoader.checkClassLoaderPermission(var1, Reflection.getCallerClass());
         }

         return var1;
      }
   }

   ClassLoader getClassLoader0() {
      return this.classLoader;
   }

   public TypeVariable<Class<T>>[] getTypeParameters() {
      ClassRepository var1 = this.getGenericInfo();
      return var1 != null ? (TypeVariable[])var1.getTypeParameters() : (TypeVariable[])(new TypeVariable[0]);
   }

   public native Class<? super T> getSuperclass();

   public Type getGenericSuperclass() {
      ClassRepository var1 = this.getGenericInfo();
      if (var1 == null) {
         return this.getSuperclass();
      } else {
         return this.isInterface() ? null : var1.getSuperclass();
      }
   }

   public Package getPackage() {
      return Package.getPackage(this);
   }

   public Class<?>[] getInterfaces() {
      Class.ReflectionData var1 = this.reflectionData();
      if (var1 == null) {
         return this.getInterfaces0();
      } else {
         Class[] var2 = var1.interfaces;
         if (var2 == null) {
            var2 = this.getInterfaces0();
            var1.interfaces = var2;
         }

         return (Class[])var2.clone();
      }
   }

   private native Class<?>[] getInterfaces0();

   public Type[] getGenericInterfaces() {
      ClassRepository var1 = this.getGenericInfo();
      return (Type[])(var1 == null ? this.getInterfaces() : var1.getSuperInterfaces());
   }

   public native Class<?> getComponentType();

   public native int getModifiers();

   public native Object[] getSigners();

   native void setSigners(Object[] var1);

   @CallerSensitive
   public Method getEnclosingMethod() throws SecurityException {
      Class.EnclosingMethodInfo var1 = this.getEnclosingMethodInfo();
      if (var1 == null) {
         return null;
      } else if (!var1.isMethod()) {
         return null;
      } else {
         MethodRepository var2 = MethodRepository.make(var1.getDescriptor(), this.getFactory());
         Class var3 = toClass(var2.getReturnType());
         Type[] var4 = var2.getParameterTypes();
         Class[] var5 = new Class[var4.length];

         for(int var6 = 0; var6 < var5.length; ++var6) {
            var5[var6] = toClass(var4[var6]);
         }

         Class var14 = var1.getEnclosingClass();
         var14.checkMemberAccess(1, Reflection.getCallerClass(), true);
         Method[] var7 = var14.getDeclaredMethods();
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            Method var10 = var7[var9];
            if (var10.getName().equals(var1.getName())) {
               Class[] var11 = var10.getParameterTypes();
               if (var11.length == var5.length) {
                  boolean var12 = true;

                  for(int var13 = 0; var13 < var11.length; ++var13) {
                     if (!var11[var13].equals(var5[var13])) {
                        var12 = false;
                        break;
                     }
                  }

                  if (var12 && var10.getReturnType().equals(var3)) {
                     return var10;
                  }
               }
            }
         }

         throw new InternalError("Enclosing method not found");
      }
   }

   private native Object[] getEnclosingMethod0();

   private Class.EnclosingMethodInfo getEnclosingMethodInfo() {
      Object[] var1 = this.getEnclosingMethod0();
      return var1 == null ? null : new Class.EnclosingMethodInfo(var1);
   }

   private static Class<?> toClass(Type var0) {
      return var0 instanceof GenericArrayType ? Array.newInstance(toClass(((GenericArrayType)var0).getGenericComponentType()), 0).getClass() : (Class)var0;
   }

   @CallerSensitive
   public Constructor<?> getEnclosingConstructor() throws SecurityException {
      Class.EnclosingMethodInfo var1 = this.getEnclosingMethodInfo();
      if (var1 == null) {
         return null;
      } else if (!var1.isConstructor()) {
         return null;
      } else {
         ConstructorRepository var2 = ConstructorRepository.make(var1.getDescriptor(), this.getFactory());
         Type[] var3 = var2.getParameterTypes();
         Class[] var4 = new Class[var3.length];

         for(int var5 = 0; var5 < var4.length; ++var5) {
            var4[var5] = toClass(var3[var5]);
         }

         Class var13 = var1.getEnclosingClass();
         var13.checkMemberAccess(1, Reflection.getCallerClass(), true);
         Constructor[] var6 = var13.getDeclaredConstructors();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Constructor var9 = var6[var8];
            Class[] var10 = var9.getParameterTypes();
            if (var10.length == var4.length) {
               boolean var11 = true;

               for(int var12 = 0; var12 < var10.length; ++var12) {
                  if (!var10[var12].equals(var4[var12])) {
                     var11 = false;
                     break;
                  }
               }

               if (var11) {
                  return var9;
               }
            }
         }

         throw new InternalError("Enclosing constructor not found");
      }
   }

   @CallerSensitive
   public Class<?> getDeclaringClass() throws SecurityException {
      Class var1 = this.getDeclaringClass0();
      if (var1 != null) {
         var1.checkPackageAccess(ClassLoader.getClassLoader(Reflection.getCallerClass()), true);
      }

      return var1;
   }

   private native Class<?> getDeclaringClass0();

   @CallerSensitive
   public Class<?> getEnclosingClass() throws SecurityException {
      Class.EnclosingMethodInfo var1 = this.getEnclosingMethodInfo();
      Class var2;
      if (var1 == null) {
         var2 = this.getDeclaringClass();
      } else {
         Class var3 = var1.getEnclosingClass();
         if (var3 == this || var3 == null) {
            throw new InternalError("Malformed enclosing method information");
         }

         var2 = var3;
      }

      if (var2 != null) {
         var2.checkPackageAccess(ClassLoader.getClassLoader(Reflection.getCallerClass()), true);
      }

      return var2;
   }

   public String getSimpleName() {
      if (this.isArray()) {
         return this.getComponentType().getSimpleName() + "[]";
      } else {
         String var1 = this.getSimpleBinaryName();
         if (var1 == null) {
            var1 = this.getName();
            return var1.substring(var1.lastIndexOf(".") + 1);
         } else {
            int var2 = var1.length();
            if (var2 >= 1 && var1.charAt(0) == '$') {
               int var3;
               for(var3 = 1; var3 < var2 && isAsciiDigit(var1.charAt(var3)); ++var3) {
               }

               return var1.substring(var3);
            } else {
               throw new InternalError("Malformed class name");
            }
         }
      }
   }

   public String getTypeName() {
      if (this.isArray()) {
         try {
            Class var1 = this;

            int var2;
            for(var2 = 0; var1.isArray(); var1 = var1.getComponentType()) {
               ++var2;
            }

            StringBuilder var3 = new StringBuilder();
            var3.append(var1.getName());

            for(int var4 = 0; var4 < var2; ++var4) {
               var3.append("[]");
            }

            return var3.toString();
         } catch (Throwable var5) {
         }
      }

      return this.getName();
   }

   private static boolean isAsciiDigit(char var0) {
      return '0' <= var0 && var0 <= '9';
   }

   public String getCanonicalName() {
      if (this.isArray()) {
         String var3 = this.getComponentType().getCanonicalName();
         return var3 != null ? var3 + "[]" : null;
      } else if (this.isLocalOrAnonymousClass()) {
         return null;
      } else {
         Class var1 = this.getEnclosingClass();
         if (var1 == null) {
            return this.getName();
         } else {
            String var2 = var1.getCanonicalName();
            return var2 == null ? null : var2 + "." + this.getSimpleName();
         }
      }
   }

   public boolean isAnonymousClass() {
      return "".equals(this.getSimpleName());
   }

   public boolean isLocalClass() {
      return this.isLocalOrAnonymousClass() && !this.isAnonymousClass();
   }

   public boolean isMemberClass() {
      return this.getSimpleBinaryName() != null && !this.isLocalOrAnonymousClass();
   }

   private String getSimpleBinaryName() {
      Class var1 = this.getEnclosingClass();
      if (var1 == null) {
         return null;
      } else {
         try {
            return this.getName().substring(var1.getName().length());
         } catch (IndexOutOfBoundsException var3) {
            throw new InternalError("Malformed class name", var3);
         }
      }
   }

   private boolean isLocalOrAnonymousClass() {
      return this.getEnclosingMethodInfo() != null;
   }

   @CallerSensitive
   public Class<?>[] getClasses() {
      this.checkMemberAccess(0, Reflection.getCallerClass(), false);
      return (Class[])AccessController.doPrivileged(new PrivilegedAction<Class<?>[]>() {
         public Class<?>[] run() {
            ArrayList var1 = new ArrayList();

            for(Class var2 = Class.this; var2 != null; var2 = var2.getSuperclass()) {
               Class[] var3 = var2.getDeclaredClasses();

               for(int var4 = 0; var4 < var3.length; ++var4) {
                  if (Modifier.isPublic(var3[var4].getModifiers())) {
                     var1.add(var3[var4]);
                  }
               }
            }

            return (Class[])var1.toArray(new Class[0]);
         }
      });
   }

   @CallerSensitive
   public Field[] getFields() throws SecurityException {
      this.checkMemberAccess(0, Reflection.getCallerClass(), true);
      return copyFields(this.privateGetPublicFields((Set)null));
   }

   @CallerSensitive
   public Method[] getMethods() throws SecurityException {
      this.checkMemberAccess(0, Reflection.getCallerClass(), true);
      return copyMethods(this.privateGetPublicMethods());
   }

   @CallerSensitive
   public Constructor<?>[] getConstructors() throws SecurityException {
      this.checkMemberAccess(0, Reflection.getCallerClass(), true);
      return copyConstructors(this.privateGetDeclaredConstructors(true));
   }

   @CallerSensitive
   public Field getField(String var1) throws NoSuchFieldException, SecurityException {
      this.checkMemberAccess(0, Reflection.getCallerClass(), true);
      Field var2 = this.getField0(var1);
      if (var2 == null) {
         throw new NoSuchFieldException(var1);
      } else {
         return var2;
      }
   }

   @CallerSensitive
   public Method getMethod(String var1, Class<?>... var2) throws NoSuchMethodException, SecurityException {
      this.checkMemberAccess(0, Reflection.getCallerClass(), true);
      Method var3 = this.getMethod0(var1, var2, true);
      if (var3 == null) {
         throw new NoSuchMethodException(this.getName() + "." + var1 + argumentTypesToString(var2));
      } else {
         return var3;
      }
   }

   @CallerSensitive
   public Constructor<T> getConstructor(Class<?>... var1) throws NoSuchMethodException, SecurityException {
      this.checkMemberAccess(0, Reflection.getCallerClass(), true);
      return this.getConstructor0(var1, 0);
   }

   @CallerSensitive
   public Class<?>[] getDeclaredClasses() throws SecurityException {
      this.checkMemberAccess(1, Reflection.getCallerClass(), false);
      return this.getDeclaredClasses0();
   }

   @CallerSensitive
   public Field[] getDeclaredFields() throws SecurityException {
      this.checkMemberAccess(1, Reflection.getCallerClass(), true);
      return copyFields(this.privateGetDeclaredFields(false));
   }

   @CallerSensitive
   public Method[] getDeclaredMethods() throws SecurityException {
      this.checkMemberAccess(1, Reflection.getCallerClass(), true);
      return copyMethods(this.privateGetDeclaredMethods(false));
   }

   @CallerSensitive
   public Constructor<?>[] getDeclaredConstructors() throws SecurityException {
      this.checkMemberAccess(1, Reflection.getCallerClass(), true);
      return copyConstructors(this.privateGetDeclaredConstructors(false));
   }

   @CallerSensitive
   public Field getDeclaredField(String var1) throws NoSuchFieldException, SecurityException {
      this.checkMemberAccess(1, Reflection.getCallerClass(), true);
      Field var2 = searchFields(this.privateGetDeclaredFields(false), var1);
      if (var2 == null) {
         throw new NoSuchFieldException(var1);
      } else {
         return var2;
      }
   }

   @CallerSensitive
   public Method getDeclaredMethod(String var1, Class<?>... var2) throws NoSuchMethodException, SecurityException {
      this.checkMemberAccess(1, Reflection.getCallerClass(), true);
      Method var3 = searchMethods(this.privateGetDeclaredMethods(false), var1, var2);
      if (var3 == null) {
         throw new NoSuchMethodException(this.getName() + "." + var1 + argumentTypesToString(var2));
      } else {
         return var3;
      }
   }

   @CallerSensitive
   public Constructor<T> getDeclaredConstructor(Class<?>... var1) throws NoSuchMethodException, SecurityException {
      this.checkMemberAccess(1, Reflection.getCallerClass(), true);
      return this.getConstructor0(var1, 1);
   }

   public InputStream getResourceAsStream(String var1) {
      var1 = this.resolveName(var1);
      ClassLoader var2 = this.getClassLoader0();
      return var2 == null ? ClassLoader.getSystemResourceAsStream(var1) : var2.getResourceAsStream(var1);
   }

   public URL getResource(String var1) {
      var1 = this.resolveName(var1);
      ClassLoader var2 = this.getClassLoader0();
      return var2 == null ? ClassLoader.getSystemResource(var1) : var2.getResource(var1);
   }

   public ProtectionDomain getProtectionDomain() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(SecurityConstants.GET_PD_PERMISSION);
      }

      ProtectionDomain var2 = this.getProtectionDomain0();
      if (var2 == null) {
         if (allPermDomain == null) {
            Permissions var3 = new Permissions();
            var3.add(SecurityConstants.ALL_PERMISSION);
            allPermDomain = new ProtectionDomain((CodeSource)null, var3);
         }

         var2 = allPermDomain;
      }

      return var2;
   }

   private native ProtectionDomain getProtectionDomain0();

   static native Class<?> getPrimitiveClass(String var0);

   private void checkMemberAccess(int var1, Class<?> var2, boolean var3) {
      SecurityManager var4 = System.getSecurityManager();
      if (var4 != null) {
         ClassLoader var5 = ClassLoader.getClassLoader(var2);
         ClassLoader var6 = this.getClassLoader0();
         if (var1 != 0 && var5 != var6) {
            var4.checkPermission(SecurityConstants.CHECK_MEMBER_ACCESS_PERMISSION);
         }

         this.checkPackageAccess(var5, var3);
      }

   }

   private void checkPackageAccess(ClassLoader var1, boolean var2) {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         ClassLoader var4 = this.getClassLoader0();
         if (ReflectUtil.needsPackageAccessCheck(var1, var4)) {
            String var5 = this.getName();
            int var6 = var5.lastIndexOf(46);
            if (var6 != -1) {
               String var7 = var5.substring(0, var6);
               if (!Proxy.isProxyClass(this) || ReflectUtil.isNonPublicProxyClass(this)) {
                  var3.checkPackageAccess(var7);
               }
            }
         }

         if (var2 && Proxy.isProxyClass(this)) {
            ReflectUtil.checkProxyPackageAccess(var1, this.getInterfaces());
         }
      }

   }

   private String resolveName(String var1) {
      if (var1 == null) {
         return var1;
      } else {
         if (!var1.startsWith("/")) {
            Class var2;
            for(var2 = this; var2.isArray(); var2 = var2.getComponentType()) {
            }

            String var3 = var2.getName();
            int var4 = var3.lastIndexOf(46);
            if (var4 != -1) {
               var1 = var3.substring(0, var4).replace('.', '/') + "/" + var1;
            }
         } else {
            var1 = var1.substring(1);
         }

         return var1;
      }
   }

   private Class.ReflectionData<T> reflectionData() {
      SoftReference var1 = this.reflectionData;
      int var2 = this.classRedefinedCount;
      Class.ReflectionData var3;
      return useCaches && var1 != null && (var3 = (Class.ReflectionData)var1.get()) != null && var3.redefinedCount == var2 ? var3 : this.newReflectionData(var1, var2);
   }

   private Class.ReflectionData<T> newReflectionData(SoftReference<Class.ReflectionData<T>> var1, int var2) {
      if (!useCaches) {
         return null;
      } else {
         Class.ReflectionData var3;
         do {
            var3 = new Class.ReflectionData(var2);
            if (Class.Atomic.casReflectionData(this, var1, new SoftReference(var3))) {
               return var3;
            }

            var1 = this.reflectionData;
            var2 = this.classRedefinedCount;
         } while(var1 == null || (var3 = (Class.ReflectionData)var1.get()) == null || var3.redefinedCount != var2);

         return var3;
      }
   }

   private native String getGenericSignature0();

   private GenericsFactory getFactory() {
      return CoreReflectionFactory.make(this, ClassScope.make(this));
   }

   private ClassRepository getGenericInfo() {
      ClassRepository var1 = this.genericInfo;
      if (var1 == null) {
         String var2 = this.getGenericSignature0();
         if (var2 == null) {
            var1 = ClassRepository.NONE;
         } else {
            var1 = ClassRepository.make(var2, this.getFactory());
         }

         this.genericInfo = var1;
      }

      return var1 != ClassRepository.NONE ? var1 : null;
   }

   native byte[] getRawAnnotations();

   native byte[] getRawTypeAnnotations();

   static byte[] getExecutableTypeAnnotationBytes(Executable var0) {
      return getReflectionFactory().getExecutableTypeAnnotationBytes(var0);
   }

   native ConstantPool getConstantPool();

   private Field[] privateGetDeclaredFields(boolean var1) {
      checkInitted();
      Class.ReflectionData var3 = this.reflectionData();
      Field[] var2;
      if (var3 != null) {
         var2 = var1 ? var3.declaredPublicFields : var3.declaredFields;
         if (var2 != null) {
            return var2;
         }
      }

      var2 = Reflection.filterFields(this, this.getDeclaredFields0(var1));
      if (var3 != null) {
         if (var1) {
            var3.declaredPublicFields = var2;
         } else {
            var3.declaredFields = var2;
         }
      }

      return var2;
   }

   private Field[] privateGetPublicFields(Set<Class<?>> var1) {
      checkInitted();
      Class.ReflectionData var3 = this.reflectionData();
      Field[] var2;
      if (var3 != null) {
         var2 = var3.publicFields;
         if (var2 != null) {
            return var2;
         }
      }

      ArrayList var4 = new ArrayList();
      if (var1 == null) {
         var1 = new HashSet();
      }

      Field[] var5 = this.privateGetDeclaredFields(true);
      addAll(var4, var5);
      Class[] var6 = this.getInterfaces();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Class var9 = var6[var8];
         if (!((Set)var1).contains(var9)) {
            ((Set)var1).add(var9);
            addAll(var4, var9.privateGetPublicFields((Set)var1));
         }
      }

      if (!this.isInterface()) {
         Class var10 = this.getSuperclass();
         if (var10 != null) {
            addAll(var4, var10.privateGetPublicFields((Set)var1));
         }
      }

      var2 = new Field[var4.size()];
      var4.toArray(var2);
      if (var3 != null) {
         var3.publicFields = var2;
      }

      return var2;
   }

   private static void addAll(Collection<Field> var0, Field[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         var0.add(var1[var2]);
      }

   }

   private Constructor<T>[] privateGetDeclaredConstructors(boolean var1) {
      checkInitted();
      Class.ReflectionData var3 = this.reflectionData();
      Constructor[] var2;
      if (var3 != null) {
         var2 = var1 ? var3.publicConstructors : var3.declaredConstructors;
         if (var2 != null) {
            return var2;
         }
      }

      if (this.isInterface()) {
         Constructor[] var4 = (Constructor[])(new Constructor[0]);
         var2 = var4;
      } else {
         var2 = this.getDeclaredConstructors0(var1);
      }

      if (var3 != null) {
         if (var1) {
            var3.publicConstructors = var2;
         } else {
            var3.declaredConstructors = var2;
         }
      }

      return var2;
   }

   private Method[] privateGetDeclaredMethods(boolean var1) {
      checkInitted();
      Class.ReflectionData var3 = this.reflectionData();
      Method[] var2;
      if (var3 != null) {
         var2 = var1 ? var3.declaredPublicMethods : var3.declaredMethods;
         if (var2 != null) {
            return var2;
         }
      }

      var2 = Reflection.filterMethods(this, this.getDeclaredMethods0(var1));
      if (var3 != null) {
         if (var1) {
            var3.declaredPublicMethods = var2;
         } else {
            var3.declaredMethods = var2;
         }
      }

      return var2;
   }

   private Method[] privateGetPublicMethods() {
      checkInitted();
      Class.ReflectionData var2 = this.reflectionData();
      Method[] var1;
      if (var2 != null) {
         var1 = var2.publicMethods;
         if (var1 != null) {
            return var1;
         }
      }

      Class.MethodArray var3 = new Class.MethodArray();
      Method[] var4 = this.privateGetDeclaredMethods(true);
      var3.addAll(var4);
      Class.MethodArray var9 = new Class.MethodArray();
      Class[] var5 = this.getInterfaces();
      int var6 = var5.length;

      int var7;
      for(var7 = 0; var7 < var6; ++var7) {
         Class var8 = var5[var7];
         var9.addInterfaceMethods(var8.privateGetPublicMethods());
      }

      if (!this.isInterface()) {
         Class var10 = this.getSuperclass();
         if (var10 != null) {
            Class.MethodArray var12 = new Class.MethodArray();
            var12.addAll(var10.privateGetPublicMethods());

            for(var7 = 0; var7 < var12.length(); ++var7) {
               Method var14 = var12.get(var7);
               if (var14 != null && !Modifier.isAbstract(var14.getModifiers()) && !var14.isDefault()) {
                  var9.removeByNameAndDescriptor(var14);
               }
            }

            var12.addAll(var9);
            var9 = var12;
         }
      }

      for(int var11 = 0; var11 < var3.length(); ++var11) {
         Method var13 = var3.get(var11);
         var9.removeByNameAndDescriptor(var13);
      }

      var3.addAllIfNotPresent(var9);
      var3.removeLessSpecifics();
      var3.compactAndTrim();
      var1 = var3.getArray();
      if (var2 != null) {
         var2.publicMethods = var1;
      }

      return var1;
   }

   private static Field searchFields(Field[] var0, String var1) {
      String var2 = var1.intern();

      for(int var3 = 0; var3 < var0.length; ++var3) {
         if (var0[var3].getName() == var2) {
            return getReflectionFactory().copyField(var0[var3]);
         }
      }

      return null;
   }

   private Field getField0(String var1) throws NoSuchFieldException {
      Field var2;
      if ((var2 = searchFields(this.privateGetDeclaredFields(true), var1)) != null) {
         return var2;
      } else {
         Class[] var3 = this.getInterfaces();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            Class var5 = var3[var4];
            if ((var2 = var5.getField0(var1)) != null) {
               return var2;
            }
         }

         if (!this.isInterface()) {
            Class var6 = this.getSuperclass();
            if (var6 != null && (var2 = var6.getField0(var1)) != null) {
               return var2;
            }
         }

         return null;
      }
   }

   private static Method searchMethods(Method[] var0, String var1, Class<?>[] var2) {
      Method var3 = null;
      String var4 = var1.intern();

      for(int var5 = 0; var5 < var0.length; ++var5) {
         Method var6 = var0[var5];
         if (var6.getName() == var4 && arrayContentsEq(var2, var6.getParameterTypes()) && (var3 == null || var3.getReturnType().isAssignableFrom(var6.getReturnType()))) {
            var3 = var6;
         }
      }

      return var3 == null ? var3 : getReflectionFactory().copyMethod(var3);
   }

   private Method getMethod0(String var1, Class<?>[] var2, boolean var3) {
      Class.MethodArray var4 = new Class.MethodArray(2);
      Method var5 = this.privateGetMethodRecursive(var1, var2, var3, var4);
      if (var5 != null) {
         return var5;
      } else {
         var4.removeLessSpecifics();
         return var4.getFirst();
      }
   }

   private Method privateGetMethodRecursive(String var1, Class<?>[] var2, boolean var3, Class.MethodArray var4) {
      Method var5;
      if ((var5 = searchMethods(this.privateGetDeclaredMethods(true), var1, var2)) == null || !var3 && Modifier.isStatic(var5.getModifiers())) {
         if (!this.isInterface()) {
            Class var6 = this.getSuperclass();
            if (var6 != null && (var5 = var6.getMethod0(var1, var2, true)) != null) {
               return var5;
            }
         }

         Class[] var11 = this.getInterfaces();
         Class[] var7 = var11;
         int var8 = var11.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            Class var10 = var7[var9];
            if ((var5 = var10.getMethod0(var1, var2, false)) != null) {
               var4.add(var5);
            }
         }

         return null;
      } else {
         return var5;
      }
   }

   private Constructor<T> getConstructor0(Class<?>[] var1, int var2) throws NoSuchMethodException {
      Constructor[] var3 = this.privateGetDeclaredConstructors(var2 == 0);
      Constructor[] var4 = var3;
      int var5 = var3.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Constructor var7 = var4[var6];
         if (arrayContentsEq(var1, var7.getParameterTypes())) {
            return getReflectionFactory().copyConstructor(var7);
         }
      }

      throw new NoSuchMethodException(this.getName() + ".<init>" + argumentTypesToString(var1));
   }

   private static boolean arrayContentsEq(Object[] var0, Object[] var1) {
      if (var0 != null) {
         if (var1 == null) {
            return var0.length == 0;
         } else if (var0.length != var1.length) {
            return false;
         } else {
            for(int var2 = 0; var2 < var0.length; ++var2) {
               if (var0[var2] != var1[var2]) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return var1 == null || var1.length == 0;
      }
   }

   private static Field[] copyFields(Field[] var0) {
      Field[] var1 = new Field[var0.length];
      ReflectionFactory var2 = getReflectionFactory();

      for(int var3 = 0; var3 < var0.length; ++var3) {
         var1[var3] = var2.copyField(var0[var3]);
      }

      return var1;
   }

   private static Method[] copyMethods(Method[] var0) {
      Method[] var1 = new Method[var0.length];
      ReflectionFactory var2 = getReflectionFactory();

      for(int var3 = 0; var3 < var0.length; ++var3) {
         var1[var3] = var2.copyMethod(var0[var3]);
      }

      return var1;
   }

   private static <U> Constructor<U>[] copyConstructors(Constructor<U>[] var0) {
      Constructor[] var1 = (Constructor[])var0.clone();
      ReflectionFactory var2 = getReflectionFactory();

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var1[var3] = var2.copyConstructor(var1[var3]);
      }

      return var1;
   }

   private native Field[] getDeclaredFields0(boolean var1);

   private native Method[] getDeclaredMethods0(boolean var1);

   private native Constructor<T>[] getDeclaredConstructors0(boolean var1);

   private native Class<?>[] getDeclaredClasses0();

   private static String argumentTypesToString(Class<?>[] var0) {
      StringBuilder var1 = new StringBuilder();
      var1.append("(");
      if (var0 != null) {
         for(int var2 = 0; var2 < var0.length; ++var2) {
            if (var2 > 0) {
               var1.append(", ");
            }

            Class var3 = var0[var2];
            var1.append(var3 == null ? "null" : var3.getName());
         }
      }

      var1.append(")");
      return var1.toString();
   }

   public boolean desiredAssertionStatus() {
      ClassLoader var1 = this.getClassLoader();
      if (var1 == null) {
         return desiredAssertionStatus0(this);
      } else {
         synchronized(var1.assertionLock) {
            if (var1.classAssertionStatus != null) {
               return var1.desiredAssertionStatus(this.getName());
            }
         }

         return desiredAssertionStatus0(this);
      }
   }

   private static native boolean desiredAssertionStatus0(Class<?> var0);

   public boolean isEnum() {
      return (this.getModifiers() & 16384) != 0 && this.getSuperclass() == Enum.class;
   }

   private static ReflectionFactory getReflectionFactory() {
      if (reflectionFactory == null) {
         reflectionFactory = (ReflectionFactory)AccessController.doPrivileged((PrivilegedAction)(new ReflectionFactory.GetReflectionFactoryAction()));
      }

      return reflectionFactory;
   }

   private static void checkInitted() {
      if (!initted) {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               if (System.out == null) {
                  return null;
               } else {
                  String var1 = System.getProperty("sun.reflect.noCaches");
                  if (var1 != null && var1.equals("true")) {
                     Class.useCaches = false;
                  }

                  Class.initted = true;
                  return null;
               }
            }
         });
      }
   }

   public T[] getEnumConstants() {
      Object[] var1 = this.getEnumConstantsShared();
      return var1 != null ? (Object[])var1.clone() : null;
   }

   T[] getEnumConstantsShared() {
      if (this.enumConstants == null) {
         if (!this.isEnum()) {
            return null;
         }

         try {
            final Method var1 = this.getMethod("values");
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
               public Void run() {
                  var1.setAccessible(true);
                  return null;
               }
            });
            Object[] var2 = (Object[])((Object[])var1.invoke((Object)null));
            this.enumConstants = var2;
         } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException var3) {
            return null;
         }
      }

      return this.enumConstants;
   }

   Map<String, T> enumConstantDirectory() {
      if (this.enumConstantDirectory == null) {
         Object[] var1 = this.getEnumConstantsShared();
         if (var1 == null) {
            throw new IllegalArgumentException(this.getName() + " is not an enum type");
         }

         HashMap var2 = new HashMap(2 * var1.length);
         Object[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Object var6 = var3[var5];
            var2.put(((Enum)var6).name(), var6);
         }

         this.enumConstantDirectory = var2;
      }

      return this.enumConstantDirectory;
   }

   public T cast(Object var1) {
      if (var1 != null && !this.isInstance(var1)) {
         throw new ClassCastException(this.cannotCastMsg(var1));
      } else {
         return var1;
      }
   }

   private String cannotCastMsg(Object var1) {
      return "Cannot cast " + var1.getClass().getName() + " to " + this.getName();
   }

   public <U> Class<? extends U> asSubclass(Class<U> var1) {
      if (var1.isAssignableFrom(this)) {
         return this;
      } else {
         throw new ClassCastException(this.toString());
      }
   }

   public <A extends Annotation> A getAnnotation(Class<A> var1) {
      Objects.requireNonNull(var1);
      return (Annotation)this.annotationData().annotations.get(var1);
   }

   public boolean isAnnotationPresent(Class<? extends Annotation> var1) {
      return GenericDeclaration.super.isAnnotationPresent(var1);
   }

   public <A extends Annotation> A[] getAnnotationsByType(Class<A> var1) {
      Objects.requireNonNull(var1);
      Class.AnnotationData var2 = this.annotationData();
      return AnnotationSupport.getAssociatedAnnotations(var2.declaredAnnotations, this, var1);
   }

   public Annotation[] getAnnotations() {
      return AnnotationParser.toArray(this.annotationData().annotations);
   }

   public <A extends Annotation> A getDeclaredAnnotation(Class<A> var1) {
      Objects.requireNonNull(var1);
      return (Annotation)this.annotationData().declaredAnnotations.get(var1);
   }

   public <A extends Annotation> A[] getDeclaredAnnotationsByType(Class<A> var1) {
      Objects.requireNonNull(var1);
      return AnnotationSupport.getDirectlyAndIndirectlyPresent(this.annotationData().declaredAnnotations, var1);
   }

   public Annotation[] getDeclaredAnnotations() {
      return AnnotationParser.toArray(this.annotationData().declaredAnnotations);
   }

   private Class.AnnotationData annotationData() {
      Class.AnnotationData var1;
      Class.AnnotationData var3;
      do {
         var1 = this.annotationData;
         int var2 = this.classRedefinedCount;
         if (var1 != null && var1.redefinedCount == var2) {
            return var1;
         }

         var3 = this.createAnnotationData(var2);
      } while(!Class.Atomic.casAnnotationData(this, var1, var3));

      return var3;
   }

   private Class.AnnotationData createAnnotationData(int var1) {
      Map var2 = AnnotationParser.parseAnnotations(this.getRawAnnotations(), this.getConstantPool(), this);
      Class var3 = this.getSuperclass();
      Object var4 = null;
      if (var3 != null) {
         Map var5 = var3.annotationData().annotations;
         Iterator var6 = var5.entrySet().iterator();

         while(var6.hasNext()) {
            Map.Entry var7 = (Map.Entry)var6.next();
            Class var8 = (Class)var7.getKey();
            if (AnnotationType.getInstance(var8).isInherited()) {
               if (var4 == null) {
                  var4 = new LinkedHashMap((Math.max(var2.size(), Math.min(12, var2.size() + var5.size())) * 4 + 2) / 3);
               }

               ((Map)var4).put(var8, var7.getValue());
            }
         }
      }

      if (var4 == null) {
         var4 = var2;
      } else {
         ((Map)var4).putAll(var2);
      }

      return new Class.AnnotationData((Map)var4, var2, var1);
   }

   boolean casAnnotationType(AnnotationType var1, AnnotationType var2) {
      return Class.Atomic.casAnnotationType(this, var1, var2);
   }

   AnnotationType getAnnotationType() {
      return this.annotationType;
   }

   Map<Class<? extends Annotation>, Annotation> getDeclaredAnnotationMap() {
      return this.annotationData().declaredAnnotations;
   }

   public AnnotatedType getAnnotatedSuperclass() {
      return this != Object.class && !this.isInterface() && !this.isArray() && !this.isPrimitive() && this != Void.TYPE ? TypeAnnotationParser.buildAnnotatedSuperclass(this.getRawTypeAnnotations(), this.getConstantPool(), this) : null;
   }

   public AnnotatedType[] getAnnotatedInterfaces() {
      return TypeAnnotationParser.buildAnnotatedInterfaces(this.getRawTypeAnnotations(), this.getConstantPool(), this);
   }

   static {
      registerNatives();
      useCaches = true;
      serialPersistentFields = new ObjectStreamField[0];
      initted = false;
   }

   private static class AnnotationData {
      final Map<Class<? extends Annotation>, Annotation> annotations;
      final Map<Class<? extends Annotation>, Annotation> declaredAnnotations;
      final int redefinedCount;

      AnnotationData(Map<Class<? extends Annotation>, Annotation> var1, Map<Class<? extends Annotation>, Annotation> var2, int var3) {
         this.annotations = var1;
         this.declaredAnnotations = var2;
         this.redefinedCount = var3;
      }
   }

   static class MethodArray {
      private Method[] methods;
      private int length;
      private int defaults;

      MethodArray() {
         this(20);
      }

      MethodArray(int var1) {
         if (var1 < 2) {
            throw new IllegalArgumentException("Size should be 2 or more");
         } else {
            this.methods = new Method[var1];
            this.length = 0;
            this.defaults = 0;
         }
      }

      boolean hasDefaults() {
         return this.defaults != 0;
      }

      void add(Method var1) {
         if (this.length == this.methods.length) {
            this.methods = (Method[])Arrays.copyOf((Object[])this.methods, 2 * this.methods.length);
         }

         this.methods[this.length++] = var1;
         if (var1 != null && var1.isDefault()) {
            ++this.defaults;
         }

      }

      void addAll(Method[] var1) {
         for(int var2 = 0; var2 < var1.length; ++var2) {
            this.add(var1[var2]);
         }

      }

      void addAll(Class.MethodArray var1) {
         for(int var2 = 0; var2 < var1.length(); ++var2) {
            this.add(var1.get(var2));
         }

      }

      void addIfNotPresent(Method var1) {
         for(int var2 = 0; var2 < this.length; ++var2) {
            Method var3 = this.methods[var2];
            if (var3 == var1 || var3 != null && var3.equals(var1)) {
               return;
            }
         }

         this.add(var1);
      }

      void addAllIfNotPresent(Class.MethodArray var1) {
         for(int var2 = 0; var2 < var1.length(); ++var2) {
            Method var3 = var1.get(var2);
            if (var3 != null) {
               this.addIfNotPresent(var3);
            }
         }

      }

      void addInterfaceMethods(Method[] var1) {
         Method[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Method var5 = var2[var4];
            if (!Modifier.isStatic(var5.getModifiers())) {
               this.add(var5);
            }
         }

      }

      int length() {
         return this.length;
      }

      Method get(int var1) {
         return this.methods[var1];
      }

      Method getFirst() {
         Method[] var1 = this.methods;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Method var4 = var1[var3];
            if (var4 != null) {
               return var4;
            }
         }

         return null;
      }

      void removeByNameAndDescriptor(Method var1) {
         for(int var2 = 0; var2 < this.length; ++var2) {
            Method var3 = this.methods[var2];
            if (var3 != null && this.matchesNameAndDescriptor(var3, var1)) {
               this.remove(var2);
            }
         }

      }

      private void remove(int var1) {
         if (this.methods[var1] != null && this.methods[var1].isDefault()) {
            --this.defaults;
         }

         this.methods[var1] = null;
      }

      private boolean matchesNameAndDescriptor(Method var1, Method var2) {
         return var1.getReturnType() == var2.getReturnType() && var1.getName() == var2.getName() && Class.arrayContentsEq(var1.getParameterTypes(), var2.getParameterTypes());
      }

      void compactAndTrim() {
         int var1 = 0;

         for(int var2 = 0; var2 < this.length; ++var2) {
            Method var3 = this.methods[var2];
            if (var3 != null) {
               if (var2 != var1) {
                  this.methods[var1] = var3;
               }

               ++var1;
            }
         }

         if (var1 != this.methods.length) {
            this.methods = (Method[])Arrays.copyOf((Object[])this.methods, var1);
         }

      }

      void removeLessSpecifics() {
         if (this.hasDefaults()) {
            for(int var1 = 0; var1 < this.length; ++var1) {
               Method var2 = this.get(var1);
               if (var2 != null && var2.isDefault()) {
                  for(int var3 = 0; var3 < this.length; ++var3) {
                     if (var1 != var3) {
                        Method var4 = this.get(var3);
                        if (var4 != null && this.matchesNameAndDescriptor(var2, var4) && hasMoreSpecificClass(var2, var4)) {
                           this.remove(var3);
                        }
                     }
                  }
               }
            }

         }
      }

      Method[] getArray() {
         return this.methods;
      }

      static boolean hasMoreSpecificClass(Method var0, Method var1) {
         Class var2 = var0.getDeclaringClass();
         Class var3 = var1.getDeclaringClass();
         return var2 != var3 && var3.isAssignableFrom(var2);
      }
   }

   private static class ReflectionData<T> {
      volatile Field[] declaredFields;
      volatile Field[] publicFields;
      volatile Method[] declaredMethods;
      volatile Method[] publicMethods;
      volatile Constructor<T>[] declaredConstructors;
      volatile Constructor<T>[] publicConstructors;
      volatile Field[] declaredPublicFields;
      volatile Method[] declaredPublicMethods;
      volatile Class<?>[] interfaces;
      final int redefinedCount;

      ReflectionData(int var1) {
         this.redefinedCount = var1;
      }
   }

   private static class Atomic {
      private static final Unsafe unsafe = Unsafe.getUnsafe();
      private static final long reflectionDataOffset;
      private static final long annotationTypeOffset;
      private static final long annotationDataOffset;

      private static long objectFieldOffset(Field[] var0, String var1) {
         Field var2 = Class.searchFields(var0, var1);
         if (var2 == null) {
            throw new Error("No " + var1 + " field found in java.lang.Class");
         } else {
            return unsafe.objectFieldOffset(var2);
         }
      }

      static <T> boolean casReflectionData(Class<?> var0, SoftReference<Class.ReflectionData<T>> var1, SoftReference<Class.ReflectionData<T>> var2) {
         return unsafe.compareAndSwapObject(var0, reflectionDataOffset, var1, var2);
      }

      static <T> boolean casAnnotationType(Class<?> var0, AnnotationType var1, AnnotationType var2) {
         return unsafe.compareAndSwapObject(var0, annotationTypeOffset, var1, var2);
      }

      static <T> boolean casAnnotationData(Class<?> var0, Class.AnnotationData var1, Class.AnnotationData var2) {
         return unsafe.compareAndSwapObject(var0, annotationDataOffset, var1, var2);
      }

      static {
         Field[] var0 = Class.class.getDeclaredFields0(false);
         reflectionDataOffset = objectFieldOffset(var0, "reflectionData");
         annotationTypeOffset = objectFieldOffset(var0, "annotationType");
         annotationDataOffset = objectFieldOffset(var0, "annotationData");
      }
   }

   private static final class EnclosingMethodInfo {
      private Class<?> enclosingClass;
      private String name;
      private String descriptor;

      private EnclosingMethodInfo(Object[] var1) {
         if (var1.length != 3) {
            throw new InternalError("Malformed enclosing method information");
         } else {
            try {
               this.enclosingClass = (Class)var1[0];

               assert this.enclosingClass != null;

               this.name = (String)var1[1];
               this.descriptor = (String)var1[2];

               assert this.name != null && this.descriptor != null || this.name == this.descriptor;

            } catch (ClassCastException var3) {
               throw new InternalError("Invalid type in enclosing method information", var3);
            }
         }
      }

      boolean isPartial() {
         return this.enclosingClass == null || this.name == null || this.descriptor == null;
      }

      boolean isConstructor() {
         return !this.isPartial() && "<init>".equals(this.name);
      }

      boolean isMethod() {
         return !this.isPartial() && !this.isConstructor() && !"<clinit>".equals(this.name);
      }

      Class<?> getEnclosingClass() {
         return this.enclosingClass;
      }

      String getName() {
         return this.name;
      }

      String getDescriptor() {
         return this.descriptor;
      }

      // $FF: synthetic method
      EnclosingMethodInfo(Object[] var1, Object var2) {
         this(var1);
      }
   }
}
