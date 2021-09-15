package com.sun.xml.internal.ws.client.sei;

import java.lang.reflect.Method;
import java.util.concurrent.Future;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.WebServiceException;

final class CallbackMethodHandler extends AsyncMethodHandler {
   private final int handlerPos;

   CallbackMethodHandler(SEIStub owner, Method m, int handlerPos) {
      super(owner, m);
      this.handlerPos = handlerPos;
   }

   Future<?> invoke(Object proxy, Object[] args) throws WebServiceException {
      AsyncHandler handler = (AsyncHandler)args[this.handlerPos];
      return this.doInvoke(proxy, args, handler);
   }
}
