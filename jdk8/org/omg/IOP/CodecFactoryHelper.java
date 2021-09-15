package org.omg.IOP;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class CodecFactoryHelper {
   private static String _id = "IDL:omg.org/IOP/CodecFactory:1.0";
   private static TypeCode __typeCode = null;

   public static void insert(Any var0, CodecFactory var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static CodecFactory extract(Any var0) {
      return read(var0.create_input_stream());
   }

   public static synchronized TypeCode type() {
      if (__typeCode == null) {
         __typeCode = ORB.init().create_interface_tc(id(), "CodecFactory");
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static CodecFactory read(InputStream var0) {
      throw new MARSHAL();
   }

   public static void write(OutputStream var0, CodecFactory var1) {
      throw new MARSHAL();
   }

   public static CodecFactory narrow(Object var0) {
      if (var0 == null) {
         return null;
      } else if (var0 instanceof CodecFactory) {
         return (CodecFactory)var0;
      } else {
         throw new BAD_PARAM();
      }
   }

   public static CodecFactory unchecked_narrow(Object var0) {
      if (var0 == null) {
         return null;
      } else if (var0 instanceof CodecFactory) {
         return (CodecFactory)var0;
      } else {
         throw new BAD_PARAM();
      }
   }
}
