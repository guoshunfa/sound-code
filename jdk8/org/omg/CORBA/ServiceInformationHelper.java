package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServiceInformationHelper {
   private static TypeCode _tc;

   public static void write(OutputStream var0, ServiceInformation var1) {
      var0.write_long(var1.service_options.length);
      var0.write_ulong_array(var1.service_options, 0, var1.service_options.length);
      var0.write_long(var1.service_details.length);

      for(int var2 = 0; var2 < var1.service_details.length; ++var2) {
         ServiceDetailHelper.write(var0, var1.service_details[var2]);
      }

   }

   public static ServiceInformation read(InputStream var0) {
      ServiceInformation var1 = new ServiceInformation();
      int var2 = var0.read_long();
      var1.service_options = new int[var2];
      var0.read_ulong_array(var1.service_options, 0, var1.service_options.length);
      var2 = var0.read_long();
      var1.service_details = new ServiceDetail[var2];

      for(int var3 = 0; var3 < var1.service_details.length; ++var3) {
         var1.service_details[var3] = ServiceDetailHelper.read(var0);
      }

      return var1;
   }

   public static ServiceInformation extract(Any var0) {
      InputStream var1 = var0.create_input_stream();
      return read(var1);
   }

   public static void insert(Any var0, ServiceInformation var1) {
      OutputStream var2 = var0.create_output_stream();
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static synchronized TypeCode type() {
      boolean var0 = true;
      StructMember[] var1 = null;
      if (_tc == null) {
         var1 = new StructMember[]{new StructMember("service_options", ORB.init().create_sequence_tc(0, ORB.init().get_primitive_tc(TCKind.tk_ulong)), (IDLType)null), new StructMember("service_details", ORB.init().create_sequence_tc(0, ServiceDetailHelper.type()), (IDLType)null)};
         _tc = ORB.init().create_struct_tc(id(), "ServiceInformation", var1);
      }

      return _tc;
   }

   public static String id() {
      return "IDL:omg.org/CORBA/ServiceInformation:1.0";
   }
}
