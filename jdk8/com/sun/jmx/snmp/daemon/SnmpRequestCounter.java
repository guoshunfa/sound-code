package com.sun.jmx.snmp.daemon;

final class SnmpRequestCounter {
   int reqid = 0;

   public SnmpRequestCounter() {
   }

   public synchronized int getNewId() {
      if (++this.reqid < 0) {
         this.reqid = 1;
      }

      return this.reqid;
   }
}
