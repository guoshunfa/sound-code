package org.omg.DynamicAny;

import org.omg.CORBA.Any;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class NameDynAnyPairHelper {
   private static String _id = "IDL:omg.org/DynamicAny/NameDynAnyPair:1.0";
   private static TypeCode __typeCode = null;
   private static boolean __active = false;

   public static void insert(Any var0, NameDynAnyPair var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static NameDynAnyPair extract(Any var0) {
      return read(var0.create_input_stream());
   }

   public static synchronized TypeCode type() {
      if (__typeCode == null) {
         Class var0 = TypeCode.class;
         synchronized(TypeCode.class) {
            if (__typeCode == null) {
               if (__active) {
                  return ORB.init().create_recursive_tc(_id);
               }

               __active = true;
               StructMember[] var1 = new StructMember[2];
               TypeCode var2 = null;
               var2 = ORB.init().create_string_tc(0);
               var2 = ORB.init().create_alias_tc(FieldNameHelper.id(), "FieldName", var2);
               var1[0] = new StructMember("id", var2, (IDLType)null);
               var2 = DynAnyHelper.type();
               var1[1] = new StructMember("value", var2, (IDLType)null);
               __typeCode = ORB.init().create_struct_tc(id(), "NameDynAnyPair", var1);
               __active = false;
            }
         }
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static NameDynAnyPair read(InputStream var0) {
      NameDynAnyPair var1 = new NameDynAnyPair();
      var1.id = var0.read_string();
      var1.value = DynAnyHelper.read(var0);
      return var1;
   }

   public static void write(OutputStream var0, NameDynAnyPair var1) {
      var0.write_string(var1.id);
      DynAnyHelper.write(var0, var1.value);
   }
}
