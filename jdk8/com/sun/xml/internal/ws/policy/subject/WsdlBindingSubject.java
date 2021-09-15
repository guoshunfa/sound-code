package com.sun.xml.internal.ws.policy.subject;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import javax.xml.namespace.QName;

public class WsdlBindingSubject {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(WsdlBindingSubject.class);
   private final QName name;
   private final WsdlBindingSubject.WsdlMessageType messageType;
   private final WsdlBindingSubject.WsdlNameScope nameScope;
   private final WsdlBindingSubject parent;

   WsdlBindingSubject(QName name, WsdlBindingSubject.WsdlNameScope scope, WsdlBindingSubject parent) {
      this(name, WsdlBindingSubject.WsdlMessageType.NO_MESSAGE, scope, parent);
   }

   WsdlBindingSubject(QName name, WsdlBindingSubject.WsdlMessageType messageType, WsdlBindingSubject.WsdlNameScope scope, WsdlBindingSubject parent) {
      this.name = name;
      this.messageType = messageType;
      this.nameScope = scope;
      this.parent = parent;
   }

   public static WsdlBindingSubject createBindingSubject(QName bindingName) {
      return new WsdlBindingSubject(bindingName, WsdlBindingSubject.WsdlNameScope.ENDPOINT, (WsdlBindingSubject)null);
   }

   public static WsdlBindingSubject createBindingOperationSubject(QName bindingName, QName operationName) {
      WsdlBindingSubject bindingSubject = createBindingSubject(bindingName);
      return new WsdlBindingSubject(operationName, WsdlBindingSubject.WsdlNameScope.OPERATION, bindingSubject);
   }

   public static WsdlBindingSubject createBindingMessageSubject(QName bindingName, QName operationName, QName messageName, WsdlBindingSubject.WsdlMessageType messageType) {
      if (messageType == null) {
         throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0083_MESSAGE_TYPE_NULL()));
      } else if (messageType == WsdlBindingSubject.WsdlMessageType.NO_MESSAGE) {
         throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0084_MESSAGE_TYPE_NO_MESSAGE()));
      } else if (messageType == WsdlBindingSubject.WsdlMessageType.FAULT && messageName == null) {
         throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0085_MESSAGE_FAULT_NO_NAME()));
      } else {
         WsdlBindingSubject operationSubject = createBindingOperationSubject(bindingName, operationName);
         return new WsdlBindingSubject(messageName, messageType, WsdlBindingSubject.WsdlNameScope.MESSAGE, operationSubject);
      }
   }

   public QName getName() {
      return this.name;
   }

   public WsdlBindingSubject.WsdlMessageType getMessageType() {
      return this.messageType;
   }

   public WsdlBindingSubject getParent() {
      return this.parent;
   }

   public boolean isBindingSubject() {
      if (this.nameScope == WsdlBindingSubject.WsdlNameScope.ENDPOINT) {
         return this.parent == null;
      } else {
         return false;
      }
   }

   public boolean isBindingOperationSubject() {
      return this.nameScope == WsdlBindingSubject.WsdlNameScope.OPERATION && this.parent != null ? this.parent.isBindingSubject() : false;
   }

   public boolean isBindingMessageSubject() {
      return this.nameScope == WsdlBindingSubject.WsdlNameScope.MESSAGE && this.parent != null ? this.parent.isBindingOperationSubject() : false;
   }

   public boolean equals(Object that) {
      if (this == that) {
         return true;
      } else if (that != null && that instanceof WsdlBindingSubject) {
         boolean var10000;
         WsdlBindingSubject thatSubject;
         boolean isEqual;
         label59: {
            label58: {
               thatSubject = (WsdlBindingSubject)that;
               isEqual = true;
               if (isEqual) {
                  if (this.name == null) {
                     if (thatSubject.name == null) {
                        break label58;
                     }
                  } else if (this.name.equals(thatSubject.name)) {
                     break label58;
                  }
               }

               var10000 = false;
               break label59;
            }

            var10000 = true;
         }

         label40: {
            label39: {
               isEqual = var10000;
               isEqual = isEqual && this.messageType.equals(thatSubject.messageType);
               isEqual = isEqual && this.nameScope.equals(thatSubject.nameScope);
               if (isEqual) {
                  if (this.parent == null) {
                     if (thatSubject.parent == null) {
                        break label39;
                     }
                  } else if (this.parent.equals(thatSubject.parent)) {
                     break label39;
                  }
               }

               var10000 = false;
               break label40;
            }

            var10000 = true;
         }

         isEqual = var10000;
         return isEqual;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = 23;
      int result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
      result = 31 * result + this.messageType.hashCode();
      result = 31 * result + this.nameScope.hashCode();
      result = 31 * result + (this.parent == null ? 0 : this.parent.hashCode());
      return result;
   }

   public String toString() {
      StringBuilder result = new StringBuilder("WsdlBindingSubject[");
      result.append((Object)this.name).append(", ").append((Object)this.messageType);
      result.append(", ").append((Object)this.nameScope).append(", ").append((Object)this.parent);
      return result.append("]").toString();
   }

   public static enum WsdlNameScope {
      SERVICE,
      ENDPOINT,
      OPERATION,
      MESSAGE;
   }

   public static enum WsdlMessageType {
      NO_MESSAGE,
      INPUT,
      OUTPUT,
      FAULT;
   }
}
