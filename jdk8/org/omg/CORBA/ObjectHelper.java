package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ObjectHelper {
   private static String _id = "";
   private static TypeCode __typeCode = null;

   public static void insert(Any var0, Object var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static Object extract(Any var0) {
      return read(var0.create_input_stream());
   }

   public static synchronized TypeCode type() {
      if (__typeCode == null) {
         __typeCode = ORB.init().get_primitive_tc(TCKind.tk_objref);
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static Object read(InputStream var0) {
      return var0.read_Object();
   }

   public static void write(OutputStream var0, Object var1) {
      var0.write_Object(var1);
   }
}
