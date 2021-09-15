package com.sun.corba.se.impl.io;

import com.sun.corba.se.impl.util.RepositoryId;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import org.omg.CORBA.ValueMember;
import sun.corba.Bridge;
import sun.misc.JavaSecurityAccess;
import sun.misc.SharedSecrets;

public class ObjectStreamClass implements Serializable {
   private static final boolean DEBUG_SVUID = false;
   public static final long kDefaultUID = -1L;
   private static Object[] noArgsList = new Object[0];
   private static Class<?>[] noTypesList = new Class[0];
   private boolean isEnum;
   private static final Bridge bridge = (Bridge)AccessController.doPrivileged(new PrivilegedAction<Bridge>() {
      public Bridge run() {
         return Bridge.get();
      }
   });
   private static final ObjectStreamClass.PersistentFieldsValue persistentFieldsValue = new ObjectStreamClass.PersistentFieldsValue();
   public static final int CLASS_MASK = 1553;
   public static final int FIELD_MASK = 223;
   public static final int METHOD_MASK = 3391;
   private static ObjectStreamClass.ObjectStreamClassEntry[] descriptorFor = new ObjectStreamClass.ObjectStreamClassEntry[61];
   private String name;
   private ObjectStreamClass superclass;
   private boolean serializable;
   private boolean externalizable;
   private ObjectStreamField[] fields;
   private Class<?> ofClass;
   boolean forProxyClass;
   private long suid = -1L;
   private String suidStr = null;
   private long actualSuid = -1L;
   private String actualSuidStr = null;
   int primBytes;
   int objFields;
   private boolean initialized = false;
   private Object lock = new Object();
   private boolean hasExternalizableBlockData;
   Method writeObjectMethod;
   Method readObjectMethod;
   private transient Method writeReplaceObjectMethod;
   private transient Method readResolveObjectMethod;
   private Constructor<?> cons;
   private transient ProtectionDomain[] domains;
   private String rmiiiopOptionalDataRepId = null;
   private ObjectStreamClass localClassDesc;
   private static Method hasStaticInitializerMethod = null;
   private static final long serialVersionUID = -6120832682080437368L;
   public static final ObjectStreamField[] NO_FIELDS = new ObjectStreamField[0];
   private static Comparator compareClassByName = new ObjectStreamClass.CompareClassByName();
   private static final Comparator compareObjStrFieldsByName = new ObjectStreamClass.CompareObjStrFieldsByName();
   private static Comparator compareMemberByName = new ObjectStreamClass.CompareMemberByName();

   static final ObjectStreamClass lookup(Class<?> var0) {
      ObjectStreamClass var1 = lookupInternal(var0);
      return !var1.isSerializable() && !var1.isExternalizable() ? null : var1;
   }

   static ObjectStreamClass lookupInternal(Class<?> var0) {
      ObjectStreamClass var1 = null;
      synchronized(descriptorFor) {
         var1 = findDescriptorFor(var0);
         if (var1 == null) {
            boolean var3 = Serializable.class.isAssignableFrom(var0);
            ObjectStreamClass var4 = null;
            if (var3) {
               Class var5 = var0.getSuperclass();
               if (var5 != null) {
                  var4 = lookup(var5);
               }
            }

            boolean var8 = false;
            if (var3) {
               var8 = var4 != null && var4.isExternalizable() || Externalizable.class.isAssignableFrom(var0);
               if (var8) {
                  var3 = false;
               }
            }

            var1 = new ObjectStreamClass(var0, var4, var3, var8);
         }

         var1.init();
         return var1;
      }
   }

   public final String getName() {
      return this.name;
   }

   public static final long getSerialVersionUID(Class<?> var0) {
      ObjectStreamClass var1 = lookup(var0);
      return var1 != null ? var1.getSerialVersionUID() : 0L;
   }

   public final long getSerialVersionUID() {
      return this.suid;
   }

   public final String getSerialVersionUIDStr() {
      if (this.suidStr == null) {
         this.suidStr = Long.toHexString(this.suid).toUpperCase();
      }

      return this.suidStr;
   }

   public static final long getActualSerialVersionUID(Class<?> var0) {
      ObjectStreamClass var1 = lookup(var0);
      return var1 != null ? var1.getActualSerialVersionUID() : 0L;
   }

   public final long getActualSerialVersionUID() {
      return this.actualSuid;
   }

   public final String getActualSerialVersionUIDStr() {
      if (this.actualSuidStr == null) {
         this.actualSuidStr = Long.toHexString(this.actualSuid).toUpperCase();
      }

      return this.actualSuidStr;
   }

   public final Class<?> forClass() {
      return this.ofClass;
   }

   public ObjectStreamField[] getFields() {
      if (this.fields.length > 0) {
         ObjectStreamField[] var1 = new ObjectStreamField[this.fields.length];
         System.arraycopy(this.fields, 0, var1, 0, this.fields.length);
         return var1;
      } else {
         return this.fields;
      }
   }

   public boolean hasField(ValueMember var1) {
      try {
         for(int var2 = 0; var2 < this.fields.length; ++var2) {
            if (this.fields[var2].getName().equals(var1.name) && this.fields[var2].getSignature().equals(ValueUtility.getSignature(var1))) {
               return true;
            }
         }
      } catch (Exception var3) {
      }

      return false;
   }

   final ObjectStreamField[] getFieldsNoCopy() {
      return this.fields;
   }

   public final ObjectStreamField getField(String var1) {
      for(int var2 = this.fields.length - 1; var2 >= 0; --var2) {
         if (var1.equals(this.fields[var2].getName())) {
            return this.fields[var2];
         }
      }

      return null;
   }

   public Serializable writeReplace(Serializable var1) {
      if (this.writeReplaceObjectMethod != null) {
         try {
            return (Serializable)this.writeReplaceObjectMethod.invoke(var1, noArgsList);
         } catch (Throwable var3) {
            throw new RuntimeException(var3);
         }
      } else {
         return var1;
      }
   }

   public Object readResolve(Object var1) {
      if (this.readResolveObjectMethod != null) {
         try {
            return this.readResolveObjectMethod.invoke(var1, noArgsList);
         } catch (Throwable var3) {
            throw new RuntimeException(var3);
         }
      } else {
         return var1;
      }
   }

   public final String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append(this.name);
      var1.append(": static final long serialVersionUID = ");
      var1.append(Long.toString(this.suid));
      var1.append("L;");
      return var1.toString();
   }

   private ObjectStreamClass(Class<?> var1, ObjectStreamClass var2, boolean var3, boolean var4) {
      this.ofClass = var1;
      if (Proxy.isProxyClass(var1)) {
         this.forProxyClass = true;
      }

      this.name = var1.getName();
      this.isEnum = Enum.class.isAssignableFrom(var1);
      this.superclass = var2;
      this.serializable = var3;
      if (!this.forProxyClass) {
         this.externalizable = var4;
      }

      insertDescriptorFor(this);
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

   private void init() {
      synchronized(this.lock) {
         if (!this.initialized) {
            final Class var2 = this.ofClass;
            if (this.serializable && !this.externalizable && !this.forProxyClass && !this.name.equals("java.lang.String")) {
               if (this.serializable) {
                  AccessController.doPrivileged(new PrivilegedAction() {
                     public Object run() {
                        ObjectStreamClass.this.fields = (ObjectStreamField[])ObjectStreamClass.persistentFieldsValue.get(var2);
                        if (ObjectStreamClass.this.fields == null) {
                           Field[] var1 = var2.getDeclaredFields();
                           int var2x = 0;
                           ObjectStreamField[] var3 = new ObjectStreamField[var1.length];

                           for(int var4 = 0; var4 < var1.length; ++var4) {
                              Field var5 = var1[var4];
                              int var6 = var5.getModifiers();
                              if (!Modifier.isStatic(var6) && !Modifier.isTransient(var6)) {
                                 var5.setAccessible(true);
                                 var3[var2x++] = new ObjectStreamField(var5);
                              }
                           }

                           ObjectStreamClass.this.fields = new ObjectStreamField[var2x];
                           System.arraycopy(var3, 0, ObjectStreamClass.this.fields, 0, var2x);
                        } else {
                           for(int var8 = ObjectStreamClass.this.fields.length - 1; var8 >= 0; --var8) {
                              try {
                                 Field var9 = var2.getDeclaredField(ObjectStreamClass.this.fields[var8].getName());
                                 if (ObjectStreamClass.this.fields[var8].getType() == var9.getType()) {
                                    var9.setAccessible(true);
                                    ObjectStreamClass.this.fields[var8].setField(var9);
                                 }
                              } catch (NoSuchFieldException var7) {
                              }
                           }
                        }

                        return null;
                     }
                  });
                  if (this.fields.length > 1) {
                     Arrays.sort((Object[])this.fields);
                  }

                  this.computeFieldInfo();
               }
            } else {
               this.fields = NO_FIELDS;
            }

            if (!this.isNonSerializable() && !this.isEnum) {
               AccessController.doPrivileged(new PrivilegedAction() {
                  public Object run() {
                     if (ObjectStreamClass.this.forProxyClass) {
                        ObjectStreamClass.this.suid = 0L;
                     } else {
                        try {
                           Field var1 = var2.getDeclaredField("serialVersionUID");
                           int var2x = var1.getModifiers();
                           if (Modifier.isStatic(var2x) && Modifier.isFinal(var2x)) {
                              var1.setAccessible(true);
                              ObjectStreamClass.this.suid = var1.getLong(var2);
                           } else {
                              ObjectStreamClass.this.suid = ObjectStreamClass._computeSerialVersionUID(var2);
                           }
                        } catch (NoSuchFieldException var3) {
                           ObjectStreamClass.this.suid = ObjectStreamClass._computeSerialVersionUID(var2);
                        } catch (IllegalAccessException var4) {
                           ObjectStreamClass.this.suid = ObjectStreamClass._computeSerialVersionUID(var2);
                        }
                     }

                     ObjectStreamClass.this.writeReplaceObjectMethod = ObjectStreamClass.getInheritableMethod(var2, "writeReplace", ObjectStreamClass.noTypesList, Object.class);
                     ObjectStreamClass.this.readResolveObjectMethod = ObjectStreamClass.getInheritableMethod(var2, "readResolve", ObjectStreamClass.noTypesList, Object.class);
                     ObjectStreamClass.this.domains = new ProtectionDomain[]{ObjectStreamClass.this.noPermissionsDomain()};
                     if (ObjectStreamClass.this.externalizable) {
                        ObjectStreamClass.this.cons = ObjectStreamClass.getExternalizableConstructor(var2);
                     } else {
                        ObjectStreamClass.this.cons = ObjectStreamClass.getSerializableConstructor(var2);
                     }

                     ObjectStreamClass.this.domains = ObjectStreamClass.this.getProtectionDomains(ObjectStreamClass.this.cons, var2);
                     if (ObjectStreamClass.this.serializable && !ObjectStreamClass.this.forProxyClass) {
                        ObjectStreamClass.this.writeObjectMethod = ObjectStreamClass.getPrivateMethod(var2, "writeObject", new Class[]{ObjectOutputStream.class}, Void.TYPE);
                        ObjectStreamClass.this.readObjectMethod = ObjectStreamClass.getPrivateMethod(var2, "readObject", new Class[]{ObjectInputStream.class}, Void.TYPE);
                     }

                     return null;
                  }
               });
            } else {
               this.suid = 0L;
            }

            this.actualSuid = computeStructuralUID(this, var2);
            if (this.hasWriteObject()) {
               this.rmiiiopOptionalDataRepId = this.computeRMIIIOPOptionalDataRepId();
            }

            this.initialized = true;
         }
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

   private String computeRMIIIOPOptionalDataRepId() {
      StringBuffer var1 = new StringBuffer("RMI:org.omg.custom.");
      var1.append(RepositoryId.convertToISOLatin1(this.getName()));
      var1.append(':');
      var1.append(this.getActualSerialVersionUIDStr());
      var1.append(':');
      var1.append(this.getSerialVersionUIDStr());
      return var1.toString();
   }

   public final String getRMIIIOPOptionalDataRepId() {
      return this.rmiiiopOptionalDataRepId;
   }

   ObjectStreamClass(String var1, long var2) {
      this.name = var1;
      this.suid = var2;
      this.superclass = null;
   }

   final void setClass(Class<?> var1) throws InvalidClassException {
      if (var1 == null) {
         this.localClassDesc = null;
         this.ofClass = null;
         this.computeFieldInfo();
      } else {
         this.localClassDesc = lookupInternal(var1);
         if (this.localClassDesc == null) {
            throw new InvalidClassException(var1.getName(), "Local class not compatible");
         } else {
            if (this.suid != this.localClassDesc.suid) {
               boolean var2 = this.isNonSerializable() || this.localClassDesc.isNonSerializable();
               boolean var3 = var1.isArray() && !var1.getName().equals(this.name);
               if (!var3 && !var2) {
                  throw new InvalidClassException(var1.getName(), "Local class not compatible: stream classdesc serialVersionUID=" + this.suid + " local class serialVersionUID=" + this.localClassDesc.suid);
               }
            }

            if (!compareClassNames(this.name, var1.getName(), '.')) {
               throw new InvalidClassException(var1.getName(), "Incompatible local class name. Expected class name compatible with " + this.name);
            } else if (this.serializable == this.localClassDesc.serializable && this.externalizable == this.localClassDesc.externalizable && (this.serializable || this.externalizable)) {
               ObjectStreamField[] var7 = (ObjectStreamField[])this.localClassDesc.fields;
               ObjectStreamField[] var8 = (ObjectStreamField[])this.fields;
               int var4 = 0;

               for(int var5 = 0; var5 < var8.length; ++var5) {
                  for(int var6 = var4; var6 < var7.length; ++var6) {
                     if (var8[var5].getName().equals(var7[var6].getName())) {
                        if (var8[var5].isPrimitive() && !var8[var5].typeEquals(var7[var6])) {
                           throw new InvalidClassException(var1.getName(), "The type of field " + var8[var5].getName() + " of class " + this.name + " is incompatible.");
                        }

                        var4 = var6;
                        var8[var5].setField(var7[var6].getField());
                        break;
                     }
                  }
               }

               this.computeFieldInfo();
               this.ofClass = var1;
               this.readObjectMethod = this.localClassDesc.readObjectMethod;
               this.readResolveObjectMethod = this.localClassDesc.readResolveObjectMethod;
            } else {
               throw new InvalidClassException(var1.getName(), "Serialization incompatible with Externalization");
            }
         }
      }
   }

   static boolean compareClassNames(String var0, String var1, char var2) {
      int var3 = var0.lastIndexOf(var2);
      if (var3 < 0) {
         var3 = 0;
      }

      int var4 = var1.lastIndexOf(var2);
      if (var4 < 0) {
         var4 = 0;
      }

      return var0.regionMatches(false, var3, var1, var4, var0.length() - var3);
   }

   final boolean typeEquals(ObjectStreamClass var1) {
      return this.suid == var1.suid && compareClassNames(this.name, var1.name, '.');
   }

   final void setSuperclass(ObjectStreamClass var1) {
      this.superclass = var1;
   }

   final ObjectStreamClass getSuperclass() {
      return this.superclass;
   }

   final boolean hasReadObject() {
      return this.readObjectMethod != null;
   }

   final boolean hasWriteObject() {
      return this.writeObjectMethod != null;
   }

   final boolean isCustomMarshaled() {
      return this.hasWriteObject() || this.isExternalizable() || this.superclass != null && this.superclass.isCustomMarshaled();
   }

   boolean hasExternalizableBlockDataMode() {
      return this.hasExternalizableBlockData;
   }

   Object newInstance() throws InstantiationException, InvocationTargetException, UnsupportedOperationException {
      if (!this.initialized) {
         throw new InternalError("Unexpected call when not initialized");
      } else if (this.cons == null) {
         throw new UnsupportedOperationException();
      } else {
         try {
            if (this.domains != null && this.domains.length != 0) {
               JavaSecurityAccess var1 = SharedSecrets.getJavaSecurityAccess();
               PrivilegedAction var7 = new PrivilegedAction() {
                  public Object run() {
                     try {
                        return ObjectStreamClass.this.cons.newInstance();
                     } catch (InvocationTargetException | IllegalAccessException | InstantiationException var2) {
                        throw new UndeclaredThrowableException(var2);
                     }
                  }
               };

               try {
                  return var1.doIntersectionPrivilege(var7, AccessController.getContext(), new AccessControlContext(this.domains));
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
            InternalError var2 = new InternalError();
            var2.initCause(var6);
            throw var2;
         }
      }
   }

   private static Constructor getExternalizableConstructor(Class<?> var0) {
      try {
         Constructor var1 = var0.getDeclaredConstructor();
         var1.setAccessible(true);
         return (var1.getModifiers() & 1) != 0 ? var1 : null;
      } catch (NoSuchMethodException var2) {
         return null;
      }
   }

   private static Constructor getSerializableConstructor(Class<?> var0) {
      Class var1 = var0;

      while(Serializable.class.isAssignableFrom(var1)) {
         if ((var1 = var1.getSuperclass()) == null) {
            return null;
         }
      }

      try {
         Constructor var2 = var1.getDeclaredConstructor();
         int var3 = var2.getModifiers();
         if ((var3 & 2) == 0 && ((var3 & 5) != 0 || packageEquals(var0, var1))) {
            var2 = bridge.newConstructorForSerialization(var0, var2);
            var2.setAccessible(true);
            return var2;
         } else {
            return null;
         }
      } catch (NoSuchMethodException var4) {
         return null;
      }
   }

   final ObjectStreamClass localClassDescriptor() {
      return this.localClassDesc;
   }

   boolean isSerializable() {
      return this.serializable;
   }

   boolean isExternalizable() {
      return this.externalizable;
   }

   boolean isNonSerializable() {
      return !this.externalizable && !this.serializable;
   }

   private void computeFieldInfo() {
      this.primBytes = 0;
      this.objFields = 0;

      for(int var1 = 0; var1 < this.fields.length; ++var1) {
         switch(this.fields[var1].getTypeCode()) {
         case 'B':
         case 'Z':
            ++this.primBytes;
            break;
         case 'C':
         case 'S':
            this.primBytes += 2;
            break;
         case 'D':
         case 'J':
            this.primBytes += 8;
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
            break;
         case 'F':
         case 'I':
            this.primBytes += 4;
            break;
         case 'L':
         case '[':
            ++this.objFields;
         }
      }

   }

   private static void msg(String var0) {
      System.out.println(var0);
   }

   private static long _computeSerialVersionUID(Class<?> var0) {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream(512);
      long var2 = 0L;

      try {
         MessageDigest var4 = MessageDigest.getInstance("SHA");
         DigestOutputStream var18 = new DigestOutputStream(var1, var4);
         DataOutputStream var6 = new DataOutputStream(var18);
         var6.writeUTF(var0.getName());
         int var7 = var0.getModifiers();
         var7 &= 1553;
         Method[] var8 = var0.getDeclaredMethods();
         if ((var7 & 512) != 0) {
            var7 &= -1025;
            if (var8.length > 0) {
               var7 |= 1024;
            }
         }

         var7 &= 1553;
         var6.writeInt(var7);
         int var10;
         if (!var0.isArray()) {
            Class[] var9 = var0.getInterfaces();
            Arrays.sort(var9, compareClassByName);

            for(var10 = 0; var10 < var9.length; ++var10) {
               var6.writeUTF(var9[var10].getName());
            }
         }

         Field[] var19 = var0.getDeclaredFields();
         Arrays.sort(var19, compareMemberByName);

         int var12;
         for(var10 = 0; var10 < var19.length; ++var10) {
            Field var11 = var19[var10];
            var12 = var11.getModifiers();
            if (!Modifier.isPrivate(var12) || !Modifier.isTransient(var12) && !Modifier.isStatic(var12)) {
               var6.writeUTF(var11.getName());
               var12 &= 223;
               var6.writeInt(var12);
               var6.writeUTF(getSignature(var11.getType()));
            }
         }

         if (hasStaticInitializer(var0)) {
            var6.writeUTF("<clinit>");
            var6.writeInt(8);
            var6.writeUTF("()V");
         }

         ObjectStreamClass.MethodSignature[] var20 = ObjectStreamClass.MethodSignature.removePrivateAndSort(var0.getDeclaredConstructors());

         String var14;
         int var15;
         for(int var21 = 0; var21 < var20.length; ++var21) {
            ObjectStreamClass.MethodSignature var23 = var20[var21];
            String var13 = "<init>";
            var14 = var23.signature;
            var14 = var14.replace('/', '.');
            var6.writeUTF(var13);
            var15 = var23.member.getModifiers() & 3391;
            var6.writeInt(var15);
            var6.writeUTF(var14);
         }

         ObjectStreamClass.MethodSignature[] var22 = ObjectStreamClass.MethodSignature.removePrivateAndSort(var8);

         for(var12 = 0; var12 < var22.length; ++var12) {
            ObjectStreamClass.MethodSignature var24 = var22[var12];
            var14 = var24.signature;
            var14 = var14.replace('/', '.');
            var6.writeUTF(var24.member.getName());
            var15 = var24.member.getModifiers() & 3391;
            var6.writeInt(var15);
            var6.writeUTF(var14);
         }

         var6.flush();
         byte[] var25 = var4.digest();

         for(int var26 = 0; var26 < Math.min(8, var25.length); ++var26) {
            var2 += (long)(var25[var26] & 255) << var26 * 8;
         }
      } catch (IOException var16) {
         var2 = -1L;
      } catch (NoSuchAlgorithmException var17) {
         SecurityException var5 = new SecurityException();
         var5.initCause(var17);
         throw var5;
      }

      return var2;
   }

   private static long computeStructuralUID(ObjectStreamClass var0, Class<?> var1) {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream(512);
      long var3 = 0L;

      try {
         if (!Serializable.class.isAssignableFrom(var1) || var1.isInterface()) {
            return 0L;
         }

         if (Externalizable.class.isAssignableFrom(var1)) {
            return 1L;
         }

         MessageDigest var5 = MessageDigest.getInstance("SHA");
         DigestOutputStream var14 = new DigestOutputStream(var2, var5);
         DataOutputStream var7 = new DataOutputStream(var14);
         Class var8 = var1.getSuperclass();
         if (var8 != null) {
            var7.writeLong(computeStructuralUID(lookup(var8), var8));
         }

         if (var0.hasWriteObject()) {
            var7.writeInt(2);
         } else {
            var7.writeInt(1);
         }

         ObjectStreamField[] var9 = var0.getFields();
         if (var9.length > 1) {
            Arrays.sort(var9, compareObjStrFieldsByName);
         }

         for(int var10 = 0; var10 < var9.length; ++var10) {
            var7.writeUTF(var9[var10].getName());
            var7.writeUTF(var9[var10].getSignature());
         }

         var7.flush();
         byte[] var15 = var5.digest();

         for(int var11 = 0; var11 < Math.min(8, var15.length); ++var11) {
            var3 += (long)(var15[var11] & 255) << var11 * 8;
         }
      } catch (IOException var12) {
         var3 = -1L;
      } catch (NoSuchAlgorithmException var13) {
         SecurityException var6 = new SecurityException();
         var6.initCause(var13);
         throw var6;
      }

      return var3;
   }

   static String getSignature(Class<?> var0) {
      String var1 = null;
      if (var0.isArray()) {
         Class var2 = var0;

         int var3;
         for(var3 = 0; var2.isArray(); var2 = var2.getComponentType()) {
            ++var3;
         }

         StringBuffer var4 = new StringBuffer();

         for(int var5 = 0; var5 < var3; ++var5) {
            var4.append("[");
         }

         var4.append(getSignature(var2));
         var1 = var4.toString();
      } else if (var0.isPrimitive()) {
         if (var0 == Integer.TYPE) {
            var1 = "I";
         } else if (var0 == Byte.TYPE) {
            var1 = "B";
         } else if (var0 == Long.TYPE) {
            var1 = "J";
         } else if (var0 == Float.TYPE) {
            var1 = "F";
         } else if (var0 == Double.TYPE) {
            var1 = "D";
         } else if (var0 == Short.TYPE) {
            var1 = "S";
         } else if (var0 == Character.TYPE) {
            var1 = "C";
         } else if (var0 == Boolean.TYPE) {
            var1 = "Z";
         } else if (var0 == Void.TYPE) {
            var1 = "V";
         }
      } else {
         var1 = "L" + var0.getName().replace('.', '/') + ";";
      }

      return var1;
   }

   static String getSignature(Method var0) {
      StringBuffer var1 = new StringBuffer();
      var1.append("(");
      Class[] var2 = var0.getParameterTypes();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         var1.append(getSignature(var2[var3]));
      }

      var1.append(")");
      var1.append(getSignature(var0.getReturnType()));
      return var1.toString();
   }

   static String getSignature(Constructor var0) {
      StringBuffer var1 = new StringBuffer();
      var1.append("(");
      Class[] var2 = var0.getParameterTypes();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         var1.append(getSignature(var2[var3]));
      }

      var1.append(")V");
      return var1.toString();
   }

   private static ObjectStreamClass findDescriptorFor(Class<?> var0) {
      int var1 = var0.hashCode();

      ObjectStreamClass.ObjectStreamClassEntry var3;
      for(int var2 = (var1 & Integer.MAX_VALUE) % descriptorFor.length; (var3 = descriptorFor[var2]) != null && var3.get() == null; descriptorFor[var2] = var3.next) {
      }

      for(ObjectStreamClass.ObjectStreamClassEntry var4 = var3; var3 != null; var3 = var3.next) {
         ObjectStreamClass var5 = (ObjectStreamClass)((ObjectStreamClass)var3.get());
         if (var5 == null) {
            var4.next = var3.next;
         } else {
            if (var5.ofClass == var0) {
               return var5;
            }

            var4 = var3;
         }
      }

      return null;
   }

   private static void insertDescriptorFor(ObjectStreamClass var0) {
      if (findDescriptorFor(var0.ofClass) == null) {
         int var1 = var0.ofClass.hashCode();
         int var2 = (var1 & Integer.MAX_VALUE) % descriptorFor.length;
         ObjectStreamClass.ObjectStreamClassEntry var3 = new ObjectStreamClass.ObjectStreamClassEntry(var0);
         var3.next = descriptorFor[var2];
         descriptorFor[var2] = var3;
      }
   }

   private static Field[] getDeclaredFields(final Class<?> var0) {
      return (Field[])((Field[])AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return var0.getDeclaredFields();
         }
      }));
   }

   private static boolean hasStaticInitializer(Class<?> var0) {
      if (hasStaticInitializerMethod == null) {
         Class var1 = null;

         try {
            if (var1 == null) {
               var1 = java.io.ObjectStreamClass.class;
            }

            hasStaticInitializerMethod = var1.getDeclaredMethod("hasStaticInitializer", Class.class);
         } catch (NoSuchMethodException var4) {
         }

         if (hasStaticInitializerMethod == null) {
            throw new InternalError("Can't find hasStaticInitializer method on " + var1.getName());
         }

         hasStaticInitializerMethod.setAccessible(true);
      }

      try {
         Boolean var5 = (Boolean)hasStaticInitializerMethod.invoke((Object)null, var0);
         return var5;
      } catch (Exception var3) {
         InternalError var2 = new InternalError("Error invoking hasStaticInitializer");
         var2.initCause(var3);
         throw var2;
      }
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

   private static boolean packageEquals(Class<?> var0, Class<?> var1) {
      Package var2 = var0.getPackage();
      Package var3 = var1.getPackage();
      return var2 == var3 || var2 != null && var2.equals(var3);
   }

   private static class MethodSignature implements Comparator {
      Member member;
      String signature;

      static ObjectStreamClass.MethodSignature[] removePrivateAndSort(Member[] var0) {
         int var1 = 0;

         for(int var2 = 0; var2 < var0.length; ++var2) {
            if (!Modifier.isPrivate(var0[var2].getModifiers())) {
               ++var1;
            }
         }

         ObjectStreamClass.MethodSignature[] var5 = new ObjectStreamClass.MethodSignature[var1];
         int var3 = 0;

         for(int var4 = 0; var4 < var0.length; ++var4) {
            if (!Modifier.isPrivate(var0[var4].getModifiers())) {
               var5[var3] = new ObjectStreamClass.MethodSignature(var0[var4]);
               ++var3;
            }
         }

         if (var3 > 0) {
            Arrays.sort(var5, var5[0]);
         }

         return var5;
      }

      public int compare(Object var1, Object var2) {
         if (var1 == var2) {
            return 0;
         } else {
            ObjectStreamClass.MethodSignature var3 = (ObjectStreamClass.MethodSignature)var1;
            ObjectStreamClass.MethodSignature var4 = (ObjectStreamClass.MethodSignature)var2;
            int var5;
            if (this.isConstructor()) {
               var5 = var3.signature.compareTo(var4.signature);
            } else {
               var5 = var3.member.getName().compareTo(var4.member.getName());
               if (var5 == 0) {
                  var5 = var3.signature.compareTo(var4.signature);
               }
            }

            return var5;
         }
      }

      private final boolean isConstructor() {
         return this.member instanceof Constructor;
      }

      private MethodSignature(Member var1) {
         this.member = var1;
         if (this.isConstructor()) {
            this.signature = ObjectStreamClass.getSignature((Constructor)var1);
         } else {
            this.signature = ObjectStreamClass.getSignature((Method)var1);
         }

      }
   }

   private static class CompareMemberByName implements Comparator {
      private CompareMemberByName() {
      }

      public int compare(Object var1, Object var2) {
         String var3 = ((Member)var1).getName();
         String var4 = ((Member)var2).getName();
         if (var1 instanceof Method) {
            var3 = var3 + ObjectStreamClass.getSignature((Method)var1);
            var4 = var4 + ObjectStreamClass.getSignature((Method)var2);
         } else if (var1 instanceof Constructor) {
            var3 = var3 + ObjectStreamClass.getSignature((Constructor)var1);
            var4 = var4 + ObjectStreamClass.getSignature((Constructor)var2);
         }

         return var3.compareTo(var4);
      }

      // $FF: synthetic method
      CompareMemberByName(Object var1) {
         this();
      }
   }

   private static class CompareObjStrFieldsByName implements Comparator {
      private CompareObjStrFieldsByName() {
      }

      public int compare(Object var1, Object var2) {
         ObjectStreamField var3 = (ObjectStreamField)var1;
         ObjectStreamField var4 = (ObjectStreamField)var2;
         return var3.getName().compareTo(var4.getName());
      }

      // $FF: synthetic method
      CompareObjStrFieldsByName(Object var1) {
         this();
      }
   }

   private static class CompareClassByName implements Comparator {
      private CompareClassByName() {
      }

      public int compare(Object var1, Object var2) {
         Class var3 = (Class)var1;
         Class var4 = (Class)var2;
         return var3.getName().compareTo(var4.getName());
      }

      // $FF: synthetic method
      CompareClassByName(Object var1) {
         this();
      }
   }

   private static class ObjectStreamClassEntry {
      ObjectStreamClass.ObjectStreamClassEntry next;
      private ObjectStreamClass c;

      ObjectStreamClassEntry(ObjectStreamClass var1) {
         this.c = var1;
      }

      public Object get() {
         return this.c;
      }
   }

   private static final class PersistentFieldsValue extends ClassValue<ObjectStreamField[]> {
      PersistentFieldsValue() {
      }

      protected ObjectStreamField[] computeValue(Class<?> var1) {
         try {
            Field var2 = var1.getDeclaredField("serialPersistentFields");
            int var3 = var2.getModifiers();
            if (Modifier.isPrivate(var3) && Modifier.isStatic(var3) && Modifier.isFinal(var3)) {
               var2.setAccessible(true);
               java.io.ObjectStreamField[] var4 = (java.io.ObjectStreamField[])((java.io.ObjectStreamField[])var2.get(var1));
               return translateFields(var4);
            }
         } catch (IllegalAccessException | IllegalArgumentException | ClassCastException | NoSuchFieldException var5) {
         }

         return null;
      }

      private static ObjectStreamField[] translateFields(java.io.ObjectStreamField[] var0) {
         ObjectStreamField[] var1 = new ObjectStreamField[var0.length];

         for(int var2 = 0; var2 < var0.length; ++var2) {
            var1[var2] = new ObjectStreamField(var0[var2].getName(), var0[var2].getType());
         }

         return var1;
      }
   }
}
