package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public class IllegalReceiveException extends IllegalStateException {
   private static final long serialVersionUID = 2296619040988576224L;

   public IllegalReceiveException() {
   }

   public IllegalReceiveException(String var1) {
      super(var1);
   }
}
