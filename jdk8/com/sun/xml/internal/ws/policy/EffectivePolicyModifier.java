package com.sun.xml.internal.ws.policy;

public final class EffectivePolicyModifier extends PolicyMapMutator {
   public static EffectivePolicyModifier createEffectivePolicyModifier() {
      return new EffectivePolicyModifier();
   }

   private EffectivePolicyModifier() {
   }

   public void setNewEffectivePolicyForServiceScope(PolicyMapKey key, Policy newEffectivePolicy) {
      this.getMap().setNewEffectivePolicyForScope(PolicyMap.ScopeType.SERVICE, key, newEffectivePolicy);
   }

   public void setNewEffectivePolicyForEndpointScope(PolicyMapKey key, Policy newEffectivePolicy) {
      this.getMap().setNewEffectivePolicyForScope(PolicyMap.ScopeType.ENDPOINT, key, newEffectivePolicy);
   }

   public void setNewEffectivePolicyForOperationScope(PolicyMapKey key, Policy newEffectivePolicy) {
      this.getMap().setNewEffectivePolicyForScope(PolicyMap.ScopeType.OPERATION, key, newEffectivePolicy);
   }

   public void setNewEffectivePolicyForInputMessageScope(PolicyMapKey key, Policy newEffectivePolicy) {
      this.getMap().setNewEffectivePolicyForScope(PolicyMap.ScopeType.INPUT_MESSAGE, key, newEffectivePolicy);
   }

   public void setNewEffectivePolicyForOutputMessageScope(PolicyMapKey key, Policy newEffectivePolicy) {
      this.getMap().setNewEffectivePolicyForScope(PolicyMap.ScopeType.OUTPUT_MESSAGE, key, newEffectivePolicy);
   }

   public void setNewEffectivePolicyForFaultMessageScope(PolicyMapKey key, Policy newEffectivePolicy) {
      this.getMap().setNewEffectivePolicyForScope(PolicyMap.ScopeType.FAULT_MESSAGE, key, newEffectivePolicy);
   }
}
