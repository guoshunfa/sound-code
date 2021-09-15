package com.sun.xml.internal.ws.client;

import javax.xml.ws.WebServiceException;

public abstract class AsyncInvoker implements Runnable {
   protected AsyncResponseImpl responseImpl;
   protected boolean nonNullAsyncHandlerGiven;

   public void setReceiver(AsyncResponseImpl responseImpl) {
      this.responseImpl = responseImpl;
   }

   public AsyncResponseImpl getResponseImpl() {
      return this.responseImpl;
   }

   public void setResponseImpl(AsyncResponseImpl responseImpl) {
      this.responseImpl = responseImpl;
   }

   public boolean isNonNullAsyncHandlerGiven() {
      return this.nonNullAsyncHandlerGiven;
   }

   public void setNonNullAsyncHandlerGiven(boolean nonNullAsyncHandlerGiven) {
      this.nonNullAsyncHandlerGiven = nonNullAsyncHandlerGiven;
   }

   public void run() {
      try {
         this.do_run();
      } catch (WebServiceException var2) {
         throw var2;
      } catch (Throwable var3) {
         throw new WebServiceException(var3);
      }
   }

   public abstract void do_run();
}
