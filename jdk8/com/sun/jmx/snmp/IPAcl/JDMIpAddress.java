package com.sun.jmx.snmp.IPAcl;

import java.net.UnknownHostException;

class JDMIpAddress extends Host {
   private static final long serialVersionUID = 849729919486384484L;
   protected StringBuffer address = new StringBuffer();

   JDMIpAddress(int var1) {
      super(var1);
   }

   JDMIpAddress(Parser var1, int var2) {
      super(var1, var2);
   }

   public static Node jjtCreate(int var0) {
      return new JDMIpAddress(var0);
   }

   public static Node jjtCreate(Parser var0, int var1) {
      return new JDMIpAddress(var0, var1);
   }

   protected String getHname() {
      return this.address.toString();
   }

   protected PrincipalImpl createAssociatedPrincipal() throws UnknownHostException {
      return new PrincipalImpl(this.address.toString());
   }
}
