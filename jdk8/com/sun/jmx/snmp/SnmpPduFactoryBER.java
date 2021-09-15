package com.sun.jmx.snmp;

import java.io.Serializable;

public class SnmpPduFactoryBER implements SnmpPduFactory, Serializable {
   private static final long serialVersionUID = -3525318344000547635L;

   public SnmpPdu decodeSnmpPdu(SnmpMsg var1) throws SnmpStatusException {
      return var1.decodeSnmpPdu();
   }

   public SnmpMsg encodeSnmpPdu(SnmpPdu var1, int var2) throws SnmpStatusException, SnmpTooBigException {
      switch(var1.version) {
      case 0:
      case 1:
         SnmpMessage var4 = new SnmpMessage();
         var4.encodeSnmpPdu((SnmpPduPacket)var1, var2);
         return var4;
      case 2:
      default:
         return null;
      case 3:
         SnmpV3Message var3 = new SnmpV3Message();
         var3.encodeSnmpPdu(var1, var2);
         return var3;
      }
   }
}
