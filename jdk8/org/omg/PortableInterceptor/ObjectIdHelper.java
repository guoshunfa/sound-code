package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.OctetSeqHelper;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ObjectIdHelper {
   private static String _id = "IDL:omg.org/PortableInterceptor/ObjectId:1.0";
   private static TypeCode __typeCode = null;

   public static void insert(Any var0, byte[] var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static byte[] extract(Any var0) {
      return read(var0.create_input_stream());
   }

   public static synchronized TypeCode type() {
      if (__typeCode == null) {
         __typeCode = ORB.init().get_primitive_tc(TCKind.tk_octet);
         __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
         __typeCode = ORB.init().create_alias_tc(OctetSeqHelper.id(), "OctetSeq", __typeCode);
         __typeCode = ORB.init().create_alias_tc(id(), "ObjectId", __typeCode);
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static byte[] read(InputStream var0) {
      Object var1 = null;
      byte[] var2 = OctetSeqHelper.read(var0);
      return var2;
   }

   public static void write(OutputStream var0, byte[] var1) {
      OctetSeqHelper.write(var0, var1);
   }
}
