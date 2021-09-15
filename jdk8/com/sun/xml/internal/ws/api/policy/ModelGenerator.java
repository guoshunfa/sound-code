package com.sun.xml.internal.ws.api.policy;

import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelGenerator;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;

public abstract class ModelGenerator extends PolicyModelGenerator {
   private static final ModelGenerator.SourceModelCreator CREATOR = new ModelGenerator.SourceModelCreator();

   private ModelGenerator() {
   }

   public static PolicyModelGenerator getGenerator() {
      return PolicyModelGenerator.getCompactGenerator(CREATOR);
   }

   protected static class SourceModelCreator extends PolicyModelGenerator.PolicySourceModelCreator {
      protected PolicySourceModel create(Policy policy) {
         return SourceModel.createPolicySourceModel(policy.getNamespaceVersion(), policy.getId(), policy.getName());
      }
   }
}
