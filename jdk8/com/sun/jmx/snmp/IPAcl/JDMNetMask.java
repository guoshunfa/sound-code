package com.sun.jmx.snmp.IPAcl;

import java.net.UnknownHostException;

class JDMNetMask extends Host {
   private static final long serialVersionUID = -1979318280250821787L;
   protected StringBuffer address = new StringBuffer();
   protected String mask = null;

   public JDMNetMask(int var1) {
      super(var1);
   }

   public JDMNetMask(Parser var1, int var2) {
      super(var1, var2);
   }

   public static Node jjtCreate(int var0) {
      return new JDMNetMask(var0);
   }

   public static Node jjtCreate(Parser var0, int var1) {
      return new JDMNetMask(var0, var1);
   }

   protected String getHname() {
      return this.address.toString();
   }

   protected PrincipalImpl createAssociatedPrincipal() throws UnknownHostException {
      return new NetMaskImpl(this.address.toString(), Integer.parseInt(this.mask));
   }
}
