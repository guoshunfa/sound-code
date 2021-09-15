package com.sun.corba.se.impl.protocol;

public class AddressingDispositionException extends RuntimeException {
   private short expectedAddrDisp = 0;

   public AddressingDispositionException(short var1) {
      this.expectedAddrDisp = var1;
   }

   public short expectedAddrDisp() {
      return this.expectedAddrDisp;
   }
}
