package com.sun.corba.se.impl.oa.poa;

import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.IdUniquenessPolicy;
import org.omg.PortableServer.IdUniquenessPolicyValue;

final class IdUniquenessPolicyImpl extends LocalObject implements IdUniquenessPolicy {
   private IdUniquenessPolicyValue value;

   public IdUniquenessPolicyImpl(IdUniquenessPolicyValue var1) {
      this.value = var1;
   }

   public IdUniquenessPolicyValue value() {
      return this.value;
   }

   public int policy_type() {
      return 18;
   }

   public Policy copy() {
      return new IdUniquenessPolicyImpl(this.value);
   }

   public void destroy() {
      this.value = null;
   }

   public String toString() {
      return "IdUniquenessPolicy[" + (this.value.value() == 0 ? "UNIQUE_ID" : "MULTIPLE_ID]");
   }
}
