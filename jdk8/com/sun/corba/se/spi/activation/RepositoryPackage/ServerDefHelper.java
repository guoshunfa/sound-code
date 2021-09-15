package com.sun.corba.se.spi.activation.RepositoryPackage;

import org.omg.CORBA.Any;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServerDefHelper {
   private static String _id = "IDL:activation/Repository/ServerDef:1.0";
   private static TypeCode __typeCode = null;
   private static boolean __active = false;

   public static void insert(Any var0, ServerDef var1) {
      OutputStream var2 = var0.create_output_stream();
      var0.type(type());
      write(var2, var1);
      var0.read_value(var2.create_input_stream(), type());
   }

   public static ServerDef extract(Any var0) {
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
               StructMember[] var1 = new StructMember[5];
               TypeCode var2 = null;
               var2 = ORB.init().create_string_tc(0);
               var1[0] = new StructMember("applicationName", var2, (IDLType)null);
               var2 = ORB.init().create_string_tc(0);
               var1[1] = new StructMember("serverName", var2, (IDLType)null);
               var2 = ORB.init().create_string_tc(0);
               var1[2] = new StructMember("serverClassPath", var2, (IDLType)null);
               var2 = ORB.init().create_string_tc(0);
               var1[3] = new StructMember("serverArgs", var2, (IDLType)null);
               var2 = ORB.init().create_string_tc(0);
               var1[4] = new StructMember("serverVmArgs", var2, (IDLType)null);
               __typeCode = ORB.init().create_struct_tc(id(), "ServerDef", var1);
               __active = false;
            }
         }
      }

      return __typeCode;
   }

   public static String id() {
      return _id;
   }

   public static ServerDef read(InputStream var0) {
      ServerDef var1 = new ServerDef();
      var1.applicationName = var0.read_string();
      var1.serverName = var0.read_string();
      var1.serverClassPath = var0.read_string();
      var1.serverArgs = var0.read_string();
      var1.serverVmArgs = var0.read_string();
      return var1;
   }

   public static void write(OutputStream var0, ServerDef var1) {
      var0.write_string(var1.applicationName);
      var0.write_string(var1.serverName);
      var0.write_string(var1.serverClassPath);
      var0.write_string(var1.serverArgs);
      var0.write_string(var1.serverVmArgs);
   }
}
