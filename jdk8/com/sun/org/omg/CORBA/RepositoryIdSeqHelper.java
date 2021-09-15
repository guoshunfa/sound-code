package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class RepositoryIdSeqHelper {
   private static String _id = "IDL:omg.org/CORBA/RepositoryIdSeq:1.0";
   private static TypeCode __typeCode = null;

   public static void insert(Any var0, String[] var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static String[] extract(Any var0) {
      return read(var0.create_input_stream());
   }

   public static synchronized TypeCode type() {
      if (__typeCode == null) {
         __typeCode = ORB.init().create_string_tc(0);
         __typeCode = ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", __typeCode);
         __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
         __typeCode = ORB.init().create_alias_tc(id(), "RepositoryIdSeq", __typeCode);
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static String[] read(InputStream var0) {
      String[] var1 = null;
      int var2 = var0.read_long();
      var1 = new String[var2];

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var1[var3] = RepositoryIdHelper.read(var0);
      }

      return var1;
   }

   public static void write(OutputStream var0, String[] var1) {
      var0.write_long(var1.length);

      for(int var2 = 0; var2 < var1.length; ++var2) {
         RepositoryIdHelper.write(var0, var1[var2]);
      }

   }
}
