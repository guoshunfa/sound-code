package com.sun.jmx.snmp;

import java.io.Serializable;
import java.net.InetAddress;

public abstract class SnmpPdu implements SnmpDefinitions, Serializable {
   public int type = 0;
   public int version = 0;
   public SnmpVarBind[] varBindList;
   public int requestId = 0;
   public InetAddress address;
   public int port = 0;

   public static String pduTypeToString(int var0) {
      switch(var0) {
      case 160:
         return "SnmpGet";
      case 161:
         return "SnmpGetNext";
      case 162:
         return "SnmpResponse";
      case 163:
         return "SnmpSet";
      case 164:
         return "SnmpV1Trap";
      case 165:
         return "SnmpGetBulk";
      case 166:
         return "SnmpInform";
      case 167:
         return "SnmpV2Trap";
      case 253:
         return "SnmpWalk(*)";
      default:
         return "Unknown Command = " + var0;
      }
   }
}
