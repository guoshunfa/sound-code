package com.sun.jmx.snmp.IPAcl;

import java.net.UnknownHostException;

class JDMNetMaskV6 extends JDMNetMask {
   private static final long serialVersionUID = 4505256777680576645L;

   public JDMNetMaskV6(int var1) {
      super(var1);
   }

   public JDMNetMaskV6(Parser var1, int var2) {
      super(var1, var2);
   }

   protected PrincipalImpl createAssociatedPrincipal() throws UnknownHostException {
      return new NetMaskImpl(this.address.toString(), Integer.parseInt(this.mask));
   }
}
