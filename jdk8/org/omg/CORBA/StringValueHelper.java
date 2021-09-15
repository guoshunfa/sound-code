package org.omg.CORBA;

import java.io.Serializable;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public class StringValueHelper implements BoxedValueHelper {
   private static String _id = "IDL:omg.org/CORBA/StringValue:1.0";
   private static StringValueHelper _instance = new StringValueHelper();
   private static TypeCode __typeCode = null;
   private static boolean __active = false;

   public static void insert(Any var0, String var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static String extract(Any var0) {
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
               __typeCode = ORB.init().create_string_tc(0);
               __typeCode = ORB.init().create_value_box_tc(_id, "StringValue", __typeCode);
               __active = false;
            }
         }
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static String read(InputStream var0) {
      if (!(var0 instanceof org.omg.CORBA_2_3.portable.InputStream)) {
         throw new BAD_PARAM();
      } else {
         return (String)((org.omg.CORBA_2_3.portable.InputStream)var0).read_value((BoxedValueHelper)_instance);
      }
   }

   public Serializable read_value(InputStream var1) {
      String var2 = var1.read_string();
      return var2;
   }

   public static void write(OutputStream var0, String var1) {
      if (!(var0 instanceof org.omg.CORBA_2_3.portable.OutputStream)) {
         throw new BAD_PARAM();
      } else {
         ((org.omg.CORBA_2_3.portable.OutputStream)var0).write_value(var1, (BoxedValueHelper)_instance);
      }
   }

   public void write_value(OutputStream var1, Serializable var2) {
      if (!(var2 instanceof String)) {
         throw new MARSHAL();
      } else {
         String var3 = (String)var2;
         var1.write_string(var3);
      }
   }

   public String get_id() {
      return _id;
   }
}
