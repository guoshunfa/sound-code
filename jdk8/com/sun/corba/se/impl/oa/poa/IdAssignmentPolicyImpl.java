package com.sun.corba.se.impl.oa.poa;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.IdAssignmentPolicy;
import org.omg.PortableServer.IdAssignmentPolicyValue;

final class IdAssignmentPolicyImpl extends LocalObject implements IdAssignmentPolicy {
   private IdAssignmentPolicyValue value;

   public IdAssignmentPolicyImpl(IdAssignmentPolicyValue var1) {
      this.value = var1;
   }

   public IdAssignmentPolicyValue value() {
      return this.value;
   }

   public int policy_type() {
      return 19;
   }

   public Policy copy() {
      return new IdAssignmentPolicyImpl(this.value);
   }

   public void destroy() {
      this.value = null;
   }

   public String toString() {
      return "IdAssignmentPolicy[" + (this.value.value() == 0 ? "USER_ID" : "SYSTEM_ID]");
   }
}
