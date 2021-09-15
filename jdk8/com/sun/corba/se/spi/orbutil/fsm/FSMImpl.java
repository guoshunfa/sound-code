package com.sun.corba.se.spi.orbutil.fsm;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.orbutil.fsm.StateEngineImpl;

public class FSMImpl implements FSM {
   private boolean debug;
   private State state;
   private StateEngineImpl stateEngine;

   public FSMImpl(StateEngine var1, State var2) {
      this(var1, var2, false);
   }

   public FSMImpl(StateEngine var1, State var2, boolean var3) {
      this.state = var2;
      this.stateEngine = (StateEngineImpl)var1;
      this.debug = var3;
   }

   public State getState() {
      return this.state;
   }

   public void doIt(Input var1) {
      this.stateEngine.doIt(this, var1, this.debug);
   }

   public void internalSetState(State var1) {
      if (this.debug) {
         ORBUtility.dprint((Object)this, "Calling internalSetState with nextState = " + var1);
      }

      this.state = var1;
      if (this.debug) {
         ORBUtility.dprint((Object)this, "Exiting internalSetState with state = " + this.state);
      }

   }
}
