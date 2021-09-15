package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class InitializerHelper {
   private static String _id = "IDL:omg.org/CORBA/Initializer:1.0";
   private static TypeCode __typeCode = null;
   private static boolean __active = false;

   public static void insert(Any var0, Initializer var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static Initializer extract(Any var0) {
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
               var2 = StructMemberHelper.type();
               var2 = ORB.init().create_sequence_tc(0, var2);
               var2 = ORB.init().create_alias_tc(StructMemberSeqHelper.id(), "StructMemberSeq", var2);
               var1[0] = new StructMember("members", var2, (IDLType)null);
               var2 = ORB.init().create_string_tc(0);
               var2 = ORB.init().create_alias_tc(IdentifierHelper.id(), "Identifier", var2);
               var1[1] = new StructMember("name", var2, (IDLType)null);
               __typeCode = ORB.init().create_struct_tc(id(), "Initializer", var1);
               __active = false;
            }
         }
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static Initializer read(InputStream var0) {
      Initializer var1 = new Initializer();
      var1.members = StructMemberSeqHelper.read(var0);
      var1.name = var0.read_string();
      return var1;
   }

   public static void write(OutputStream var0, Initializer var1) {
      StructMemberSeqHelper.write(var0, var1.members);
      var0.write_string(var1.name);
   }
}
