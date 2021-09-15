package com.sun.org.omg.CORBA.ValueDefPackage;

import com.sun.org.omg.CORBA.AttrDescriptionSeqHelper;
import com.sun.org.omg.CORBA.AttributeDescriptionHelper;
import com.sun.org.omg.CORBA.IdentifierHelper;
import com.sun.org.omg.CORBA.InitializerHelper;
import com.sun.org.omg.CORBA.InitializerSeqHelper;
import com.sun.org.omg.CORBA.OpDescriptionSeqHelper;
import com.sun.org.omg.CORBA.OperationDescriptionHelper;
import com.sun.org.omg.CORBA.RepositoryIdHelper;
import com.sun.org.omg.CORBA.RepositoryIdSeqHelper;
import com.sun.org.omg.CORBA.ValueMemberHelper;
import com.sun.org.omg.CORBA.ValueMemberSeqHelper;
import com.sun.org.omg.CORBA.VersionSpecHelper;
import org.omg.CORBA.Any;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class FullValueDescriptionHelper {
   private static String _id = "IDL:omg.org/CORBA/ValueDef/FullValueDescription:1.0";
   private static TypeCode __typeCode = null;
   private static boolean __active = false;

   public static void insert(Any var0, FullValueDescription var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static FullValueDescription extract(Any var0) {
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
               StructMember[] var1 = new StructMember[15];
               TypeCode var2 = null;
               var2 = ORB.init().create_string_tc(0);
               var2 = ORB.init().create_alias_tc(IdentifierHelper.id(), "Identifier", var2);
               var1[0] = new StructMember("name", var2, (IDLType)null);
               var2 = ORB.init().create_string_tc(0);
               var2 = ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", var2);
               var1[1] = new StructMember("id", var2, (IDLType)null);
               var2 = ORB.init().get_primitive_tc(TCKind.tk_boolean);
               var1[2] = new StructMember("is_abstract", var2, (IDLType)null);
               var2 = ORB.init().get_primitive_tc(TCKind.tk_boolean);
               var1[3] = new StructMember("is_custom", var2, (IDLType)null);
               var2 = ORB.init().create_string_tc(0);
               var2 = ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", var2);
               var1[4] = new StructMember("defined_in", var2, (IDLType)null);
               var2 = ORB.init().create_string_tc(0);
               var2 = ORB.init().create_alias_tc(VersionSpecHelper.id(), "VersionSpec", var2);
               var1[5] = new StructMember("version", var2, (IDLType)null);
               var2 = OperationDescriptionHelper.type();
               var2 = ORB.init().create_sequence_tc(0, var2);
               var2 = ORB.init().create_alias_tc(OpDescriptionSeqHelper.id(), "OpDescriptionSeq", var2);
               var1[6] = new StructMember("operations", var2, (IDLType)null);
               var2 = AttributeDescriptionHelper.type();
               var2 = ORB.init().create_sequence_tc(0, var2);
               var2 = ORB.init().create_alias_tc(AttrDescriptionSeqHelper.id(), "AttrDescriptionSeq", var2);
               var1[7] = new StructMember("attributes", var2, (IDLType)null);
               var2 = ValueMemberHelper.type();
               var2 = ORB.init().create_sequence_tc(0, var2);
               var2 = ORB.init().create_alias_tc(ValueMemberSeqHelper.id(), "ValueMemberSeq", var2);
               var1[8] = new StructMember("members", var2, (IDLType)null);
               var2 = InitializerHelper.type();
               var2 = ORB.init().create_sequence_tc(0, var2);
               var2 = ORB.init().create_alias_tc(InitializerSeqHelper.id(), "InitializerSeq", var2);
               var1[9] = new StructMember("initializers", var2, (IDLType)null);
               var2 = ORB.init().create_string_tc(0);
               var2 = ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", var2);
               var2 = ORB.init().create_sequence_tc(0, var2);
               var2 = ORB.init().create_alias_tc(RepositoryIdSeqHelper.id(), "RepositoryIdSeq", var2);
               var1[10] = new StructMember("supported_interfaces", var2, (IDLType)null);
               var2 = ORB.init().create_string_tc(0);
               var2 = ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", var2);
               var2 = ORB.init().create_sequence_tc(0, var2);
               var2 = ORB.init().create_alias_tc(RepositoryIdSeqHelper.id(), "RepositoryIdSeq", var2);
               var1[11] = new StructMember("abstract_base_values", var2, (IDLType)null);
               var2 = ORB.init().get_primitive_tc(TCKind.tk_boolean);
               var1[12] = new StructMember("is_truncatable", var2, (IDLType)null);
               var2 = ORB.init().create_string_tc(0);
               var2 = ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", var2);
               var1[13] = new StructMember("base_value", var2, (IDLType)null);
               var2 = ORB.init().get_primitive_tc(TCKind.tk_TypeCode);
               var1[14] = new StructMember("type", var2, (IDLType)null);
               __typeCode = ORB.init().create_struct_tc(id(), "FullValueDescription", var1);
               __active = false;
            }
         }
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static FullValueDescription read(InputStream var0) {
      FullValueDescription var1 = new FullValueDescription();
      var1.name = var0.read_string();
      var1.id = var0.read_string();
      var1.is_abstract = var0.read_boolean();
      var1.is_custom = var0.read_boolean();
      var1.defined_in = var0.read_string();
      var1.version = var0.read_string();
      var1.operations = OpDescriptionSeqHelper.read(var0);
      var1.attributes = AttrDescriptionSeqHelper.read(var0);
      var1.members = ValueMemberSeqHelper.read(var0);
      var1.initializers = InitializerSeqHelper.read(var0);
      var1.supported_interfaces = RepositoryIdSeqHelper.read(var0);
      var1.abstract_base_values = RepositoryIdSeqHelper.read(var0);
      var1.is_truncatable = var0.read_boolean();
      var1.base_value = var0.read_string();
      var1.type = var0.read_TypeCode();
      return var1;
   }

   public static void write(OutputStream var0, FullValueDescription var1) {
      var0.write_string(var1.name);
      var0.write_string(var1.id);
      var0.write_boolean(var1.is_abstract);
      var0.write_boolean(var1.is_custom);
      var0.write_string(var1.defined_in);
      var0.write_string(var1.version);
      OpDescriptionSeqHelper.write(var0, var1.operations);
      AttrDescriptionSeqHelper.write(var0, var1.attributes);
      ValueMemberSeqHelper.write(var0, var1.members);
      InitializerSeqHelper.write(var0, var1.initializers);
      RepositoryIdSeqHelper.write(var0, var1.supported_interfaces);
      RepositoryIdSeqHelper.write(var0, var1.abstract_base_values);
      var0.write_boolean(var1.is_truncatable);
      var0.write_string(var1.base_value);
      var0.write_TypeCode(var1.type);
   }
}
