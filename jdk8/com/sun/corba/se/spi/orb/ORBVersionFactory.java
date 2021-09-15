package com.sun.corba.se.spi.orb;

import com.sun.corba.se.impl.orb.ORBVersionImpl;
import org.omg.CORBA.portable.InputStream;

public class ORBVersionFactory {
   private ORBVersionFactory() {
   }

   public static ORBVersion getFOREIGN() {
      return ORBVersionImpl.FOREIGN;
   }

   public static ORBVersion getOLD() {
      return ORBVersionImpl.OLD;
   }

   public static ORBVersion getNEW() {
      return ORBVersionImpl.NEW;
   }

   public static ORBVersion getJDK1_3_1_01() {
      return ORBVersionImpl.JDK1_3_1_01;
   }

   public static ORBVersion getNEWER() {
      return ORBVersionImpl.NEWER;
   }

   public static ORBVersion getPEORB() {
      return ORBVersionImpl.PEORB;
   }

   public static ORBVersion getORBVersion() {
      return ORBVersionImpl.PEORB;
   }

   public static ORBVersion create(InputStream var0) {
      byte var1 = var0.read_octet();
      return byteToVersion(var1);
   }

   private static ORBVersion byteToVersion(byte var0) {
      switch(var0) {
      case 0:
         return ORBVersionImpl.FOREIGN;
      case 1:
         return ORBVersionImpl.OLD;
      case 2:
         return ORBVersionImpl.NEW;
      case 3:
         return ORBVersionImpl.JDK1_3_1_01;
      case 10:
         return ORBVersionImpl.NEWER;
      case 20:
         return ORBVersionImpl.PEORB;
      default:
         return new ORBVersionImpl(var0);
      }
   }
}
