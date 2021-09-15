package com.sun.corba.se.impl.oa.poa;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.RequestProcessingPolicy;
import org.omg.PortableServer.RequestProcessingPolicyValue;

public class RequestProcessingPolicyImpl extends LocalObject implements RequestProcessingPolicy {
   private RequestProcessingPolicyValue value;

   public RequestProcessingPolicyImpl(RequestProcessingPolicyValue var1) {
      this.value = var1;
   }

   public RequestProcessingPolicyValue value() {
      return this.value;
   }

   public int policy_type() {
      return 22;
   }

   public Policy copy() {
      return new RequestProcessingPolicyImpl(this.value);
   }

   public void destroy() {
      this.value = null;
   }

   public String toString() {
      String var1 = null;
      switch(this.value.value()) {
      case 0:
         var1 = "USE_ACTIVE_OBJECT_MAP_ONLY";
         break;
      case 1:
         var1 = "USE_DEFAULT_SERVANT";
         break;
      case 2:
         var1 = "USE_SERVANT_MANAGER";
      }

      return "RequestProcessingPolicy[" + var1 + "]";
   }
}
