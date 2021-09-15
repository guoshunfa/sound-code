package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import javax.xml.namespace.QName;

public final class PolicyMapKey {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyMapKey.class);
   private final QName service;
   private final QName port;
   private final QName operation;
   private final QName faultMessage;
   private PolicyMapKeyHandler handler;

   PolicyMapKey(QName service, QName port, QName operation, PolicyMapKeyHandler handler) {
      this(service, port, operation, (QName)null, handler);
   }

   PolicyMapKey(QName service, QName port, QName operation, QName faultMessage, PolicyMapKeyHandler handler) {
      if (handler == null) {
         throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0046_POLICY_MAP_KEY_HANDLER_NOT_SET()));
      } else {
         this.service = service;
         this.port = port;
         this.operation = operation;
         this.faultMessage = faultMessage;
         this.handler = handler;
      }
   }

   PolicyMapKey(PolicyMapKey that) {
      this.service = that.service;
      this.port = that.port;
      this.operation = that.operation;
      this.faultMessage = that.faultMessage;
      this.handler = that.handler;
   }

   public QName getOperation() {
      return this.operation;
   }

   public QName getPort() {
      return this.port;
   }

   public QName getService() {
      return this.service;
   }

   void setHandler(PolicyMapKeyHandler handler) {
      if (handler == null) {
         throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0046_POLICY_MAP_KEY_HANDLER_NOT_SET()));
      } else {
         this.handler = handler;
      }
   }

   public QName getFaultMessage() {
      return this.faultMessage;
   }

   public boolean equals(Object that) {
      if (this == that) {
         return true;
      } else if (that == null) {
         return false;
      } else {
         return that instanceof PolicyMapKey ? this.handler.areEqual(this, (PolicyMapKey)that) : false;
      }
   }

   public int hashCode() {
      return this.handler.generateHashCode(this);
   }

   public String toString() {
      StringBuffer result = new StringBuffer("PolicyMapKey(");
      result.append((Object)this.service).append(", ").append((Object)this.port).append(", ").append((Object)this.operation).append(", ").append((Object)this.faultMessage);
      return result.append(")").toString();
   }
}
