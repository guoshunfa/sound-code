package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class LongLongSeqHelper {
   private static String _id = "IDL:omg.org/CORBA/LongLongSeq:1.0";
   private static TypeCode __typeCode = null;

   public static void insert(Any var0, long[] var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static long[] extract(Any var0) {
      return read(var0.create_input_stream());
   }

   public static synchronized TypeCode type() {
      if (__typeCode == null) {
         __typeCode = ORB.init().get_primitive_tc(TCKind.tk_longlong);
         __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
         __typeCode = ORB.init().create_alias_tc(id(), "LongLongSeq", __typeCode);
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static long[] read(InputStream var0) {
      java.lang.Object var1 = null;
      int var2 = var0.read_long();
      long[] var3 = new long[var2];
      var0.read_longlong_array(var3, 0, var2);
      return var3;
   }

   public static void write(OutputStream var0, long[] var1) {
      var0.write_long(var1.length);
      var0.write_longlong_array(var1, 0, var1.length);
   }
}
