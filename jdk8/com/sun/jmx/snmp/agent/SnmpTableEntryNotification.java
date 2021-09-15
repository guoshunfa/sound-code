package com.sun.jmx.snmp.agent;

import javax.management.Notification;
import javax.management.ObjectName;

public class SnmpTableEntryNotification extends Notification {
   public static final String SNMP_ENTRY_ADDED = "jmx.snmp.table.entry.added";
   public static final String SNMP_ENTRY_REMOVED = "jmx.snmp.table.entry.removed";
   private final Object entry;
   private final ObjectName name;
   private static final long serialVersionUID = 5832592016227890252L;

   SnmpTableEntryNotification(String var1, Object var2, long var3, long var5, Object var7, ObjectName var8) {
      super(var1, var2, var3, var5);
      this.entry = var7;
      this.name = var8;
   }

   public Object getEntry() {
      return this.entry;
   }

   public ObjectName getEntryName() {
      return this.name;
   }
}
