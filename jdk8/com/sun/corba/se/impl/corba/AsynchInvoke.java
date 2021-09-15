package com.sun.corba.se.impl.corba;

import com.sun.corba.se.spi.orb.ORB;

public class AsynchInvoke implements Runnable {
   private RequestImpl _req;
   private ORB _orb;
   private boolean _notifyORB;

   public AsynchInvoke(ORB var1, RequestImpl var2, boolean var3) {
      this._orb = var1;
      this._req = var2;
      this._notifyORB = var3;
   }

   public void run() {
      this._req.doInvocation();
      synchronized(this._req) {
         this._req.gotResponse = true;
         this._req.notify();
      }

      if (this._notifyORB) {
         this._orb.notifyORB();
      }

   }
}
