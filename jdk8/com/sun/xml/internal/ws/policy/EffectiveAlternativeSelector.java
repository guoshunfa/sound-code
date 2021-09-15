package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class EffectiveAlternativeSelector {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(EffectiveAlternativeSelector.class);

   public static void doSelection(EffectivePolicyModifier modifier) throws PolicyException {
      AssertionValidationProcessor validationProcessor = AssertionValidationProcessor.getInstance();
      selectAlternatives(modifier, validationProcessor);
   }

   protected static void selectAlternatives(EffectivePolicyModifier modifier, AssertionValidationProcessor validationProcessor) throws PolicyException {
      PolicyMap map = modifier.getMap();
      Iterator var3 = map.getAllServiceScopeKeys().iterator();

      PolicyMapKey mapKey;
      Policy oldPolicy;
      while(var3.hasNext()) {
         mapKey = (PolicyMapKey)var3.next();
         oldPolicy = map.getServiceEffectivePolicy(mapKey);
         modifier.setNewEffectivePolicyForServiceScope(mapKey, selectBestAlternative(oldPolicy, validationProcessor));
      }

      var3 = map.getAllEndpointScopeKeys().iterator();

      while(var3.hasNext()) {
         mapKey = (PolicyMapKey)var3.next();
         oldPolicy = map.getEndpointEffectivePolicy(mapKey);
         modifier.setNewEffectivePolicyForEndpointScope(mapKey, selectBestAlternative(oldPolicy, validationProcessor));
      }

      var3 = map.getAllOperationScopeKeys().iterator();

      while(var3.hasNext()) {
         mapKey = (PolicyMapKey)var3.next();
         oldPolicy = map.getOperationEffectivePolicy(mapKey);
         modifier.setNewEffectivePolicyForOperationScope(mapKey, selectBestAlternative(oldPolicy, validationProcessor));
      }

      var3 = map.getAllInputMessageScopeKeys().iterator();

      while(var3.hasNext()) {
         mapKey = (PolicyMapKey)var3.next();
         oldPolicy = map.getInputMessageEffectivePolicy(mapKey);
         modifier.setNewEffectivePolicyForInputMessageScope(mapKey, selectBestAlternative(oldPolicy, validationProcessor));
      }

      var3 = map.getAllOutputMessageScopeKeys().iterator();

      while(var3.hasNext()) {
         mapKey = (PolicyMapKey)var3.next();
         oldPolicy = map.getOutputMessageEffectivePolicy(mapKey);
         modifier.setNewEffectivePolicyForOutputMessageScope(mapKey, selectBestAlternative(oldPolicy, validationProcessor));
      }

      var3 = map.getAllFaultMessageScopeKeys().iterator();

      while(var3.hasNext()) {
         mapKey = (PolicyMapKey)var3.next();
         oldPolicy = map.getFaultMessageEffectivePolicy(mapKey);
         modifier.setNewEffectivePolicyForFaultMessageScope(mapKey, selectBestAlternative(oldPolicy, validationProcessor));
      }

   }

   private static Policy selectBestAlternative(Policy policy, AssertionValidationProcessor validationProcessor) throws PolicyException {
      AssertionSet bestAlternative = null;
      EffectiveAlternativeSelector.AlternativeFitness bestAlternativeFitness = EffectiveAlternativeSelector.AlternativeFitness.UNEVALUATED;
      Iterator var4 = policy.iterator();

      while(var4.hasNext()) {
         AssertionSet alternative = (AssertionSet)var4.next();
         EffectiveAlternativeSelector.AlternativeFitness alternativeFitness = alternative.isEmpty() ? EffectiveAlternativeSelector.AlternativeFitness.SUPPORTED_EMPTY : EffectiveAlternativeSelector.AlternativeFitness.UNEVALUATED;
         Iterator var7 = alternative.iterator();

         while(var7.hasNext()) {
            PolicyAssertion assertion = (PolicyAssertion)var7.next();
            PolicyAssertionValidator.Fitness assertionFitness = validationProcessor.validateClientSide(assertion);
            switch(assertionFitness) {
            case UNKNOWN:
            case UNSUPPORTED:
            case INVALID:
               LOGGER.warning(LocalizationMessages.WSP_0075_PROBLEMATIC_ASSERTION_STATE(assertion.getName(), assertionFitness));
            case SUPPORTED:
            default:
               alternativeFitness = alternativeFitness.combine(assertionFitness);
            }
         }

         if (bestAlternativeFitness.compareTo(alternativeFitness) < 0) {
            bestAlternative = alternative;
            bestAlternativeFitness = alternativeFitness;
         }

         if (bestAlternativeFitness == EffectiveAlternativeSelector.AlternativeFitness.SUPPORTED) {
            break;
         }
      }

      switch(bestAlternativeFitness) {
      case INVALID:
         throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0053_INVALID_CLIENT_SIDE_ALTERNATIVE()));
      case UNKNOWN:
      case UNSUPPORTED:
      case PARTIALLY_SUPPORTED:
         LOGGER.warning(LocalizationMessages.WSP_0019_SUBOPTIMAL_ALTERNATIVE_SELECTED(bestAlternativeFitness));
      default:
         Collection<AssertionSet> alternativeSet = null;
         if (bestAlternative != null) {
            alternativeSet = new LinkedList();
            alternativeSet.add(bestAlternative);
         }

         return Policy.createPolicy(policy.getNamespaceVersion(), policy.getName(), policy.getId(), alternativeSet);
      }
   }

   private static enum AlternativeFitness {
      UNEVALUATED {
         EffectiveAlternativeSelector.AlternativeFitness combine(PolicyAssertionValidator.Fitness assertionFitness) {
            switch(assertionFitness) {
            case UNKNOWN:
               return UNKNOWN;
            case UNSUPPORTED:
               return UNSUPPORTED;
            case SUPPORTED:
               return SUPPORTED;
            case INVALID:
               return INVALID;
            default:
               return UNEVALUATED;
            }
         }
      },
      INVALID {
         EffectiveAlternativeSelector.AlternativeFitness combine(PolicyAssertionValidator.Fitness assertionFitness) {
            return INVALID;
         }
      },
      UNKNOWN {
         EffectiveAlternativeSelector.AlternativeFitness combine(PolicyAssertionValidator.Fitness assertionFitness) {
            switch(assertionFitness) {
            case UNKNOWN:
               return UNKNOWN;
            case UNSUPPORTED:
               return UNSUPPORTED;
            case SUPPORTED:
               return PARTIALLY_SUPPORTED;
            case INVALID:
               return INVALID;
            default:
               return UNEVALUATED;
            }
         }
      },
      UNSUPPORTED {
         EffectiveAlternativeSelector.AlternativeFitness combine(PolicyAssertionValidator.Fitness assertionFitness) {
            switch(assertionFitness) {
            case UNKNOWN:
            case UNSUPPORTED:
               return UNSUPPORTED;
            case SUPPORTED:
               return PARTIALLY_SUPPORTED;
            case INVALID:
               return INVALID;
            default:
               return UNEVALUATED;
            }
         }
      },
      PARTIALLY_SUPPORTED {
         EffectiveAlternativeSelector.AlternativeFitness combine(PolicyAssertionValidator.Fitness assertionFitness) {
            switch(assertionFitness) {
            case UNKNOWN:
            case UNSUPPORTED:
            case SUPPORTED:
               return PARTIALLY_SUPPORTED;
            case INVALID:
               return INVALID;
            default:
               return UNEVALUATED;
            }
         }
      },
      SUPPORTED_EMPTY {
         EffectiveAlternativeSelector.AlternativeFitness combine(PolicyAssertionValidator.Fitness assertionFitness) {
            throw new UnsupportedOperationException("Combine operation was called unexpectedly on 'SUPPORTED_EMPTY' alternative fitness enumeration state.");
         }
      },
      SUPPORTED {
         EffectiveAlternativeSelector.AlternativeFitness combine(PolicyAssertionValidator.Fitness assertionFitness) {
            switch(assertionFitness) {
            case UNKNOWN:
            case UNSUPPORTED:
               return PARTIALLY_SUPPORTED;
            case SUPPORTED:
               return SUPPORTED;
            case INVALID:
               return INVALID;
            default:
               return UNEVALUATED;
            }
         }
      };

      private AlternativeFitness() {
      }

      abstract EffectiveAlternativeSelector.AlternativeFitness combine(PolicyAssertionValidator.Fitness var1);

      // $FF: synthetic method
      AlternativeFitness(Object x2) {
         this();
      }
   }
}
