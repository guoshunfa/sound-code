package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;

class SnmpEntryOid extends SnmpOid {
   private static final long serialVersionUID = 9212653887791059564L;

   public SnmpEntryOid(long[] var1, int var2) {
      int var3 = var1.length - var2;
      long[] var4 = new long[var3];
      System.arraycopy(var1, var2, var4, 0, var3);
      this.components = var4;
      this.componentCount = var3;
   }
}
