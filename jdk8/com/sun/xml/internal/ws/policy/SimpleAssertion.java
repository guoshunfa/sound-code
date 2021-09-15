package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import java.util.Collection;

public abstract class SimpleAssertion extends PolicyAssertion {
   protected SimpleAssertion() {
   }

   protected SimpleAssertion(AssertionData data, Collection<? extends PolicyAssertion> assertionParameters) {
      super(data, assertionParameters);
   }

   public final boolean hasNestedPolicy() {
      return false;
   }

   public final NestedPolicy getNestedPolicy() {
      return null;
   }
}
