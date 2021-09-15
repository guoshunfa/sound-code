package com.sun.xml.internal.ws.client.sei;

import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.client.AsyncInvoker;
import com.sun.xml.internal.ws.client.AsyncResponseImpl;
import com.sun.xml.internal.ws.client.RequestContext;
import com.sun.xml.internal.ws.client.ResponseContext;
import java.lang.reflect.Method;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;

abstract class AsyncMethodHandler extends MethodHandler {
   AsyncMethodHandler(SEIStub owner, Method m) {
      super(owner, m);
   }

   protected final Response<Object> doInvoke(Object proxy, Object[] args, AsyncHandler handler) {
      AsyncInvoker invoker = new AsyncMethodHandler.SEIAsyncInvoker(proxy, args);
      invoker.setNonNullAsyncHandlerGiven(handler != null);
      AsyncResponseImpl<Object> ft = new AsyncResponseImpl(invoker, handler);
      invoker.setReceiver(ft);
      ft.run();
      return ft;
   }

   ValueGetterFactory getValueGetterFactory() {
      return ValueGetterFactory.ASYNC;
   }

   private class SEIAsyncInvoker extends AsyncInvoker {
      private final RequestContext rc;
      private final Object[] args;

      SEIAsyncInvoker(Object proxy, Object[] args) {
         this.rc = AsyncMethodHandler.this.owner.requestContext.copy();
         this.args = args;
      }

      public void do_run() {
         JavaCallInfo call = AsyncMethodHandler.this.owner.databinding.createJavaCallInfo(AsyncMethodHandler.this.method, this.args);
         Packet req = (Packet)AsyncMethodHandler.this.owner.databinding.serializeRequest(call);
         Fiber.CompletionCallback callback = new Fiber.CompletionCallback() {
            public void onCompletion(@NotNull Packet response) {
               SEIAsyncInvoker.this.responseImpl.setResponseContext(new ResponseContext(response));
               Message msg = response.getMessage();
               if (msg != null) {
                  try {
                     Object[] rargs = new Object[1];
                     JavaCallInfo call = AsyncMethodHandler.this.owner.databinding.createJavaCallInfo(AsyncMethodHandler.this.method, rargs);
                     call = AsyncMethodHandler.this.owner.databinding.deserializeResponse(response, call);
                     if (call.getException() != null) {
                        throw call.getException();
                     }

                     SEIAsyncInvoker.this.responseImpl.set(rargs[0], (Throwable)null);
                  } catch (Throwable var5) {
                     if (var5 instanceof RuntimeException) {
                        if (var5 instanceof WebServiceException) {
                           SEIAsyncInvoker.this.responseImpl.set((Object)null, var5);
                           return;
                        }
                     } else if (var5 instanceof Exception) {
                        SEIAsyncInvoker.this.responseImpl.set((Object)null, var5);
                        return;
                     }

                     SEIAsyncInvoker.this.responseImpl.set((Object)null, new WebServiceException(var5));
                  }

               }
            }

            public void onCompletion(@NotNull Throwable error) {
               if (error instanceof WebServiceException) {
                  SEIAsyncInvoker.this.responseImpl.set((Object)null, error);
               } else {
                  SEIAsyncInvoker.this.responseImpl.set((Object)null, new WebServiceException(error));
               }

            }
         };
         AsyncMethodHandler.this.owner.doProcessAsync(this.responseImpl, req, this.rc, callback);
      }
   }
}
