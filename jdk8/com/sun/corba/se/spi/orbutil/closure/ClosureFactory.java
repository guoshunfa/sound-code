package com.sun.corba.se.spi.orbutil.closure;

import com.sun.corba.se.impl.orbutil.closure.Constant;
import com.sun.corba.se.impl.orbutil.closure.Future;

public abstract class ClosureFactory {
   private ClosureFactory() {
   }

   public static Closure makeConstant(Object var0) {
      return new Constant(var0);
   }

   public static Closure makeFuture(Closure var0) {
      return new Future(var0);
   }
}
