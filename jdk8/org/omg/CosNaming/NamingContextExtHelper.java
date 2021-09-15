package org.omg.CosNaming;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;

public abstract class NamingContextExtHelper {
   private static String _id = "IDL:omg.org/CosNaming/NamingContextExt:1.0";
   private static TypeCode __typeCode = null;

   public static void insert(Any var0, NamingContextExt var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static NamingContextExt extract(Any var0) {
      return read(var0.create_input_stream());
   }

   public static synchronized TypeCode type() {
      if (__typeCode == null) {
         __typeCode = ORB.init().create_interface_tc(id(), "NamingContextExt");
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static NamingContextExt read(InputStream var0) {
      return narrow(var0.read_Object(_NamingContextExtStub.class));
   }

   public static void write(OutputStream var0, NamingContextExt var1) {
      var0.write_Object(var1);
   }

   public static NamingContextExt narrow(Object var0) {
      if (var0 == null) {
         return null;
      } else if (var0 instanceof NamingContextExt) {
         return (NamingContextExt)var0;
      } else if (!var0._is_a(id())) {
         throw new BAD_PARAM();
      } else {
         Delegate var1 = ((ObjectImpl)var0)._get_delegate();
         _NamingContextExtStub var2 = new _NamingContextExtStub();
         var2._set_delegate(var1);
         return var2;
      }
   }

   public static NamingContextExt unchecked_narrow(Object var0) {
      if (var0 == null) {
         return null;
      } else if (var0 instanceof NamingContextExt) {
         return (NamingContextExt)var0;
      } else {
         Delegate var1 = ((ObjectImpl)var0)._get_delegate();
         _NamingContextExtStub var2 = new _NamingContextExtStub();
         var2._set_delegate(var1);
         return var2;
      }
   }
}
