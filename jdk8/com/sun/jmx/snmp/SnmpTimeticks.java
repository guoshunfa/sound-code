package com.sun.jmx.snmp;

public class SnmpTimeticks extends SnmpUnsignedInt {
   static final String name = "TimeTicks";
   private static final long serialVersionUID = -5486435222360030630L;

   public SnmpTimeticks(int var1) throws IllegalArgumentException {
      super(var1);
   }

   public SnmpTimeticks(Integer var1) throws IllegalArgumentException {
      super(var1);
   }

   public SnmpTimeticks(long var1) throws IllegalArgumentException {
      super(var1 > 0L ? var1 & 4294967295L : var1);
   }

   public SnmpTimeticks(Long var1) throws IllegalArgumentException {
      this(var1);
   }

   public static final String printTimeTicks(long var0) {
      StringBuffer var6 = new StringBuffer();
      var0 /= 100L;
      int var5 = (int)(var0 / 86400L);
      var0 %= 86400L;
      int var4 = (int)(var0 / 3600L);
      var0 %= 3600L;
      int var3 = (int)(var0 / 60L);
      int var2 = (int)(var0 % 60L);
      if (var5 == 0) {
         var6.append(var4 + ":" + var3 + ":" + var2);
         return var6.toString();
      } else {
         if (var5 == 1) {
            var6.append("1 day ");
         } else {
            var6.append(var5 + " days ");
         }

         var6.append(var4 + ":" + var3 + ":" + var2);
         return var6.toString();
      }
   }

   public final String toString() {
      return printTimeTicks(this.value);
   }

   public final String getTypeName() {
      return "TimeTicks";
   }
}
