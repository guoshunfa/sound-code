package com.sun.xml.internal.ws.client.sei;

import java.lang.reflect.Method;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;

final class PollingMethodHandler extends AsyncMethodHandler {
   PollingMethodHandler(SEIStub owner, Method m) {
      super(owner, m);
   }

   Response<?> invoke(Object proxy, Object[] args) throws WebServiceException {
      return this.doInvoke(proxy, args, (AsyncHandler)null);
   }
}
