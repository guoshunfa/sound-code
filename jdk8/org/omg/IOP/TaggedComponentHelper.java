package org.omg.IOP;

import org.omg.CORBA.Any;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class TaggedComponentHelper {
   private static String _id = "IDL:omg.org/IOP/TaggedComponent:1.0";
   private static TypeCode __typeCode = null;
   private static boolean __active = false;

   public static void insert(Any var0, TaggedComponent var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static TaggedComponent extract(Any var0) {
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
               var2 = ORB.init().create_alias_tc(ComponentIdHelper.id(), "ComponentId", var2);
               var1[0] = new StructMember("tag", var2, (IDLType)null);
               var2 = ORB.init().get_primitive_tc(TCKind.tk_octet);
               var2 = ORB.init().create_sequence_tc(0, var2);
               var1[1] = new StructMember("component_data", var2, (IDLType)null);
               __typeCode = ORB.init().create_struct_tc(id(), "TaggedComponent", var1);
               __active = false;
            }
         }
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static TaggedComponent read(InputStream var0) {
      TaggedComponent var1 = new TaggedComponent();
      var1.tag = var0.read_ulong();
      int var2 = var0.read_long();
      var1.component_data = new byte[var2];
      var0.read_octet_array(var1.component_data, 0, var2);
      return var1;
   }

   public static void write(OutputStream var0, TaggedComponent var1) {
      var0.write_ulong(var1.tag);
      var0.write_long(var1.component_data.length);
      var0.write_octet_array(var1.component_data, 0, var1.component_data.length);
   }
}
