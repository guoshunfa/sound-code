package com.sun.corba.se.spi.extension;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;

public class RequestPartitioningPolicy extends LocalObject implements Policy {
   private static ORBUtilSystemException wrapper = ORBUtilSystemException.get("oa.ior");
   public static final int DEFAULT_VALUE = 0;
   private final int value;

   public RequestPartitioningPolicy(int var1) {
      if (var1 >= 0 && var1 <= 63) {
         this.value = var1;
      } else {
         throw wrapper.invalidRequestPartitioningPolicyValue(new Integer(var1), new Integer(0), new Integer(63));
      }
   }

   public int getValue() {
      return this.value;
   }

   public int policy_type() {
      return 1398079491;
   }

   public Policy copy() {
      return this;
   }

   public void destroy() {
   }

   public String toString() {
      return "RequestPartitioningPolicy[" + this.value + "]";
   }
}
