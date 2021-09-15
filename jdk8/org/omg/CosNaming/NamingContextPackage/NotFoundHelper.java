package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.Any;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CosNaming.NameComponentHelper;
import org.omg.CosNaming.NameHelper;

public abstract class NotFoundHelper {
   private static String _id = "IDL:omg.org/CosNaming/NamingContext/NotFound:1.0";
   private static TypeCode __typeCode = null;
   private static boolean __active = false;

   public static void insert(Any var0, NotFound var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static NotFound extract(Any var0) {
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
               StructMember[] var1 = new StructMember[2];
               TypeCode var2 = null;
               var2 = NotFoundReasonHelper.type();
               var1[0] = new StructMember("why", var2, (IDLType)null);
               var2 = NameComponentHelper.type();
               var2 = ORB.init().create_sequence_tc(0, var2);
               var2 = ORB.init().create_alias_tc(NameHelper.id(), "Name", var2);
               var1[1] = new StructMember("rest_of_name", var2, (IDLType)null);
               __typeCode = ORB.init().create_exception_tc(id(), "NotFound", var1);
               __active = false;
            }
         }
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static NotFound read(InputStream var0) {
      NotFound var1 = new NotFound();
      var0.read_string();
      var1.why = NotFoundReasonHelper.read(var0);
      var1.rest_of_name = NameHelper.read(var0);
      return var1;
   }

   public static void write(OutputStream var0, NotFound var1) {
      var0.write_string(id());
      NotFoundReasonHelper.write(var0, var1.why);
      NameHelper.write(var0, var1.rest_of_name);
   }
}
