package com.sun.corba.se.impl.corba;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class AnyImplHelper {
   private static String _id = "IDL:omg.org/CORBA/Any:1.0";
   private static TypeCode __typeCode = null;

   public static void insert(Any var0, Any var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static Any extract(Any var0) {
      return read(var0.create_input_stream());
   }

   public static synchronized TypeCode type() {
      if (__typeCode == null) {
         __typeCode = ORB.init().get_primitive_tc(TCKind.tk_any);
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static Any read(InputStream var0) {
      return var0.read_any();
   }

   public static void write(OutputStream var0, Any var1) {
      var0.write_any(var1);
   }
}
