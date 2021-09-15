package com.sun.jndi.cosnaming;

import org.omg.CORBA.ORB;

class OrbReuseTracker {
   int referenceCnt;
   ORB orb;
   private static final boolean debug = false;

   OrbReuseTracker(ORB var1) {
      this.orb = var1;
      ++this.referenceCnt;
   }

   synchronized void incRefCount() {
      ++this.referenceCnt;
   }

   synchronized void decRefCount() {
      --this.referenceCnt;
      if (this.referenceCnt == 0) {
         this.orb.destroy();
      }

   }
}
