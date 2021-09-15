package com.sun.corba.se.impl.orbutil;

import com.sun.corba.se.impl.io.ObjectStreamClass;
import com.sun.corba.se.impl.io.ValueUtility;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import org.omg.CORBA.ValueMember;

public class ObjectStreamClass_1_3_1 implements Serializable {
   public static final long kDefaultUID = -1L;
   private static Object[] noArgsList = new Object[0];
   private static Class<?>[] noTypesList = new Class[0];
   private static Hashtable translatedFields;
   private static ObjectStreamClass_1_3_1.ObjectStreamClassEntry[] descriptorFor = new ObjectStreamClass_1_3_1.ObjectStreamClassEntry[61];
   private String name;
   private ObjectStreamClass_1_3_1 superclass;
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
   private Object lock = new Object();
   private boolean hasWriteObjectMethod;
   private boolean hasExternalizableBlockData;
   Method writeObjectMethod;
   Method readObjectMethod;
   private transient Method writeReplaceObjectMethod;
   private transient Method readResolveObjectMethod;
   private ObjectStreamClass_1_3_1 localClassDesc;
   private static final long serialVersionUID = -6120832682080437368L;
   public static final ObjectStreamField[] NO_FIELDS = new ObjectStreamField[0];
   private static Comparator compareClassByName = new ObjectStreamClass_1_3_1.CompareClassByName();
   private static Comparator compareMemberByName = new ObjectStreamClass_1_3_1.CompareMemberByName();

   static final ObjectStreamClass_1_3_1 lookup(Class<?> var0) {
      ObjectStreamClass_1_3_1 var1 = lookupInternal(var0);
      return !var1.isSerializable() && !var1.isExternalizable() ? null : var1;
   }

   static ObjectStreamClass_1_3_1 lookupInternal(Class<?> var0) {
      ObjectStreamClass_1_3_1 var1 = null;
      synchronized(descriptorFor) {
         var1 = findDescriptorFor(var0);
         if (var1 != null) {
            return var1;
         }

         boolean var3 = Serializable.class.isAssignableFrom(var0);
         ObjectStreamClass_1_3_1 var4 = null;
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

         var1 = new ObjectStreamClass_1_3_1(var0, var4, var3, var8);
      }

      var1.init();
      return var1;
   }

   public final String getName() {
      return this.name;
   }

   public static final long getSerialVersionUID(Class<?> var0) {
      ObjectStreamClass_1_3_1 var1 = lookup(var0);
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
      ObjectStreamClass_1_3_1 var1 = lookup(var0);
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
      for(int var2 = 0; var2 < this.fields.length; ++var2) {
         try {
            if (this.fields[var2].getName().equals(var1.name) && this.fields[var2].getSignature().equals(ValueUtility.getSignature(var1))) {
               return true;
            }
         } catch (Throwable var4) {
         }
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
            throw new RuntimeException(var3.getMessage());
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
            throw new RuntimeException(var3.getMessage());
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

   private ObjectStreamClass_1_3_1(Class<?> var1, ObjectStreamClass_1_3_1 var2, boolean var3, boolean var4) {
      this.ofClass = var1;
      if (Proxy.isProxyClass(var1)) {
         this.forProxyClass = true;
      }

      this.name = var1.getName();
      this.superclass = var2;
      this.serializable = var3;
      if (!this.forProxyClass) {
         this.externalizable = var4;
      }

      insertDescriptorFor(this);
   }

   private void init() {
      synchronized(this.lock) {
         final Class var2 = this.ofClass;
         if (this.fields == null) {
            if (this.serializable && !this.externalizable && !this.forProxyClass && !this.name.equals("java.lang.String")) {
               if (this.serializable) {
                  AccessController.doPrivileged(new PrivilegedAction() {
                     public Object run() {
                        try {
                           Field var1 = var2.getDeclaredField("serialPersistentFields");
                           var1.setAccessible(true);
                           java.io.ObjectStreamField[] var2x = (java.io.ObjectStreamField[])((java.io.ObjectStreamField[])var1.get(var2));
                           int var3 = var1.getModifiers();
                           if (Modifier.isPrivate(var3) && Modifier.isStatic(var3) && Modifier.isFinal(var3)) {
                              ObjectStreamClass_1_3_1.this.fields = (ObjectStreamField[])((ObjectStreamField[])ObjectStreamClass_1_3_1.translateFields((Object[])((Object[])var1.get(var2))));
                           }
                        } catch (NoSuchFieldException var7) {
                           ObjectStreamClass_1_3_1.this.fields = null;
                        } catch (IllegalAccessException var8) {
                           ObjectStreamClass_1_3_1.this.fields = null;
                        } catch (IllegalArgumentException var9) {
                           ObjectStreamClass_1_3_1.this.fields = null;
                        } catch (ClassCastException var10) {
                           ObjectStreamClass_1_3_1.this.fields = null;
                        }

                        if (ObjectStreamClass_1_3_1.this.fields == null) {
                           Field[] var11 = var2.getDeclaredFields();
                           int var13 = 0;
                           ObjectStreamField[] var15 = new ObjectStreamField[var11.length];

                           for(int var4 = 0; var4 < var11.length; ++var4) {
                              int var5 = var11[var4].getModifiers();
                              if (!Modifier.isStatic(var5) && !Modifier.isTransient(var5)) {
                                 var15[var13++] = new ObjectStreamField(var11[var4]);
                              }
                           }

                           ObjectStreamClass_1_3_1.this.fields = new ObjectStreamField[var13];
                           System.arraycopy(var15, 0, ObjectStreamClass_1_3_1.this.fields, 0, var13);
                        } else {
                           for(int var12 = ObjectStreamClass_1_3_1.this.fields.length - 1; var12 >= 0; --var12) {
                              try {
                                 Field var14 = var2.getDeclaredField(ObjectStreamClass_1_3_1.this.fields[var12].getName());
                                 if (ObjectStreamClass_1_3_1.this.fields[var12].getType() == var14.getType()) {
                                    ObjectStreamClass_1_3_1.this.fields[var12].setField(var14);
                                 }
                              } catch (NoSuchFieldException var6) {
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

            if (this.isNonSerializable()) {
               this.suid = 0L;
            } else {
               AccessController.doPrivileged(new PrivilegedAction() {
                  public Object run() {
                     int var2x;
                     if (ObjectStreamClass_1_3_1.this.forProxyClass) {
                        ObjectStreamClass_1_3_1.this.suid = 0L;
                     } else {
                        try {
                           Field var1 = var2.getDeclaredField("serialVersionUID");
                           var2x = var1.getModifiers();
                           if (Modifier.isStatic(var2x) && Modifier.isFinal(var2x)) {
                              var1.setAccessible(true);
                              ObjectStreamClass_1_3_1.this.suid = var1.getLong(var2);
                           } else {
                              ObjectStreamClass_1_3_1.this.suid = ObjectStreamClass.getSerialVersionUID(var2);
                           }
                        } catch (NoSuchFieldException var7) {
                           ObjectStreamClass_1_3_1.this.suid = ObjectStreamClass.getSerialVersionUID(var2);
                        } catch (IllegalAccessException var8) {
                           ObjectStreamClass_1_3_1.this.suid = ObjectStreamClass.getSerialVersionUID(var2);
                        }
                     }

                     try {
                        ObjectStreamClass_1_3_1.this.writeReplaceObjectMethod = var2.getDeclaredMethod("writeReplace", ObjectStreamClass_1_3_1.noTypesList);
                        if (Modifier.isStatic(ObjectStreamClass_1_3_1.this.writeReplaceObjectMethod.getModifiers())) {
                           ObjectStreamClass_1_3_1.this.writeReplaceObjectMethod = null;
                        } else {
                           ObjectStreamClass_1_3_1.this.writeReplaceObjectMethod.setAccessible(true);
                        }
                     } catch (NoSuchMethodException var4) {
                     }

                     try {
                        ObjectStreamClass_1_3_1.this.readResolveObjectMethod = var2.getDeclaredMethod("readResolve", ObjectStreamClass_1_3_1.noTypesList);
                        if (Modifier.isStatic(ObjectStreamClass_1_3_1.this.readResolveObjectMethod.getModifiers())) {
                           ObjectStreamClass_1_3_1.this.readResolveObjectMethod = null;
                        } else {
                           ObjectStreamClass_1_3_1.this.readResolveObjectMethod.setAccessible(true);
                        }
                     } catch (NoSuchMethodException var3) {
                     }

                     if (ObjectStreamClass_1_3_1.this.serializable && !ObjectStreamClass_1_3_1.this.forProxyClass) {
                        Class[] var9;
                        try {
                           var9 = new Class[]{ObjectOutputStream.class};
                           ObjectStreamClass_1_3_1.this.writeObjectMethod = var2.getDeclaredMethod("writeObject", var9);
                           ObjectStreamClass_1_3_1.this.hasWriteObjectMethod = true;
                           var2x = ObjectStreamClass_1_3_1.this.writeObjectMethod.getModifiers();
                           if (!Modifier.isPrivate(var2x) || Modifier.isStatic(var2x)) {
                              ObjectStreamClass_1_3_1.this.writeObjectMethod = null;
                              ObjectStreamClass_1_3_1.this.hasWriteObjectMethod = false;
                           }
                        } catch (NoSuchMethodException var6) {
                        }

                        try {
                           var9 = new Class[]{ObjectInputStream.class};
                           ObjectStreamClass_1_3_1.this.readObjectMethod = var2.getDeclaredMethod("readObject", var9);
                           var2x = ObjectStreamClass_1_3_1.this.readObjectMethod.getModifiers();
                           if (!Modifier.isPrivate(var2x) || Modifier.isStatic(var2x)) {
                              ObjectStreamClass_1_3_1.this.readObjectMethod = null;
                           }
                        } catch (NoSuchMethodException var5) {
                        }
                     }

                     return null;
                  }
               });
            }

            this.actualSuid = computeStructuralUID(this, var2);
         }
      }
   }

   ObjectStreamClass_1_3_1(String var1, long var2) {
      this.name = var1;
      this.suid = var2;
      this.superclass = null;
   }

   private static Object[] translateFields(Object[] var0) throws NoSuchFieldException {
      try {
         java.io.ObjectStreamField[] var1 = (java.io.ObjectStreamField[])((java.io.ObjectStreamField[])var0);
         Object[] var2 = null;
         if (translatedFields == null) {
            translatedFields = new Hashtable();
         }

         var2 = (Object[])((Object[])translatedFields.get(var1));
         if (var2 != null) {
            return var2;
         } else {
            Class var3 = ObjectStreamField.class;
            var2 = (Object[])((Object[])Array.newInstance(var3, var0.length));
            Object[] var4 = new Object[2];
            Class[] var5 = new Class[]{String.class, Class.class};
            Constructor var6 = var3.getDeclaredConstructor(var5);

            for(int var7 = var1.length - 1; var7 >= 0; --var7) {
               var4[0] = var1[var7].getName();
               var4[1] = var1[var7].getType();
               var2[var7] = var6.newInstance(var4);
            }

            translatedFields.put(var1, var2);
            return (Object[])var2;
         }
      } catch (Throwable var8) {
         throw new NoSuchFieldException();
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

   final boolean typeEquals(ObjectStreamClass_1_3_1 var1) {
      return this.suid == var1.suid && compareClassNames(this.name, var1.name, '.');
   }

   final void setSuperclass(ObjectStreamClass_1_3_1 var1) {
      this.superclass = var1;
   }

   final ObjectStreamClass_1_3_1 getSuperclass() {
      return this.superclass;
   }

   final boolean hasWriteObject() {
      return this.hasWriteObjectMethod;
   }

   final boolean isCustomMarshaled() {
      return this.hasWriteObject() || this.isExternalizable();
   }

   boolean hasExternalizableBlockDataMode() {
      return this.hasExternalizableBlockData;
   }

   final ObjectStreamClass_1_3_1 localClassDescriptor() {
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

   private static long computeStructuralUID(ObjectStreamClass_1_3_1 var0, Class<?> var1) {
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
         DigestOutputStream var6 = new DigestOutputStream(var2, var5);
         DataOutputStream var7 = new DataOutputStream(var6);
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
         int var10 = 0;

         for(int var11 = 0; var11 < var9.length; ++var11) {
            if (var9[var11].getField() != null) {
               ++var10;
            }
         }

         Field[] var17 = new Field[var10];
         int var12 = 0;

         int var13;
         for(var13 = 0; var12 < var9.length; ++var12) {
            if (var9[var12].getField() != null) {
               var17[var13++] = var9[var12].getField();
            }
         }

         if (var17.length > 1) {
            Arrays.sort(var17, compareMemberByName);
         }

         for(var12 = 0; var12 < var17.length; ++var12) {
            Field var19 = var17[var12];
            int var14 = var19.getModifiers();
            var7.writeUTF(var19.getName());
            var7.writeUTF(getSignature(var19.getType()));
         }

         var7.flush();
         byte[] var18 = var5.digest();

         for(var13 = 0; var13 < Math.min(8, var18.length); ++var13) {
            var3 += (long)(var18[var13] & 255) << var13 * 8;
         }
      } catch (IOException var15) {
         var3 = -1L;
      } catch (NoSuchAlgorithmException var16) {
         throw new SecurityException(var16.getMessage());
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

   private static ObjectStreamClass_1_3_1 findDescriptorFor(Class<?> var0) {
      int var1 = var0.hashCode();

      ObjectStreamClass_1_3_1.ObjectStreamClassEntry var3;
      for(int var2 = (var1 & Integer.MAX_VALUE) % descriptorFor.length; (var3 = descriptorFor[var2]) != null && var3.get() == null; descriptorFor[var2] = var3.next) {
      }

      for(ObjectStreamClass_1_3_1.ObjectStreamClassEntry var4 = var3; var3 != null; var3 = var3.next) {
         ObjectStreamClass_1_3_1 var5 = (ObjectStreamClass_1_3_1)((ObjectStreamClass_1_3_1)var3.get());
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

   private static void insertDescriptorFor(ObjectStreamClass_1_3_1 var0) {
      if (findDescriptorFor(var0.ofClass) == null) {
         int var1 = var0.ofClass.hashCode();
         int var2 = (var1 & Integer.MAX_VALUE) % descriptorFor.length;
         ObjectStreamClass_1_3_1.ObjectStreamClassEntry var3 = new ObjectStreamClass_1_3_1.ObjectStreamClassEntry(var0);
         var3.next = descriptorFor[var2];
         descriptorFor[var2] = var3;
      }
   }

   private static Field[] getDeclaredFields(final Class var0) {
      return (Field[])((Field[])AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return var0.getDeclaredFields();
         }
      }));
   }

   private static class MethodSignature implements Comparator {
      Member member;
      String signature;

      static ObjectStreamClass_1_3_1.MethodSignature[] removePrivateAndSort(Member[] var0) {
         int var1 = 0;

         for(int var2 = 0; var2 < var0.length; ++var2) {
            if (!Modifier.isPrivate(var0[var2].getModifiers())) {
               ++var1;
            }
         }

         ObjectStreamClass_1_3_1.MethodSignature[] var5 = new ObjectStreamClass_1_3_1.MethodSignature[var1];
         int var3 = 0;

         for(int var4 = 0; var4 < var0.length; ++var4) {
            if (!Modifier.isPrivate(var0[var4].getModifiers())) {
               var5[var3] = new ObjectStreamClass_1_3_1.MethodSignature(var0[var4]);
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
            ObjectStreamClass_1_3_1.MethodSignature var3 = (ObjectStreamClass_1_3_1.MethodSignature)var1;
            ObjectStreamClass_1_3_1.MethodSignature var4 = (ObjectStreamClass_1_3_1.MethodSignature)var2;
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
            this.signature = ObjectStreamClass_1_3_1.getSignature((Constructor)var1);
         } else {
            this.signature = ObjectStreamClass_1_3_1.getSignature((Method)var1);
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
            var3 = var3 + ObjectStreamClass_1_3_1.getSignature((Method)var1);
            var4 = var4 + ObjectStreamClass_1_3_1.getSignature((Method)var2);
         } else if (var1 instanceof Constructor) {
            var3 = var3 + ObjectStreamClass_1_3_1.getSignature((Constructor)var1);
            var4 = var4 + ObjectStreamClass_1_3_1.getSignature((Constructor)var2);
         }

         return var3.compareTo(var4);
      }

      // $FF: synthetic method
      CompareMemberByName(Object var1) {
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
      ObjectStreamClass_1_3_1.ObjectStreamClassEntry next;
      private ObjectStreamClass_1_3_1 c;

      ObjectStreamClassEntry(ObjectStreamClass_1_3_1 var1) {
         this.c = var1;
      }

      public Object get() {
         return this.c;
      }
   }
}
