package com.sun.corba.se.impl.corba;

import org.omg.CORBA.Principal;

public class PrincipalImpl extends Principal {
   private byte[] value;

   public void name(byte[] var1) {
      this.value = var1;
   }

   public byte[] name() {
      return this.value;
   }
}
