package com.sun.corba.se.impl.oa.poa;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.ImplicitActivationPolicy;
import org.omg.PortableServer.ImplicitActivationPolicyValue;

final class ImplicitActivationPolicyImpl extends LocalObject implements ImplicitActivationPolicy {
   private ImplicitActivationPolicyValue value;

   public ImplicitActivationPolicyImpl(ImplicitActivationPolicyValue var1) {
      this.value = var1;
   }

   public ImplicitActivationPolicyValue value() {
      return this.value;
   }

   public int policy_type() {
      return 20;
   }

   public Policy copy() {
      return new ImplicitActivationPolicyImpl(this.value);
   }

   public void destroy() {
      this.value = null;
   }

   public String toString() {
      return "ImplicitActivationPolicy[" + (this.value.value() == 0 ? "IMPLICIT_ACTIVATION" : "NO_IMPLICIT_ACTIVATION]");
   }
}
