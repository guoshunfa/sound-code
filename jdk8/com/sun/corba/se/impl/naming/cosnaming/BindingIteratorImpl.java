package com.sun.corba.se.impl.naming.cosnaming;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingHolder;
import org.omg.CosNaming.BindingIteratorPOA;
import org.omg.CosNaming.BindingListHolder;

public abstract class BindingIteratorImpl extends BindingIteratorPOA {
   protected ORB orb;

   public BindingIteratorImpl(ORB var1) throws Exception {
      this.orb = var1;
   }

   public synchronized boolean next_one(BindingHolder var1) {
      return this.NextOne(var1);
   }

   public synchronized boolean next_n(int var1, BindingListHolder var2) {
      if (var1 == 0) {
         throw new BAD_PARAM(" 'how_many' parameter is set to 0 which is invalid");
      } else {
         return this.list(var1, var2);
      }
   }

   public boolean list(int var1, BindingListHolder var2) {
      int var3 = Math.min(this.RemainingElements(), var1);
      Binding[] var4 = new Binding[var3];
      BindingHolder var5 = new BindingHolder();

      int var6;
      for(var6 = 0; var6 < var3 && this.NextOne(var5); ++var6) {
         var4[var6] = var5.value;
      }

      if (var6 == 0) {
         var2.value = new Binding[0];
         return false;
      } else {
         var2.value = var4;
         return true;
      }
   }

   public synchronized void destroy() {
      this.Destroy();
   }

   protected abstract boolean NextOne(BindingHolder var1);

   protected abstract void Destroy();

   protected abstract int RemainingElements();
}
