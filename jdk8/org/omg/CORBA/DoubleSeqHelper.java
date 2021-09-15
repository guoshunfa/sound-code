package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class DoubleSeqHelper {
   private static String _id = "IDL:omg.org/CORBA/DoubleSeq:1.0";
   private static TypeCode __typeCode = null;

   public static void insert(Any var0, double[] var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static double[] extract(Any var0) {
      return read(var0.create_input_stream());
   }

   public static synchronized TypeCode type() {
      if (__typeCode == null) {
         __typeCode = ORB.init().get_primitive_tc(TCKind.tk_double);
         __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
         __typeCode = ORB.init().create_alias_tc(id(), "DoubleSeq", __typeCode);
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static double[] read(InputStream var0) {
      java.lang.Object var1 = null;
      int var2 = var0.read_long();
      double[] var3 = new double[var2];
      var0.read_double_array(var3, 0, var2);
      return var3;
   }

   public static void write(OutputStream var0, double[] var1) {
      var0.write_long(var1.length);
      var0.write_double_array(var1, 0, var1.length);
   }
}
