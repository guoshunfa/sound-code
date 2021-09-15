package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ObjectReferenceTemplateHelper {
   private static String _id = "IDL:omg.org/PortableInterceptor/ObjectReferenceTemplate:1.0";
   private static TypeCode __typeCode = null;
   private static boolean __active = false;

   public static void insert(Any var0, ObjectReferenceTemplate var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static ObjectReferenceTemplate extract(Any var0) {
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
               ValueMember[] var1 = new ValueMember[0];
               Object var2 = null;
               __typeCode = ORB.init().create_value_tc(_id, "ObjectReferenceTemplate", (short)2, (TypeCode)null, var1);
               __active = false;
            }
         }
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static ObjectReferenceTemplate read(InputStream var0) {
      return (ObjectReferenceTemplate)((org.omg.CORBA_2_3.portable.InputStream)var0).read_value(id());
   }

   public static void write(OutputStream var0, ObjectReferenceTemplate var1) {
      ((org.omg.CORBA_2_3.portable.OutputStream)var0).write_value(var1, (String)id());
   }
}
