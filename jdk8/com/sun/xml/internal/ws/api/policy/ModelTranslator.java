package com.sun.xml.internal.ws.api.policy;

import com.sun.xml.internal.ws.config.management.policy.ManagementAssertionCreator;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelTranslator;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionCreator;
import com.sun.xml.internal.ws.resources.ManagementMessages;
import java.util.Arrays;

public class ModelTranslator extends PolicyModelTranslator {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(ModelTranslator.class);
   private static final PolicyAssertionCreator[] JAXWS_ASSERTION_CREATORS = new PolicyAssertionCreator[]{new ManagementAssertionCreator()};
   private static final ModelTranslator translator;
   private static final PolicyException creationException;

   private ModelTranslator() throws PolicyException {
      super(Arrays.asList(JAXWS_ASSERTION_CREATORS));
   }

   public static ModelTranslator getTranslator() throws PolicyException {
      if (creationException != null) {
         throw (PolicyException)LOGGER.logSevereException(creationException);
      } else {
         return translator;
      }
   }

   static {
      ModelTranslator tempTranslator = null;
      PolicyException tempException = null;

      try {
         tempTranslator = new ModelTranslator();
      } catch (PolicyException var6) {
         tempException = var6;
         LOGGER.warning(ManagementMessages.WSM_1007_FAILED_MODEL_TRANSLATOR_INSTANTIATION(), var6);
      } finally {
         translator = tempTranslator;
         creationException = tempException;
      }

   }
}
