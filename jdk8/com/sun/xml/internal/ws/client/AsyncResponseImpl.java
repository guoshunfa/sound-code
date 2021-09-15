package com.sun.xml.internal.ws.client;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.Cancelable;
import com.sun.xml.internal.ws.util.CompletedFuture;
import java.util.Map;
import java.util.concurrent.FutureTask;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;

public final class AsyncResponseImpl<T> extends FutureTask<T> implements Response<T>, ResponseContextReceiver {
   private final AsyncHandler<T> handler;
   private ResponseContext responseContext;
   private final Runnable callable;
   private Cancelable cancelable;

   public AsyncResponseImpl(Runnable runnable, @Nullable AsyncHandler<T> handler) {
      super(runnable, (Object)null);
      this.callable = runnable;
      this.handler = handler;
   }

   public void run() {
      try {
         this.callable.run();
      } catch (WebServiceException var2) {
         this.set((Object)null, var2);
      } catch (Throwable var3) {
         this.set((Object)null, new WebServiceException(var3));
      }

   }

   public ResponseContext getContext() {
      return this.responseContext;
   }

   public void setResponseContext(ResponseContext rc) {
      this.responseContext = rc;
   }

   public void set(T v, Throwable t) {
      if (this.handler != null) {
         try {
            class CallbackFuture<T> extends CompletedFuture<T> implements Response<T> {
               public CallbackFuture(T v, Throwable t) {
                  super(v, t);
               }

               public Map<String, Object> getContext() {
                  return AsyncResponseImpl.this.getContext();
               }
            }

            this.handler.handleResponse(new CallbackFuture(v, t));
         } catch (Throwable var4) {
            super.setException(var4);
            return;
         }
      }

      if (t != null) {
         super.setException(t);
      } else {
         super.set(v);
      }

   }

   public void setCancelable(Cancelable cancelable) {
      this.cancelable = cancelable;
   }

   public boolean cancel(boolean mayInterruptIfRunning) {
      if (this.cancelable != null) {
         this.cancelable.cancel(mayInterruptIfRunning);
      }

      return super.cancel(mayInterruptIfRunning);
   }
}
