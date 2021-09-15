package com.sun.corba.se.impl.orbutil.fsm;

import com.sun.corba.se.spi.orbutil.fsm.Action;
import com.sun.corba.se.spi.orbutil.fsm.FSM;
import com.sun.corba.se.spi.orbutil.fsm.Guard;
import com.sun.corba.se.spi.orbutil.fsm.GuardBase;
import com.sun.corba.se.spi.orbutil.fsm.Input;
import com.sun.corba.se.spi.orbutil.fsm.State;

public class GuardedAction {
   private static Guard trueGuard = new GuardBase("true") {
      public Guard.Result evaluate(FSM var1, Input var2) {
         return Guard.Result.ENABLED;
      }
   };
   private Guard guard;
   private Action action;
   private State nextState;

   public GuardedAction(Action var1, State var2) {
      this.guard = trueGuard;
      this.action = var1;
      this.nextState = var2;
   }

   public GuardedAction(Guard var1, Action var2, State var3) {
      this.guard = var1;
      this.action = var2;
      this.nextState = var3;
   }

   public String toString() {
      return "GuardedAction[action=" + this.action + " guard=" + this.guard + " nextState=" + this.nextState + "]";
   }

   public Action getAction() {
      return this.action;
   }

   public Guard getGuard() {
      return this.guard;
   }

   public State getNextState() {
      return this.nextState;
   }
}
