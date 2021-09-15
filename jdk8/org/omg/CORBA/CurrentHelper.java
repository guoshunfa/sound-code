package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class CurrentHelper {
   private static String _id = "IDL:omg.org/CORBA/Current:1.0";
   private static TypeCode __typeCode = null;

   public static void insert(Any var0, Current var1) {
      throw new MARSHAL();
   }

   public static Current extract(Any var0) {
      throw new MARSHAL();
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
      } else {
         throw new BAD_PARAM();
      }
   }
}
