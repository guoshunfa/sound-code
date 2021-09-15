package com.sun.jmx.snmp.IPAcl;

import java.net.UnknownHostException;

class JDMHostName extends Host {
   private static final long serialVersionUID = -9120082068923591122L;
   protected StringBuffer name = new StringBuffer();

   JDMHostName(int var1) {
      super(var1);
   }

   JDMHostName(Parser var1, int var2) {
      super(var1, var2);
   }

   public static Node jjtCreate(int var0) {
      return new JDMHostName(var0);
   }

   public static Node jjtCreate(Parser var0, int var1) {
      return new JDMHostName(var0, var1);
   }

   protected String getHname() {
      return this.name.toString();
   }

   protected PrincipalImpl createAssociatedPrincipal() throws UnknownHostException {
      return new PrincipalImpl(this.name.toString());
   }
}
