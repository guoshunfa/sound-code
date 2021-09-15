package com.sun.xml.internal.ws.api.policy;

import com.sun.xml.internal.ws.policy.EffectiveAlternativeSelector;
import com.sun.xml.internal.ws.policy.EffectivePolicyModifier;
import com.sun.xml.internal.ws.policy.PolicyException;

public class AlternativeSelector extends EffectiveAlternativeSelector {
   public static void doSelection(EffectivePolicyModifier modifier) throws PolicyException {
      ValidationProcessor validationProcessor = ValidationProcessor.getInstance();
      selectAlternatives(modifier, validationProcessor);
   }
}
