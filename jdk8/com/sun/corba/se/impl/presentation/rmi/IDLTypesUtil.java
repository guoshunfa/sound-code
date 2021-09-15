package com.sun.corba.se.impl.presentation.rmi;

import java.io.Externalizable;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashSet;
import java.util.Iterator;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.IDLEntity;

public final class IDLTypesUtil {
   private static final String GET_PROPERTY_PREFIX = "get";
   private static final String SET_PROPERTY_PREFIX = "set";
   private static final String IS_PROPERTY_PREFIX = "is";
   public static final int VALID_TYPE = 0;
   public static final int INVALID_TYPE = 1;
   public static final boolean FOLLOW_RMIC = true;

   public void validateRemoteInterface(Class var1) throws IDLTypeException {
      if (var1 == null) {
         throw new IllegalArgumentException();
      } else {
         String var5;
         if (!var1.isInterface()) {
            var5 = "Class " + var1 + " must be a java interface.";
            throw new IDLTypeException(var5);
         } else if (!Remote.class.isAssignableFrom(var1)) {
            var5 = "Class " + var1 + " must extend java.rmi.Remote, either directly or indirectly.";
            throw new IDLTypeException(var5);
         } else {
            Method[] var2 = var1.getMethods();

            for(int var3 = 0; var3 < var2.length; ++var3) {
               Method var4 = var2[var3];
               this.validateExceptions(var4);
            }

            this.validateConstants(var1);
         }
      }
   }

   public boolean isRemoteInterface(Class var1) {
      boolean var2 = true;

      try {
         this.validateRemoteInterface(var1);
      } catch (IDLTypeException var4) {
         var2 = false;
      }

      return var2;
   }

   public boolean isPrimitive(Class var1) {
      if (var1 == null) {
         throw new IllegalArgumentException();
      } else {
         return var1.isPrimitive();
      }
   }

   public boolean isValue(Class var1) {
      if (var1 == null) {
         throw new IllegalArgumentException();
      } else {
         return !var1.isInterface() && Serializable.class.isAssignableFrom(var1) && !Remote.class.isAssignableFrom(var1);
      }
   }

   public boolean isArray(Class var1) {
      boolean var2 = false;
      if (var1 == null) {
         throw new IllegalArgumentException();
      } else {
         if (var1.isArray()) {
            Class var3 = var1.getComponentType();
            var2 = this.isPrimitive(var3) || this.isRemoteInterface(var3) || this.isEntity(var3) || this.isException(var3) || this.isValue(var3) || this.isObjectReference(var3);
         }

         return var2;
      }
   }

   public boolean isException(Class var1) {
      if (var1 == null) {
         throw new IllegalArgumentException();
      } else {
         return this.isCheckedException(var1) && !this.isRemoteException(var1) && this.isValue(var1);
      }
   }

   public boolean isRemoteException(Class var1) {
      if (var1 == null) {
         throw new IllegalArgumentException();
      } else {
         return RemoteException.class.isAssignableFrom(var1);
      }
   }

   public boolean isCheckedException(Class var1) {
      if (var1 == null) {
         throw new IllegalArgumentException();
      } else {
         return Throwable.class.isAssignableFrom(var1) && !RuntimeException.class.isAssignableFrom(var1) && !Error.class.isAssignableFrom(var1);
      }
   }

   public boolean isObjectReference(Class var1) {
      if (var1 == null) {
         throw new IllegalArgumentException();
      } else {
         return var1.isInterface() && Object.class.isAssignableFrom(var1);
      }
   }

   public boolean isEntity(Class var1) {
      if (var1 == null) {
         throw new IllegalArgumentException();
      } else {
         Class var2 = var1.getSuperclass();
         return !var1.isInterface() && var2 != null && IDLEntity.class.isAssignableFrom(var1);
      }
   }

   public boolean isPropertyAccessorMethod(Method var1, Class var2) {
      String var3 = var1.getName();
      Class var4 = var1.getReturnType();
      Class[] var5 = var1.getParameterTypes();
      Class[] var6 = var1.getExceptionTypes();
      String var7 = null;
      if (var3.startsWith("get")) {
         if (var5.length == 0 && var4 != Void.TYPE && !this.readHasCorrespondingIsProperty(var1, var2)) {
            var7 = "get";
         }
      } else if (var3.startsWith("set")) {
         if (var4 == Void.TYPE && var5.length == 1 && (this.hasCorrespondingReadProperty(var1, var2, "get") || this.hasCorrespondingReadProperty(var1, var2, "is"))) {
            var7 = "set";
         }
      } else if (var3.startsWith("is") && var5.length == 0 && var4 == Boolean.TYPE && !this.isHasCorrespondingReadProperty(var1, var2)) {
         var7 = "is";
      }

      if (var7 != null && (!this.validPropertyExceptions(var1) || var3.length() <= var7.length())) {
         var7 = null;
      }

      return var7 != null;
   }

   private boolean hasCorrespondingReadProperty(Method var1, Class var2, String var3) {
      String var4 = var1.getName();
      Class[] var5 = var1.getParameterTypes();
      boolean var6 = false;

      try {
         String var7 = var4.replaceFirst("set", var3);
         Method var8 = var2.getMethod(var7);
         var6 = this.isPropertyAccessorMethod(var8, var2) && var8.getReturnType() == var5[0];
      } catch (Exception var9) {
      }

      return var6;
   }

   private boolean readHasCorrespondingIsProperty(Method var1, Class var2) {
      return false;
   }

   private boolean isHasCorrespondingReadProperty(Method var1, Class var2) {
      String var3 = var1.getName();
      boolean var4 = false;

      try {
         String var5 = var3.replaceFirst("is", "get");
         Method var6 = var2.getMethod(var5);
         var4 = this.isPropertyAccessorMethod(var6, var2);
      } catch (Exception var7) {
      }

      return var4;
   }

   public String getAttributeNameForProperty(String var1) {
      String var2 = null;
      String var3 = null;
      if (var1.startsWith("get")) {
         var3 = "get";
      } else if (var1.startsWith("set")) {
         var3 = "set";
      } else if (var1.startsWith("is")) {
         var3 = "is";
      }

      if (var3 != null && var3.length() < var1.length()) {
         String var4 = var1.substring(var3.length());
         if (var4.length() >= 2 && Character.isUpperCase(var4.charAt(0)) && Character.isUpperCase(var4.charAt(1))) {
            var2 = var4;
         } else {
            var2 = Character.toLowerCase(var4.charAt(0)) + var4.substring(1);
         }
      }

      return var2;
   }

   public IDLType getPrimitiveIDLTypeMapping(Class var1) {
      if (var1 == null) {
         throw new IllegalArgumentException();
      } else {
         if (var1.isPrimitive()) {
            if (var1 == Void.TYPE) {
               return new IDLType(var1, "void");
            }

            if (var1 == Boolean.TYPE) {
               return new IDLType(var1, "boolean");
            }

            if (var1 == Character.TYPE) {
               return new IDLType(var1, "wchar");
            }

            if (var1 == Byte.TYPE) {
               return new IDLType(var1, "octet");
            }

            if (var1 == Short.TYPE) {
               return new IDLType(var1, "short");
            }

            if (var1 == Integer.TYPE) {
               return new IDLType(var1, "long");
            }

            if (var1 == Long.TYPE) {
               return new IDLType(var1, "long_long");
            }

            if (var1 == Float.TYPE) {
               return new IDLType(var1, "float");
            }

            if (var1 == Double.TYPE) {
               return new IDLType(var1, "double");
            }
         }

         return null;
      }
   }

   public IDLType getSpecialCaseIDLTypeMapping(Class var1) {
      if (var1 == null) {
         throw new IllegalArgumentException();
      } else if (var1 == java.lang.Object.class) {
         return new IDLType(var1, new String[]{"java", "lang"}, "Object");
      } else if (var1 == String.class) {
         return new IDLType(var1, new String[]{"CORBA"}, "WStringValue");
      } else if (var1 == Class.class) {
         return new IDLType(var1, new String[]{"javax", "rmi", "CORBA"}, "ClassDesc");
      } else if (var1 == Serializable.class) {
         return new IDLType(var1, new String[]{"java", "io"}, "Serializable");
      } else if (var1 == Externalizable.class) {
         return new IDLType(var1, new String[]{"java", "io"}, "Externalizable");
      } else if (var1 == Remote.class) {
         return new IDLType(var1, new String[]{"java", "rmi"}, "Remote");
      } else {
         return var1 == Object.class ? new IDLType(var1, "Object") : null;
      }
   }

   private void validateExceptions(Method var1) throws IDLTypeException {
      Class[] var2 = var1.getExceptionTypes();
      boolean var3 = false;

      int var4;
      Class var5;
      for(var4 = 0; var4 < var2.length; ++var4) {
         var5 = var2[var4];
         if (this.isRemoteExceptionOrSuperClass(var5)) {
            var3 = true;
            break;
         }
      }

      if (!var3) {
         String var7 = "Method '" + var1 + "' must throw at least one exception of type java.rmi.RemoteException or one of its super-classes";
         throw new IDLTypeException(var7);
      } else {
         for(var4 = 0; var4 < var2.length; ++var4) {
            var5 = var2[var4];
            if (this.isCheckedException(var5) && !this.isValue(var5) && !this.isRemoteException(var5)) {
               String var6 = "Exception '" + var5 + "' on method '" + var1 + "' is not a allowed RMI/IIOP exception type";
               throw new IDLTypeException(var6);
            }
         }

      }
   }

   private boolean validPropertyExceptions(Method var1) {
      Class[] var2 = var1.getExceptionTypes();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         Class var4 = var2[var3];
         if (this.isCheckedException(var4) && !this.isRemoteException(var4)) {
            return false;
         }
      }

      return true;
   }

   private boolean isRemoteExceptionOrSuperClass(Class var1) {
      return var1 == RemoteException.class || var1 == IOException.class || var1 == Exception.class || var1 == Throwable.class;
   }

   private void validateDirectInterfaces(Class var1) throws IDLTypeException {
      Class[] var2 = var1.getInterfaces();
      if (var2.length >= 2) {
         HashSet var3 = new HashSet();
         HashSet var4 = new HashSet();

         for(int var5 = 0; var5 < var2.length; ++var5) {
            Class var6 = var2[var5];
            Method[] var7 = var6.getMethods();
            var4.clear();

            for(int var8 = 0; var8 < var7.length; ++var8) {
               var4.add(var7[var8].getName());
            }

            Iterator var11 = var4.iterator();

            while(var11.hasNext()) {
               String var9 = (String)var11.next();
               if (var3.contains(var9)) {
                  String var10 = "Class " + var1 + " inherits method " + var9 + " from multiple direct interfaces.";
                  throw new IDLTypeException(var10);
               }

               var3.add(var9);
            }
         }

      }
   }

   private void validateConstants(final Class var1) throws IDLTypeException {
      Field[] var2 = null;

      try {
         var2 = (Field[])((Field[])AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public java.lang.Object run() throws Exception {
               return var1.getFields();
            }
         }));
      } catch (PrivilegedActionException var7) {
         IDLTypeException var4 = new IDLTypeException();
         var4.initCause(var7);
         throw var4;
      }

      for(int var3 = 0; var3 < var2.length; ++var3) {
         Field var8 = var2[var3];
         Class var5 = var8.getType();
         if (var5 != String.class && !this.isPrimitive(var5)) {
            String var6 = "Constant field '" + var8.getName() + "' in class '" + var8.getDeclaringClass().getName() + "' has invalid type' " + var8.getType() + "'. Constants in RMI/IIOP interfaces can only have primitive types and java.lang.String types.";
            throw new IDLTypeException(var6);
         }
      }

   }
}
