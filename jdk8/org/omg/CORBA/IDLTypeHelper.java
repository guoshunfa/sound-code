package org.omg.CORBA;

import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;

public abstract class IDLTypeHelper {
   private static String _id = "IDL:omg.org/CORBA/IDLType:1.0";
   private static TypeCode __typeCode = null;

   public static void insert(Any var0, IDLType var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static IDLType extract(Any var0) {
      return read(var0.create_input_stream());
   }

   public static synchronized TypeCode type() {
      if (__typeCode == null) {
         __typeCode = ORB.init().create_interface_tc(id(), "IDLType");
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static IDLType read(InputStream var0) {
      return narrow(var0.read_Object(_IDLTypeStub.class));
   }

   public static void write(OutputStream var0, IDLType var1) {
      var0.write_Object(var1);
   }

   public static IDLType narrow(Object var0) {
      if (var0 == null) {
         return null;
      } else if (var0 instanceof IDLType) {
         return (IDLType)var0;
      } else if (!var0._is_a(id())) {
         throw new BAD_PARAM();
      } else {
         Delegate var1 = ((ObjectImpl)var0)._get_delegate();
         return new _IDLTypeStub(var1);
      }
   }
}
