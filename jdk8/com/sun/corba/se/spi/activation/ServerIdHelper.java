package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServerIdHelper {
   private static String _id = "IDL:activation/ServerId:1.0";
   private static TypeCode __typeCode = null;

   public static void insert(Any var0, int var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static int extract(Any var0) {
      return read(var0.create_input_stream());
   }

   public static synchronized TypeCode type() {
      if (__typeCode == null) {
         __typeCode = ORB.init().get_primitive_tc(TCKind.tk_long);
         __typeCode = ORB.init().create_alias_tc(id(), "ServerId", __typeCode);
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static int read(InputStream var0) {
      boolean var1 = false;
      int var2 = var0.read_long();
      return var2;
   }

   public static void write(OutputStream var0, int var1) {
      var0.write_long(var1);
   }
}
