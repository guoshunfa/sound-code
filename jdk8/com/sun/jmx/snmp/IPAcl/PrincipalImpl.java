package com.sun.jmx.snmp.IPAcl;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;

class PrincipalImpl implements Principal, Serializable {
   private static final long serialVersionUID = -7910027842878976761L;
   private InetAddress[] add = null;

   public PrincipalImpl() throws UnknownHostException {
      this.add = new InetAddress[1];
      this.add[0] = InetAddress.getLocalHost();
   }

   public PrincipalImpl(String var1) throws UnknownHostException {
      if (!var1.equals("localhost") && !var1.equals("127.0.0.1")) {
         this.add = InetAddress.getAllByName(var1);
      } else {
         this.add = new InetAddress[1];
         this.add[0] = InetAddress.getByName(var1);
      }

   }

   public PrincipalImpl(InetAddress var1) {
      this.add = new InetAddress[1];
      this.add[0] = var1;
   }

   public String getName() {
      return this.add[0].toString();
   }

   public boolean equals(Object var1) {
      if (var1 instanceof PrincipalImpl) {
         for(int var2 = 0; var2 < this.add.length; ++var2) {
            if (this.add[var2].equals(((PrincipalImpl)var1).getAddress())) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.add[0].hashCode();
   }

   public String toString() {
      return "PrincipalImpl :" + this.add[0].toString();
   }

   public InetAddress getAddress() {
      return this.add[0];
   }

   public InetAddress[] getAddresses() {
      return this.add;
   }
}
