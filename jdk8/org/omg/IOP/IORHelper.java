package org.omg.IOP;

import org.omg.CORBA.Any;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class IORHelper {
   private static String _id = "IDL:omg.org/IOP/IOR:1.0";
   private static TypeCode __typeCode = null;
   private static boolean __active = false;

   public static void insert(Any var0, IOR var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static IOR extract(Any var0) {
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
               var2 = ORB.init().create_string_tc(0);
               var1[0] = new StructMember("type_id", var2, (IDLType)null);
               var2 = TaggedProfileHelper.type();
               var2 = ORB.init().create_sequence_tc(0, var2);
               var1[1] = new StructMember("profiles", var2, (IDLType)null);
               __typeCode = ORB.init().create_struct_tc(id(), "IOR", var1);
               __active = false;
            }
         }
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static IOR read(InputStream var0) {
      IOR var1 = new IOR();
      var1.type_id = var0.read_string();
      int var2 = var0.read_long();
      var1.profiles = new TaggedProfile[var2];

      for(int var3 = 0; var3 < var1.profiles.length; ++var3) {
         var1.profiles[var3] = TaggedProfileHelper.read(var0);
      }

      return var1;
   }

   public static void write(OutputStream var0, IOR var1) {
      var0.write_string(var1.type_id);
      var0.write_long(var1.profiles.length);

      for(int var2 = 0; var2 < var1.profiles.length; ++var2) {
         TaggedProfileHelper.write(var0, var1.profiles[var2]);
      }

   }
}
