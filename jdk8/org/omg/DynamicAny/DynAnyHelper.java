package org.omg.DynamicAny;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;

public abstract class DynAnyHelper {
   private static String _id = "IDL:omg.org/DynamicAny/DynAny:1.0";
   private static TypeCode __typeCode = null;

   public static void insert(Any var0, DynAny var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static DynAny extract(Any var0) {
      return read(var0.create_input_stream());
   }

   public static synchronized TypeCode type() {
      if (__typeCode == null) {
         __typeCode = ORB.init().create_interface_tc(id(), "DynAny");
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static DynAny read(InputStream var0) {
      throw new MARSHAL();
   }

   public static void write(OutputStream var0, DynAny var1) {
      throw new MARSHAL();
   }

   public static DynAny narrow(Object var0) {
      if (var0 == null) {
         return null;
      } else if (var0 instanceof DynAny) {
         return (DynAny)var0;
      } else if (!var0._is_a(id())) {
         throw new BAD_PARAM();
      } else {
         Delegate var1 = ((ObjectImpl)var0)._get_delegate();
         _DynAnyStub var2 = new _DynAnyStub();
         var2._set_delegate(var1);
         return var2;
      }
   }

   public static DynAny unchecked_narrow(Object var0) {
      if (var0 == null) {
         return null;
      } else if (var0 instanceof DynAny) {
         return (DynAny)var0;
      } else {
         Delegate var1 = ((ObjectImpl)var0)._get_delegate();
         _DynAnyStub var2 = new _DynAnyStub();
         var2._set_delegate(var1);
         return var2;
      }
   }
}
