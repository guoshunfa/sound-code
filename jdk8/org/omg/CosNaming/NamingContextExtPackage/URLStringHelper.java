package org.omg.CosNaming.NamingContextExtPackage;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class URLStringHelper {
   private static String _id = "IDL:omg.org/CosNaming/NamingContextExt/URLString:1.0";
   private static TypeCode __typeCode = null;

   public static void insert(Any var0, String var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static String extract(Any var0) {
      return read(var0.create_input_stream());
   }

   public static synchronized TypeCode type() {
      if (__typeCode == null) {
         __typeCode = ORB.init().create_string_tc(0);
         __typeCode = ORB.init().create_alias_tc(id(), "URLString", __typeCode);
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static String read(InputStream var0) {
      String var1 = null;
      var1 = var0.read_string();
      return var1;
   }

   public static void write(OutputStream var0, String var1) {
      var0.write_string(var1);
   }
}
