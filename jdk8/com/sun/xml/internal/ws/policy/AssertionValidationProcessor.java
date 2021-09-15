package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class AssertionValidationProcessor {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(AssertionValidationProcessor.class);
   private final Collection<PolicyAssertionValidator> validators;

   private AssertionValidationProcessor() throws PolicyException {
      this((Collection)null);
   }

   protected AssertionValidationProcessor(Collection<PolicyAssertionValidator> policyValidators) throws PolicyException {
      this.validators = new LinkedList();
      PolicyAssertionValidator[] var2 = (PolicyAssertionValidator[])PolicyUtils.ServiceProvider.load(PolicyAssertionValidator.class);
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         PolicyAssertionValidator validator = var2[var4];
         this.validators.add(validator);
      }

      if (policyValidators != null) {
         Iterator var6 = policyValidators.iterator();

         while(var6.hasNext()) {
            PolicyAssertionValidator validator = (PolicyAssertionValidator)var6.next();
            this.validators.add(validator);
         }
      }

      if (this.validators.size() == 0) {
         throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0076_NO_SERVICE_PROVIDERS_FOUND(PolicyAssertionValidator.class.getName())));
      }
   }

   public static AssertionValidationProcessor getInstance() throws PolicyException {
      return new AssertionValidationProcessor();
   }

   public PolicyAssertionValidator.Fitness validateClientSide(PolicyAssertion assertion) throws PolicyException {
      PolicyAssertionValidator.Fitness assertionFitness = PolicyAssertionValidator.Fitness.UNKNOWN;
      Iterator var3 = this.validators.iterator();

      while(var3.hasNext()) {
         PolicyAssertionValidator validator = (PolicyAssertionValidator)var3.next();
         assertionFitness = assertionFitness.combine(validator.validateClientSide(assertion));
         if (assertionFitness == PolicyAssertionValidator.Fitness.SUPPORTED) {
            break;
         }
      }

      return assertionFitness;
   }

   public PolicyAssertionValidator.Fitness validateServerSide(PolicyAssertion assertion) throws PolicyException {
      PolicyAssertionValidator.Fitness assertionFitness = PolicyAssertionValidator.Fitness.UNKNOWN;
      Iterator var3 = this.validators.iterator();

      while(var3.hasNext()) {
         PolicyAssertionValidator validator = (PolicyAssertionValidator)var3.next();
         assertionFitness = assertionFitness.combine(validator.validateServerSide(assertion));
         if (assertionFitness == PolicyAssertionValidator.Fitness.SUPPORTED) {
            break;
         }
      }

      return assertionFitness;
   }
}
