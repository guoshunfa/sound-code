package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServerHelper {
   private static String _id = "IDL:activation/Server:1.0";
   private static TypeCode __typeCode = null;

   public static void insert(Any var0, Server var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static Server extract(Any var0) {
      return read(var0.create_input_stream());
   }

   public static synchronized TypeCode type() {
      if (__typeCode == null) {
         __typeCode = ORB.init().create_interface_tc(id(), "Server");
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static Server read(InputStream var0) {
      return narrow(var0.read_Object(_ServerStub.class));
   }

   public static void write(OutputStream var0, Server var1) {
      var0.write_Object(var1);
   }

   public static Server narrow(Object var0) {
      if (var0 == null) {
         return null;
      } else if (var0 instanceof Server) {
         return (Server)var0;
      } else if (!var0._is_a(id())) {
         throw new BAD_PARAM();
      } else {
         Delegate var1 = ((ObjectImpl)var0)._get_delegate();
         _ServerStub var2 = new _ServerStub();
         var2._set_delegate(var1);
         return var2;
      }
   }

   public static Server unchecked_narrow(Object var0) {
      if (var0 == null) {
         return null;
      } else if (var0 instanceof Server) {
         return (Server)var0;
      } else {
         Delegate var1 = ((ObjectImpl)var0)._get_delegate();
         _ServerStub var2 = new _ServerStub();
         var2._set_delegate(var1);
         return var2;
      }
   }
}
