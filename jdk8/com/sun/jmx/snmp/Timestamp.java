package com.sun.jmx.snmp;

import java.io.Serializable;
import java.util.Date;

public class Timestamp implements Serializable {
   private static final long serialVersionUID = -242456119149401823L;
   private long sysUpTime;
   private long crtime;
   private SnmpTimeticks uptimeCache = null;

   public Timestamp() {
      this.crtime = System.currentTimeMillis();
   }

   public Timestamp(long var1, long var3) {
      this.sysUpTime = var1;
      this.crtime = var3;
   }

   public Timestamp(long var1) {
      this.sysUpTime = var1;
      this.crtime = System.currentTimeMillis();
   }

   public final synchronized SnmpTimeticks getTimeTicks() {
      if (this.uptimeCache == null) {
         this.uptimeCache = new SnmpTimeticks((int)this.sysUpTime);
      }

      return this.uptimeCache;
   }

   public final long getSysUpTime() {
      return this.sysUpTime;
   }

   public final synchronized Date getDate() {
      return new Date(this.crtime);
   }

   public final long getDateTime() {
      return this.crtime;
   }

   public final String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append("{SysUpTime = " + SnmpTimeticks.printTimeTicks(this.sysUpTime));
      var1.append("} {Timestamp = " + this.getDate().toString() + "}");
      return var1.toString();
   }
}
