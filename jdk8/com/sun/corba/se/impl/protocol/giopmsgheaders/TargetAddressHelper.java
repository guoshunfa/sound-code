package com.sun.corba.se.impl.protocol.giopmsgheaders;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.UnionMember;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.IOP.TaggedProfile;
import org.omg.IOP.TaggedProfileHelper;

public abstract class TargetAddressHelper {
   private static String _id = "IDL:messages/TargetAddress:1.0";
   private static TypeCode __typeCode = null;

   public static void insert(Any var0, TargetAddress var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static TargetAddress extract(Any var0) {
      return read(var0.create_input_stream());
   }

   public static synchronized TypeCode type() {
      if (__typeCode == null) {
         TypeCode var0 = ORB.init().get_primitive_tc(TCKind.tk_short);
         var0 = ORB.init().create_alias_tc(AddressingDispositionHelper.id(), "AddressingDisposition", var0);
         UnionMember[] var1 = new UnionMember[3];
         Any var3 = ORB.init().create_any();
         var3.insert_short((short)0);
         TypeCode var2 = ORB.init().get_primitive_tc(TCKind.tk_octet);
         var2 = ORB.init().create_sequence_tc(0, var2);
         var1[0] = new UnionMember("object_key", var3, var2, (IDLType)null);
         var3 = ORB.init().create_any();
         var3.insert_short((short)1);
         var2 = TaggedProfileHelper.type();
         var1[1] = new UnionMember("profile", var3, var2, (IDLType)null);
         var3 = ORB.init().create_any();
         var3.insert_short((short)2);
         var2 = IORAddressingInfoHelper.type();
         var1[2] = new UnionMember("ior", var3, var2, (IDLType)null);
         __typeCode = ORB.init().create_union_tc(id(), "TargetAddress", var0, var1);
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static TargetAddress read(InputStream var0) {
      TargetAddress var1 = new TargetAddress();
      boolean var2 = false;
      short var7 = var0.read_short();
      switch(var7) {
      case 0:
         Object var3 = null;
         int var4 = var0.read_long();
         byte[] var8 = new byte[var4];
         var0.read_octet_array(var8, 0, var4);
         var1.object_key(var8);
         break;
      case 1:
         TaggedProfile var5 = null;
         var5 = TaggedProfileHelper.read(var0);
         var1.profile(var5);
         break;
      case 2:
         IORAddressingInfo var6 = null;
         var6 = IORAddressingInfoHelper.read(var0);
         var1.ior(var6);
         break;
      default:
         throw new BAD_OPERATION();
      }

      return var1;
   }

   public static void write(OutputStream var0, TargetAddress var1) {
      var0.write_short(var1.discriminator());
      switch(var1.discriminator()) {
      case 0:
         var0.write_long(var1.object_key().length);
         var0.write_octet_array(var1.object_key(), 0, var1.object_key().length);
         break;
      case 1:
         TaggedProfileHelper.write(var0, var1.profile());
         break;
      case 2:
         IORAddressingInfoHelper.write(var0, var1.ior());
         break;
      default:
         throw new BAD_OPERATION();
      }

   }
}
