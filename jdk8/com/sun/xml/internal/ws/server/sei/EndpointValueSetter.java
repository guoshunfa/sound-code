package com.sun.xml.internal.ws.server.sei;

import com.sun.xml.internal.ws.model.ParameterImpl;
import javax.xml.ws.Holder;

public abstract class EndpointValueSetter {
   private static final EndpointValueSetter[] POOL = new EndpointValueSetter[16];

   private EndpointValueSetter() {
   }

   abstract void put(Object var1, Object[] var2);

   public static EndpointValueSetter get(ParameterImpl p) {
      int idx = p.getIndex();
      if (p.isIN()) {
         return (EndpointValueSetter)(idx < POOL.length ? POOL[idx] : new EndpointValueSetter.Param(idx));
      } else {
         return new EndpointValueSetter.HolderParam(idx);
      }
   }

   // $FF: synthetic method
   EndpointValueSetter(Object x0) {
      this();
   }

   static {
      for(int i = 0; i < POOL.length; ++i) {
         POOL[i] = new EndpointValueSetter.Param(i);
      }

   }

   static final class HolderParam extends EndpointValueSetter.Param {
      public HolderParam(int idx) {
         super(idx);
      }

      void put(Object obj, Object[] args) {
         Holder holder = new Holder();
         if (obj != null) {
            holder.value = obj;
         }

         args[this.idx] = holder;
      }
   }

   static class Param extends EndpointValueSetter {
      protected final int idx;

      public Param(int idx) {
         super(null);
         this.idx = idx;
      }

      void put(Object obj, Object[] args) {
         if (obj != null) {
            args[this.idx] = obj;
         }

      }
   }
}
