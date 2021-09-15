package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServiceDetailHelper {
   private static TypeCode _tc;

   public static void write(OutputStream var0, ServiceDetail var1) {
      var0.write_ulong(var1.service_detail_type);
      var0.write_long(var1.service_detail.length);
      var0.write_octet_array(var1.service_detail, 0, var1.service_detail.length);
   }

   public static ServiceDetail read(InputStream var0) {
      ServiceDetail var1 = new ServiceDetail();
      var1.service_detail_type = var0.read_ulong();
      int var2 = var0.read_long();
      var1.service_detail = new byte[var2];
      var0.read_octet_array(var1.service_detail, 0, var1.service_detail.length);
      return var1;
   }

   public static ServiceDetail extract(Any var0) {
      InputStream var1 = var0.create_input_stream();
      return read(var1);
   }

   public static void insert(Any var0, ServiceDetail var1) {
      OutputStream var2 = var0.create_output_stream();
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static synchronized TypeCode type() {
      boolean var0 = true;
      StructMember[] var1 = null;
      if (_tc == null) {
         var1 = new StructMember[]{new StructMember("service_detail_type", ORB.init().get_primitive_tc(TCKind.tk_ulong), (IDLType)null), new StructMember("service_detail", ORB.init().create_sequence_tc(0, ORB.init().get_primitive_tc(TCKind.tk_octet)), (IDLType)null)};
         _tc = ORB.init().create_struct_tc(id(), "ServiceDetail", var1);
      }

      return _tc;
   }

   public static String id() {
      return "IDL:omg.org/CORBA/ServiceDetail:1.0";
   }
}
