package org.omg.PortableInterceptor;

import org.omg.CORBA.Object;
import org.omg.CORBA.UserException;

public final class ForwardRequest extends UserException {
   public Object forward = null;

   public ForwardRequest() {
      super(ForwardRequestHelper.id());
   }

   public ForwardRequest(Object var1) {
      super(ForwardRequestHelper.id());
      this.forward = var1;
   }

   public ForwardRequest(String var1, Object var2) {
      super(ForwardRequestHelper.id() + "  " + var1);
      this.forward = var2;
   }
}
