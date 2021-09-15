package com.sun.corba.se.impl.oa.poa;

abstract class POAPolicyMediatorFactory {
   static POAPolicyMediator create(Policies var0, POAImpl var1) {
      if (var0.retainServants()) {
         if (var0.useActiveMapOnly()) {
            return new POAPolicyMediatorImpl_R_AOM(var0, var1);
         } else if (var0.useDefaultServant()) {
            return new POAPolicyMediatorImpl_R_UDS(var0, var1);
         } else if (var0.useServantManager()) {
            return new POAPolicyMediatorImpl_R_USM(var0, var1);
         } else {
            throw var1.invocationWrapper().pmfCreateRetain();
         }
      } else if (var0.useDefaultServant()) {
         return new POAPolicyMediatorImpl_NR_UDS(var0, var1);
      } else if (var0.useServantManager()) {
         return new POAPolicyMediatorImpl_NR_USM(var0, var1);
      } else {
         throw var1.invocationWrapper().pmfCreateNonRetain();
      }
   }
}
