package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class FloatSeqHelper {
   private static String _id = "IDL:omg.org/CORBA/FloatSeq:1.0";
   private static TypeCode __typeCode = null;

   public static void insert(Any var0, float[] var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static float[] extract(Any var0) {
      return read(var0.create_input_stream());
   }

   public static synchronized TypeCode type() {
      if (__typeCode == null) {
         __typeCode = ORB.init().get_primitive_tc(TCKind.tk_float);
         __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
         __typeCode = ORB.init().create_alias_tc(id(), "FloatSeq", __typeCode);
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static float[] read(InputStream var0) {
      java.lang.Object var1 = null;
      int var2 = var0.read_long();
      float[] var3 = new float[var2];
      var0.read_float_array(var3, 0, var2);
      return var3;
   }

   public static void write(OutputStream var0, float[] var1) {
      var0.write_long(var1.length);
      var0.write_float_array(var1, 0, var1.length);
   }
}
