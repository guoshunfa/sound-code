package com.sun.corba.se.impl.orbutil.closure;

import com.sun.corba.se.spi.orbutil.closure.Closure;

public class Future implements Closure {
   private boolean evaluated = false;
   private Closure closure;
   private Object value;

   public Future(Closure var1) {
      this.closure = var1;
      this.value = null;
   }

   public synchronized Object evaluate() {
      if (!this.evaluated) {
         this.evaluated = true;
         this.value = this.closure.evaluate();
      }

      return this.value;
   }
}
