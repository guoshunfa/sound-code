package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.protocol.PIHandler;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.LocalObject;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAManagerPackage.State;

public class POAManagerImpl extends LocalObject implements POAManager {
   private final POAFactory factory;
   private PIHandler pihandler;
   private State state;
   private Set poas = new HashSet(4);
   private int nInvocations = 0;
   private int nWaiters = 0;
   private int myId = 0;
   private boolean debug;
   private boolean explicitStateChange;

   private String stateToString(State var1) {
      switch(var1.value()) {
      case 0:
         return "State[HOLDING]";
      case 1:
         return "State[ACTIVE]";
      case 2:
         return "State[DISCARDING]";
      case 3:
         return "State[INACTIVE]";
      default:
         return "State[UNKNOWN]";
      }
   }

   public String toString() {
      return "POAManagerImpl[myId=" + this.myId + " state=" + this.stateToString(this.state) + " nInvocations=" + this.nInvocations + " nWaiters=" + this.nWaiters + "]";
   }

   POAFactory getFactory() {
      return this.factory;
   }

   PIHandler getPIHandler() {
      return this.pihandler;
   }

   private void countedWait() {
      try {
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling countedWait on POAManager " + this + " nWaiters=" + this.nWaiters);
         }

         ++this.nWaiters;
         this.wait();
      } catch (InterruptedException var5) {
      } finally {
         --this.nWaiters;
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Exiting countedWait on POAManager " + this + " nWaiters=" + this.nWaiters);
         }

      }

   }

   private void notifyWaiters() {
      if (this.debug) {
         ORBUtility.dprint((Object)this, "Calling notifyWaiters on POAManager " + this + " nWaiters=" + this.nWaiters);
      }

      if (this.nWaiters > 0) {
         this.notifyAll();
      }

   }

   public int getManagerId() {
      return this.myId;
   }

   POAManagerImpl(POAFactory var1, PIHandler var2) {
      this.factory = var1;
      var1.addPoaManager(this);
      this.pihandler = var2;
      this.myId = var1.newPOAManagerId();
      this.state = State.HOLDING;
      this.debug = var1.getORB().poaDebugFlag;
      this.explicitStateChange = false;
      if (this.debug) {
         ORBUtility.dprint((Object)this, "Creating POAManagerImpl " + this);
      }

   }

   synchronized void addPOA(POA var1) {
      if (this.state.value() == 3) {
         POASystemException var2 = this.factory.getWrapper();
         throw var2.addPoaInactive(CompletionStatus.COMPLETED_NO);
      } else {
         this.poas.add(var1);
      }
   }

   synchronized void removePOA(POA var1) {
      this.poas.remove(var1);
      if (this.poas.isEmpty()) {
         this.factory.removePoaManager(this);
      }

   }

   public short getORTState() {
      switch(this.state.value()) {
      case 0:
         return 0;
      case 1:
         return 1;
      case 2:
         return 2;
      case 3:
         return 3;
      default:
         return 4;
      }
   }

   public synchronized void activate() throws AdapterInactive {
      this.explicitStateChange = true;
      if (this.debug) {
         ORBUtility.dprint((Object)this, "Calling activate on POAManager " + this);
      }

      try {
         if (this.state.value() == 3) {
            throw new AdapterInactive();
         }

         this.state = State.ACTIVE;
         this.pihandler.adapterManagerStateChanged(this.myId, this.getORTState());
         this.notifyWaiters();
      } finally {
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Exiting activate on POAManager " + this);
         }

      }

   }

   public synchronized void hold_requests(boolean var1) throws AdapterInactive {
      this.explicitStateChange = true;
      if (this.debug) {
         ORBUtility.dprint((Object)this, "Calling hold_requests on POAManager " + this);
      }

      try {
         if (this.state.value() == 3) {
            throw new AdapterInactive();
         }

         this.state = State.HOLDING;
         this.pihandler.adapterManagerStateChanged(this.myId, this.getORTState());
         this.notifyWaiters();
         if (var1) {
            while(this.state.value() == 0 && this.nInvocations > 0) {
               this.countedWait();
            }
         }
      } finally {
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Exiting hold_requests on POAManager " + this);
         }

      }

   }

   public synchronized void discard_requests(boolean var1) throws AdapterInactive {
      this.explicitStateChange = true;
      if (this.debug) {
         ORBUtility.dprint((Object)this, "Calling hold_requests on POAManager " + this);
      }

      try {
         if (this.state.value() == 3) {
            throw new AdapterInactive();
         }

         this.state = State.DISCARDING;
         this.pihandler.adapterManagerStateChanged(this.myId, this.getORTState());
         this.notifyWaiters();
         if (var1) {
            while(this.state.value() == 2 && this.nInvocations > 0) {
               this.countedWait();
            }
         }
      } finally {
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Exiting hold_requests on POAManager " + this);
         }

      }

   }

   public void deactivate(boolean var1, boolean var2) throws AdapterInactive {
      this.explicitStateChange = true;
      boolean var13 = false;

      try {
         var13 = true;
         synchronized(this) {
            if (this.debug) {
               ORBUtility.dprint((Object)this, "Calling deactivate on POAManager " + this);
            }

            if (this.state.value() == 3) {
               throw new AdapterInactive();
            }

            this.state = State.INACTIVE;
            this.pihandler.adapterManagerStateChanged(this.myId, this.getORTState());
            this.notifyWaiters();
         }

         POAManagerImpl.POAManagerDeactivator var3 = new POAManagerImpl.POAManagerDeactivator(this, var1, this.debug);
         if (var2) {
            var3.run();
            var13 = false;
         } else {
            Thread var4 = new Thread(var3);
            var4.start();
            var13 = false;
         }
      } finally {
         if (var13) {
            synchronized(this) {
               if (this.debug) {
                  ORBUtility.dprint((Object)this, "Exiting deactivate on POAManager " + this);
               }

            }
         }
      }

      synchronized(this) {
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Exiting deactivate on POAManager " + this);
         }

      }
   }

   public State get_state() {
      return this.state;
   }

   synchronized void checkIfActive() {
      try {
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling checkIfActive for POAManagerImpl " + this);
         }

         this.checkState();
      } finally {
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Exiting checkIfActive for POAManagerImpl " + this);
         }

      }

   }

   private void checkState() {
      while(this.state.value() != 1) {
         switch(this.state.value()) {
         case 0:
            while(this.state.value() == 0) {
               this.countedWait();
            }
         case 1:
         default:
            break;
         case 2:
            throw this.factory.getWrapper().poaDiscarding();
         case 3:
            throw this.factory.getWrapper().poaInactive();
         }
      }

   }

   synchronized void enter() {
      try {
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling enter for POAManagerImpl " + this);
         }

         this.checkState();
         ++this.nInvocations;
      } finally {
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Exiting enter for POAManagerImpl " + this);
         }

      }

   }

   synchronized void exit() {
      try {
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling exit for POAManagerImpl " + this);
         }

         --this.nInvocations;
         if (this.nInvocations == 0) {
            this.notifyWaiters();
         }
      } finally {
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Exiting exit for POAManagerImpl " + this);
         }

      }

   }

   public synchronized void implicitActivation() {
      if (!this.explicitStateChange) {
         try {
            this.activate();
         } catch (AdapterInactive var2) {
         }
      }

   }

   private class POAManagerDeactivator implements Runnable {
      private boolean etherealize_objects;
      private POAManagerImpl pmi;
      private boolean debug;

      POAManagerDeactivator(POAManagerImpl var2, boolean var3, boolean var4) {
         this.etherealize_objects = var3;
         this.pmi = var2;
         this.debug = var4;
      }

      public void run() {
         boolean var15 = false;

         try {
            var15 = true;
            synchronized(this.pmi) {
               if (this.debug) {
                  ORBUtility.dprint((Object)this, "Calling run with etherealize_objects=" + this.etherealize_objects + " pmi=" + this.pmi);
               }

               while(this.pmi.nInvocations > 0) {
                  POAManagerImpl.this.countedWait();
               }
            }

            if (this.etherealize_objects) {
               Iterator var1 = null;
               synchronized(this.pmi) {
                  if (this.debug) {
                     ORBUtility.dprint((Object)this, "run: Preparing to etherealize with pmi=" + this.pmi);
                  }

                  var1 = (new HashSet(this.pmi.poas)).iterator();
               }

               while(var1.hasNext()) {
                  ((POAImpl)var1.next()).etherealizeAll();
               }

               synchronized(this.pmi) {
                  if (this.debug) {
                     ORBUtility.dprint((Object)this, "run: removing POAManager and clearing poas with pmi=" + this.pmi);
                  }

                  POAManagerImpl.this.factory.removePoaManager(this.pmi);
                  POAManagerImpl.this.poas.clear();
                  var15 = false;
               }
            } else {
               var15 = false;
            }
         } finally {
            if (var15) {
               if (this.debug) {
                  synchronized(this.pmi) {
                     ORBUtility.dprint((Object)this, "Exiting run");
                  }
               }

            }
         }

         if (this.debug) {
            synchronized(this.pmi) {
               ORBUtility.dprint((Object)this, "Exiting run");
            }
         }

      }
   }
}
