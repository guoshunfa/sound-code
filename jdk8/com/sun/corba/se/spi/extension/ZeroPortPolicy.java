package com.sun.corba.se.spi.extension;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;

public class ZeroPortPolicy extends LocalObject implements Policy {
   private static ZeroPortPolicy policy = new ZeroPortPolicy(true);
   private boolean flag = true;

   private ZeroPortPolicy(boolean var1) {
      this.flag = var1;
   }

   public String toString() {
      return "ZeroPortPolicy[" + this.flag + "]";
   }

   public boolean forceZeroPort() {
      return this.flag;
   }

   public static synchronized ZeroPortPolicy getPolicy() {
      return policy;
   }

   public int policy_type() {
      return 1398079489;
   }

   public Policy copy() {
      return this;
   }

   public void destroy() {
   }
}
