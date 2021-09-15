package com.sun.corba.se.spi.orbutil.fsm;

public interface Guard {
   Guard.Result evaluate(FSM var1, Input var2);

   public static final class Result {
      private String name;
      public static final Guard.Result ENABLED = new Guard.Result("ENABLED");
      public static final Guard.Result DISABLED = new Guard.Result("DISABLED");
      public static final Guard.Result DEFERED = new Guard.Result("DEFERED");

      private Result(String var1) {
         this.name = var1;
      }

      public static Guard.Result convert(boolean var0) {
         return var0 ? ENABLED : DISABLED;
      }

      public Guard.Result complement() {
         if (this == ENABLED) {
            return DISABLED;
         } else {
            return this == DISABLED ? ENABLED : DEFERED;
         }
      }

      public String toString() {
         return "Guard.Result[" + this.name + "]";
      }
   }

   public static final class Complement extends GuardBase {
      private Guard guard;

      public Complement(GuardBase var1) {
         super("not(" + var1.getName() + ")");
         this.guard = var1;
      }

      public Guard.Result evaluate(FSM var1, Input var2) {
         return this.guard.evaluate(var1, var2).complement();
      }
   }
}
