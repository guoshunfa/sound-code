package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class UnionMemberHelper {
   private static String _id = "IDL:omg.org/CORBA/UnionMember:1.0";
   private static TypeCode __typeCode = null;
   private static boolean __active = false;

   public static void insert(Any var0, UnionMember var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static UnionMember extract(Any var0) {
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
               StructMember[] var1 = new StructMember[4];
               TypeCode var2 = null;
               var2 = ORB.init().create_string_tc(0);
               var2 = ORB.init().create_alias_tc(IdentifierHelper.id(), "Identifier", var2);
               var1[0] = new StructMember("name", var2, (IDLType)null);
               var2 = ORB.init().get_primitive_tc(TCKind.tk_any);
               var1[1] = new StructMember("label", var2, (IDLType)null);
               var2 = ORB.init().get_primitive_tc(TCKind.tk_TypeCode);
               var1[2] = new StructMember("type", var2, (IDLType)null);
               var2 = IDLTypeHelper.type();
               var1[3] = new StructMember("type_def", var2, (IDLType)null);
               __typeCode = ORB.init().create_struct_tc(id(), "UnionMember", var1);
               __active = false;
            }
         }
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static UnionMember read(InputStream var0) {
      UnionMember var1 = new UnionMember();
      var1.name = var0.read_string();
      var1.label = var0.read_any();
      var1.type = var0.read_TypeCode();
      var1.type_def = IDLTypeHelper.read(var0);
      return var1;
   }

   public static void write(OutputStream var0, UnionMember var1) {
      var0.write_string(var1.name);
      var0.write_any(var1.label);
      var0.write_TypeCode(var1.type);
      IDLTypeHelper.write(var0, var1.type_def);
   }
}
