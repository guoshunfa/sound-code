package com.sun.corba.se.impl.oa.poa;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.LifespanPolicy;
import org.omg.PortableServer.LifespanPolicyValue;

final class LifespanPolicyImpl extends LocalObject implements LifespanPolicy {
   private LifespanPolicyValue value;

   public LifespanPolicyImpl(LifespanPolicyValue var1) {
      this.value = var1;
   }

   public LifespanPolicyValue value() {
      return this.value;
   }

   public int policy_type() {
      return 17;
   }

   public Policy copy() {
      return new LifespanPolicyImpl(this.value);
   }

   public void destroy() {
      this.value = null;
   }

   public String toString() {
      return "LifespanPolicy[" + (this.value.value() == 0 ? "TRANSIENT" : "PERSISTENT]");
   }
}
