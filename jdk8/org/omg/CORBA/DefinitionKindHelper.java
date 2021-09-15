package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class DefinitionKindHelper {
   private static String _id = "IDL:omg.org/CORBA/DefinitionKind:1.0";
   private static TypeCode __typeCode = null;

   public static void insert(Any var0, DefinitionKind var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static DefinitionKind extract(Any var0) {
      return read(var0.create_input_stream());
   }

   public static synchronized TypeCode type() {
      if (__typeCode == null) {
         __typeCode = ORB.init().create_enum_tc(id(), "DefinitionKind", new String[]{"dk_none", "dk_all", "dk_Attribute", "dk_Constant", "dk_Exception", "dk_Interface", "dk_Module", "dk_Operation", "dk_Typedef", "dk_Alias", "dk_Struct", "dk_Union", "dk_Enum", "dk_Primitive", "dk_String", "dk_Sequence", "dk_Array", "dk_Repository", "dk_Wstring", "dk_Fixed", "dk_Value", "dk_ValueBox", "dk_ValueMember", "dk_Native"});
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static DefinitionKind read(InputStream var0) {
      return DefinitionKind.from_int(var0.read_long());
   }

   public static void write(OutputStream var0, DefinitionKind var1) {
      var0.write_long(var1.value());
   }
}
