package java.io;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.misc.JavaSecurityAccess;
import sun.misc.SharedSecrets;
import sun.misc.Unsafe;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.ReflectionFactory;
import sun.reflect.misc.ReflectUtil;

public class ObjectStreamClass implements Serializable {
   public static final ObjectStreamField[] NO_FIELDS = new ObjectStreamField[0];
   private static final long serialVersionUID = -6120832682080437368L;
   private static final ObjectStreamField[] serialPersistentFields;
   private static boolean disableSerialConstructorChecks;
   private static final ReflectionFactory reflFactory;
   private Class<?> cl;
   private String name;
   private volatile Long suid;
   private boolean isProxy;
   private boolean isEnum;
   private boolean serializable;
   private boolean externalizable;
   private boolean hasWriteObjectData;
   private boolean hasBlockExternalData = true;
   private ClassNotFoundException resolveEx;
   private ObjectStreamClass.ExceptionInfo deserializeEx;
   private ObjectStreamClass.ExceptionInfo serializeEx;
   private ObjectStreamClass.ExceptionInfo defaultSerializeEx;
   private ObjectStreamField[] fields;
   private int primDataSize;
   private int numObjFields;
   private ObjectStreamClass.FieldReflector fieldRefl;
   private volatile ObjectStreamClass.ClassDataSlot[] dataLayout;
   private Constructor<?> cons;
   private ProtectionDomain[] domains;
   private Method writeObjectMethod;
   private Method readObjectMethod;
   private Method readObjectNoDataMethod;
   private Method writeReplaceMethod;
   private Method readResolveMethod;
   private ObjectStreamClass localDesc;
   private ObjectStreamClass superDesc;
   private boolean initialized;

   private static native void initNative();

   public static ObjectStreamClass lookup(Class<?> var0) {
      return lookup(var0, false);
   }

   public static ObjectStreamClass lookupAny(Class<?> var0) {
      return lookup(var0, true);
   }

   public String getName() {
      return this.name;
   }

   public long getSerialVersionUID() {
      if (this.suid == null) {
         this.suid = (Long)AccessController.doPrivileged(new PrivilegedAction<Long>() {
            public Long run() {
               return ObjectStreamClass.computeDefaultSUID(ObjectStreamClass.this.cl);
            }
         });
      }

      return this.suid;
   }

   @CallerSensitive
   public Class<?> forClass() {
      if (this.cl == null) {
         return null;
      } else {
         this.requireInitialized();
         if (System.getSecurityManager() != null) {
            Class var1 = Reflection.getCallerClass();
            if (ReflectUtil.needsPackageAccessCheck(var1.getClassLoader(), this.cl.getClassLoader())) {
               ReflectUtil.checkPackageAccess(this.cl);
            }
         }

         return this.cl;
      }
   }

   public ObjectStreamField[] getFields() {
      return this.getFields(true);
   }

   public ObjectStreamField getField(String var1) {
      return this.getField(var1, (Class)null);
   }

   public String toString() {
      return this.name + ": static final long serialVersionUID = " + this.getSerialVersionUID() + "L;";
   }

   static ObjectStreamClass lookup(Class<?> var0, boolean var1) {
      if (!var1 && !Serializable.class.isAssignableFrom(var0)) {
         return null;
      } else {
         processQueue(ObjectStreamClass.Caches.localDescsQueue, ObjectStreamClass.Caches.localDescs);
         ObjectStreamClass.WeakClassKey var2 = new ObjectStreamClass.WeakClassKey(var0, ObjectStreamClass.Caches.localDescsQueue);
         Reference var3 = (Reference)ObjectStreamClass.Caches.localDescs.get(var2);
         Object var4 = null;
         if (var3 != null) {
            var4 = var3.get();
         }

         ObjectStreamClass.EntryFuture var5 = null;
         if (var4 == null) {
            ObjectStreamClass.EntryFuture var6 = new ObjectStreamClass.EntryFuture();
            SoftReference var7 = new SoftReference(var6);

            do {
               if (var3 != null) {
                  ObjectStreamClass.Caches.localDescs.remove(var2, var3);
               }

               var3 = (Reference)ObjectStreamClass.Caches.localDescs.putIfAbsent(var2, var7);
               if (var3 != null) {
                  var4 = var3.get();
               }
            } while(var3 != null && var4 == null);

            if (var4 == null) {
               var5 = var6;
            }
         }

         if (var4 instanceof ObjectStreamClass) {
            return (ObjectStreamClass)var4;
         } else {
            if (var4 instanceof ObjectStreamClass.EntryFuture) {
               var5 = (ObjectStreamClass.EntryFuture)var4;
               if (var5.getOwner() == Thread.currentThread()) {
                  var4 = null;
               } else {
                  var4 = var5.get();
               }
            }

            if (var4 == null) {
               try {
                  var4 = new ObjectStreamClass(var0);
               } catch (Throwable var8) {
                  var4 = var8;
               }

               if (var5.set(var4)) {
                  ObjectStreamClass.Caches.localDescs.put(var2, new SoftReference(var4));
               } else {
                  var4 = var5.get();
               }
            }

            if (var4 instanceof ObjectStreamClass) {
               return (ObjectStreamClass)var4;
            } else if (var4 instanceof RuntimeException) {
               throw (RuntimeException)var4;
            } else if (var4 instanceof Error) {
               throw (Error)var4;
            } else {
               throw new InternalError("unexpected entry: " + var4);
            }
         }
      }
   }

   private ObjectStreamClass(final Class<?> var1) {
      this.cl = var1;
      this.name = var1.getName();
      this.isProxy = Proxy.isProxyClass(var1);
      this.isEnum = Enum.class.isAssignableFrom(var1);
      this.serializable = Serializable.class.isAssignableFrom(var1);
      this.externalizable = Externalizable.class.isAssignableFrom(var1);
      Class var2 = var1.getSuperclass();
      this.superDesc = var2 != null ? lookup(var2, false) : null;
      this.localDesc = this;
      if (this.serializable) {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               if (ObjectStreamClass.this.isEnum) {
                  ObjectStreamClass.this.suid = 0L;
                  ObjectStreamClass.this.fields = ObjectStreamClass.NO_FIELDS;
                  return null;
               } else if (var1.isArray()) {
                  ObjectStreamClass.this.fields = ObjectStreamClass.NO_FIELDS;
                  return null;
               } else {
                  ObjectStreamClass.this.suid = ObjectStreamClass.getDeclaredSUID(var1);

                  try {
                     ObjectStreamClass.this.fields = ObjectStreamClass.getSerialFields(var1);
                     ObjectStreamClass.this.computeFieldOffsets();
                  } catch (InvalidClassException var2) {
                     ObjectStreamClass.this.serializeEx = ObjectStreamClass.this.deserializeEx = new ObjectStreamClass.ExceptionInfo(var2.classname, var2.getMessage());
                     ObjectStreamClass.this.fields = ObjectStreamClass.NO_FIELDS;
                  }

                  if (ObjectStreamClass.this.externalizable) {
                     ObjectStreamClass.this.cons = ObjectStreamClass.getExternalizableConstructor(var1);
                  } else {
                     ObjectStreamClass.this.cons = ObjectStreamClass.getSerializableConstructor(var1);
                     ObjectStreamClass.this.writeObjectMethod = ObjectStreamClass.getPrivateMethod(var1, "writeObject", new Class[]{ObjectOutputStream.class}, Void.TYPE);
                     ObjectStreamClass.this.readObjectMethod = ObjectStreamClass.getPrivateMethod(var1, "readObject", new Class[]{ObjectInputStream.class}, Void.TYPE);
                     ObjectStreamClass.this.readObjectNoDataMethod = ObjectStreamClass.getPrivateMethod(var1, "readObjectNoData", (Class[])null, Void.TYPE);
                     ObjectStreamClass.this.hasWriteObjectData = ObjectStreamClass.this.writeObjectMethod != null;
                  }

                  ObjectStreamClass.this.domains = ObjectStreamClass.this.getProtectionDomains(ObjectStreamClass.this.cons, var1);
                  ObjectStreamClass.this.writeReplaceMethod = ObjectStreamClass.getInheritableMethod(var1, "writeReplace", (Class[])null, Object.class);
                  ObjectStreamClass.this.readResolveMethod = ObjectStreamClass.getInheritableMethod(var1, "readResolve", (Class[])null, Object.class);
                  return null;
               }
            }
         });
      } else {
         this.suid = 0L;
         this.fields = NO_FIELDS;
      }

      try {
         this.fieldRefl = getReflector(this.fields, this);
      } catch (InvalidClassException var4) {
         throw new InternalError(var4);
      }

      if (this.deserializeEx == null) {
         if (this.isEnum) {
            this.deserializeEx = new ObjectStreamClass.ExceptionInfo(this.name, "enum type");
         } else if (this.cons == null) {
            this.deserializeEx = new ObjectStreamClass.ExceptionInfo(this.name, "no valid constructor");
         }
      }

      for(int var3 = 0; var3 < this.fields.length; ++var3) {
         if (this.fields[var3].getField() == null) {
            this.defaultSerializeEx = new ObjectStreamClass.ExceptionInfo(this.name, "unmatched serializable field(s) declared");
         }
      }

      this.initialized = true;
   }

   ObjectStreamClass() {
   }

   private ProtectionDomain noPermissionsDomain() {
      Permissions var1 = new Permissions();
      var1.setReadOnly();
      return new ProtectionDomain((CodeSource)null, var1);
   }

   private ProtectionDomain[] getProtectionDomains(Constructor<?> var1, Class<?> var2) {
      ProtectionDomain[] var3 = null;
      if (var1 != null && var2.getClassLoader() != null && System.getSecurityManager() != null) {
         Class var4 = var2;
         Class var5 = var1.getDeclaringClass();
         HashSet var6 = null;

         while(var4 != var5) {
            ProtectionDomain var7 = var4.getProtectionDomain();
            if (var7 != null) {
               if (var6 == null) {
                  var6 = new HashSet();
               }

               var6.add(var7);
            }

            var4 = var4.getSuperclass();
            if (var4 == null) {
               if (var6 == null) {
                  var6 = new HashSet();
               } else {
                  var6.clear();
               }

               var6.add(this.noPermissionsDomain());
               break;
            }
         }

         if (var6 != null) {
            var3 = (ProtectionDomain[])var6.toArray(new ProtectionDomain[0]);
         }
      }

      return var3;
   }

   void initProxy(Class<?> var1, ClassNotFoundException var2, ObjectStreamClass var3) throws InvalidClassException {
      ObjectStreamClass var4 = null;
      if (var1 != null) {
         var4 = lookup(var1, true);
         if (!var4.isProxy) {
            throw new InvalidClassException("cannot bind proxy descriptor to a non-proxy class");
         }
      }

      this.cl = var1;
      this.resolveEx = var2;
      this.superDesc = var3;
      this.isProxy = true;
      this.serializable = true;
      this.suid = 0L;
      this.fields = NO_FIELDS;
      if (var4 != null) {
         this.localDesc = var4;
         this.name = this.localDesc.name;
         this.externalizable = this.localDesc.externalizable;
         this.writeReplaceMethod = this.localDesc.writeReplaceMethod;
         this.readResolveMethod = this.localDesc.readResolveMethod;
         this.deserializeEx = this.localDesc.deserializeEx;
         this.domains = this.localDesc.domains;
         this.cons = this.localDesc.cons;
      }

      this.fieldRefl = getReflector(this.fields, this.localDesc);
      this.initialized = true;
   }

   void initNonProxy(ObjectStreamClass var1, Class<?> var2, ClassNotFoundException var3, ObjectStreamClass var4) throws InvalidClassException {
      long var5 = Long.valueOf(var1.getSerialVersionUID());
      ObjectStreamClass var7 = null;
      if (var2 != null) {
         var7 = lookup(var2, true);
         if (var7.isProxy) {
            throw new InvalidClassException("cannot bind non-proxy descriptor to a proxy class");
         }

         if (var1.isEnum != var7.isEnum) {
            throw new InvalidClassException(var1.isEnum ? "cannot bind enum descriptor to a non-enum class" : "cannot bind non-enum descriptor to an enum class");
         }

         if (var1.serializable == var7.serializable && !var2.isArray() && var5 != var7.getSerialVersionUID()) {
            throw new InvalidClassException(var7.name, "local class incompatible: stream classdesc serialVersionUID = " + var5 + ", local class serialVersionUID = " + var7.getSerialVersionUID());
         }

         if (!classNamesEqual(var1.name, var7.name)) {
            throw new InvalidClassException(var7.name, "local class name incompatible with stream class name \"" + var1.name + "\"");
         }

         if (!var1.isEnum) {
            if (var1.serializable == var7.serializable && var1.externalizable != var7.externalizable) {
               throw new InvalidClassException(var7.name, "Serializable incompatible with Externalizable");
            }

            if (var1.serializable != var7.serializable || var1.externalizable != var7.externalizable || !var1.serializable && !var1.externalizable) {
               this.deserializeEx = new ObjectStreamClass.ExceptionInfo(var7.name, "class invalid for deserialization");
            }
         }
      }

      this.cl = var2;
      this.resolveEx = var3;
      this.superDesc = var4;
      this.name = var1.name;
      this.suid = var5;
      this.isProxy = false;
      this.isEnum = var1.isEnum;
      this.serializable = var1.serializable;
      this.externalizable = var1.externalizable;
      this.hasBlockExternalData = var1.hasBlockExternalData;
      this.hasWriteObjectData = var1.hasWriteObjectData;
      this.fields = var1.fields;
      this.primDataSize = var1.primDataSize;
      this.numObjFields = var1.numObjFields;
      if (var7 != null) {
         this.localDesc = var7;
         this.writeObjectMethod = this.localDesc.writeObjectMethod;
         this.readObjectMethod = this.localDesc.readObjectMethod;
         this.readObjectNoDataMethod = this.localDesc.readObjectNoDataMethod;
         this.writeReplaceMethod = this.localDesc.writeReplaceMethod;
         this.readResolveMethod = this.localDesc.readResolveMethod;
         if (this.deserializeEx == null) {
            this.deserializeEx = this.localDesc.deserializeEx;
         }

         this.domains = this.localDesc.domains;
         this.cons = this.localDesc.cons;
      }

      this.fieldRefl = getReflector(this.fields, this.localDesc);
      this.fields = this.fieldRefl.getFields();
      this.initialized = true;
   }

   void readNonProxy(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      this.name = var1.readUTF();
      this.suid = var1.readLong();
      this.isProxy = false;
      byte var2 = var1.readByte();
      this.hasWriteObjectData = (var2 & 1) != 0;
      this.hasBlockExternalData = (var2 & 8) != 0;
      this.externalizable = (var2 & 4) != 0;
      boolean var3 = (var2 & 2) != 0;
      if (this.externalizable && var3) {
         throw new InvalidClassException(this.name, "serializable and externalizable flags conflict");
      } else {
         this.serializable = this.externalizable || var3;
         this.isEnum = (var2 & 16) != 0;
         if (this.isEnum && this.suid != 0L) {
            throw new InvalidClassException(this.name, "enum descriptor has non-zero serialVersionUID: " + this.suid);
         } else {
            short var4 = var1.readShort();
            if (this.isEnum && var4 != 0) {
               throw new InvalidClassException(this.name, "enum descriptor has non-zero field count: " + var4);
            } else {
               this.fields = var4 > 0 ? new ObjectStreamField[var4] : NO_FIELDS;

               for(int var5 = 0; var5 < var4; ++var5) {
                  char var6 = (char)var1.readByte();
                  String var7 = var1.readUTF();
                  String var8 = var6 != 'L' && var6 != '[' ? new String(new char[]{var6}) : var1.readTypeString();

                  try {
                     this.fields[var5] = new ObjectStreamField(var7, var8, false);
                  } catch (RuntimeException var10) {
                     throw (IOException)(new InvalidClassException(this.name, "invalid descriptor for field " + var7)).initCause(var10);
                  }
               }

               this.computeFieldOffsets();
            }
         }
      }
   }

   void writeNonProxy(ObjectOutputStream var1) throws IOException {
      var1.writeUTF(this.name);
      var1.writeLong(this.getSerialVersionUID());
      byte var2 = 0;
      int var3;
      if (this.externalizable) {
         var2 = (byte)(var2 | 4);
         var3 = var1.getProtocolVersion();
         if (var3 != 1) {
            var2 = (byte)(var2 | 8);
         }
      } else if (this.serializable) {
         var2 = (byte)(var2 | 2);
      }

      if (this.hasWriteObjectData) {
         var2 = (byte)(var2 | 1);
      }

      if (this.isEnum) {
         var2 = (byte)(var2 | 16);
      }

      var1.writeByte(var2);
      var1.writeShort(this.fields.length);

      for(var3 = 0; var3 < this.fields.length; ++var3) {
         ObjectStreamField var4 = this.fields[var3];
         var1.writeByte(var4.getTypeCode());
         var1.writeUTF(var4.getName());
         if (!var4.isPrimitive()) {
            var1.writeTypeString(var4.getTypeString());
         }
      }

   }

   ClassNotFoundException getResolveException() {
      return this.resolveEx;
   }

   private final void requireInitialized() {
      if (!this.initialized) {
         throw new InternalError("Unexpected call when not initialized");
      }
   }

   void checkDeserialize() throws InvalidClassException {
      this.requireInitialized();
      if (this.deserializeEx != null) {
         throw this.deserializeEx.newInvalidClassException();
      }
   }

   void checkSerialize() throws InvalidClassException {
      this.requireInitialized();
      if (this.serializeEx != null) {
         throw this.serializeEx.newInvalidClassException();
      }
   }

   void checkDefaultSerialize() throws InvalidClassException {
      this.requireInitialized();
      if (this.defaultSerializeEx != null) {
         throw this.defaultSerializeEx.newInvalidClassException();
      }
   }

   ObjectStreamClass getSuperDesc() {
      this.requireInitialized();
      return this.superDesc;
   }

   ObjectStreamClass getLocalDesc() {
      this.requireInitialized();
      return this.localDesc;
   }

   ObjectStreamField[] getFields(boolean var1) {
      return var1 ? (ObjectStreamField[])this.fields.clone() : this.fields;
   }

   ObjectStreamField getField(String var1, Class<?> var2) {
      for(int var3 = 0; var3 < this.fields.length; ++var3) {
         ObjectStreamField var4 = this.fields[var3];
         if (var4.getName().equals(var1)) {
            if (var2 == null || var2 == Object.class && !var4.isPrimitive()) {
               return var4;
            }

            Class var5 = var4.getType();
            if (var5 != null && var2.isAssignableFrom(var5)) {
               return var4;
            }
         }
      }

      return null;
   }

   boolean isProxy() {
      this.requireInitialized();
      return this.isProxy;
   }

   boolean isEnum() {
      this.requireInitialized();
      return this.isEnum;
   }

   boolean isExternalizable() {
      this.requireInitialized();
      return this.externalizable;
   }

   boolean isSerializable() {
      this.requireInitialized();
      return this.serializable;
   }

   boolean hasBlockExternalData() {
      this.requireInitialized();
      return this.hasBlockExternalData;
   }

   boolean hasWriteObjectData() {
      this.requireInitialized();
      return this.hasWriteObjectData;
   }

   boolean isInstantiable() {
      this.requireInitialized();
      return this.cons != null;
   }

   boolean hasWriteObjectMethod() {
      this.requireInitialized();
      return this.writeObjectMethod != null;
   }

   boolean hasReadObjectMethod() {
      this.requireInitialized();
      return this.readObjectMethod != null;
   }

   boolean hasReadObjectNoDataMethod() {
      this.requireInitialized();
      return this.readObjectNoDataMethod != null;
   }

   boolean hasWriteReplaceMethod() {
      this.requireInitialized();
      return this.writeReplaceMethod != null;
   }

   boolean hasReadResolveMethod() {
      this.requireInitialized();
      return this.readResolveMethod != null;
   }

   Object newInstance() throws InstantiationException, InvocationTargetException, UnsupportedOperationException {
      this.requireInitialized();
      if (this.cons == null) {
         throw new UnsupportedOperationException();
      } else {
         try {
            if (this.domains != null && this.domains.length != 0) {
               JavaSecurityAccess var1 = SharedSecrets.getJavaSecurityAccess();
               PrivilegedAction var2 = () -> {
                  try {
                     return this.cons.newInstance();
                  } catch (InvocationTargetException | IllegalAccessException | InstantiationException var2) {
                     throw new UndeclaredThrowableException(var2);
                  }
               };

               try {
                  return var1.doIntersectionPrivilege(var2, AccessController.getContext(), new AccessControlContext(this.domains));
               } catch (UndeclaredThrowableException var5) {
                  Throwable var4 = var5.getCause();
                  if (var4 instanceof InstantiationException) {
                     throw (InstantiationException)var4;
                  } else if (var4 instanceof InvocationTargetException) {
                     throw (InvocationTargetException)var4;
                  } else if (var4 instanceof IllegalAccessException) {
                     throw (IllegalAccessException)var4;
                  } else {
                     throw var5;
                  }
               }
            } else {
               return this.cons.newInstance();
            }
         } catch (IllegalAccessException var6) {
            throw new InternalError(var6);
         }
      }
   }

   void invokeWriteObject(Object var1, ObjectOutputStream var2) throws IOException, UnsupportedOperationException {
      this.requireInitialized();
      if (this.writeObjectMethod != null) {
         try {
            this.writeObjectMethod.invoke(var1, var2);
         } catch (InvocationTargetException var5) {
            Throwable var4 = var5.getTargetException();
            if (var4 instanceof IOException) {
               throw (IOException)var4;
            }

            throwMiscException(var4);
         } catch (IllegalAccessException var6) {
            throw new InternalError(var6);
         }

      } else {
         throw new UnsupportedOperationException();
      }
   }

   void invokeReadObject(Object var1, ObjectInputStream var2) throws ClassNotFoundException, IOException, UnsupportedOperationException {
      this.requireInitialized();
      if (this.readObjectMethod != null) {
         try {
            this.readObjectMethod.invoke(var1, var2);
         } catch (InvocationTargetException var5) {
            Throwable var4 = var5.getTargetException();
            if (var4 instanceof ClassNotFoundException) {
               throw (ClassNotFoundException)var4;
            }

            if (var4 instanceof IOException) {
               throw (IOException)var4;
            }

            throwMiscException(var4);
         } catch (IllegalAccessException var6) {
            throw new InternalError(var6);
         }

      } else {
         throw new UnsupportedOperationException();
      }
   }

   void invokeReadObjectNoData(Object var1) throws IOException, UnsupportedOperationException {
      this.requireInitialized();
      if (this.readObjectNoDataMethod != null) {
         try {
            this.readObjectNoDataMethod.invoke(var1, (Object[])null);
         } catch (InvocationTargetException var4) {
            Throwable var3 = var4.getTargetException();
            if (var3 instanceof ObjectStreamException) {
               throw (ObjectStreamException)var3;
            }

            throwMiscException(var3);
         } catch (IllegalAccessException var5) {
            throw new InternalError(var5);
         }

      } else {
         throw new UnsupportedOperationException();
      }
   }

   Object invokeWriteReplace(Object var1) throws IOException, UnsupportedOperationException {
      this.requireInitialized();
      if (this.writeReplaceMethod != null) {
         try {
            return this.writeReplaceMethod.invoke(var1, (Object[])null);
         } catch (InvocationTargetException var4) {
            Throwable var3 = var4.getTargetException();
            if (var3 instanceof ObjectStreamException) {
               throw (ObjectStreamException)var3;
            } else {
               throwMiscException(var3);
               throw new InternalError(var3);
            }
         } catch (IllegalAccessException var5) {
            throw new InternalError(var5);
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }

   Object invokeReadResolve(Object var1) throws IOException, UnsupportedOperationException {
      this.requireInitialized();
      if (this.readResolveMethod != null) {
         try {
            return this.readResolveMethod.invoke(var1, (Object[])null);
         } catch (InvocationTargetException var4) {
            Throwable var3 = var4.getTargetException();
            if (var3 instanceof ObjectStreamException) {
               throw (ObjectStreamException)var3;
            } else {
               throwMiscException(var3);
               throw new InternalError(var3);
            }
         } catch (IllegalAccessException var5) {
            throw new InternalError(var5);
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }

   ObjectStreamClass.ClassDataSlot[] getClassDataLayout() throws InvalidClassException {
      if (this.dataLayout == null) {
         this.dataLayout = this.getClassDataLayout0();
      }

      return this.dataLayout;
   }

   private ObjectStreamClass.ClassDataSlot[] getClassDataLayout0() throws InvalidClassException {
      ArrayList var1 = new ArrayList();
      Class var2 = this.cl;

      Class var3;
      for(var3 = this.cl; var3 != null && Serializable.class.isAssignableFrom(var3); var3 = var3.getSuperclass()) {
      }

      HashSet var4 = new HashSet(3);

      for(ObjectStreamClass var5 = this; var5 != null; var5 = var5.superDesc) {
         if (var4.contains(var5.name)) {
            throw new InvalidClassException("Circular reference.");
         }

         var4.add(var5.name);
         String var6 = var5.cl != null ? var5.cl.getName() : var5.name;
         Class var7 = null;

         Class var8;
         for(var8 = var2; var8 != var3; var8 = var8.getSuperclass()) {
            if (var6.equals(var8.getName())) {
               var7 = var8;
               break;
            }
         }

         if (var7 != null) {
            for(var8 = var2; var8 != var7; var8 = var8.getSuperclass()) {
               var1.add(new ObjectStreamClass.ClassDataSlot(lookup(var8, true), false));
            }

            var2 = var7.getSuperclass();
         }

         var1.add(new ObjectStreamClass.ClassDataSlot(var5.getVariantFor(var7), true));
      }

      for(Class var9 = var2; var9 != var3; var9 = var9.getSuperclass()) {
         var1.add(new ObjectStreamClass.ClassDataSlot(lookup(var9, true), false));
      }

      Collections.reverse(var1);
      return (ObjectStreamClass.ClassDataSlot[])var1.toArray(new ObjectStreamClass.ClassDataSlot[var1.size()]);
   }

   int getPrimDataSize() {
      return this.primDataSize;
   }

   int getNumObjFields() {
      return this.numObjFields;
   }

   void getPrimFieldValues(Object var1, byte[] var2) {
      this.fieldRefl.getPrimFieldValues(var1, var2);
   }

   void setPrimFieldValues(Object var1, byte[] var2) {
      this.fieldRefl.setPrimFieldValues(var1, var2);
   }

   void getObjFieldValues(Object var1, Object[] var2) {
      this.fieldRefl.getObjFieldValues(var1, var2);
   }

   void setObjFieldValues(Object var1, Object[] var2) {
      this.fieldRefl.setObjFieldValues(var1, var2);
   }

   private void computeFieldOffsets() throws InvalidClassException {
      this.primDataSize = 0;
      this.numObjFields = 0;
      int var1 = -1;

      for(int var2 = 0; var2 < this.fields.length; ++var2) {
         ObjectStreamField var3 = this.fields[var2];
         switch(var3.getTypeCode()) {
         case 'B':
         case 'Z':
            var3.setOffset(this.primDataSize++);
            break;
         case 'C':
         case 'S':
            var3.setOffset(this.primDataSize);
            this.primDataSize += 2;
            break;
         case 'D':
         case 'J':
            var3.setOffset(this.primDataSize);
            this.primDataSize += 8;
            break;
         case 'E':
         case 'G':
         case 'H':
         case 'K':
         case 'M':
         case 'N':
         case 'O':
         case 'P':
         case 'Q':
         case 'R':
         case 'T':
         case 'U':
         case 'V':
         case 'W':
         case 'X':
         case 'Y':
         default:
            throw new InternalError();
         case 'F':
         case 'I':
            var3.setOffset(this.primDataSize);
            this.primDataSize += 4;
            break;
         case 'L':
         case '[':
            var3.setOffset(this.numObjFields++);
            if (var1 == -1) {
               var1 = var2;
            }
         }
      }

      if (var1 != -1 && var1 + this.numObjFields != this.fields.length) {
         throw new InvalidClassException(this.name, "illegal field order");
      }
   }

   private ObjectStreamClass getVariantFor(Class<?> var1) throws InvalidClassException {
      if (this.cl == var1) {
         return this;
      } else {
         ObjectStreamClass var2 = new ObjectStreamClass();
         if (this.isProxy) {
            var2.initProxy(var1, (ClassNotFoundException)null, this.superDesc);
         } else {
            var2.initNonProxy(this, var1, (ClassNotFoundException)null, this.superDesc);
         }

         return var2;
      }
   }

   private static Constructor<?> getExternalizableConstructor(Class<?> var0) {
      try {
         Constructor var1 = var0.getDeclaredConstructor((Class[])null);
         var1.setAccessible(true);
         return (var1.getModifiers() & 1) != 0 ? var1 : null;
      } catch (NoSuchMethodException var2) {
         return null;
      }
   }

   private static boolean superHasAccessibleConstructor(Class<?> var0) {
      Class var1 = var0.getSuperclass();

      assert Serializable.class.isAssignableFrom(var0);

      assert var1 != null;

      Constructor[] var2;
      int var3;
      int var4;
      Constructor var5;
      if (packageEquals(var0, var1)) {
         var2 = var1.getDeclaredConstructors();
         var3 = var2.length;

         for(var4 = 0; var4 < var3; ++var4) {
            var5 = var2[var4];
            if ((var5.getModifiers() & 2) == 0) {
               return true;
            }
         }

         return false;
      } else if ((var1.getModifiers() & 5) == 0) {
         return false;
      } else {
         var2 = var1.getDeclaredConstructors();
         var3 = var2.length;

         for(var4 = 0; var4 < var3; ++var4) {
            var5 = var2[var4];
            if ((var5.getModifiers() & 5) != 0) {
               return true;
            }
         }

         return false;
      }
   }

   private static Constructor<?> getSerializableConstructor(Class<?> var0) {
      Class var1 = var0;

      Class var2;
      do {
         if (!Serializable.class.isAssignableFrom(var1)) {
            try {
               Constructor var5 = var1.getDeclaredConstructor((Class[])null);
               int var3 = var5.getModifiers();
               if ((var3 & 2) == 0 && ((var3 & 5) != 0 || packageEquals(var0, var1))) {
                  var5 = reflFactory.newConstructorForSerialization(var0, var5);
                  var5.setAccessible(true);
                  return var5;
               }

               return null;
            } catch (NoSuchMethodException var4) {
               return null;
            }
         }

         var2 = var1;
      } while((var1 = var1.getSuperclass()) != null && (disableSerialConstructorChecks || superHasAccessibleConstructor(var2)));

      return null;
   }

   private static Method getInheritableMethod(Class<?> var0, String var1, Class<?>[] var2, Class<?> var3) {
      Method var4 = null;
      Class var5 = var0;

      while(var5 != null) {
         try {
            var4 = var5.getDeclaredMethod(var1, var2);
            break;
         } catch (NoSuchMethodException var7) {
            var5 = var5.getSuperclass();
         }
      }

      if (var4 != null && var4.getReturnType() == var3) {
         var4.setAccessible(true);
         int var6 = var4.getModifiers();
         if ((var6 & 1032) != 0) {
            return null;
         } else if ((var6 & 5) != 0) {
            return var4;
         } else if ((var6 & 2) != 0) {
            return var0 == var5 ? var4 : null;
         } else {
            return packageEquals(var0, var5) ? var4 : null;
         }
      } else {
         return null;
      }
   }

   private static Method getPrivateMethod(Class<?> var0, String var1, Class<?>[] var2, Class<?> var3) {
      try {
         Method var4 = var0.getDeclaredMethod(var1, var2);
         var4.setAccessible(true);
         int var5 = var4.getModifiers();
         return var4.getReturnType() == var3 && (var5 & 8) == 0 && (var5 & 2) != 0 ? var4 : null;
      } catch (NoSuchMethodException var6) {
         return null;
      }
   }

   private static boolean packageEquals(Class<?> var0, Class<?> var1) {
      return var0.getClassLoader() == var1.getClassLoader() && getPackageName(var0).equals(getPackageName(var1));
   }

   private static String getPackageName(Class<?> var0) {
      String var1 = var0.getName();
      int var2 = var1.lastIndexOf(91);
      if (var2 >= 0) {
         var1 = var1.substring(var2 + 2);
      }

      var2 = var1.lastIndexOf(46);
      return var2 >= 0 ? var1.substring(0, var2) : "";
   }

   private static boolean classNamesEqual(String var0, String var1) {
      var0 = var0.substring(var0.lastIndexOf(46) + 1);
      var1 = var1.substring(var1.lastIndexOf(46) + 1);
      return var0.equals(var1);
   }

   private static String getClassSignature(Class<?> var0) {
      StringBuilder var1;
      for(var1 = new StringBuilder(); var0.isArray(); var0 = var0.getComponentType()) {
         var1.append('[');
      }

      if (var0.isPrimitive()) {
         if (var0 == Integer.TYPE) {
            var1.append('I');
         } else if (var0 == Byte.TYPE) {
            var1.append('B');
         } else if (var0 == Long.TYPE) {
            var1.append('J');
         } else if (var0 == Float.TYPE) {
            var1.append('F');
         } else if (var0 == Double.TYPE) {
            var1.append('D');
         } else if (var0 == Short.TYPE) {
            var1.append('S');
         } else if (var0 == Character.TYPE) {
            var1.append('C');
         } else if (var0 == Boolean.TYPE) {
            var1.append('Z');
         } else {
            if (var0 != Void.TYPE) {
               throw new InternalError();
            }

            var1.append('V');
         }
      } else {
         var1.append('L' + var0.getName().replace('.', '/') + ';');
      }

      return var1.toString();
   }

   private static String getMethodSignature(Class<?>[] var0, Class<?> var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append('(');

      for(int var3 = 0; var3 < var0.length; ++var3) {
         var2.append(getClassSignature(var0[var3]));
      }

      var2.append(')');
      var2.append(getClassSignature(var1));
      return var2.toString();
   }

   private static void throwMiscException(Throwable var0) throws IOException {
      if (var0 instanceof RuntimeException) {
         throw (RuntimeException)var0;
      } else if (var0 instanceof Error) {
         throw (Error)var0;
      } else {
         IOException var1 = new IOException("unexpected exception type");
         var1.initCause(var0);
         throw var1;
      }
   }

   private static ObjectStreamField[] getSerialFields(Class<?> var0) throws InvalidClassException {
      ObjectStreamField[] var1;
      if (Serializable.class.isAssignableFrom(var0) && !Externalizable.class.isAssignableFrom(var0) && !Proxy.isProxyClass(var0) && !var0.isInterface()) {
         if ((var1 = getDeclaredSerialFields(var0)) == null) {
            var1 = getDefaultSerialFields(var0);
         }

         Arrays.sort((Object[])var1);
      } else {
         var1 = NO_FIELDS;
      }

      return var1;
   }

   private static ObjectStreamField[] getDeclaredSerialFields(Class<?> var0) throws InvalidClassException {
      ObjectStreamField[] var1 = null;

      try {
         Field var2 = var0.getDeclaredField("serialPersistentFields");
         byte var3 = 26;
         if ((var2.getModifiers() & var3) == var3) {
            var2.setAccessible(true);
            var1 = (ObjectStreamField[])((ObjectStreamField[])var2.get((Object)null));
         }
      } catch (Exception var9) {
      }

      if (var1 == null) {
         return null;
      } else if (var1.length == 0) {
         return NO_FIELDS;
      } else {
         ObjectStreamField[] var10 = new ObjectStreamField[var1.length];
         HashSet var11 = new HashSet(var1.length);

         for(int var4 = 0; var4 < var1.length; ++var4) {
            ObjectStreamField var5 = var1[var4];
            String var6 = var5.getName();
            if (var11.contains(var6)) {
               throw new InvalidClassException("multiple serializable fields named " + var6);
            }

            var11.add(var6);

            try {
               Field var7 = var0.getDeclaredField(var6);
               if (var7.getType() == var5.getType() && (var7.getModifiers() & 8) == 0) {
                  var10[var4] = new ObjectStreamField(var7, var5.isUnshared(), true);
               }
            } catch (NoSuchFieldException var8) {
            }

            if (var10[var4] == null) {
               var10[var4] = new ObjectStreamField(var6, var5.getType(), var5.isUnshared());
            }
         }

         return var10;
      }
   }

   private static ObjectStreamField[] getDefaultSerialFields(Class<?> var0) {
      Field[] var1 = var0.getDeclaredFields();
      ArrayList var2 = new ArrayList();
      short var3 = 136;

      int var4;
      for(var4 = 0; var4 < var1.length; ++var4) {
         if ((var1[var4].getModifiers() & var3) == 0) {
            var2.add(new ObjectStreamField(var1[var4], false, true));
         }
      }

      var4 = var2.size();
      return var4 == 0 ? NO_FIELDS : (ObjectStreamField[])var2.toArray(new ObjectStreamField[var4]);
   }

   private static Long getDeclaredSUID(Class<?> var0) {
      try {
         Field var1 = var0.getDeclaredField("serialVersionUID");
         byte var2 = 24;
         if ((var1.getModifiers() & var2) == var2) {
            var1.setAccessible(true);
            return var1.getLong((Object)null);
         }
      } catch (Exception var3) {
      }

      return null;
   }

   private static long computeDefaultSUID(Class<?> var0) {
      if (Serializable.class.isAssignableFrom(var0) && !Proxy.isProxyClass(var0)) {
         try {
            ByteArrayOutputStream var1 = new ByteArrayOutputStream();
            DataOutputStream var2 = new DataOutputStream(var1);
            var2.writeUTF(var0.getName());
            int var3 = var0.getModifiers() & 1553;
            Method[] var4 = var0.getDeclaredMethods();
            if ((var3 & 512) != 0) {
               var3 = var4.length > 0 ? var3 | 1024 : var3 & -1025;
            }

            var2.writeInt(var3);
            int var7;
            if (!var0.isArray()) {
               Class[] var5 = var0.getInterfaces();
               String[] var6 = new String[var5.length];

               for(var7 = 0; var7 < var5.length; ++var7) {
                  var6[var7] = var5[var7].getName();
               }

               Arrays.sort((Object[])var6);

               for(var7 = 0; var7 < var6.length; ++var7) {
                  var2.writeUTF(var6[var7]);
               }
            }

            Field[] var17 = var0.getDeclaredFields();
            ObjectStreamClass.MemberSignature[] var18 = new ObjectStreamClass.MemberSignature[var17.length];

            for(var7 = 0; var7 < var17.length; ++var7) {
               var18[var7] = new ObjectStreamClass.MemberSignature(var17[var7]);
            }

            Arrays.sort(var18, new Comparator<ObjectStreamClass.MemberSignature>() {
               public int compare(ObjectStreamClass.MemberSignature var1, ObjectStreamClass.MemberSignature var2) {
                  return var1.name.compareTo(var2.name);
               }
            });

            int var9;
            for(var7 = 0; var7 < var18.length; ++var7) {
               ObjectStreamClass.MemberSignature var8 = var18[var7];
               var9 = var8.member.getModifiers() & 223;
               if ((var9 & 2) == 0 || (var9 & 136) == 0) {
                  var2.writeUTF(var8.name);
                  var2.writeInt(var9);
                  var2.writeUTF(var8.signature);
               }
            }

            if (hasStaticInitializer(var0)) {
               var2.writeUTF("<clinit>");
               var2.writeInt(8);
               var2.writeUTF("()V");
            }

            Constructor[] var20 = var0.getDeclaredConstructors();
            ObjectStreamClass.MemberSignature[] var19 = new ObjectStreamClass.MemberSignature[var20.length];

            for(var9 = 0; var9 < var20.length; ++var9) {
               var19[var9] = new ObjectStreamClass.MemberSignature(var20[var9]);
            }

            Arrays.sort(var19, new Comparator<ObjectStreamClass.MemberSignature>() {
               public int compare(ObjectStreamClass.MemberSignature var1, ObjectStreamClass.MemberSignature var2) {
                  return var1.signature.compareTo(var2.signature);
               }
            });

            for(var9 = 0; var9 < var19.length; ++var9) {
               ObjectStreamClass.MemberSignature var10 = var19[var9];
               int var11 = var10.member.getModifiers() & 3391;
               if ((var11 & 2) == 0) {
                  var2.writeUTF("<init>");
                  var2.writeInt(var11);
                  var2.writeUTF(var10.signature.replace('/', '.'));
               }
            }

            ObjectStreamClass.MemberSignature[] var22 = new ObjectStreamClass.MemberSignature[var4.length];

            int var21;
            for(var21 = 0; var21 < var4.length; ++var21) {
               var22[var21] = new ObjectStreamClass.MemberSignature(var4[var21]);
            }

            Arrays.sort(var22, new Comparator<ObjectStreamClass.MemberSignature>() {
               public int compare(ObjectStreamClass.MemberSignature var1, ObjectStreamClass.MemberSignature var2) {
                  int var3 = var1.name.compareTo(var2.name);
                  if (var3 == 0) {
                     var3 = var1.signature.compareTo(var2.signature);
                  }

                  return var3;
               }
            });

            for(var21 = 0; var21 < var22.length; ++var21) {
               ObjectStreamClass.MemberSignature var23 = var22[var21];
               int var12 = var23.member.getModifiers() & 3391;
               if ((var12 & 2) == 0) {
                  var2.writeUTF(var23.name);
                  var2.writeInt(var12);
                  var2.writeUTF(var23.signature.replace('/', '.'));
               }
            }

            var2.flush();
            MessageDigest var24 = MessageDigest.getInstance("SHA");
            byte[] var25 = var24.digest(var1.toByteArray());
            long var26 = 0L;

            for(int var14 = Math.min(var25.length, 8) - 1; var14 >= 0; --var14) {
               var26 = var26 << 8 | (long)(var25[var14] & 255);
            }

            return var26;
         } catch (IOException var15) {
            throw new InternalError(var15);
         } catch (NoSuchAlgorithmException var16) {
            throw new SecurityException(var16.getMessage());
         }
      } else {
         return 0L;
      }
   }

   private static native boolean hasStaticInitializer(Class<?> var0);

   private static ObjectStreamClass.FieldReflector getReflector(ObjectStreamField[] var0, ObjectStreamClass var1) throws InvalidClassException {
      Class var2 = var1 != null && var0.length > 0 ? var1.cl : null;
      processQueue(ObjectStreamClass.Caches.reflectorsQueue, ObjectStreamClass.Caches.reflectors);
      ObjectStreamClass.FieldReflectorKey var3 = new ObjectStreamClass.FieldReflectorKey(var2, var0, ObjectStreamClass.Caches.reflectorsQueue);
      Reference var4 = (Reference)ObjectStreamClass.Caches.reflectors.get(var3);
      Object var5 = null;
      if (var4 != null) {
         var5 = var4.get();
      }

      ObjectStreamClass.EntryFuture var6 = null;
      if (var5 == null) {
         ObjectStreamClass.EntryFuture var7 = new ObjectStreamClass.EntryFuture();
         SoftReference var8 = new SoftReference(var7);

         do {
            if (var4 != null) {
               ObjectStreamClass.Caches.reflectors.remove(var3, var4);
            }

            var4 = (Reference)ObjectStreamClass.Caches.reflectors.putIfAbsent(var3, var8);
            if (var4 != null) {
               var5 = var4.get();
            }
         } while(var4 != null && var5 == null);

         if (var5 == null) {
            var6 = var7;
         }
      }

      if (var5 instanceof ObjectStreamClass.FieldReflector) {
         return (ObjectStreamClass.FieldReflector)var5;
      } else {
         if (var5 instanceof ObjectStreamClass.EntryFuture) {
            var5 = ((ObjectStreamClass.EntryFuture)var5).get();
         } else if (var5 == null) {
            try {
               var5 = new ObjectStreamClass.FieldReflector(matchFields(var0, var1));
            } catch (Throwable var9) {
               var5 = var9;
            }

            var6.set(var5);
            ObjectStreamClass.Caches.reflectors.put(var3, new SoftReference(var5));
         }

         if (var5 instanceof ObjectStreamClass.FieldReflector) {
            return (ObjectStreamClass.FieldReflector)var5;
         } else if (var5 instanceof InvalidClassException) {
            throw (InvalidClassException)var5;
         } else if (var5 instanceof RuntimeException) {
            throw (RuntimeException)var5;
         } else if (var5 instanceof Error) {
            throw (Error)var5;
         } else {
            throw new InternalError("unexpected entry: " + var5);
         }
      }
   }

   private static ObjectStreamField[] matchFields(ObjectStreamField[] var0, ObjectStreamClass var1) throws InvalidClassException {
      ObjectStreamField[] var2 = var1 != null ? var1.fields : NO_FIELDS;
      ObjectStreamField[] var3 = new ObjectStreamField[var0.length];

      for(int var4 = 0; var4 < var0.length; ++var4) {
         ObjectStreamField var5 = var0[var4];
         ObjectStreamField var6 = null;

         for(int var7 = 0; var7 < var2.length; ++var7) {
            ObjectStreamField var8 = var2[var7];
            if (var5.getName().equals(var8.getName())) {
               if ((var5.isPrimitive() || var8.isPrimitive()) && var5.getTypeCode() != var8.getTypeCode()) {
                  throw new InvalidClassException(var1.name, "incompatible types for field " + var5.getName());
               }

               if (var8.getField() != null) {
                  var6 = new ObjectStreamField(var8.getField(), var8.isUnshared(), false);
               } else {
                  var6 = new ObjectStreamField(var8.getName(), var8.getSignature(), var8.isUnshared());
               }
            }
         }

         if (var6 == null) {
            var6 = new ObjectStreamField(var5.getName(), var5.getSignature(), false);
         }

         var6.setOffset(var5.getOffset());
         var3[var4] = var6;
      }

      return var3;
   }

   static void processQueue(ReferenceQueue<Class<?>> var0, ConcurrentMap<? extends WeakReference<Class<?>>, ?> var1) {
      Reference var2;
      while((var2 = var0.poll()) != null) {
         var1.remove(var2);
      }

   }

   static {
      serialPersistentFields = NO_FIELDS;
      disableSerialConstructorChecks = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            String var1 = "jdk.disableSerialConstructorChecks";
            return "true".equals(System.getProperty(var1)) ? Boolean.TRUE : Boolean.FALSE;
         }
      });
      reflFactory = (ReflectionFactory)AccessController.doPrivileged((PrivilegedAction)(new ReflectionFactory.GetReflectionFactoryAction()));
      initNative();
   }

   static class WeakClassKey extends WeakReference<Class<?>> {
      private final int hash;

      WeakClassKey(Class<?> var1, ReferenceQueue<Class<?>> var2) {
         super(var1, var2);
         this.hash = System.identityHashCode(var1);
      }

      public int hashCode() {
         return this.hash;
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof ObjectStreamClass.WeakClassKey)) {
            return false;
         } else {
            Object var2 = this.get();
            return var2 != null && var2 == ((ObjectStreamClass.WeakClassKey)var1).get();
         }
      }
   }

   private static class FieldReflectorKey extends WeakReference<Class<?>> {
      private final String sigs;
      private final int hash;
      private final boolean nullClass;

      FieldReflectorKey(Class<?> var1, ObjectStreamField[] var2, ReferenceQueue<Class<?>> var3) {
         super(var1, var3);
         this.nullClass = var1 == null;
         StringBuilder var4 = new StringBuilder();

         for(int var5 = 0; var5 < var2.length; ++var5) {
            ObjectStreamField var6 = var2[var5];
            var4.append(var6.getName()).append(var6.getSignature());
         }

         this.sigs = var4.toString();
         this.hash = System.identityHashCode(var1) + this.sigs.hashCode();
      }

      public int hashCode() {
         return this.hash;
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof ObjectStreamClass.FieldReflectorKey)) {
            return false;
         } else {
            boolean var10000;
            label26: {
               ObjectStreamClass.FieldReflectorKey var2 = (ObjectStreamClass.FieldReflectorKey)var1;
               if (this.nullClass) {
                  if (!var2.nullClass) {
                     break label26;
                  }
               } else {
                  Class var3;
                  if ((var3 = (Class)this.get()) == null || var3 != var2.get()) {
                     break label26;
                  }
               }

               if (this.sigs.equals(var2.sigs)) {
                  var10000 = true;
                  return var10000;
               }
            }

            var10000 = false;
            return var10000;
         }
      }
   }

   private static class FieldReflector {
      private static final Unsafe unsafe = Unsafe.getUnsafe();
      private final ObjectStreamField[] fields;
      private final int numPrimFields;
      private final long[] readKeys;
      private final long[] writeKeys;
      private final int[] offsets;
      private final char[] typeCodes;
      private final Class<?>[] types;

      FieldReflector(ObjectStreamField[] var1) {
         this.fields = var1;
         int var2 = var1.length;
         this.readKeys = new long[var2];
         this.writeKeys = new long[var2];
         this.offsets = new int[var2];
         this.typeCodes = new char[var2];
         ArrayList var3 = new ArrayList();
         HashSet var4 = new HashSet();

         for(int var5 = 0; var5 < var2; ++var5) {
            ObjectStreamField var6 = var1[var5];
            Field var7 = var6.getField();
            long var8 = var7 != null ? unsafe.objectFieldOffset(var7) : -1L;
            this.readKeys[var5] = var8;
            this.writeKeys[var5] = var4.add(var8) ? var8 : -1L;
            this.offsets[var5] = var6.getOffset();
            this.typeCodes[var5] = var6.getTypeCode();
            if (!var6.isPrimitive()) {
               var3.add(var7 != null ? var7.getType() : null);
            }
         }

         this.types = (Class[])var3.toArray(new Class[var3.size()]);
         this.numPrimFields = var2 - this.types.length;
      }

      ObjectStreamField[] getFields() {
         return this.fields;
      }

      void getPrimFieldValues(Object var1, byte[] var2) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            for(int var3 = 0; var3 < this.numPrimFields; ++var3) {
               long var4 = this.readKeys[var3];
               int var6 = this.offsets[var3];
               switch(this.typeCodes[var3]) {
               case 'B':
                  var2[var6] = unsafe.getByte(var1, var4);
                  break;
               case 'C':
                  Bits.putChar(var2, var6, unsafe.getChar(var1, var4));
                  break;
               case 'D':
                  Bits.putDouble(var2, var6, unsafe.getDouble(var1, var4));
                  break;
               case 'E':
               case 'G':
               case 'H':
               case 'K':
               case 'L':
               case 'M':
               case 'N':
               case 'O':
               case 'P':
               case 'Q':
               case 'R':
               case 'T':
               case 'U':
               case 'V':
               case 'W':
               case 'X':
               case 'Y':
               default:
                  throw new InternalError();
               case 'F':
                  Bits.putFloat(var2, var6, unsafe.getFloat(var1, var4));
                  break;
               case 'I':
                  Bits.putInt(var2, var6, unsafe.getInt(var1, var4));
                  break;
               case 'J':
                  Bits.putLong(var2, var6, unsafe.getLong(var1, var4));
                  break;
               case 'S':
                  Bits.putShort(var2, var6, unsafe.getShort(var1, var4));
                  break;
               case 'Z':
                  Bits.putBoolean(var2, var6, unsafe.getBoolean(var1, var4));
               }
            }

         }
      }

      void setPrimFieldValues(Object var1, byte[] var2) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            for(int var3 = 0; var3 < this.numPrimFields; ++var3) {
               long var4 = this.writeKeys[var3];
               if (var4 != -1L) {
                  int var6 = this.offsets[var3];
                  switch(this.typeCodes[var3]) {
                  case 'B':
                     unsafe.putByte(var1, var4, var2[var6]);
                     break;
                  case 'C':
                     unsafe.putChar(var1, var4, Bits.getChar(var2, var6));
                     break;
                  case 'D':
                     unsafe.putDouble(var1, var4, Bits.getDouble(var2, var6));
                     break;
                  case 'E':
                  case 'G':
                  case 'H':
                  case 'K':
                  case 'L':
                  case 'M':
                  case 'N':
                  case 'O':
                  case 'P':
                  case 'Q':
                  case 'R':
                  case 'T':
                  case 'U':
                  case 'V':
                  case 'W':
                  case 'X':
                  case 'Y':
                  default:
                     throw new InternalError();
                  case 'F':
                     unsafe.putFloat(var1, var4, Bits.getFloat(var2, var6));
                     break;
                  case 'I':
                     unsafe.putInt(var1, var4, Bits.getInt(var2, var6));
                     break;
                  case 'J':
                     unsafe.putLong(var1, var4, Bits.getLong(var2, var6));
                     break;
                  case 'S':
                     unsafe.putShort(var1, var4, Bits.getShort(var2, var6));
                     break;
                  case 'Z':
                     unsafe.putBoolean(var1, var4, Bits.getBoolean(var2, var6));
                  }
               }
            }

         }
      }

      void getObjFieldValues(Object var1, Object[] var2) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            int var3 = this.numPrimFields;

            while(var3 < this.fields.length) {
               switch(this.typeCodes[var3]) {
               case 'L':
               case '[':
                  var2[this.offsets[var3]] = unsafe.getObject(var1, this.readKeys[var3]);
                  ++var3;
                  break;
               default:
                  throw new InternalError();
               }
            }

         }
      }

      void setObjFieldValues(Object var1, Object[] var2) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            for(int var3 = this.numPrimFields; var3 < this.fields.length; ++var3) {
               long var4 = this.writeKeys[var3];
               if (var4 != -1L) {
                  switch(this.typeCodes[var3]) {
                  case 'L':
                  case '[':
                     Object var6 = var2[this.offsets[var3]];
                     if (var6 != null && !this.types[var3 - this.numPrimFields].isInstance(var6)) {
                        Field var7 = this.fields[var3].getField();
                        throw new ClassCastException("cannot assign instance of " + var6.getClass().getName() + " to field " + var7.getDeclaringClass().getName() + "." + var7.getName() + " of type " + var7.getType().getName() + " in instance of " + var1.getClass().getName());
                     }

                     unsafe.putObject(var1, var4, var6);
                     break;
                  default:
                     throw new InternalError();
                  }
               }
            }

         }
      }
   }

   private static class MemberSignature {
      public final Member member;
      public final String name;
      public final String signature;

      public MemberSignature(Field var1) {
         this.member = var1;
         this.name = var1.getName();
         this.signature = ObjectStreamClass.getClassSignature(var1.getType());
      }

      public MemberSignature(Constructor<?> var1) {
         this.member = var1;
         this.name = var1.getName();
         this.signature = ObjectStreamClass.getMethodSignature(var1.getParameterTypes(), Void.TYPE);
      }

      public MemberSignature(Method var1) {
         this.member = var1;
         this.name = var1.getName();
         this.signature = ObjectStreamClass.getMethodSignature(var1.getParameterTypes(), var1.getReturnType());
      }
   }

   static class ClassDataSlot {
      final ObjectStreamClass desc;
      final boolean hasData;

      ClassDataSlot(ObjectStreamClass var1, boolean var2) {
         this.desc = var1;
         this.hasData = var2;
      }
   }

   private static class EntryFuture {
      private static final Object unset = new Object();
      private final Thread owner;
      private Object entry;

      private EntryFuture() {
         this.owner = Thread.currentThread();
         this.entry = unset;
      }

      synchronized boolean set(Object var1) {
         if (this.entry != unset) {
            return false;
         } else {
            this.entry = var1;
            this.notifyAll();
            return true;
         }
      }

      synchronized Object get() {
         boolean var1 = false;

         while(this.entry == unset) {
            try {
               this.wait();
            } catch (InterruptedException var3) {
               var1 = true;
            }
         }

         if (var1) {
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
               public Void run() {
                  Thread.currentThread().interrupt();
                  return null;
               }
            });
         }

         return this.entry;
      }

      Thread getOwner() {
         return this.owner;
      }

      // $FF: synthetic method
      EntryFuture(Object var1) {
         this();
      }
   }

   private static class ExceptionInfo {
      private final String className;
      private final String message;

      ExceptionInfo(String var1, String var2) {
         this.className = var1;
         this.message = var2;
      }

      InvalidClassException newInvalidClassException() {
         return new InvalidClassException(this.className, this.message);
      }
   }

   private static class Caches {
      static final ConcurrentMap<ObjectStreamClass.WeakClassKey, Reference<?>> localDescs = new ConcurrentHashMap();
      static final ConcurrentMap<ObjectStreamClass.FieldReflectorKey, Reference<?>> reflectors = new ConcurrentHashMap();
      private static final ReferenceQueue<Class<?>> localDescsQueue = new ReferenceQueue();
      private static final ReferenceQueue<Class<?>> reflectorsQueue = new ReferenceQueue();
   }
}
