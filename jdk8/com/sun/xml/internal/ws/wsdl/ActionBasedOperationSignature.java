package com.sun.xml.internal.ws.wsdl;

import com.sun.istack.internal.NotNull;
import javax.xml.namespace.QName;

public class ActionBasedOperationSignature {
   private final String action;
   private final QName payloadQName;

   public ActionBasedOperationSignature(@NotNull String action, @NotNull QName payloadQName) {
      this.action = action;
      this.payloadQName = payloadQName;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ActionBasedOperationSignature that = (ActionBasedOperationSignature)o;
         if (!this.action.equals(that.action)) {
            return false;
         } else {
            return this.payloadQName.equals(that.payloadQName);
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.action.hashCode();
      result = 31 * result + this.payloadQName.hashCode();
      return result;
   }
}
