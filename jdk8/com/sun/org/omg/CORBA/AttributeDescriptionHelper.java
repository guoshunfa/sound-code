package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class AttributeDescriptionHelper {
   private static String _id = "IDL:omg.org/CORBA/AttributeDescription:1.0";
   private static TypeCode __typeCode = null;
   private static boolean __active = false;

   public static void insert(Any var0, AttributeDescription var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static AttributeDescription extract(Any var0) {
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
               StructMember[] var1 = new StructMember[6];
               TypeCode var2 = null;
               var2 = ORB.init().create_string_tc(0);
               var2 = ORB.init().create_alias_tc(IdentifierHelper.id(), "Identifier", var2);
               var1[0] = new StructMember("name", var2, (IDLType)null);
               var2 = ORB.init().create_string_tc(0);
               var2 = ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", var2);
               var1[1] = new StructMember("id", var2, (IDLType)null);
               var2 = ORB.init().create_string_tc(0);
               var2 = ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", var2);
               var1[2] = new StructMember("defined_in", var2, (IDLType)null);
               var2 = ORB.init().create_string_tc(0);
               var2 = ORB.init().create_alias_tc(VersionSpecHelper.id(), "VersionSpec", var2);
               var1[3] = new StructMember("version", var2, (IDLType)null);
               var2 = ORB.init().get_primitive_tc(TCKind.tk_TypeCode);
               var1[4] = new StructMember("type", var2, (IDLType)null);
               var2 = AttributeModeHelper.type();
               var1[5] = new StructMember("mode", var2, (IDLType)null);
               __typeCode = ORB.init().create_struct_tc(id(), "AttributeDescription", var1);
               __active = false;
            }
         }
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static AttributeDescription read(InputStream var0) {
      AttributeDescription var1 = new AttributeDescription();
      var1.name = var0.read_string();
      var1.id = var0.read_string();
      var1.defined_in = var0.read_string();
      var1.version = var0.read_string();
      var1.type = var0.read_TypeCode();
      var1.mode = AttributeModeHelper.read(var0);
      return var1;
   }

   public static void write(OutputStream var0, AttributeDescription var1) {
      var0.write_string(var1.name);
      var0.write_string(var1.id);
      var0.write_string(var1.defined_in);
      var0.write_string(var1.version);
      var0.write_TypeCode(var1.type);
      AttributeModeHelper.write(var0, var1.mode);
   }
}
