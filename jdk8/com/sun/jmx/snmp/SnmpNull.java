package com.sun.jmx.snmp;

public class SnmpNull extends SnmpValue {
   private static final long serialVersionUID = 1783782515994279177L;
   static final String name = "Null";
   private int tag;

   public SnmpNull() {
      this.tag = 5;
      this.tag = 5;
   }

   public SnmpNull(String var1) {
      this();
   }

   public SnmpNull(int var1) {
      this.tag = 5;
      this.tag = var1;
   }

   public int getTag() {
      return this.tag;
   }

   public String toString() {
      String var1 = "";
      if (this.tag != 5) {
         var1 = var1 + "[" + this.tag + "] ";
      }

      var1 = var1 + "NULL";
      switch(this.tag) {
      case 128:
         var1 = var1 + " (noSuchObject)";
         break;
      case 129:
         var1 = var1 + " (noSuchInstance)";
         break;
      case 130:
         var1 = var1 + " (endOfMibView)";
      }

      return var1;
   }

   public SnmpOid toOid() {
      throw new IllegalArgumentException();
   }

   public final synchronized SnmpValue duplicate() {
      return (SnmpValue)this.clone();
   }

   public final synchronized Object clone() {
      SnmpNull var1 = null;

      try {
         var1 = (SnmpNull)super.clone();
         var1.tag = this.tag;
         return var1;
      } catch (CloneNotSupportedException var3) {
         throw new InternalError(var3);
      }
   }

   public final String getTypeName() {
      return "Null";
   }

   public boolean isNoSuchObjectValue() {
      return this.tag == 128;
   }

   public boolean isNoSuchInstanceValue() {
      return this.tag == 129;
   }

   public boolean isEndOfMibViewValue() {
      return this.tag == 130;
   }
}
