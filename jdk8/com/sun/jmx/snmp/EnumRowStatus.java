package com.sun.jmx.snmp;

import java.io.Serializable;
import java.util.Hashtable;

public class EnumRowStatus extends Enumerated implements Serializable {
   private static final long serialVersionUID = 8966519271130162420L;
   public static final int unspecified = 0;
   public static final int active = 1;
   public static final int notInService = 2;
   public static final int notReady = 3;
   public static final int createAndGo = 4;
   public static final int createAndWait = 5;
   public static final int destroy = 6;
   static final Hashtable<Integer, String> intTable = new Hashtable();
   static final Hashtable<String, Integer> stringTable = new Hashtable();

   public EnumRowStatus(int var1) throws IllegalArgumentException {
      super(var1);
   }

   public EnumRowStatus(Enumerated var1) throws IllegalArgumentException {
      this(var1.intValue());
   }

   public EnumRowStatus(long var1) throws IllegalArgumentException {
      this((int)var1);
   }

   public EnumRowStatus(Integer var1) throws IllegalArgumentException {
      super(var1);
   }

   public EnumRowStatus(Long var1) throws IllegalArgumentException {
      this(var1);
   }

   public EnumRowStatus() throws IllegalArgumentException {
      this(0);
   }

   public EnumRowStatus(String var1) throws IllegalArgumentException {
      super(var1);
   }

   public EnumRowStatus(SnmpInt var1) throws IllegalArgumentException {
      this(var1.intValue());
   }

   public SnmpInt toSnmpValue() throws IllegalArgumentException {
      if (this.value == 0) {
         throw new IllegalArgumentException("`unspecified' is not a valid SNMP value.");
      } else {
         return new SnmpInt(this.value);
      }
   }

   public static boolean isValidValue(int var0) {
      if (var0 < 0) {
         return false;
      } else {
         return var0 <= 6;
      }
   }

   protected Hashtable<Integer, String> getIntTable() {
      return getRSIntTable();
   }

   protected Hashtable<String, Integer> getStringTable() {
      return getRSStringTable();
   }

   static Hashtable<Integer, String> getRSIntTable() {
      return intTable;
   }

   static Hashtable<String, Integer> getRSStringTable() {
      return stringTable;
   }

   static {
      intTable.put(new Integer(0), "unspecified");
      intTable.put(new Integer(3), "notReady");
      intTable.put(new Integer(6), "destroy");
      intTable.put(new Integer(2), "notInService");
      intTable.put(new Integer(5), "createAndWait");
      intTable.put(new Integer(1), "active");
      intTable.put(new Integer(4), "createAndGo");
      stringTable.put("unspecified", new Integer(0));
      stringTable.put("notReady", new Integer(3));
      stringTable.put("destroy", new Integer(6));
      stringTable.put("notInService", new Integer(2));
      stringTable.put("createAndWait", new Integer(5));
      stringTable.put("active", new Integer(1));
      stringTable.put("createAndGo", new Integer(4));
   }
}
