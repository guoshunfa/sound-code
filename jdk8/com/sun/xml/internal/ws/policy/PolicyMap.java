package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.namespace.QName;

public final class PolicyMap implements Iterable<Policy> {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyMap.class);
   private static final PolicyMapKeyHandler serviceKeyHandler = new PolicyMapKeyHandler() {
      public boolean areEqual(PolicyMapKey key1, PolicyMapKey key2) {
         return key1.getService().equals(key2.getService());
      }

      public int generateHashCode(PolicyMapKey key) {
         int resultx = 17;
         int result = 37 * resultx + key.getService().hashCode();
         return result;
      }
   };
   private static final PolicyMapKeyHandler endpointKeyHandler = new PolicyMapKeyHandler() {
      public boolean areEqual(PolicyMapKey key1, PolicyMapKey key2) {
         boolean var10000;
         boolean retVal;
         label23: {
            label22: {
               retVal = true;
               retVal = retVal && key1.getService().equals(key2.getService());
               if (retVal) {
                  if (key1.getPort() == null) {
                     if (key2.getPort() == null) {
                        break label22;
                     }
                  } else if (key1.getPort().equals(key2.getPort())) {
                     break label22;
                  }
               }

               var10000 = false;
               break label23;
            }

            var10000 = true;
         }

         retVal = var10000;
         return retVal;
      }

      public int generateHashCode(PolicyMapKey key) {
         int resultx = 17;
         int result = 37 * resultx + key.getService().hashCode();
         result = 37 * result + (key.getPort() == null ? 0 : key.getPort().hashCode());
         return result;
      }
   };
   private static final PolicyMapKeyHandler operationAndInputOutputMessageKeyHandler = new PolicyMapKeyHandler() {
      public boolean areEqual(PolicyMapKey key1, PolicyMapKey key2) {
         boolean var10000;
         boolean retVal;
         label39: {
            label38: {
               retVal = true;
               retVal = retVal && key1.getService().equals(key2.getService());
               if (retVal) {
                  if (key1.getPort() == null) {
                     if (key2.getPort() == null) {
                        break label38;
                     }
                  } else if (key1.getPort().equals(key2.getPort())) {
                     break label38;
                  }
               }

               var10000 = false;
               break label39;
            }

            var10000 = true;
         }

         label30: {
            label29: {
               retVal = var10000;
               if (retVal) {
                  if (key1.getOperation() == null) {
                     if (key2.getOperation() == null) {
                        break label29;
                     }
                  } else if (key1.getOperation().equals(key2.getOperation())) {
                     break label29;
                  }
               }

               var10000 = false;
               break label30;
            }

            var10000 = true;
         }

         retVal = var10000;
         return retVal;
      }

      public int generateHashCode(PolicyMapKey key) {
         int resultx = 17;
         int result = 37 * resultx + key.getService().hashCode();
         result = 37 * result + (key.getPort() == null ? 0 : key.getPort().hashCode());
         result = 37 * result + (key.getOperation() == null ? 0 : key.getOperation().hashCode());
         return result;
      }
   };
   private static final PolicyMapKeyHandler faultMessageHandler = new PolicyMapKeyHandler() {
      public boolean areEqual(PolicyMapKey key1, PolicyMapKey key2) {
         boolean var10000;
         boolean retVal;
         label55: {
            label54: {
               retVal = true;
               retVal = retVal && key1.getService().equals(key2.getService());
               if (retVal) {
                  if (key1.getPort() == null) {
                     if (key2.getPort() == null) {
                        break label54;
                     }
                  } else if (key1.getPort().equals(key2.getPort())) {
                     break label54;
                  }
               }

               var10000 = false;
               break label55;
            }

            var10000 = true;
         }

         label46: {
            label45: {
               retVal = var10000;
               if (retVal) {
                  if (key1.getOperation() == null) {
                     if (key2.getOperation() == null) {
                        break label45;
                     }
                  } else if (key1.getOperation().equals(key2.getOperation())) {
                     break label45;
                  }
               }

               var10000 = false;
               break label46;
            }

            var10000 = true;
         }

         label37: {
            label36: {
               retVal = var10000;
               if (retVal) {
                  if (key1.getFaultMessage() == null) {
                     if (key2.getFaultMessage() == null) {
                        break label36;
                     }
                  } else if (key1.getFaultMessage().equals(key2.getFaultMessage())) {
                     break label36;
                  }
               }

               var10000 = false;
               break label37;
            }

            var10000 = true;
         }

         retVal = var10000;
         return retVal;
      }

      public int generateHashCode(PolicyMapKey key) {
         int resultx = 17;
         int result = 37 * resultx + key.getService().hashCode();
         result = 37 * result + (key.getPort() == null ? 0 : key.getPort().hashCode());
         result = 37 * result + (key.getOperation() == null ? 0 : key.getOperation().hashCode());
         result = 37 * result + (key.getFaultMessage() == null ? 0 : key.getFaultMessage().hashCode());
         return result;
      }
   };
   private static final PolicyMerger merger = PolicyMerger.getMerger();
   private final PolicyMap.ScopeMap serviceMap;
   private final PolicyMap.ScopeMap endpointMap;
   private final PolicyMap.ScopeMap operationMap;
   private final PolicyMap.ScopeMap inputMessageMap;
   private final PolicyMap.ScopeMap outputMessageMap;
   private final PolicyMap.ScopeMap faultMessageMap;

   private PolicyMap() {
      this.serviceMap = new PolicyMap.ScopeMap(merger, serviceKeyHandler);
      this.endpointMap = new PolicyMap.ScopeMap(merger, endpointKeyHandler);
      this.operationMap = new PolicyMap.ScopeMap(merger, operationAndInputOutputMessageKeyHandler);
      this.inputMessageMap = new PolicyMap.ScopeMap(merger, operationAndInputOutputMessageKeyHandler);
      this.outputMessageMap = new PolicyMap.ScopeMap(merger, operationAndInputOutputMessageKeyHandler);
      this.faultMessageMap = new PolicyMap.ScopeMap(merger, faultMessageHandler);
   }

   public static PolicyMap createPolicyMap(Collection<? extends PolicyMapMutator> mutators) {
      PolicyMap result = new PolicyMap();
      if (mutators != null && !mutators.isEmpty()) {
         Iterator var2 = mutators.iterator();

         while(var2.hasNext()) {
            PolicyMapMutator mutator = (PolicyMapMutator)var2.next();
            mutator.connect(result);
         }
      }

      return result;
   }

   public Policy getServiceEffectivePolicy(PolicyMapKey key) throws PolicyException {
      return this.serviceMap.getEffectivePolicy(key);
   }

   public Policy getEndpointEffectivePolicy(PolicyMapKey key) throws PolicyException {
      return this.endpointMap.getEffectivePolicy(key);
   }

   public Policy getOperationEffectivePolicy(PolicyMapKey key) throws PolicyException {
      return this.operationMap.getEffectivePolicy(key);
   }

   public Policy getInputMessageEffectivePolicy(PolicyMapKey key) throws PolicyException {
      return this.inputMessageMap.getEffectivePolicy(key);
   }

   public Policy getOutputMessageEffectivePolicy(PolicyMapKey key) throws PolicyException {
      return this.outputMessageMap.getEffectivePolicy(key);
   }

   public Policy getFaultMessageEffectivePolicy(PolicyMapKey key) throws PolicyException {
      return this.faultMessageMap.getEffectivePolicy(key);
   }

   public Collection<PolicyMapKey> getAllServiceScopeKeys() {
      return this.serviceMap.getAllKeys();
   }

   public Collection<PolicyMapKey> getAllEndpointScopeKeys() {
      return this.endpointMap.getAllKeys();
   }

   public Collection<PolicyMapKey> getAllOperationScopeKeys() {
      return this.operationMap.getAllKeys();
   }

   public Collection<PolicyMapKey> getAllInputMessageScopeKeys() {
      return this.inputMessageMap.getAllKeys();
   }

   public Collection<PolicyMapKey> getAllOutputMessageScopeKeys() {
      return this.outputMessageMap.getAllKeys();
   }

   public Collection<PolicyMapKey> getAllFaultMessageScopeKeys() {
      return this.faultMessageMap.getAllKeys();
   }

   void putSubject(PolicyMap.ScopeType scopeType, PolicyMapKey key, PolicySubject subject) {
      switch(scopeType) {
      case SERVICE:
         this.serviceMap.putSubject(key, subject);
         break;
      case ENDPOINT:
         this.endpointMap.putSubject(key, subject);
         break;
      case OPERATION:
         this.operationMap.putSubject(key, subject);
         break;
      case INPUT_MESSAGE:
         this.inputMessageMap.putSubject(key, subject);
         break;
      case OUTPUT_MESSAGE:
         this.outputMessageMap.putSubject(key, subject);
         break;
      case FAULT_MESSAGE:
         this.faultMessageMap.putSubject(key, subject);
         break;
      default:
         throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0002_UNRECOGNIZED_SCOPE_TYPE(scopeType)));
      }

   }

   void setNewEffectivePolicyForScope(PolicyMap.ScopeType scopeType, PolicyMapKey key, Policy newEffectivePolicy) throws IllegalArgumentException {
      if (scopeType != null && key != null && newEffectivePolicy != null) {
         switch(scopeType) {
         case SERVICE:
            this.serviceMap.setNewEffectivePolicy(key, newEffectivePolicy);
            break;
         case ENDPOINT:
            this.endpointMap.setNewEffectivePolicy(key, newEffectivePolicy);
            break;
         case OPERATION:
            this.operationMap.setNewEffectivePolicy(key, newEffectivePolicy);
            break;
         case INPUT_MESSAGE:
            this.inputMessageMap.setNewEffectivePolicy(key, newEffectivePolicy);
            break;
         case OUTPUT_MESSAGE:
            this.outputMessageMap.setNewEffectivePolicy(key, newEffectivePolicy);
            break;
         case FAULT_MESSAGE:
            this.faultMessageMap.setNewEffectivePolicy(key, newEffectivePolicy);
            break;
         default:
            throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0002_UNRECOGNIZED_SCOPE_TYPE(scopeType)));
         }

      } else {
         throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0062_INPUT_PARAMS_MUST_NOT_BE_NULL()));
      }
   }

   public Collection<PolicySubject> getPolicySubjects() {
      List<PolicySubject> subjects = new LinkedList();
      this.addSubjects(subjects, this.serviceMap);
      this.addSubjects(subjects, this.endpointMap);
      this.addSubjects(subjects, this.operationMap);
      this.addSubjects(subjects, this.inputMessageMap);
      this.addSubjects(subjects, this.outputMessageMap);
      this.addSubjects(subjects, this.faultMessageMap);
      return subjects;
   }

   public boolean isInputMessageSubject(PolicySubject subject) {
      Iterator var2 = this.inputMessageMap.getStoredScopes().iterator();

      PolicyScope scope;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         scope = (PolicyScope)var2.next();
      } while(!scope.getPolicySubjects().contains(subject));

      return true;
   }

   public boolean isOutputMessageSubject(PolicySubject subject) {
      Iterator var2 = this.outputMessageMap.getStoredScopes().iterator();

      PolicyScope scope;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         scope = (PolicyScope)var2.next();
      } while(!scope.getPolicySubjects().contains(subject));

      return true;
   }

   public boolean isFaultMessageSubject(PolicySubject subject) {
      Iterator var2 = this.faultMessageMap.getStoredScopes().iterator();

      PolicyScope scope;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         scope = (PolicyScope)var2.next();
      } while(!scope.getPolicySubjects().contains(subject));

      return true;
   }

   public boolean isEmpty() {
      return this.serviceMap.isEmpty() && this.endpointMap.isEmpty() && this.operationMap.isEmpty() && this.inputMessageMap.isEmpty() && this.outputMessageMap.isEmpty() && this.faultMessageMap.isEmpty();
   }

   private void addSubjects(Collection<PolicySubject> subjects, PolicyMap.ScopeMap scopeMap) {
      Iterator var3 = scopeMap.getStoredScopes().iterator();

      while(var3.hasNext()) {
         PolicyScope scope = (PolicyScope)var3.next();
         Collection<PolicySubject> scopedSubjects = scope.getPolicySubjects();
         subjects.addAll(scopedSubjects);
      }

   }

   public static PolicyMapKey createWsdlServiceScopeKey(QName service) throws IllegalArgumentException {
      if (service == null) {
         throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0031_SERVICE_PARAM_MUST_NOT_BE_NULL()));
      } else {
         return new PolicyMapKey(service, (QName)null, (QName)null, serviceKeyHandler);
      }
   }

   public static PolicyMapKey createWsdlEndpointScopeKey(QName service, QName port) throws IllegalArgumentException {
      if (service != null && port != null) {
         return new PolicyMapKey(service, port, (QName)null, endpointKeyHandler);
      } else {
         throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0033_SERVICE_AND_PORT_PARAM_MUST_NOT_BE_NULL(service, port)));
      }
   }

   public static PolicyMapKey createWsdlOperationScopeKey(QName service, QName port, QName operation) throws IllegalArgumentException {
      return createOperationOrInputOutputMessageKey(service, port, operation);
   }

   public static PolicyMapKey createWsdlMessageScopeKey(QName service, QName port, QName operation) throws IllegalArgumentException {
      return createOperationOrInputOutputMessageKey(service, port, operation);
   }

   public static PolicyMapKey createWsdlFaultMessageScopeKey(QName service, QName port, QName operation, QName fault) throws IllegalArgumentException {
      if (service != null && port != null && operation != null && fault != null) {
         return new PolicyMapKey(service, port, operation, fault, faultMessageHandler);
      } else {
         throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0030_SERVICE_PORT_OPERATION_FAULT_MSG_PARAM_MUST_NOT_BE_NULL(service, port, operation, fault)));
      }
   }

   private static PolicyMapKey createOperationOrInputOutputMessageKey(QName service, QName port, QName operation) {
      if (service != null && port != null && operation != null) {
         return new PolicyMapKey(service, port, operation, operationAndInputOutputMessageKeyHandler);
      } else {
         throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0029_SERVICE_PORT_OPERATION_PARAM_MUST_NOT_BE_NULL(service, port, operation)));
      }
   }

   public String toString() {
      StringBuffer result = new StringBuffer();
      if (null != this.serviceMap) {
         result.append("\nServiceMap=").append((Object)this.serviceMap);
      }

      if (null != this.endpointMap) {
         result.append("\nEndpointMap=").append((Object)this.endpointMap);
      }

      if (null != this.operationMap) {
         result.append("\nOperationMap=").append((Object)this.operationMap);
      }

      if (null != this.inputMessageMap) {
         result.append("\nInputMessageMap=").append((Object)this.inputMessageMap);
      }

      if (null != this.outputMessageMap) {
         result.append("\nOutputMessageMap=").append((Object)this.outputMessageMap);
      }

      if (null != this.faultMessageMap) {
         result.append("\nFaultMessageMap=").append((Object)this.faultMessageMap);
      }

      return result.toString();
   }

   public Iterator<Policy> iterator() {
      return new Iterator<Policy>() {
         private final Iterator<Iterator<Policy>> mainIterator;
         private Iterator<Policy> currentScopeIterator;

         {
            Collection<Iterator<Policy>> scopeIterators = new ArrayList(6);
            scopeIterators.add(PolicyMap.this.serviceMap.iterator());
            scopeIterators.add(PolicyMap.this.endpointMap.iterator());
            scopeIterators.add(PolicyMap.this.operationMap.iterator());
            scopeIterators.add(PolicyMap.this.inputMessageMap.iterator());
            scopeIterators.add(PolicyMap.this.outputMessageMap.iterator());
            scopeIterators.add(PolicyMap.this.faultMessageMap.iterator());
            this.mainIterator = scopeIterators.iterator();
            this.currentScopeIterator = (Iterator)this.mainIterator.next();
         }

         public boolean hasNext() {
            while(true) {
               if (!this.currentScopeIterator.hasNext()) {
                  if (this.mainIterator.hasNext()) {
                     this.currentScopeIterator = (Iterator)this.mainIterator.next();
                     continue;
                  }

                  return false;
               }

               return true;
            }
         }

         public Policy next() {
            if (this.hasNext()) {
               return (Policy)this.currentScopeIterator.next();
            } else {
               throw (NoSuchElementException)PolicyMap.LOGGER.logSevereException(new NoSuchElementException(LocalizationMessages.WSP_0054_NO_MORE_ELEMS_IN_POLICY_MAP()));
            }
         }

         public void remove() {
            throw (UnsupportedOperationException)PolicyMap.LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0034_REMOVE_OPERATION_NOT_SUPPORTED()));
         }
      };
   }

   private static final class ScopeMap implements Iterable<Policy> {
      private final Map<PolicyMapKey, PolicyScope> internalMap = new HashMap();
      private final PolicyMapKeyHandler scopeKeyHandler;
      private final PolicyMerger merger;

      ScopeMap(PolicyMerger merger, PolicyMapKeyHandler scopeKeyHandler) {
         this.merger = merger;
         this.scopeKeyHandler = scopeKeyHandler;
      }

      Policy getEffectivePolicy(PolicyMapKey key) throws PolicyException {
         PolicyScope scope = (PolicyScope)this.internalMap.get(this.createLocalCopy(key));
         return scope == null ? null : scope.getEffectivePolicy(this.merger);
      }

      void putSubject(PolicyMapKey key, PolicySubject subject) {
         PolicyMapKey localKey = this.createLocalCopy(key);
         PolicyScope scope = (PolicyScope)this.internalMap.get(localKey);
         if (scope == null) {
            List<PolicySubject> list = new LinkedList();
            list.add(subject);
            this.internalMap.put(localKey, new PolicyScope(list));
         } else {
            scope.attach(subject);
         }

      }

      void setNewEffectivePolicy(PolicyMapKey key, Policy newEffectivePolicy) {
         PolicySubject subject = new PolicySubject(key, newEffectivePolicy);
         PolicyMapKey localKey = this.createLocalCopy(key);
         PolicyScope scope = (PolicyScope)this.internalMap.get(localKey);
         if (scope == null) {
            List<PolicySubject> list = new LinkedList();
            list.add(subject);
            this.internalMap.put(localKey, new PolicyScope(list));
         } else {
            scope.dettachAllSubjects();
            scope.attach(subject);
         }

      }

      Collection<PolicyScope> getStoredScopes() {
         return this.internalMap.values();
      }

      Set<PolicyMapKey> getAllKeys() {
         return this.internalMap.keySet();
      }

      private PolicyMapKey createLocalCopy(PolicyMapKey key) {
         if (key == null) {
            throw (IllegalArgumentException)PolicyMap.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0045_POLICY_MAP_KEY_MUST_NOT_BE_NULL()));
         } else {
            PolicyMapKey localKeyCopy = new PolicyMapKey(key);
            localKeyCopy.setHandler(this.scopeKeyHandler);
            return localKeyCopy;
         }
      }

      public Iterator<Policy> iterator() {
         return new Iterator<Policy>() {
            private final Iterator<PolicyMapKey> keysIterator;

            {
               this.keysIterator = ScopeMap.this.internalMap.keySet().iterator();
            }

            public boolean hasNext() {
               return this.keysIterator.hasNext();
            }

            public Policy next() {
               PolicyMapKey key = (PolicyMapKey)this.keysIterator.next();

               try {
                  return ScopeMap.this.getEffectivePolicy(key);
               } catch (PolicyException var3) {
                  throw (IllegalStateException)PolicyMap.LOGGER.logSevereException(new IllegalStateException(LocalizationMessages.WSP_0069_EXCEPTION_WHILE_RETRIEVING_EFFECTIVE_POLICY_FOR_KEY(key), var3));
               }
            }

            public void remove() {
               throw (UnsupportedOperationException)PolicyMap.LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0034_REMOVE_OPERATION_NOT_SUPPORTED()));
            }
         };
      }

      public boolean isEmpty() {
         return this.internalMap.isEmpty();
      }

      public String toString() {
         return this.internalMap.toString();
      }
   }

   static enum ScopeType {
      SERVICE,
      ENDPOINT,
      OPERATION,
      INPUT_MESSAGE,
      OUTPUT_MESSAGE,
      FAULT_MESSAGE;
   }
}
