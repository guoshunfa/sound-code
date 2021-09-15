package com.sun.corba.se.spi.extension;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;

public class CopyObjectPolicy extends LocalObject implements Policy {
   private final int value;

   public CopyObjectPolicy(int var1) {
      this.value = var1;
   }

   public int getValue() {
      return this.value;
   }

   public int policy_type() {
      return 1398079490;
   }

   public Policy copy() {
      return this;
   }

   public void destroy() {
   }

   public String toString() {
      return "CopyObjectPolicy[" + this.value + "]";
   }
}
