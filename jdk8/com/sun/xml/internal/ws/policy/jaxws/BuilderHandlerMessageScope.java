package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapExtender;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import com.sun.xml.internal.ws.policy.PolicySubject;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;

final class BuilderHandlerMessageScope extends BuilderHandler {
   private final QName service;
   private final QName port;
   private final QName operation;
   private final QName message;
   private final BuilderHandlerMessageScope.Scope scope;

   BuilderHandlerMessageScope(Collection<String> policyURIs, Map<String, PolicySourceModel> policyStore, Object policySubject, BuilderHandlerMessageScope.Scope scope, QName service, QName port, QName operation, QName message) {
      super(policyURIs, policyStore, policySubject);
      this.service = service;
      this.port = port;
      this.operation = operation;
      this.scope = scope;
      this.message = message;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof BuilderHandlerMessageScope)) {
         return false;
      } else {
         boolean var10000;
         BuilderHandlerMessageScope that;
         boolean result;
         label108: {
            label107: {
               that = (BuilderHandlerMessageScope)obj;
               result = true;
               if (result) {
                  if (this.policySubject == null) {
                     if (that.policySubject == null) {
                        break label107;
                     }
                  } else if (this.policySubject.equals(that.policySubject)) {
                     break label107;
                  }
               }

               var10000 = false;
               break label108;
            }

            var10000 = true;
         }

         label99: {
            label98: {
               result = var10000;
               if (result) {
                  if (this.scope == null) {
                     if (that.scope == null) {
                        break label98;
                     }
                  } else if (this.scope.equals(that.scope)) {
                     break label98;
                  }
               }

               var10000 = false;
               break label99;
            }

            var10000 = true;
         }

         label90: {
            label89: {
               result = var10000;
               if (result) {
                  if (this.message == null) {
                     if (that.message == null) {
                        break label89;
                     }
                  } else if (this.message.equals(that.message)) {
                     break label89;
                  }
               }

               var10000 = false;
               break label90;
            }

            var10000 = true;
         }

         result = var10000;
         if (this.scope != BuilderHandlerMessageScope.Scope.FaultMessageScope) {
            label79: {
               label78: {
                  if (result) {
                     if (this.service == null) {
                        if (that.service == null) {
                           break label78;
                        }
                     } else if (this.service.equals(that.service)) {
                        break label78;
                     }
                  }

                  var10000 = false;
                  break label79;
               }

               var10000 = true;
            }

            label70: {
               label69: {
                  result = var10000;
                  if (result) {
                     if (this.port == null) {
                        if (that.port == null) {
                           break label69;
                        }
                     } else if (this.port.equals(that.port)) {
                        break label69;
                     }
                  }

                  var10000 = false;
                  break label70;
               }

               var10000 = true;
            }

            label61: {
               label60: {
                  result = var10000;
                  if (result) {
                     if (this.operation == null) {
                        if (that.operation == null) {
                           break label60;
                        }
                     } else if (this.operation.equals(that.operation)) {
                        break label60;
                     }
                  }

                  var10000 = false;
                  break label61;
               }

               var10000 = true;
            }

            result = var10000;
         }

         return result;
      }
   }

   public int hashCode() {
      int hashCode = 19;
      int hashCode = 31 * hashCode + (this.policySubject == null ? 0 : this.policySubject.hashCode());
      hashCode = 31 * hashCode + (this.message == null ? 0 : this.message.hashCode());
      hashCode = 31 * hashCode + (this.scope == null ? 0 : this.scope.hashCode());
      if (this.scope != BuilderHandlerMessageScope.Scope.FaultMessageScope) {
         hashCode = 31 * hashCode + (this.service == null ? 0 : this.service.hashCode());
         hashCode = 31 * hashCode + (this.port == null ? 0 : this.port.hashCode());
         hashCode = 31 * hashCode + (this.operation == null ? 0 : this.operation.hashCode());
      }

      return hashCode;
   }

   protected void doPopulate(PolicyMapExtender policyMapExtender) throws PolicyException {
      PolicyMapKey mapKey;
      if (BuilderHandlerMessageScope.Scope.FaultMessageScope == this.scope) {
         mapKey = PolicyMap.createWsdlFaultMessageScopeKey(this.service, this.port, this.operation, this.message);
      } else {
         mapKey = PolicyMap.createWsdlMessageScopeKey(this.service, this.port, this.operation);
      }

      Iterator var3;
      PolicySubject subject;
      if (BuilderHandlerMessageScope.Scope.InputMessageScope == this.scope) {
         var3 = this.getPolicySubjects().iterator();

         while(var3.hasNext()) {
            subject = (PolicySubject)var3.next();
            policyMapExtender.putInputMessageSubject(mapKey, subject);
         }
      } else if (BuilderHandlerMessageScope.Scope.OutputMessageScope == this.scope) {
         var3 = this.getPolicySubjects().iterator();

         while(var3.hasNext()) {
            subject = (PolicySubject)var3.next();
            policyMapExtender.putOutputMessageSubject(mapKey, subject);
         }
      } else if (BuilderHandlerMessageScope.Scope.FaultMessageScope == this.scope) {
         var3 = this.getPolicySubjects().iterator();

         while(var3.hasNext()) {
            subject = (PolicySubject)var3.next();
            policyMapExtender.putFaultMessageSubject(mapKey, subject);
         }
      }

   }

   static enum Scope {
      InputMessageScope,
      OutputMessageScope,
      FaultMessageScope;
   }
}
