package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

public abstract class PolicyMapMutator {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyMapMutator.class);
   private PolicyMap map = null;

   PolicyMapMutator() {
   }

   public void connect(PolicyMap map) {
      if (this.isConnected()) {
         throw (IllegalStateException)LOGGER.logSevereException(new IllegalStateException(LocalizationMessages.WSP_0044_POLICY_MAP_MUTATOR_ALREADY_CONNECTED()));
      } else {
         this.map = map;
      }
   }

   public PolicyMap getMap() {
      return this.map;
   }

   public void disconnect() {
      this.map = null;
   }

   public boolean isConnected() {
      return this.map != null;
   }
}
