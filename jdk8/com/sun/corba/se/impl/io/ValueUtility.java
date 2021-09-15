package com.sun.corba.se.impl.io;

import com.sun.corba.se.impl.util.RepositoryId;
import com.sun.org.omg.CORBA.AttributeDescription;
import com.sun.org.omg.CORBA.Initializer;
import com.sun.org.omg.CORBA.OperationDescription;
import com.sun.org.omg.CORBA._IDLTypeStub;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.SendingContext.CodeBase;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.rmi.Remote;
import java.util.Iterator;
import java.util.Stack;
import javax.rmi.CORBA.ValueHandler;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ValueMember;
import sun.corba.JavaCorbaAccess;
import sun.corba.SharedSecrets;

public class ValueUtility {
   public static final short PRIVATE_MEMBER = 0;
   public static final short PUBLIC_MEMBER = 1;
   private static final String[] primitiveConstants = new String[]{null, null, "S", "I", "S", "I", "F", "D", "Z", "C", "B", null, null, null, null, null, null, null, null, null, null, null, null, "J", "J", "D", "C", null, null, null, null, null, null};

   public static String getSignature(ValueMember var0) throws ClassNotFoundException {
      if (var0.type.kind().value() != 30 && var0.type.kind().value() != 29 && var0.type.kind().value() != 14) {
         return primitiveConstants[var0.type.kind().value()];
      } else {
         Class var1 = RepositoryId.cache.getId(var0.id).getClassFromType();
         return ObjectStreamClass.getSignature(var1);
      }
   }

   public static FullValueDescription translate(ORB var0, ObjectStreamClass var1, ValueHandler var2) {
      FullValueDescription var3 = new FullValueDescription();
      Class var4 = var1.forClass();
      ValueHandlerImpl var5 = (ValueHandlerImpl)var2;
      String var6 = var5.createForAnyType(var4);
      var3.name = var5.getUnqualifiedName(var6);
      if (var3.name == null) {
         var3.name = "";
      }

      var3.id = var5.getRMIRepositoryID(var4);
      if (var3.id == null) {
         var3.id = "";
      }

      var3.is_abstract = ObjectStreamClassCorbaExt.isAbstractInterface(var4);
      var3.is_custom = var1.hasWriteObject() || var1.isExternalizable();
      var3.defined_in = var5.getDefinedInId(var6);
      if (var3.defined_in == null) {
         var3.defined_in = "";
      }

      var3.version = var5.getSerialVersionUID(var6);
      if (var3.version == null) {
         var3.version = "";
      }

      var3.operations = new OperationDescription[0];
      var3.attributes = new AttributeDescription[0];
      ValueUtility.IdentityKeyValueStack var7 = new ValueUtility.IdentityKeyValueStack();
      var3.members = translateMembers(var0, var1, var2, var7);
      var3.initializers = new Initializer[0];
      Class[] var8 = var1.forClass().getInterfaces();
      int var9 = 0;
      var3.supported_interfaces = new String[var8.length];

      int var10;
      for(var10 = 0; var10 < var8.length; ++var10) {
         var3.supported_interfaces[var10] = var5.createForAnyType(var8[var10]);
         if (!Remote.class.isAssignableFrom(var8[var10]) || !Modifier.isPublic(var8[var10].getModifiers())) {
            ++var9;
         }
      }

      var3.abstract_base_values = new String[var9];

      for(var10 = 0; var10 < var8.length; ++var10) {
         if (!Remote.class.isAssignableFrom(var8[var10]) || !Modifier.isPublic(var8[var10].getModifiers())) {
            var3.abstract_base_values[var10] = var5.createForAnyType(var8[var10]);
         }
      }

      var3.is_truncatable = false;
      Class var11 = var1.forClass().getSuperclass();
      if (Serializable.class.isAssignableFrom(var11)) {
         var3.base_value = var5.getRMIRepositoryID(var11);
      } else {
         var3.base_value = "";
      }

      var3.type = var0.get_primitive_tc(TCKind.tk_value);
      return var3;
   }

   private static ValueMember[] translateMembers(ORB var0, ObjectStreamClass var1, ValueHandler var2, ValueUtility.IdentityKeyValueStack var3) {
      ValueHandlerImpl var4 = (ValueHandlerImpl)var2;
      ObjectStreamField[] var5 = var1.getFields();
      int var6 = var5.length;
      ValueMember[] var7 = new ValueMember[var6];

      for(int var8 = 0; var8 < var6; ++var8) {
         String var9 = var4.getRMIRepositoryID(var5[var8].getClazz());
         var7[var8] = new ValueMember();
         var7[var8].name = var5[var8].getName();
         var7[var8].id = var9;
         var7[var8].defined_in = var4.getDefinedInId(var9);
         var7[var8].version = "1.0";
         var7[var8].type_def = new _IDLTypeStub();
         if (var5[var8].getField() == null) {
            var7[var8].access = 0;
         } else {
            int var10 = var5[var8].getField().getModifiers();
            if (Modifier.isPublic(var10)) {
               var7[var8].access = 1;
            } else {
               var7[var8].access = 0;
            }
         }

         switch(var5[var8].getTypeCode()) {
         case 'B':
            var7[var8].type = var0.get_primitive_tc(TCKind.tk_octet);
            break;
         case 'C':
            var7[var8].type = var0.get_primitive_tc(var4.getJavaCharTCKind());
            break;
         case 'D':
            var7[var8].type = var0.get_primitive_tc(TCKind.tk_double);
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
            var7[var8].type = createTypeCodeForClassInternal(var0, var5[var8].getClazz(), var4, var3);
            var7[var8].id = var4.createForAnyType(var5[var8].getType());
            break;
         case 'F':
            var7[var8].type = var0.get_primitive_tc(TCKind.tk_float);
            break;
         case 'I':
            var7[var8].type = var0.get_primitive_tc(TCKind.tk_long);
            break;
         case 'J':
            var7[var8].type = var0.get_primitive_tc(TCKind.tk_longlong);
            break;
         case 'S':
            var7[var8].type = var0.get_primitive_tc(TCKind.tk_short);
            break;
         case 'Z':
            var7[var8].type = var0.get_primitive_tc(TCKind.tk_boolean);
         }
      }

      return var7;
   }

   private static boolean exists(String var0, String[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (var0.equals(var1[var2])) {
            return true;
         }
      }

      return false;
   }

   public static boolean isAssignableFrom(String var0, FullValueDescription var1, CodeBase var2) {
      if (exists(var0, var1.supported_interfaces)) {
         return true;
      } else if (var0.equals(var1.id)) {
         return true;
      } else if (var1.base_value != null && !var1.base_value.equals("")) {
         FullValueDescription var3 = var2.meta(var1.base_value);
         return isAssignableFrom(var0, var3, var2);
      } else {
         return false;
      }
   }

   public static TypeCode createTypeCodeForClass(ORB var0, Class var1, ValueHandler var2) {
      ValueUtility.IdentityKeyValueStack var3 = new ValueUtility.IdentityKeyValueStack();
      TypeCode var4 = createTypeCodeForClassInternal(var0, var1, var2, var3);
      return var4;
   }

   private static TypeCode createTypeCodeForClassInternal(ORB var0, Class var1, ValueHandler var2, ValueUtility.IdentityKeyValueStack var3) {
      TypeCode var4 = null;
      String var5 = (String)var3.get(var1);
      if (var5 != null) {
         return var0.create_recursive_tc(var5);
      } else {
         var5 = var2.getRMIRepositoryID(var1);
         if (var5 == null) {
            var5 = "";
         }

         var3.push(var1, var5);
         var4 = createTypeCodeInternal(var0, var1, var2, var5, var3);
         var3.pop();
         return var4;
      }
   }

   private static TypeCode createTypeCodeInternal(ORB var0, Class var1, ValueHandler var2, String var3, ValueUtility.IdentityKeyValueStack var4) {
      TypeCode var7;
      if (var1.isArray()) {
         Class var11 = var1.getComponentType();
         TypeCode var12;
         if (var11.isPrimitive()) {
            var12 = getPrimitiveTypeCodeForClass(var0, var11, var2);
         } else {
            var12 = createTypeCodeForClassInternal(var0, var11, var2, var4);
         }

         var7 = var0.create_sequence_tc(0, var12);
         return var0.create_value_box_tc(var3, "Sequence", var7);
      } else if (var1 == String.class) {
         TypeCode var10 = var0.create_string_tc(0);
         return var0.create_value_box_tc(var3, "StringValue", var10);
      } else if (Remote.class.isAssignableFrom(var1)) {
         return var0.get_primitive_tc(TCKind.tk_objref);
      } else if (Object.class.isAssignableFrom(var1)) {
         return var0.get_primitive_tc(TCKind.tk_objref);
      } else {
         ObjectStreamClass var5 = ObjectStreamClass.lookup(var1);
         if (var5 == null) {
            return var0.create_value_box_tc(var3, "Value", var0.get_primitive_tc(TCKind.tk_value));
         } else {
            int var6 = var5.isCustomMarshaled() ? 1 : 0;
            var7 = null;
            Class var8 = var1.getSuperclass();
            if (var8 != null && Serializable.class.isAssignableFrom(var8)) {
               var7 = createTypeCodeForClassInternal(var0, var8, var2, var4);
            }

            ValueMember[] var9 = translateMembers(var0, var5, var2, var4);
            return var0.create_value_tc(var3, var1.getName(), (short)var6, var7, var9);
         }
      }
   }

   public static TypeCode getPrimitiveTypeCodeForClass(ORB var0, Class var1, ValueHandler var2) {
      if (var1 == Integer.TYPE) {
         return var0.get_primitive_tc(TCKind.tk_long);
      } else if (var1 == Byte.TYPE) {
         return var0.get_primitive_tc(TCKind.tk_octet);
      } else if (var1 == Long.TYPE) {
         return var0.get_primitive_tc(TCKind.tk_longlong);
      } else if (var1 == Float.TYPE) {
         return var0.get_primitive_tc(TCKind.tk_float);
      } else if (var1 == Double.TYPE) {
         return var0.get_primitive_tc(TCKind.tk_double);
      } else if (var1 == Short.TYPE) {
         return var0.get_primitive_tc(TCKind.tk_short);
      } else if (var1 == Character.TYPE) {
         return var0.get_primitive_tc(((ValueHandlerImpl)var2).getJavaCharTCKind());
      } else {
         return var1 == Boolean.TYPE ? var0.get_primitive_tc(TCKind.tk_boolean) : var0.get_primitive_tc(TCKind.tk_any);
      }
   }

   static {
      SharedSecrets.setJavaCorbaAccess(new JavaCorbaAccess() {
         public ValueHandlerImpl newValueHandlerImpl() {
            return ValueHandlerImpl.getInstance();
         }

         public Class<?> loadClass(String var1) throws ClassNotFoundException {
            return Thread.currentThread().getContextClassLoader() != null ? Thread.currentThread().getContextClassLoader().loadClass(var1) : ClassLoader.getSystemClassLoader().loadClass(var1);
         }
      });
   }

   private static class IdentityKeyValueStack {
      Stack pairs;

      private IdentityKeyValueStack() {
         this.pairs = null;
      }

      java.lang.Object get(java.lang.Object var1) {
         if (this.pairs == null) {
            return null;
         } else {
            Iterator var2 = this.pairs.iterator();

            ValueUtility.IdentityKeyValueStack.KeyValuePair var3;
            do {
               if (!var2.hasNext()) {
                  return null;
               }

               var3 = (ValueUtility.IdentityKeyValueStack.KeyValuePair)var2.next();
            } while(var3.key != var1);

            return var3.value;
         }
      }

      void push(java.lang.Object var1, java.lang.Object var2) {
         if (this.pairs == null) {
            this.pairs = new Stack();
         }

         this.pairs.push(new ValueUtility.IdentityKeyValueStack.KeyValuePair(var1, var2));
      }

      void pop() {
         this.pairs.pop();
      }

      // $FF: synthetic method
      IdentityKeyValueStack(java.lang.Object var1) {
         this();
      }

      private static class KeyValuePair {
         java.lang.Object key;
         java.lang.Object value;

         KeyValuePair(java.lang.Object var1, java.lang.Object var2) {
            this.key = var1;
            this.value = var2;
         }

         boolean equals(ValueUtility.IdentityKeyValueStack.KeyValuePair var1) {
            return var1.key == this.key;
         }
      }
   }
}
