package com.sun.corba.se.spi.orb;

import com.sun.corba.se.impl.corba.TypeCodeFactory;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.oa.poa.BadServerIdHandler;
import com.sun.corba.se.impl.presentation.rmi.PresentationManagerImpl;
import com.sun.corba.se.impl.transport.ByteBufferPoolImpl;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyFactory;
import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
import com.sun.corba.se.spi.logging.LogWrapperBase;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import com.sun.corba.se.spi.monitoring.MonitoringManager;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import com.sun.corba.se.spi.presentation.rmi.PresentationDefaults;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.protocol.ClientDelegateFactory;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.protocol.PIHandler;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import com.sun.corba.se.spi.resolver.LocalResolver;
import com.sun.corba.se.spi.resolver.Resolver;
import com.sun.corba.se.spi.servicecontext.ServiceContextRegistry;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
import com.sun.corba.se.spi.transport.CorbaTransportManager;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import sun.awt.AppContext;
import sun.corba.SharedSecrets;

public abstract class ORB extends com.sun.corba.se.org.omg.CORBA.ORB implements Broker, TypeCodeFactory {
   public static boolean ORBInitDebug = false;
   public boolean transportDebugFlag = false;
   public boolean subcontractDebugFlag = false;
   public boolean poaDebugFlag = false;
   public boolean poaConcurrencyDebugFlag = false;
   public boolean poaFSMDebugFlag = false;
   public boolean orbdDebugFlag = false;
   public boolean namingDebugFlag = false;
   public boolean serviceContextDebugFlag = false;
   public boolean transientObjectManagerDebugFlag = false;
   public boolean giopVersionDebugFlag = false;
   public boolean shutdownDebugFlag = false;
   public boolean giopDebugFlag = false;
   public boolean invocationTimingDebugFlag = false;
   public boolean orbInitDebugFlag = false;
   protected static ORBUtilSystemException staticWrapper;
   protected ORBUtilSystemException wrapper = ORBUtilSystemException.get(this, "rpc.presentation");
   protected OMGSystemException omgWrapper = OMGSystemException.get(this, "rpc.presentation");
   private Map typeCodeMap = new HashMap();
   private TypeCodeImpl[] primitiveTypeCodeConstants = new TypeCodeImpl[]{new TypeCodeImpl(this, 0), new TypeCodeImpl(this, 1), new TypeCodeImpl(this, 2), new TypeCodeImpl(this, 3), new TypeCodeImpl(this, 4), new TypeCodeImpl(this, 5), new TypeCodeImpl(this, 6), new TypeCodeImpl(this, 7), new TypeCodeImpl(this, 8), new TypeCodeImpl(this, 9), new TypeCodeImpl(this, 10), new TypeCodeImpl(this, 11), new TypeCodeImpl(this, 12), new TypeCodeImpl(this, 13), new TypeCodeImpl(this, 14), null, null, null, new TypeCodeImpl(this, 18), null, null, null, null, new TypeCodeImpl(this, 23), new TypeCodeImpl(this, 24), new TypeCodeImpl(this, 25), new TypeCodeImpl(this, 26), new TypeCodeImpl(this, 27), new TypeCodeImpl(this, 28), new TypeCodeImpl(this, 29), new TypeCodeImpl(this, 30), new TypeCodeImpl(this, 31), new TypeCodeImpl(this, 32)};
   ByteBufferPool byteBufferPool;
   private Map wrapperMap = new ConcurrentHashMap();
   private static final Object pmLock = new Object();
   private static Map staticWrapperMap = new ConcurrentHashMap();
   protected MonitoringManager monitoringManager = MonitoringFactories.getMonitoringManagerFactory().createMonitoringManager("orb", "ORB Management and Monitoring Root");

   public abstract boolean isLocalHost(String var1);

   public abstract boolean isLocalServerId(int var1, int var2);

   public abstract OAInvocationInfo peekInvocationInfo();

   public abstract void pushInvocationInfo(OAInvocationInfo var1);

   public abstract OAInvocationInfo popInvocationInfo();

   public abstract CorbaTransportManager getCorbaTransportManager();

   public abstract LegacyServerSocketManager getLegacyServerSocketManager();

   private static PresentationManager setupPresentationManager() {
      staticWrapper = ORBUtilSystemException.get("rpc.presentation");
      boolean var0 = (Boolean)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return Boolean.getBoolean("com.sun.CORBA.ORBUseDynamicStub");
         }
      });
      PresentationManager.StubFactoryFactory var1 = (PresentationManager.StubFactoryFactory)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            PresentationManager.StubFactoryFactory var1 = PresentationDefaults.getProxyStubFactoryFactory();
            String var2 = System.getProperty("com.sun.CORBA.ORBDynamicStubFactoryFactoryClass", "com.sun.corba.se.impl.presentation.rmi.bcel.StubFactoryFactoryBCELImpl");

            try {
               Class var3 = SharedSecrets.getJavaCorbaAccess().loadClass(var2);
               var1 = (PresentationManager.StubFactoryFactory)var3.newInstance();
            } catch (Exception var4) {
               ORB.staticWrapper.errorInSettingDynamicStubFactoryFactory((Throwable)var4, var2);
            }

            return var1;
         }
      });
      PresentationManagerImpl var2 = new PresentationManagerImpl(var0);
      var2.setStubFactoryFactory(false, PresentationDefaults.getStaticStubFactoryFactory());
      var2.setStubFactoryFactory(true, var1);
      return var2;
   }

   public void destroy() {
      this.wrapper = null;
      this.omgWrapper = null;
      this.typeCodeMap = null;
      this.primitiveTypeCodeConstants = null;
      this.byteBufferPool = null;
   }

   public static PresentationManager getPresentationManager() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null && AppContext.getAppContexts().size() > 0) {
         AppContext var1 = AppContext.getAppContext();
         if (var1 != null) {
            synchronized(pmLock) {
               PresentationManager var3 = (PresentationManager)var1.get(PresentationManager.class);
               if (var3 == null) {
                  var3 = setupPresentationManager();
                  var1.put(PresentationManager.class, var3);
               }

               return var3;
            }
         }
      }

      return ORB.Holder.defaultPresentationManager;
   }

   public static PresentationManager.StubFactoryFactory getStubFactoryFactory() {
      PresentationManager var0 = getPresentationManager();
      boolean var1 = var0.useDynamicStubs();
      return var0.getStubFactoryFactory(var1);
   }

   protected ORB() {
   }

   public TypeCodeImpl get_primitive_tc(int var1) {
      synchronized(this) {
         this.checkShutdownState();
      }

      try {
         return this.primitiveTypeCodeConstants[var1];
      } catch (Throwable var4) {
         throw this.wrapper.invalidTypecodeKind((Throwable)var4, new Integer(var1));
      }
   }

   public synchronized void setTypeCode(String var1, TypeCodeImpl var2) {
      this.checkShutdownState();
      this.typeCodeMap.put(var1, var2);
   }

   public synchronized TypeCodeImpl getTypeCode(String var1) {
      this.checkShutdownState();
      return (TypeCodeImpl)this.typeCodeMap.get(var1);
   }

   public MonitoringManager getMonitoringManager() {
      synchronized(this) {
         this.checkShutdownState();
      }

      return this.monitoringManager;
   }

   public abstract void set_parameters(Properties var1);

   public abstract ORBVersion getORBVersion();

   public abstract void setORBVersion(ORBVersion var1);

   public abstract IOR getFVDCodeBaseIOR();

   public abstract void handleBadServerId(ObjectKey var1);

   public abstract void setBadServerIdHandler(BadServerIdHandler var1);

   public abstract void initBadServerIdHandler();

   public abstract void notifyORB();

   public abstract PIHandler getPIHandler();

   public abstract void checkShutdownState();

   public abstract boolean isDuringDispatch();

   public abstract void startingDispatch();

   public abstract void finishedDispatch();

   public abstract int getTransientServerId();

   public abstract ServiceContextRegistry getServiceContextRegistry();

   public abstract RequestDispatcherRegistry getRequestDispatcherRegistry();

   public abstract ORBData getORBData();

   public abstract void setClientDelegateFactory(ClientDelegateFactory var1);

   public abstract ClientDelegateFactory getClientDelegateFactory();

   public abstract void setCorbaContactInfoListFactory(CorbaContactInfoListFactory var1);

   public abstract CorbaContactInfoListFactory getCorbaContactInfoListFactory();

   public abstract void setResolver(Resolver var1);

   public abstract Resolver getResolver();

   public abstract void setLocalResolver(LocalResolver var1);

   public abstract LocalResolver getLocalResolver();

   public abstract void setURLOperation(Operation var1);

   public abstract Operation getURLOperation();

   public abstract void setINSDelegate(CorbaServerRequestDispatcher var1);

   public abstract TaggedComponentFactoryFinder getTaggedComponentFactoryFinder();

   public abstract IdentifiableFactoryFinder getTaggedProfileFactoryFinder();

   public abstract IdentifiableFactoryFinder getTaggedProfileTemplateFactoryFinder();

   public abstract ObjectKeyFactory getObjectKeyFactory();

   public abstract void setObjectKeyFactory(ObjectKeyFactory var1);

   public Logger getLogger(String var1) {
      synchronized(this) {
         this.checkShutdownState();
      }

      ORBData var2 = this.getORBData();
      String var3;
      if (var2 == null) {
         var3 = "_INITIALIZING_";
      } else {
         var3 = var2.getORBId();
         if (var3.equals("")) {
            var3 = "_DEFAULT_";
         }
      }

      return getCORBALogger(var3, var1);
   }

   public static Logger staticGetLogger(String var0) {
      return getCORBALogger("_CORBA_", var0);
   }

   private static Logger getCORBALogger(String var0, String var1) {
      String var2 = "javax.enterprise.resource.corba." + var0 + "." + var1;
      return Logger.getLogger(var2, "com.sun.corba.se.impl.logging.LogStrings");
   }

   public LogWrapperBase getLogWrapper(String var1, String var2, LogWrapperFactory var3) {
      StringPair var4 = new StringPair(var1, var2);
      LogWrapperBase var5 = (LogWrapperBase)this.wrapperMap.get(var4);
      if (var5 == null) {
         var5 = var3.create(this.getLogger(var1));
         this.wrapperMap.put(var4, var5);
      }

      return var5;
   }

   public static LogWrapperBase staticGetLogWrapper(String var0, String var1, LogWrapperFactory var2) {
      StringPair var3 = new StringPair(var0, var1);
      LogWrapperBase var4 = (LogWrapperBase)staticWrapperMap.get(var3);
      if (var4 == null) {
         var4 = var2.create(staticGetLogger(var0));
         staticWrapperMap.put(var3, var4);
      }

      return var4;
   }

   public ByteBufferPool getByteBufferPool() {
      synchronized(this) {
         this.checkShutdownState();
      }

      if (this.byteBufferPool == null) {
         this.byteBufferPool = new ByteBufferPoolImpl(this);
      }

      return this.byteBufferPool;
   }

   public abstract void setThreadPoolManager(ThreadPoolManager var1);

   public abstract ThreadPoolManager getThreadPoolManager();

   public abstract CopierManager getCopierManager();

   public abstract void validateIORClass(String var1);

   static class Holder {
      static final PresentationManager defaultPresentationManager = ORB.setupPresentationManager();
   }
}
