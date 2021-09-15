package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.orbutil.concurrent.CondVar;
import com.sun.corba.se.spi.orbutil.fsm.Action;
import com.sun.corba.se.spi.orbutil.fsm.ActionBase;
import com.sun.corba.se.spi.orbutil.fsm.FSM;
import com.sun.corba.se.spi.orbutil.fsm.FSMImpl;
import com.sun.corba.se.spi.orbutil.fsm.Guard;
import com.sun.corba.se.spi.orbutil.fsm.GuardBase;
import com.sun.corba.se.spi.orbutil.fsm.Input;
import com.sun.corba.se.spi.orbutil.fsm.InputImpl;
import com.sun.corba.se.spi.orbutil.fsm.State;
import com.sun.corba.se.spi.orbutil.fsm.StateEngine;
import com.sun.corba.se.spi.orbutil.fsm.StateEngineFactory;
import com.sun.corba.se.spi.orbutil.fsm.StateImpl;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;

public class AOMEntry extends FSMImpl {
   private final Thread[] etherealizer;
   private final int[] counter;
   private final CondVar wait;
   final POAImpl poa;
   public static final State INVALID = new StateImpl("Invalid");
   public static final State INCARN = new StateImpl("Incarnating") {
      public void postAction(FSM var1) {
         AOMEntry var2 = (AOMEntry)var1;
         var2.wait.broadcast();
      }
   };
   public static final State VALID = new StateImpl("Valid");
   public static final State ETHP = new StateImpl("EtherealizePending");
   public static final State ETH = new StateImpl("Etherealizing") {
      public void preAction(FSM var1) {
         AOMEntry var2 = (AOMEntry)var1;
         Thread var3 = var2.etherealizer[0];
         if (var3 != null) {
            var3.start();
         }

      }

      public void postAction(FSM var1) {
         AOMEntry var2 = (AOMEntry)var1;
         var2.wait.broadcast();
      }
   };
   public static final State DESTROYED = new StateImpl("Destroyed");
   static final Input START_ETH = new InputImpl("startEtherealize");
   static final Input ETH_DONE = new InputImpl("etherealizeDone");
   static final Input INC_DONE = new InputImpl("incarnateDone");
   static final Input INC_FAIL = new InputImpl("incarnateFailure");
   static final Input ACTIVATE = new InputImpl("activateObject");
   static final Input ENTER = new InputImpl("enter");
   static final Input EXIT = new InputImpl("exit");
   private static Action incrementAction = new ActionBase("increment") {
      public void doIt(FSM var1, Input var2) {
         AOMEntry var3 = (AOMEntry)var1;
         int var10002 = var3.counter[0]++;
      }
   };
   private static Action decrementAction = new ActionBase("decrement") {
      public void doIt(FSM var1, Input var2) {
         AOMEntry var3 = (AOMEntry)var1;
         if (var3.counter[0] > 0) {
            int var10002 = var3.counter[0]--;
         } else {
            throw var3.poa.lifecycleWrapper().aomEntryDecZero();
         }
      }
   };
   private static Action throwIllegalStateExceptionAction = new ActionBase("throwIllegalStateException") {
      public void doIt(FSM var1, Input var2) {
         throw new IllegalStateException("No transitions allowed from the DESTROYED state");
      }
   };
   private static Action oaaAction = new ActionBase("throwObjectAlreadyActive") {
      public void doIt(FSM var1, Input var2) {
         throw new RuntimeException(new ObjectAlreadyActive());
      }
   };
   private static Guard waitGuard = new GuardBase("wait") {
      public Guard.Result evaluate(FSM var1, Input var2) {
         AOMEntry var3 = (AOMEntry)var1;

         try {
            var3.wait.await();
         } catch (InterruptedException var5) {
         }

         return Guard.Result.DEFERED;
      }
   };
   private static GuardBase greaterZeroGuard = new AOMEntry.CounterGuard(0);
   private static Guard zeroGuard;
   private static GuardBase greaterOneGuard;
   private static Guard oneGuard;
   private static StateEngine engine;

   public AOMEntry(POAImpl var1) {
      super(engine, INVALID, var1.getORB().poaFSMDebugFlag);
      this.poa = var1;
      this.etherealizer = new Thread[1];
      this.etherealizer[0] = null;
      this.counter = new int[1];
      this.counter[0] = 0;
      this.wait = new CondVar(var1.poaMutex, var1.getORB().poaConcurrencyDebugFlag);
   }

   public void startEtherealize(Thread var1) {
      this.etherealizer[0] = var1;
      this.doIt(START_ETH);
   }

   public void etherealizeComplete() {
      this.doIt(ETH_DONE);
   }

   public void incarnateComplete() {
      this.doIt(INC_DONE);
   }

   public void incarnateFailure() {
      this.doIt(INC_FAIL);
   }

   public void activateObject() throws ObjectAlreadyActive {
      try {
         this.doIt(ACTIVATE);
      } catch (RuntimeException var3) {
         Throwable var2 = var3.getCause();
         if (var2 instanceof ObjectAlreadyActive) {
            throw (ObjectAlreadyActive)var2;
         } else {
            throw var3;
         }
      }
   }

   public void enter() {
      this.doIt(ENTER);
   }

   public void exit() {
      this.doIt(EXIT);
   }

   static {
      zeroGuard = new Guard.Complement(greaterZeroGuard);
      greaterOneGuard = new AOMEntry.CounterGuard(1);
      oneGuard = new Guard.Complement(greaterOneGuard);
      engine = StateEngineFactory.create();
      engine.add(INVALID, ENTER, incrementAction, INCARN);
      engine.add(INVALID, ACTIVATE, (Action)null, VALID);
      engine.setDefault(INVALID);
      engine.add(INCARN, ENTER, waitGuard, (Action)null, INCARN);
      engine.add(INCARN, EXIT, (Action)null, INCARN);
      engine.add(INCARN, START_ETH, waitGuard, (Action)null, INCARN);
      engine.add(INCARN, INC_DONE, (Action)null, VALID);
      engine.add(INCARN, INC_FAIL, decrementAction, INVALID);
      engine.add(INCARN, ACTIVATE, oaaAction, INCARN);
      engine.add(VALID, ENTER, incrementAction, VALID);
      engine.add(VALID, EXIT, decrementAction, VALID);
      engine.add(VALID, START_ETH, greaterZeroGuard, (Action)null, ETHP);
      engine.add(VALID, START_ETH, zeroGuard, (Action)null, ETH);
      engine.add(VALID, ACTIVATE, oaaAction, VALID);
      engine.add(ETHP, ENTER, waitGuard, (Action)null, ETHP);
      engine.add(ETHP, START_ETH, (Action)null, ETHP);
      engine.add(ETHP, EXIT, greaterOneGuard, decrementAction, ETHP);
      engine.add(ETHP, EXIT, oneGuard, decrementAction, ETH);
      engine.add(ETHP, ACTIVATE, oaaAction, ETHP);
      engine.add(ETH, START_ETH, (Action)null, ETH);
      engine.add(ETH, ETH_DONE, (Action)null, DESTROYED);
      engine.add(ETH, ACTIVATE, oaaAction, ETH);
      engine.add(ETH, ENTER, waitGuard, (Action)null, ETH);
      engine.setDefault(DESTROYED, throwIllegalStateExceptionAction, DESTROYED);
      engine.done();
   }

   private static class CounterGuard extends GuardBase {
      private int value;

      public CounterGuard(int var1) {
         super("counter>" + var1);
         this.value = var1;
      }

      public Guard.Result evaluate(FSM var1, Input var2) {
         AOMEntry var3 = (AOMEntry)var1;
         return Guard.Result.convert(var3.counter[0] > this.value);
      }
   }
}
