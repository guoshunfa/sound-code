package com.sun.jmx.snmp;

public class SnmpOpaque extends SnmpString {
   private static final long serialVersionUID = 380952213936036664L;
   static final String name = "Opaque";

   public SnmpOpaque(byte[] var1) {
      super(var1);
   }

   public SnmpOpaque(Byte[] var1) {
      super(var1);
   }

   public SnmpOpaque(String var1) {
      super(var1);
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();

      for(int var2 = 0; var2 < this.value.length; ++var2) {
         byte var3 = this.value[var2];
         int var4 = var3 >= 0 ? var3 : var3 + 256;
         var1.append(Character.forDigit(var4 / 16, 16));
         var1.append(Character.forDigit(var4 % 16, 16));
      }

      return var1.toString();
   }

   public final String getTypeName() {
      return "Opaque";
   }
}
