package com.sun.corba.se.impl.orbutil.fsm;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.orbutil.fsm.Action;
import com.sun.corba.se.spi.orbutil.fsm.ActionBase;
import com.sun.corba.se.spi.orbutil.fsm.FSM;
import com.sun.corba.se.spi.orbutil.fsm.FSMImpl;
import com.sun.corba.se.spi.orbutil.fsm.Guard;
import com.sun.corba.se.spi.orbutil.fsm.Input;
import com.sun.corba.se.spi.orbutil.fsm.State;
import com.sun.corba.se.spi.orbutil.fsm.StateEngine;
import com.sun.corba.se.spi.orbutil.fsm.StateImpl;
import java.util.Iterator;
import java.util.Set;
import org.omg.CORBA.INTERNAL;

public class StateEngineImpl implements StateEngine {
   private static Action emptyAction = new ActionBase("Empty") {
      public void doIt(FSM var1, Input var2) {
      }
   };
   private boolean initializing = true;
   private Action defaultAction = new ActionBase("Invalid Transition") {
      public void doIt(FSM var1, Input var2) {
         throw new INTERNAL("Invalid transition attempted from " + var1.getState() + " under " + var2);
      }
   };

   public StateEngine add(State var1, Input var2, Guard var3, Action var4, State var5) throws IllegalArgumentException, IllegalStateException {
      this.mustBeInitializing();
      StateImpl var6 = (StateImpl)var1;
      GuardedAction var7 = new GuardedAction(var3, var4, var5);
      var6.addGuardedAction(var2, var7);
      return this;
   }

   public StateEngine add(State var1, Input var2, Action var3, State var4) throws IllegalArgumentException, IllegalStateException {
      this.mustBeInitializing();
      StateImpl var5 = (StateImpl)var1;
      GuardedAction var6 = new GuardedAction(var3, var4);
      var5.addGuardedAction(var2, var6);
      return this;
   }

   public StateEngine setDefault(State var1, Action var2, State var3) throws IllegalArgumentException, IllegalStateException {
      this.mustBeInitializing();
      StateImpl var4 = (StateImpl)var1;
      var4.setDefaultAction(var2);
      var4.setDefaultNextState(var3);
      return this;
   }

   public StateEngine setDefault(State var1, State var2) throws IllegalArgumentException, IllegalStateException {
      return this.setDefault(var1, emptyAction, var2);
   }

   public StateEngine setDefault(State var1) throws IllegalArgumentException, IllegalStateException {
      return this.setDefault(var1, var1);
   }

   public void done() throws IllegalStateException {
      this.mustBeInitializing();
      this.initializing = false;
   }

   public void setDefaultAction(Action var1) throws IllegalStateException {
      this.mustBeInitializing();
      this.defaultAction = var1;
   }

   public void doIt(FSM var1, Input var2, boolean var3) {
      if (var3) {
         ORBUtility.dprint((Object)this, "doIt enter: currentState = " + var1.getState() + " in = " + var2);
      }

      try {
         this.innerDoIt(var1, var2, var3);
      } finally {
         if (var3) {
            ORBUtility.dprint((Object)this, "doIt exit");
         }

      }

   }

   private StateImpl getDefaultNextState(StateImpl var1) {
      StateImpl var2 = (StateImpl)var1.getDefaultNextState();
      if (var2 == null) {
         var2 = var1;
      }

      return var2;
   }

   private Action getDefaultAction(StateImpl var1) {
      Action var2 = var1.getDefaultAction();
      if (var2 == null) {
         var2 = this.defaultAction;
      }

      return var2;
   }

   private void innerDoIt(FSM var1, Input var2, boolean var3) {
      if (var3) {
         ORBUtility.dprint((Object)this, "Calling innerDoIt with input " + var2);
      }

      StateImpl var4 = null;
      StateImpl var5 = null;
      Action var6 = null;
      boolean var7 = false;

      do {
         var7 = false;
         var4 = (StateImpl)var1.getState();
         var5 = this.getDefaultNextState(var4);
         var6 = this.getDefaultAction(var4);
         if (var3) {
            ORBUtility.dprint((Object)this, "currentState      = " + var4);
            ORBUtility.dprint((Object)this, "in                = " + var2);
            ORBUtility.dprint((Object)this, "default nextState = " + var5);
            ORBUtility.dprint((Object)this, "default action    = " + var6);
         }

         Set var8 = var4.getGuardedActions(var2);
         if (var8 != null) {
            Iterator var9 = var8.iterator();

            while(var9.hasNext()) {
               GuardedAction var10 = (GuardedAction)var9.next();
               Guard.Result var11 = var10.getGuard().evaluate(var1, var2);
               if (var3) {
                  ORBUtility.dprint((Object)this, "doIt: evaluated " + var10 + " with result " + var11);
               }

               if (var11 == Guard.Result.ENABLED) {
                  var5 = (StateImpl)var10.getNextState();
                  var6 = var10.getAction();
                  if (var3) {
                     ORBUtility.dprint((Object)this, "nextState = " + var5);
                     ORBUtility.dprint((Object)this, "action    = " + var6);
                  }
                  break;
               }

               if (var11 == Guard.Result.DEFERED) {
                  var7 = true;
                  break;
               }
            }
         }
      } while(var7);

      this.performStateTransition(var1, var2, var5, var6, var3);
   }

   private void performStateTransition(FSM var1, Input var2, StateImpl var3, Action var4, boolean var5) {
      StateImpl var6 = (StateImpl)var1.getState();
      boolean var7 = !var6.equals(var3);
      if (var7) {
         if (var5) {
            ORBUtility.dprint((Object)this, "doIt: executing postAction for state " + var6);
         }

         try {
            var6.postAction(var1);
         } catch (Throwable var17) {
            if (var5) {
               ORBUtility.dprint((Object)this, "doIt: postAction threw " + var17);
            }

            if (var17 instanceof ThreadDeath) {
               throw (ThreadDeath)var17;
            }
         }
      }

      try {
         if (var4 != null) {
            var4.doIt(var1, var2);
         }
      } finally {
         if (var7) {
            if (var5) {
               ORBUtility.dprint((Object)this, "doIt: executing preAction for state " + var3);
            }

            try {
               var3.preAction(var1);
            } catch (Throwable var15) {
               if (var5) {
                  ORBUtility.dprint((Object)this, "doIt: preAction threw " + var15);
               }

               if (var15 instanceof ThreadDeath) {
                  throw (ThreadDeath)var15;
               }
            }

            ((FSMImpl)var1).internalSetState(var3);
         }

         if (var5) {
            ORBUtility.dprint((Object)this, "doIt: state is now " + var3);
         }

      }

   }

   public FSM makeFSM(State var1) throws IllegalStateException {
      this.mustNotBeInitializing();
      return new FSMImpl(this, var1);
   }

   private void mustBeInitializing() throws IllegalStateException {
      if (!this.initializing) {
         throw new IllegalStateException("Invalid method call after initialization completed");
      }
   }

   private void mustNotBeInitializing() throws IllegalStateException {
      if (this.initializing) {
         throw new IllegalStateException("Invalid method call before initialization completed");
      }
   }
}
