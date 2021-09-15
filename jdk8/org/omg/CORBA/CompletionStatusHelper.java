package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class CompletionStatusHelper {
   private static String _id = "IDL:omg.org/CORBA/CompletionStatus:1.0";
   private static TypeCode __typeCode = null;

   public static void insert(Any var0, CompletionStatus var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static CompletionStatus extract(Any var0) {
      return read(var0.create_input_stream());
   }

   public static synchronized TypeCode type() {
      if (__typeCode == null) {
         __typeCode = ORB.init().create_enum_tc(id(), "CompletionStatus", new String[]{"COMPLETED_YES", "COMPLETED_NO", "COMPLETED_MAYBE"});
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static CompletionStatus read(InputStream var0) {
      return CompletionStatus.from_int(var0.read_long());
   }

   public static void write(OutputStream var0, CompletionStatus var1) {
      var0.write_long(var1.value());
   }
}
