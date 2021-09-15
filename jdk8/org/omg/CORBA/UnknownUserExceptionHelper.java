package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class UnknownUserExceptionHelper {
   private static String _id = "IDL:omg.org/CORBA/UnknownUserException:1.0";
   private static TypeCode __typeCode = null;
   private static boolean __active = false;

   public static void insert(Any var0, UnknownUserException var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static UnknownUserException extract(Any var0) {
      return read(var0.create_input_stream());
   }

   public static synchronized TypeCode type() {
      if (__typeCode == null) {
         Class var0 = TypeCode.class;
         synchronized(TypeCode.class) {
            if (__typeCode == null) {
               if (__active) {
                  return ORB.init().create_recursive_tc(_id);
               }

               __active = true;
               StructMember[] var1 = new StructMember[1];
               TypeCode var2 = null;
               var2 = ORB.init().get_primitive_tc(TCKind.tk_any);
               var1[0] = new StructMember("except", var2, (IDLType)null);
               __typeCode = ORB.init().create_exception_tc(id(), "UnknownUserException", var1);
               __active = false;
            }
         }
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static UnknownUserException read(InputStream var0) {
      UnknownUserException var1 = new UnknownUserException();
      var0.read_string();
      var1.except = var0.read_any();
      return var1;
   }

   public static void write(OutputStream var0, UnknownUserException var1) {
      var0.write_string(id());
      var0.write_any(var1.except);
   }
}
