package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.ClassFormatException;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;

public abstract class Type implements Serializable {
   protected byte type;
   protected String signature;
   public static final BasicType VOID = new BasicType((byte)12);
   public static final BasicType BOOLEAN = new BasicType((byte)4);
   public static final BasicType INT = new BasicType((byte)10);
   public static final BasicType SHORT = new BasicType((byte)9);
   public static final BasicType BYTE = new BasicType((byte)8);
   public static final BasicType LONG = new BasicType((byte)11);
   public static final BasicType DOUBLE = new BasicType((byte)7);
   public static final BasicType FLOAT = new BasicType((byte)6);
   public static final BasicType CHAR = new BasicType((byte)5);
   public static final ObjectType OBJECT = new ObjectType("java.lang.Object");
   public static final ObjectType STRING = new ObjectType("java.lang.String");
   public static final ObjectType STRINGBUFFER = new ObjectType("java.lang.StringBuffer");
   public static final ObjectType THROWABLE = new ObjectType("java.lang.Throwable");
   public static final Type[] NO_ARGS = new Type[0];
   public static final ReferenceType NULL = new ReferenceType() {
   };
   public static final Type UNKNOWN = new Type(15, "<unknown object>") {
   };
   private static int consumed_chars = 0;

   protected Type(byte t, String s) {
      this.type = t;
      this.signature = s;
   }

   public String getSignature() {
      return this.signature;
   }

   public byte getType() {
      return this.type;
   }

   public int getSize() {
      switch(this.type) {
      case 7:
      case 11:
         return 2;
      case 12:
         return 0;
      default:
         return 1;
      }
   }

   public String toString() {
      return !this.equals(NULL) && this.type < 15 ? Utility.signatureToString(this.signature, false) : this.signature;
   }

   public static String getMethodSignature(Type return_type, Type[] arg_types) {
      StringBuffer buf = new StringBuffer("(");
      int length = arg_types == null ? 0 : arg_types.length;

      for(int i = 0; i < length; ++i) {
         buf.append(arg_types[i].getSignature());
      }

      buf.append(')');
      buf.append(return_type.getSignature());
      return buf.toString();
   }

   public static final Type getType(String signature) throws StringIndexOutOfBoundsException {
      byte type = Utility.typeOfSignature(signature);
      if (type <= 12) {
         consumed_chars = 1;
         return BasicType.getType(type);
      } else {
         int dim;
         if (type != 13) {
            dim = signature.indexOf(59);
            if (dim < 0) {
               throw new ClassFormatException("Invalid signature: " + signature);
            } else {
               consumed_chars = dim + 1;
               return new ObjectType(signature.substring(1, dim).replace('/', '.'));
            }
         } else {
            dim = 0;

            do {
               ++dim;
            } while(signature.charAt(dim) == '[');

            Type t = getType(signature.substring(dim));
            consumed_chars += dim;
            return new ArrayType(t, dim);
         }
      }
   }

   public static Type getReturnType(String signature) {
      try {
         int index = signature.lastIndexOf(41) + 1;
         return getType(signature.substring(index));
      } catch (StringIndexOutOfBoundsException var2) {
         throw new ClassFormatException("Invalid method signature: " + signature);
      }
   }

   public static Type[] getArgumentTypes(String signature) {
      ArrayList vec = new ArrayList();

      try {
         if (signature.charAt(0) != '(') {
            throw new ClassFormatException("Invalid method signature: " + signature);
         }

         for(int index = 1; signature.charAt(index) != ')'; index += consumed_chars) {
            vec.add(getType(signature.substring(index)));
         }
      } catch (StringIndexOutOfBoundsException var5) {
         throw new ClassFormatException("Invalid method signature: " + signature);
      }

      Type[] types = new Type[vec.size()];
      vec.toArray(types);
      return types;
   }

   public static Type getType(Class cl) {
      if (cl == null) {
         throw new IllegalArgumentException("Class must not be null");
      } else if (cl.isArray()) {
         return getType(cl.getName());
      } else if (cl.isPrimitive()) {
         if (cl == Integer.TYPE) {
            return INT;
         } else if (cl == Void.TYPE) {
            return VOID;
         } else if (cl == Double.TYPE) {
            return DOUBLE;
         } else if (cl == Float.TYPE) {
            return FLOAT;
         } else if (cl == Boolean.TYPE) {
            return BOOLEAN;
         } else if (cl == Byte.TYPE) {
            return BYTE;
         } else if (cl == Short.TYPE) {
            return SHORT;
         } else if (cl == Byte.TYPE) {
            return BYTE;
         } else if (cl == Long.TYPE) {
            return LONG;
         } else if (cl == Character.TYPE) {
            return CHAR;
         } else {
            throw new IllegalStateException("Ooops, what primitive type is " + cl);
         }
      } else {
         return new ObjectType(cl.getName());
      }
   }

   public static String getSignature(Method meth) {
      StringBuffer sb = new StringBuffer("(");
      Class[] params = meth.getParameterTypes();

      for(int j = 0; j < params.length; ++j) {
         sb.append(getType(params[j]).getSignature());
      }

      sb.append(")");
      sb.append(getType(meth.getReturnType()).getSignature());
      return sb.toString();
   }
}
