package com.sun.corba.se.spi.orbutil.fsm;

import com.sun.corba.se.impl.orbutil.fsm.GuardedAction;
import com.sun.corba.se.impl.orbutil.fsm.NameBase;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StateImpl extends NameBase implements State {
   private Action defaultAction = null;
   private State defaultNextState;
   private Map inputToGuardedActions = new HashMap();

   public StateImpl(String var1) {
      super(var1);
   }

   public void preAction(FSM var1) {
   }

   public void postAction(FSM var1) {
   }

   public State getDefaultNextState() {
      return this.defaultNextState;
   }

   public void setDefaultNextState(State var1) {
      this.defaultNextState = var1;
   }

   public Action getDefaultAction() {
      return this.defaultAction;
   }

   public void setDefaultAction(Action var1) {
      this.defaultAction = var1;
   }

   public void addGuardedAction(Input var1, GuardedAction var2) {
      Object var3 = (Set)this.inputToGuardedActions.get(var1);
      if (var3 == null) {
         var3 = new HashSet();
         this.inputToGuardedActions.put(var1, var3);
      }

      ((Set)var3).add(var2);
   }

   public Set getGuardedActions(Input var1) {
      return (Set)this.inputToGuardedActions.get(var1);
   }
}
