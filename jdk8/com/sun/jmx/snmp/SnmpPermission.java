package com.sun.jmx.snmp;

import java.security.BasicPermission;

public class SnmpPermission extends BasicPermission {
   public SnmpPermission(String var1) {
      super(var1);
   }

   public SnmpPermission(String var1, String var2) {
      super(var1, var2);
   }
}
