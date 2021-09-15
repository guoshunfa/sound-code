package com.sun.corba.se.impl.protocol.giopmsgheaders;

import org.omg.CORBA.Any;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.IOP.IORHelper;

public abstract class IORAddressingInfoHelper {
   private static String _id = "IDL:messages/IORAddressingInfo:1.0";
   private static TypeCode __typeCode = null;
   private static boolean __active = false;

   public static void insert(Any var0, IORAddressingInfo var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static IORAddressingInfo extract(Any var0) {
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
               var2 = ORB.init().get_primitive_tc(TCKind.tk_ulong);
               var1[0] = new StructMember("selected_profile_index", var2, (IDLType)null);
               var2 = IORHelper.type();
               var1[1] = new StructMember("ior", var2, (IDLType)null);
               __typeCode = ORB.init().create_struct_tc(id(), "IORAddressingInfo", var1);
               __active = false;
            }
         }
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static IORAddressingInfo read(InputStream var0) {
      IORAddressingInfo var1 = new IORAddressingInfo();
      var1.selected_profile_index = var0.read_ulong();
      var1.ior = IORHelper.read(var0);
      return var1;
   }

   public static void write(OutputStream var0, IORAddressingInfo var1) {
      var0.write_ulong(var1.selected_profile_index);
      IORHelper.write(var0, var1.ior);
   }
}
