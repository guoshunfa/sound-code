package org.omg.PortableServer;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class CurrentHelper {
   private static String _id = "IDL:omg.org/PortableServer/Current:2.3";
   private static TypeCode __typeCode = null;

   public static void insert(Any var0, Current var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static Current extract(Any var0) {
      return read(var0.create_input_stream());
   }

   public static synchronized TypeCode type() {
      if (__typeCode == null) {
         __typeCode = ORB.init().create_interface_tc(id(), "Current");
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static Current read(InputStream var0) {
      throw new MARSHAL();
   }

   public static void write(OutputStream var0, Current var1) {
      throw new MARSHAL();
   }

   public static Current narrow(Object var0) {
      if (var0 == null) {
         return null;
      } else if (var0 instanceof Current) {
         return (Current)var0;
      } else if (!var0._is_a(id())) {
         throw new BAD_PARAM();
      } else {
         return null;
      }
   }
}
