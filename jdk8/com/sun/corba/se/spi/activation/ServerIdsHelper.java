package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServerIdsHelper {
   private static String _id = "IDL:activation/ServerIds:1.0";
   private static TypeCode __typeCode = null;

   public static void insert(Any var0, int[] var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static int[] extract(Any var0) {
      return read(var0.create_input_stream());
   }

   public static synchronized TypeCode type() {
      if (__typeCode == null) {
         __typeCode = ORB.init().get_primitive_tc(TCKind.tk_long);
         __typeCode = ORB.init().create_alias_tc(ServerIdHelper.id(), "ServerId", __typeCode);
         __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
         __typeCode = ORB.init().create_alias_tc(id(), "ServerIds", __typeCode);
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static int[] read(InputStream var0) {
      Object var1 = null;
      int var2 = var0.read_long();
      int[] var4 = new int[var2];

      for(int var3 = 0; var3 < var4.length; ++var3) {
         var4[var3] = ServerIdHelper.read(var0);
      }

      return var4;
   }

   public static void write(OutputStream var0, int[] var1) {
      var0.write_long(var1.length);

      for(int var2 = 0; var2 < var1.length; ++var2) {
         ServerIdHelper.write(var0, var1[var2]);
      }

   }
}
