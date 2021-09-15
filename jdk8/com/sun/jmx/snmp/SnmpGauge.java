package com.sun.jmx.snmp;

public class SnmpGauge extends SnmpUnsignedInt {
   private static final long serialVersionUID = -8366622742122792945L;
   static final String name = "Gauge32";

   public SnmpGauge(int var1) throws IllegalArgumentException {
      super(var1);
   }

   public SnmpGauge(Integer var1) throws IllegalArgumentException {
      super(var1);
   }

   public SnmpGauge(long var1) throws IllegalArgumentException {
      super(var1);
   }

   public SnmpGauge(Long var1) throws IllegalArgumentException {
      super(var1);
   }

   public final String getTypeName() {
      return "Gauge32";
   }
}
