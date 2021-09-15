package org.omg.CORBA;

import java.io.Serializable;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ValueBaseHelper {
   private static String _id = "IDL:omg.org/CORBA/ValueBase:1.0";
   private static TypeCode __typeCode = null;

   public static void insert(Any var0, Serializable var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static Serializable extract(Any var0) {
      return read(var0.create_input_stream());
   }

   public static synchronized TypeCode type() {
      if (__typeCode == null) {
         __typeCode = ORB.init().get_primitive_tc(TCKind.tk_value);
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static Serializable read(InputStream var0) {
      return ((org.omg.CORBA_2_3.portable.InputStream)var0).read_value();
   }

   public static void write(OutputStream var0, Serializable var1) {
      ((org.omg.CORBA_2_3.portable.OutputStream)var0).write_value(var1);
   }
}
