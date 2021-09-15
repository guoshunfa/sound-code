package com.sun.corba.se.spi.orbutil.fsm;

class NegateGuard implements Guard {
   Guard guard;

   public NegateGuard(Guard var1) {
      this.guard = var1;
   }

   public Guard.Result evaluate(FSM var1, Input var2) {
      return this.guard.evaluate(var1, var2).complement();
   }
}
