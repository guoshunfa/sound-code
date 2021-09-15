package com.sun.jmx.snmp.IPAcl;

import java.net.UnknownHostException;

class JDMIpMask extends Host {
   private static final long serialVersionUID = -8211312690652331386L;
   protected StringBuffer address = new StringBuffer();

   JDMIpMask(int var1) {
      super(var1);
   }

   JDMIpMask(Parser var1, int var2) {
      super(var1, var2);
   }

   public static Node jjtCreate(int var0) {
      return new JDMIpMask(var0);
   }

   public static Node jjtCreate(Parser var0, int var1) {
      return new JDMIpMask(var0, var1);
   }

   protected String getHname() {
      return this.address.toString();
   }

   protected PrincipalImpl createAssociatedPrincipal() throws UnknownHostException {
      return new GroupImpl(this.address.toString());
   }
}
