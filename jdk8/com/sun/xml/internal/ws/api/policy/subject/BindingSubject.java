package com.sun.xml.internal.ws.api.policy.subject;

import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.resources.BindingApiMessages;
import javax.xml.namespace.QName;

public class BindingSubject {
   private static final Logger LOGGER = Logger.getLogger(BindingSubject.class);
   private final QName name;
   private final BindingSubject.WsdlMessageType messageType;
   private final BindingSubject.WsdlNameScope nameScope;
   private final BindingSubject parent;

   BindingSubject(QName name, BindingSubject.WsdlNameScope scope, BindingSubject parent) {
      this(name, BindingSubject.WsdlMessageType.NO_MESSAGE, scope, parent);
   }

   BindingSubject(QName name, BindingSubject.WsdlMessageType messageType, BindingSubject.WsdlNameScope scope, BindingSubject parent) {
      this.name = name;
      this.messageType = messageType;
      this.nameScope = scope;
      this.parent = parent;
   }

   public static BindingSubject createBindingSubject(QName bindingName) {
      return new BindingSubject(bindingName, BindingSubject.WsdlNameScope.ENDPOINT, (BindingSubject)null);
   }

   public static BindingSubject createOperationSubject(QName bindingName, QName operationName) {
      BindingSubject bindingSubject = createBindingSubject(bindingName);
      return new BindingSubject(operationName, BindingSubject.WsdlNameScope.OPERATION, bindingSubject);
   }

   public static BindingSubject createInputMessageSubject(QName bindingName, QName operationName, QName messageName) {
      BindingSubject operationSubject = createOperationSubject(bindingName, operationName);
      return new BindingSubject(messageName, BindingSubject.WsdlMessageType.INPUT, BindingSubject.WsdlNameScope.MESSAGE, operationSubject);
   }

   public static BindingSubject createOutputMessageSubject(QName bindingName, QName operationName, QName messageName) {
      BindingSubject operationSubject = createOperationSubject(bindingName, operationName);
      return new BindingSubject(messageName, BindingSubject.WsdlMessageType.OUTPUT, BindingSubject.WsdlNameScope.MESSAGE, operationSubject);
   }

   public static BindingSubject createFaultMessageSubject(QName bindingName, QName operationName, QName messageName) {
      if (messageName == null) {
         throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(BindingApiMessages.BINDING_API_NO_FAULT_MESSAGE_NAME()));
      } else {
         BindingSubject operationSubject = createOperationSubject(bindingName, operationName);
         return new BindingSubject(messageName, BindingSubject.WsdlMessageType.FAULT, BindingSubject.WsdlNameScope.MESSAGE, operationSubject);
      }
   }

   public QName getName() {
      return this.name;
   }

   public BindingSubject getParent() {
      return this.parent;
   }

   public boolean isBindingSubject() {
      if (this.nameScope == BindingSubject.WsdlNameScope.ENDPOINT) {
         return this.parent == null;
      } else {
         return false;
      }
   }

   public boolean isOperationSubject() {
      return this.nameScope == BindingSubject.WsdlNameScope.OPERATION && this.parent != null ? this.parent.isBindingSubject() : false;
   }

   public boolean isMessageSubject() {
      return this.nameScope == BindingSubject.WsdlNameScope.MESSAGE && this.parent != null ? this.parent.isOperationSubject() : false;
   }

   public boolean isInputMessageSubject() {
      return this.isMessageSubject() && this.messageType == BindingSubject.WsdlMessageType.INPUT;
   }

   public boolean isOutputMessageSubject() {
      return this.isMessageSubject() && this.messageType == BindingSubject.WsdlMessageType.OUTPUT;
   }

   public boolean isFaultMessageSubject() {
      return this.isMessageSubject() && this.messageType == BindingSubject.WsdlMessageType.FAULT;
   }

   public boolean equals(Object that) {
      if (this == that) {
         return true;
      } else if (that != null && that instanceof BindingSubject) {
         boolean var10000;
         BindingSubject thatSubject;
         boolean isEqual;
         label59: {
            label58: {
               thatSubject = (BindingSubject)that;
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
      int result = 29 * result + (this.name == null ? 0 : this.name.hashCode());
      result = 29 * result + this.messageType.hashCode();
      result = 29 * result + this.nameScope.hashCode();
      result = 29 * result + (this.parent == null ? 0 : this.parent.hashCode());
      return result;
   }

   public String toString() {
      StringBuilder result = new StringBuilder("BindingSubject[");
      result.append((Object)this.name).append(", ").append((Object)this.messageType);
      result.append(", ").append((Object)this.nameScope).append(", ").append((Object)this.parent);
      return result.append("]").toString();
   }

   private static enum WsdlNameScope {
      SERVICE,
      ENDPOINT,
      OPERATION,
      MESSAGE;
   }

   private static enum WsdlMessageType {
      NO_MESSAGE,
      INPUT,
      OUTPUT,
      FAULT;
   }
}
