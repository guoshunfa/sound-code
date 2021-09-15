package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.ior.ObjectAdapterIdArray;
import com.sun.corba.se.impl.ior.POAObjectKeyTemplate;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.orbutil.concurrent.CondVar;
import com.sun.corba.se.impl.orbutil.concurrent.ReentrantMutex;
import com.sun.corba.se.impl.orbutil.concurrent.Sync;
import com.sun.corba.se.impl.orbutil.concurrent.SyncUtil;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORTemplateList;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.TaggedProfile;
import com.sun.corba.se.spi.oa.OADestroyed;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapterBase;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.ForwardException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SystemException;
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import org.omg.PortableServer.AdapterActivator;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.IdAssignmentPolicy;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.IdUniquenessPolicy;
import org.omg.PortableServer.IdUniquenessPolicyValue;
import org.omg.PortableServer.ImplicitActivationPolicy;
import org.omg.PortableServer.ImplicitActivationPolicyValue;
import org.omg.PortableServer.LifespanPolicy;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.RequestProcessingPolicy;
import org.omg.PortableServer.RequestProcessingPolicyValue;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantManager;
import org.omg.PortableServer.ServantRetentionPolicy;
import org.omg.PortableServer.ServantRetentionPolicyValue;
import org.omg.PortableServer.ThreadPolicy;
import org.omg.PortableServer.ThreadPolicyValue;
import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;
import org.omg.PortableServer.POAPackage.AdapterNonExistent;
import org.omg.PortableServer.POAPackage.InvalidPolicy;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongAdapter;
import org.omg.PortableServer.POAPackage.WrongPolicy;

public class POAImpl extends ObjectAdapterBase implements POA {
   private boolean debug;
   private static final int STATE_START = 0;
   private static final int STATE_INIT = 1;
   private static final int STATE_INIT_DONE = 2;
   private static final int STATE_RUN = 3;
   private static final int STATE_DESTROYING = 4;
   private static final int STATE_DESTROYED = 5;
   private int state;
   private POAPolicyMediator mediator;
   private int numLevels;
   private ObjectAdapterId poaId;
   private String name;
   private POAManagerImpl manager;
   private int uniquePOAId;
   private POAImpl parent;
   private Map children;
   private AdapterActivator activator;
   private int invocationCount;
   Sync poaMutex;
   private CondVar adapterActivatorCV;
   private CondVar invokeCV;
   private CondVar beingDestroyedCV;
   protected ThreadLocal isDestroying;

   private String stateToString() {
      switch(this.state) {
      case 0:
         return "START";
      case 1:
         return "INIT";
      case 2:
         return "INIT_DONE";
      case 3:
         return "RUN";
      case 4:
         return "DESTROYING";
      case 5:
         return "DESTROYED";
      default:
         return "UNKNOWN(" + this.state + ")";
      }
   }

   public String toString() {
      return "POA[" + this.poaId.toString() + ", uniquePOAId=" + this.uniquePOAId + ", state=" + this.stateToString() + ", invocationCount=" + this.invocationCount + "]";
   }

   boolean getDebug() {
      return this.debug;
   }

   static POAFactory getPOAFactory(ORB var0) {
      return (POAFactory)var0.getRequestDispatcherRegistry().getObjectAdapterFactory(32);
   }

   static POAImpl makeRootPOA(ORB var0) {
      POAManagerImpl var1 = new POAManagerImpl(getPOAFactory(var0), var0.getPIHandler());
      POAImpl var2 = new POAImpl("RootPOA", (POAImpl)null, var0, 0);
      var2.initialize(var1, Policies.rootPOAPolicies);
      return var2;
   }

   int getPOAId() {
      return this.uniquePOAId;
   }

   void lock() {
      SyncUtil.acquire(this.poaMutex);
      if (this.debug) {
         ORBUtility.dprint((Object)this, "LOCKED poa " + this);
      }

   }

   void unlock() {
      if (this.debug) {
         ORBUtility.dprint((Object)this, "UNLOCKED poa " + this);
      }

      this.poaMutex.release();
   }

   Policies getPolicies() {
      return this.mediator.getPolicies();
   }

   private POAImpl(String var1, POAImpl var2, ORB var3, int var4) {
      super(var3);
      this.debug = var3.poaDebugFlag;
      if (this.debug) {
         ORBUtility.dprint((Object)this, "Creating POA with name=" + var1 + " parent=" + var2);
      }

      this.state = var4;
      this.name = var1;
      this.parent = var2;
      this.children = new HashMap();
      this.activator = null;
      this.uniquePOAId = getPOAFactory(var3).newPOAId();
      if (var2 == null) {
         this.numLevels = 1;
      } else {
         this.numLevels = var2.numLevels + 1;
         var2.children.put(var1, this);
      }

      String[] var5 = new String[this.numLevels];
      POAImpl var6 = this;

      for(int var7 = this.numLevels - 1; var6 != null; var6 = var6.parent) {
         var5[var7--] = var6.name;
      }

      this.poaId = new ObjectAdapterIdArray(var5);
      this.invocationCount = 0;
      this.poaMutex = new ReentrantMutex(var3.poaConcurrencyDebugFlag);
      this.adapterActivatorCV = new CondVar(this.poaMutex, var3.poaConcurrencyDebugFlag);
      this.invokeCV = new CondVar(this.poaMutex, var3.poaConcurrencyDebugFlag);
      this.beingDestroyedCV = new CondVar(this.poaMutex, var3.poaConcurrencyDebugFlag);
      this.isDestroying = new ThreadLocal() {
         protected Object initialValue() {
            return Boolean.FALSE;
         }
      };
   }

   private void initialize(POAManagerImpl var1, Policies var2) {
      if (this.debug) {
         ORBUtility.dprint((Object)this, "Initializing poa " + this + " with POAManager=" + var1 + " policies=" + var2);
      }

      this.manager = var1;
      var1.addPOA(this);
      this.mediator = POAPolicyMediatorFactory.create(var2, this);
      int var3 = this.mediator.getServerId();
      int var4 = this.mediator.getScid();
      String var5 = this.getORB().getORBData().getORBId();
      POAObjectKeyTemplate var6 = new POAObjectKeyTemplate(this.getORB(), var4, var3, var5, this.poaId);
      if (this.debug) {
         ORBUtility.dprint((Object)this, "Initializing poa: oktemp=" + var6);
      }

      boolean var7 = true;
      this.initializeTemplate(var6, var7, var2, (String)null, (String)null, var6.getObjectAdapterId());
      if (this.state == 0) {
         this.state = 3;
      } else {
         if (this.state != 1) {
            throw this.lifecycleWrapper().illegalPoaStateTrans();
         }

         this.state = 2;
      }

   }

   private boolean waitUntilRunning() {
      if (this.debug) {
         ORBUtility.dprint((Object)this, "Calling waitUntilRunning on poa " + this);
      }

      while(this.state < 3) {
         try {
            this.adapterActivatorCV.await();
         } catch (InterruptedException var2) {
         }
      }

      if (this.debug) {
         ORBUtility.dprint((Object)this, "Exiting waitUntilRunning on poa " + this);
      }

      return this.state == 3;
   }

   private boolean destroyIfNotInitDone() {
      boolean var6;
      try {
         this.lock();
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling destroyIfNotInitDone on poa " + this);
         }

         boolean var1 = this.state == 2;
         if (var1) {
            this.state = 3;
         } else {
            POAImpl.DestroyThread var2 = new POAImpl.DestroyThread(false, this.debug);
            var2.doIt(this, true);
         }

         var6 = var1;
      } finally {
         this.adapterActivatorCV.broadcast();
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Exiting destroyIfNotInitDone on poa " + this);
         }

         this.unlock();
      }

      return var6;
   }

   private byte[] internalReferenceToId(org.omg.CORBA.Object var1) throws WrongAdapter {
      IOR var2 = ORBUtility.getIOR(var1);
      IORTemplateList var3 = var2.getIORTemplates();
      ObjectReferenceFactory var4 = this.getCurrentFactory();
      IORTemplateList var5 = IORFactories.getIORTemplateList(var4);
      if (!var5.isEquivalent(var3)) {
         throw new WrongAdapter();
      } else {
         Iterator var6 = var2.iterator();
         if (!var6.hasNext()) {
            throw this.iorWrapper().noProfilesInIor();
         } else {
            TaggedProfile var7 = (TaggedProfile)((TaggedProfile)var6.next());
            ObjectId var8 = var7.getObjectId();
            return var8.getId();
         }
      }
   }

   void etherealizeAll() {
      try {
         this.lock();
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling etheralizeAll on poa " + this);
         }

         this.mediator.etherealizeAll();
      } finally {
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Exiting etheralizeAll on poa " + this);
         }

         this.unlock();
      }

   }

   public POA create_POA(String var1, POAManager var2, Policy[] var3) throws AdapterAlreadyExists, InvalidPolicy {
      POAImpl var8;
      try {
         this.lock();
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling create_POA(name=" + var1 + " theManager=" + var2 + " policies=" + var3 + ") on poa " + this);
         }

         if (this.state > 3) {
            throw this.omgLifecycleWrapper().createPoaDestroy();
         }

         POAImpl var4 = (POAImpl)((POAImpl)this.children.get(var1));
         if (var4 == null) {
            var4 = new POAImpl(var1, this, this.getORB(), 0);
         }

         try {
            var4.lock();
            if (this.debug) {
               ORBUtility.dprint((Object)this, "Calling create_POA: new poa is " + var4);
            }

            if (var4.state != 0 && var4.state != 1) {
               throw new AdapterAlreadyExists();
            }

            POAManagerImpl var5 = (POAManagerImpl)var2;
            if (var5 == null) {
               var5 = new POAManagerImpl(this.manager.getFactory(), this.manager.getPIHandler());
            }

            int var6 = this.getORB().getCopierManager().getDefaultId();
            Policies var7 = new Policies(var3, var6);
            var4.initialize(var5, var7);
            var8 = var4;
         } finally {
            var4.unlock();
         }
      } finally {
         this.unlock();
      }

      return var8;
   }

   public POA find_POA(String var1, boolean var2) throws AdapterNonExistent {
      POAImpl var3 = null;
      AdapterActivator var4 = null;
      this.lock();
      if (this.debug) {
         ORBUtility.dprint((Object)this, "Calling find_POA(name=" + var1 + " activate=" + var2 + ") on poa " + this);
      }

      var3 = (POAImpl)this.children.get(var1);
      if (var3 != null) {
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling find_POA: found poa " + var3);
         }

         try {
            var3.lock();
            this.unlock();
            if (!var3.waitUntilRunning()) {
               throw this.omgLifecycleWrapper().poaDestroyed();
            }
         } finally {
            var3.unlock();
         }
      } else {
         try {
            if (this.debug) {
               ORBUtility.dprint((Object)this, "Calling find_POA: no poa found");
            }

            if (!var2 || this.activator == null) {
               throw new AdapterNonExistent();
            }

            var3 = new POAImpl(var1, this, this.getORB(), 1);
            if (this.debug) {
               ORBUtility.dprint((Object)this, "Calling find_POA: created poa " + var3);
            }

            var4 = this.activator;
         } finally {
            this.unlock();
         }
      }

      if (var4 != null) {
         boolean var5 = false;
         boolean var6 = false;
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling find_POA: calling AdapterActivator");
         }

         try {
            synchronized(var4) {
               var5 = var4.unknown_adapter(this, var1);
            }
         } catch (SystemException var30) {
            throw this.omgLifecycleWrapper().adapterActivatorException((Throwable)var30, var1, this.poaId.toString());
         } catch (Throwable var31) {
            this.lifecycleWrapper().unexpectedException((Throwable)var31, this.toString());
            if (var31 instanceof ThreadDeath) {
               throw (ThreadDeath)var31;
            }
         } finally {
            var6 = var3.destroyIfNotInitDone();
         }

         if (!var5) {
            if (this.debug) {
               ORBUtility.dprint((Object)this, "Calling find_POA: AdapterActivator returned false");
            }

            throw new AdapterNonExistent();
         }

         if (!var6) {
            throw this.omgLifecycleWrapper().adapterActivatorException(var1, this.poaId.toString());
         }
      }

      return var3;
   }

   public void destroy(boolean var1, boolean var2) {
      if (var2 && this.getORB().isDuringDispatch()) {
         throw this.lifecycleWrapper().destroyDeadlock();
      } else {
         POAImpl.DestroyThread var3 = new POAImpl.DestroyThread(var1, this.debug);
         var3.doIt(this, var2);
      }
   }

   public ThreadPolicy create_thread_policy(ThreadPolicyValue var1) {
      return new ThreadPolicyImpl(var1);
   }

   public LifespanPolicy create_lifespan_policy(LifespanPolicyValue var1) {
      return new LifespanPolicyImpl(var1);
   }

   public IdUniquenessPolicy create_id_uniqueness_policy(IdUniquenessPolicyValue var1) {
      return new IdUniquenessPolicyImpl(var1);
   }

   public IdAssignmentPolicy create_id_assignment_policy(IdAssignmentPolicyValue var1) {
      return new IdAssignmentPolicyImpl(var1);
   }

   public ImplicitActivationPolicy create_implicit_activation_policy(ImplicitActivationPolicyValue var1) {
      return new ImplicitActivationPolicyImpl(var1);
   }

   public ServantRetentionPolicy create_servant_retention_policy(ServantRetentionPolicyValue var1) {
      return new ServantRetentionPolicyImpl(var1);
   }

   public RequestProcessingPolicy create_request_processing_policy(RequestProcessingPolicyValue var1) {
      return new RequestProcessingPolicyImpl(var1);
   }

   public String the_name() {
      String var1;
      try {
         this.lock();
         var1 = this.name;
      } finally {
         this.unlock();
      }

      return var1;
   }

   public POA the_parent() {
      POAImpl var1;
      try {
         this.lock();
         var1 = this.parent;
      } finally {
         this.unlock();
      }

      return var1;
   }

   public POA[] the_children() {
      try {
         this.lock();
         Collection var1 = this.children.values();
         int var2 = var1.size();
         POA[] var3 = new POA[var2];
         int var4 = 0;

         POA var6;
         for(Iterator var5 = var1.iterator(); var5.hasNext(); var3[var4++] = var6) {
            var6 = (POA)((POA)var5.next());
         }

         POA[] var10 = var3;
         return var10;
      } finally {
         this.unlock();
      }
   }

   public POAManager the_POAManager() {
      POAManagerImpl var1;
      try {
         this.lock();
         var1 = this.manager;
      } finally {
         this.unlock();
      }

      return var1;
   }

   public AdapterActivator the_activator() {
      AdapterActivator var1;
      try {
         this.lock();
         var1 = this.activator;
      } finally {
         this.unlock();
      }

      return var1;
   }

   public void the_activator(AdapterActivator var1) {
      try {
         this.lock();
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling the_activator on poa " + this + " activator=" + var1);
         }

         this.activator = var1;
      } finally {
         this.unlock();
      }

   }

   public ServantManager get_servant_manager() throws WrongPolicy {
      ServantManager var1;
      try {
         this.lock();
         var1 = this.mediator.getServantManager();
      } finally {
         this.unlock();
      }

      return var1;
   }

   public void set_servant_manager(ServantManager var1) throws WrongPolicy {
      try {
         this.lock();
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling set_servant_manager on poa " + this + " servantManager=" + var1);
         }

         this.mediator.setServantManager(var1);
      } finally {
         this.unlock();
      }

   }

   public Servant get_servant() throws NoServant, WrongPolicy {
      Servant var1;
      try {
         this.lock();
         var1 = this.mediator.getDefaultServant();
      } finally {
         this.unlock();
      }

      return var1;
   }

   public void set_servant(Servant var1) throws WrongPolicy {
      try {
         this.lock();
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling set_servant on poa " + this + " defaultServant=" + var1);
         }

         this.mediator.setDefaultServant(var1);
      } finally {
         this.unlock();
      }

   }

   public byte[] activate_object(Servant var1) throws ServantAlreadyActive, WrongPolicy {
      byte[] var3;
      try {
         this.lock();
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling activate_object on poa " + this + " (servant=" + var1 + ")");
         }

         byte[] var2 = this.mediator.newSystemId();

         try {
            this.mediator.activateObject(var2, var1);
         } catch (ObjectAlreadyActive var7) {
         }

         var3 = var2;
      } finally {
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Exiting activate_object on poa " + this);
         }

         this.unlock();
      }

      return var3;
   }

   public void activate_object_with_id(byte[] var1, Servant var2) throws ObjectAlreadyActive, ServantAlreadyActive, WrongPolicy {
      try {
         this.lock();
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling activate_object_with_id on poa " + this + " (servant=" + var2 + " id=" + var1 + ")");
         }

         byte[] var3 = (byte[])((byte[])var1.clone());
         this.mediator.activateObject(var3, var2);
      } finally {
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Exiting activate_object_with_id on poa " + this);
         }

         this.unlock();
      }

   }

   public void deactivate_object(byte[] var1) throws ObjectNotActive, WrongPolicy {
      try {
         this.lock();
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling deactivate_object on poa " + this + " (id=" + var1 + ")");
         }

         this.mediator.deactivateObject(var1);
      } finally {
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Exiting deactivate_object on poa " + this);
         }

         this.unlock();
      }

   }

   public org.omg.CORBA.Object create_reference(String var1) throws WrongPolicy {
      org.omg.CORBA.Object var2;
      try {
         this.lock();
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling create_reference(repId=" + var1 + ") on poa " + this);
         }

         var2 = this.makeObject(var1, this.mediator.newSystemId());
      } finally {
         this.unlock();
      }

      return var2;
   }

   public org.omg.CORBA.Object create_reference_with_id(byte[] var1, String var2) {
      org.omg.CORBA.Object var4;
      try {
         this.lock();
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling create_reference_with_id(oid=" + var1 + " repId=" + var2 + ") on poa " + this);
         }

         byte[] var3 = (byte[])((byte[])var1.clone());
         var4 = this.makeObject(var2, var3);
      } finally {
         this.unlock();
      }

      return var4;
   }

   public byte[] servant_to_id(Servant var1) throws ServantNotActive, WrongPolicy {
      byte[] var2;
      try {
         this.lock();
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling servant_to_id(servant=" + var1 + ") on poa " + this);
         }

         var2 = this.mediator.servantToId(var1);
      } finally {
         this.unlock();
      }

      return var2;
   }

   public org.omg.CORBA.Object servant_to_reference(Servant var1) throws ServantNotActive, WrongPolicy {
      org.omg.CORBA.Object var4;
      try {
         this.lock();
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling servant_to_reference(servant=" + var1 + ") on poa " + this);
         }

         byte[] var2 = this.mediator.servantToId(var1);
         String var3 = var1._all_interfaces(this, var2)[0];
         var4 = this.create_reference_with_id(var2, var3);
      } finally {
         this.unlock();
      }

      return var4;
   }

   public Servant reference_to_servant(org.omg.CORBA.Object var1) throws ObjectNotActive, WrongPolicy, WrongAdapter {
      Servant var3;
      try {
         this.lock();
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling reference_to_servant(reference=" + var1 + ") on poa " + this);
         }

         if (this.state >= 4) {
            throw this.lifecycleWrapper().adapterDestroyed();
         }

         byte[] var2 = this.internalReferenceToId(var1);
         var3 = this.mediator.idToServant(var2);
      } finally {
         this.unlock();
      }

      return var3;
   }

   public byte[] reference_to_id(org.omg.CORBA.Object var1) throws WrongAdapter, WrongPolicy {
      byte[] var2;
      try {
         this.lock();
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling reference_to_id(reference=" + var1 + ") on poa " + this);
         }

         if (this.state >= 4) {
            throw this.lifecycleWrapper().adapterDestroyed();
         }

         var2 = this.internalReferenceToId(var1);
      } finally {
         this.unlock();
      }

      return var2;
   }

   public Servant id_to_servant(byte[] var1) throws ObjectNotActive, WrongPolicy {
      Servant var2;
      try {
         this.lock();
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling id_to_servant(id=" + var1 + ") on poa " + this);
         }

         if (this.state >= 4) {
            throw this.lifecycleWrapper().adapterDestroyed();
         }

         var2 = this.mediator.idToServant(var1);
      } finally {
         this.unlock();
      }

      return var2;
   }

   public org.omg.CORBA.Object id_to_reference(byte[] var1) throws ObjectNotActive, WrongPolicy {
      org.omg.CORBA.Object var4;
      try {
         this.lock();
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling id_to_reference(id=" + var1 + ") on poa " + this);
         }

         if (this.state >= 4) {
            throw this.lifecycleWrapper().adapterDestroyed();
         }

         Servant var2 = this.mediator.idToServant(var1);
         String var3 = var2._all_interfaces(this, var1)[0];
         var4 = this.makeObject(var3, var1);
      } finally {
         this.unlock();
      }

      return var4;
   }

   public byte[] id() {
      byte[] var1;
      try {
         this.lock();
         var1 = this.getAdapterId();
      } finally {
         this.unlock();
      }

      return var1;
   }

   public Policy getEffectivePolicy(int var1) {
      return this.mediator.getPolicies().get_effective_policy(var1);
   }

   public int getManagerId() {
      return this.manager.getManagerId();
   }

   public short getState() {
      return this.manager.getORTState();
   }

   public String[] getInterfaces(Object var1, byte[] var2) {
      Servant var3 = (Servant)var1;
      return var3._all_interfaces(this, var2);
   }

   protected ObjectCopierFactory getObjectCopierFactory() {
      int var1 = this.mediator.getPolicies().getCopierId();
      CopierManager var2 = this.getORB().getCopierManager();
      return var2.getObjectCopierFactory(var1);
   }

   public void enter() throws OADestroyed {
      try {
         this.lock();
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling enter on poa " + this);
         }

         while(this.state == 4 && this.isDestroying.get() == Boolean.FALSE) {
            try {
               this.beingDestroyedCV.await();
            } catch (InterruptedException var5) {
            }
         }

         if (!this.waitUntilRunning()) {
            throw new OADestroyed();
         }

         ++this.invocationCount;
      } finally {
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Exiting enter on poa " + this);
         }

         this.unlock();
      }

      this.manager.enter();
   }

   public void exit() {
      try {
         this.lock();
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling exit on poa " + this);
         }

         --this.invocationCount;
         if (this.invocationCount == 0 && this.state == 4) {
            this.invokeCV.broadcast();
         }
      } finally {
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Exiting exit on poa " + this);
         }

         this.unlock();
      }

      this.manager.exit();
   }

   public void getInvocationServant(OAInvocationInfo var1) {
      try {
         this.lock();
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling getInvocationServant on poa " + this);
         }

         Object var2 = null;

         try {
            var2 = this.mediator.getInvocationServant(var1.id(), var1.getOperation());
         } catch (ForwardRequest var7) {
            throw new ForwardException(this.getORB(), var7.forward_reference);
         }

         var1.setServant(var2);
      } finally {
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Exiting getInvocationServant on poa " + this);
         }

         this.unlock();
      }

   }

   public org.omg.CORBA.Object getLocalServant(byte[] var1) {
      return null;
   }

   public void returnServant() {
      try {
         this.lock();
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling returnServant on poa " + this);
         }

         this.mediator.returnServant();
      } catch (Throwable var5) {
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Exception " + var5 + " in returnServant on poa " + this);
         }

         if (var5 instanceof Error) {
            throw (Error)var5;
         }

         if (var5 instanceof RuntimeException) {
            throw (RuntimeException)var5;
         }
      } finally {
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Exiting returnServant on poa " + this);
         }

         this.unlock();
      }

   }

   static class DestroyThread extends Thread {
      private boolean wait;
      private boolean etherealize;
      private boolean debug;
      private POAImpl thePoa;

      public DestroyThread(boolean var1, boolean var2) {
         this.etherealize = var1;
         this.debug = var2;
      }

      public void doIt(POAImpl var1, boolean var2) {
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling DestroyThread.doIt(thePOA=" + var1 + " wait=" + var2 + " etherealize=" + this.etherealize);
         }

         this.thePoa = var1;
         this.wait = var2;
         if (var2) {
            this.run();
         } else {
            try {
               this.setDaemon(true);
            } catch (Exception var4) {
            }

            this.start();
         }

      }

      public void run() {
         HashSet var1 = new HashSet();
         this.performDestroy(this.thePoa, var1);
         Iterator var2 = var1.iterator();
         ObjectReferenceTemplate[] var3 = new ObjectReferenceTemplate[var1.size()];

         for(int var4 = 0; var2.hasNext(); var3[var4++] = (ObjectReferenceTemplate)var2.next()) {
         }

         this.thePoa.getORB().getPIHandler().adapterStateChanged(var3, (short)4);
      }

      private boolean prepareForDestruction(POAImpl var1, Set var2) {
         POAImpl[] var3 = null;

         label103: {
            boolean var4;
            try {
               var1.lock();
               if (this.debug) {
                  ORBUtility.dprint((Object)this, "Calling performDestroy on poa " + var1);
               }

               if (var1.state <= 3) {
                  var1.state = 4;
                  var1.isDestroying.set(Boolean.TRUE);
                  var3 = (POAImpl[])((POAImpl[])var1.children.values().toArray(new POAImpl[0]));
                  break label103;
               }

               if (this.wait) {
                  while(var1.state != 5) {
                     try {
                        var1.beingDestroyedCV.await();
                     } catch (InterruptedException var8) {
                     }
                  }
               }

               var4 = false;
            } finally {
               var1.unlock();
            }

            return var4;
         }

         for(int var10 = 0; var10 < var3.length; ++var10) {
            this.performDestroy(var3[var10], var2);
         }

         return true;
      }

      public void performDestroy(POAImpl var1, Set var2) {
         if (this.prepareForDestruction(var1, var2)) {
            POAImpl var3 = var1.parent;
            boolean var4 = var3 == null;

            try {
               if (!var4) {
                  var3.lock();
               }

               try {
                  var1.lock();
                  this.completeDestruction(var1, var3, var2);
               } finally {
                  var1.unlock();
                  if (var4) {
                     var1.manager.getFactory().registerRootPOA();
                  }

               }
            } finally {
               if (!var4) {
                  var3.unlock();
                  var1.parent = null;
               }

            }

         }
      }

      private void completeDestruction(POAImpl var1, POAImpl var2, Set var3) {
         if (this.debug) {
            ORBUtility.dprint((Object)this, "Calling completeDestruction on poa " + var1);
         }

         try {
            while(var1.invocationCount != 0) {
               try {
                  var1.invokeCV.await();
               } catch (InterruptedException var9) {
               }
            }

            if (var1.mediator != null) {
               if (this.etherealize) {
                  var1.mediator.etherealizeAll();
               }

               var1.mediator.clearAOM();
            }

            if (var1.manager != null) {
               var1.manager.removePOA(var1);
            }

            if (var2 != null) {
               var2.children.remove(var1.name);
            }

            var3.add(var1.getAdapterTemplate());
         } catch (Throwable var10) {
            if (var10 instanceof ThreadDeath) {
               throw (ThreadDeath)var10;
            }

            var1.lifecycleWrapper().unexpectedException((Throwable)var10, var1.toString());
         } finally {
            var1.state = 5;
            var1.beingDestroyedCV.broadcast();
            var1.isDestroying.set(Boolean.FALSE);
            if (this.debug) {
               ORBUtility.dprint((Object)this, "Exiting completeDestruction on poa " + var1);
            }

         }

      }
   }
}
