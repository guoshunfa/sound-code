package org.omg.Messaging;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class SyncScopeHelper {
   private static String _id = "IDL:omg.org/Messaging/SyncScope:1.0";
   private static TypeCode __typeCode = null;

   public static void insert(Any var0, short var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static short extract(Any var0) {
      return read(var0.create_input_stream());
   }

   public static synchronized TypeCode type() {
      if (__typeCode == null) {
         __typeCode = ORB.init().get_primitive_tc(TCKind.tk_short);
         __typeCode = ORB.init().create_alias_tc(id(), "SyncScope", __typeCode);
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static short read(InputStream var0) {
      boolean var1 = false;
      short var2 = var0.read_short();
      return var2;
   }

   public static void write(OutputStream var0, short var1) {
      var0.write_short(var1);
   }
}
