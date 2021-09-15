package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import java.util.Collection;

public abstract class ComplexAssertion extends PolicyAssertion {
   private final NestedPolicy nestedPolicy;

   protected ComplexAssertion() {
      this.nestedPolicy = NestedPolicy.createNestedPolicy(AssertionSet.emptyAssertionSet());
   }

   protected ComplexAssertion(AssertionData data, Collection<? extends PolicyAssertion> assertionParameters, AssertionSet nestedAlternative) {
      super(data, assertionParameters);
      AssertionSet nestedSet = nestedAlternative != null ? nestedAlternative : AssertionSet.emptyAssertionSet();
      this.nestedPolicy = NestedPolicy.createNestedPolicy(nestedSet);
   }

   public final boolean hasNestedPolicy() {
      return true;
   }

   public final NestedPolicy getNestedPolicy() {
      return this.nestedPolicy;
   }
}
