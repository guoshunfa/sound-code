package com.sun.xml.internal.ws.policy.spi;

import com.sun.xml.internal.ws.policy.PolicyAssertion;

public interface PolicyAssertionValidator {
   PolicyAssertionValidator.Fitness validateClientSide(PolicyAssertion var1);

   PolicyAssertionValidator.Fitness validateServerSide(PolicyAssertion var1);

   String[] declareSupportedDomains();

   public static enum Fitness {
      UNKNOWN,
      INVALID,
      UNSUPPORTED,
      SUPPORTED;

      public PolicyAssertionValidator.Fitness combine(PolicyAssertionValidator.Fitness other) {
         return this.compareTo(other) < 0 ? other : this;
      }
   }
}
