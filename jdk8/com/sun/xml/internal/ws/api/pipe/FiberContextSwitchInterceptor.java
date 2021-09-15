package com.sun.xml.internal.ws.api.pipe;

public interface FiberContextSwitchInterceptor {
   <R, P> R execute(Fiber var1, P var2, FiberContextSwitchInterceptor.Work<R, P> var3);

   public interface Work<R, P> {
      R execute(P var1);
   }
}
