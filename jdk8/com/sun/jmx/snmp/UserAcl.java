package com.sun.jmx.snmp;

public interface UserAcl {
   String getName();

   boolean checkReadPermission(String var1);

   boolean checkReadPermission(String var1, String var2, int var3);

   boolean checkContextName(String var1);

   boolean checkWritePermission(String var1);

   boolean checkWritePermission(String var1, String var2, int var3);
}
