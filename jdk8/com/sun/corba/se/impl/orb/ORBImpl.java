package com.sun.corba.se.impl.orb;

import com.sun.corba.se.impl.copyobject.CopierManagerImpl;
import com.sun.corba.se.impl.corba.AnyImpl;
import com.sun.corba.se.impl.corba.AsynchInvoke;
import com.sun.corba.se.impl.corba.ContextListImpl;
import com.sun.corba.se.impl.corba.EnvironmentImpl;
import com.sun.corba.se.impl.corba.ExceptionListImpl;
import com.sun.corba.se.impl.corba.NVListImpl;
import com.sun.corba.se.impl.corba.NamedValueImpl;
import com.sun.corba.se.impl.corba.RequestImpl;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.impl.encoding.CachedCodeBase;
import com.sun.corba.se.impl.interceptors.PIHandlerImpl;
import com.sun.corba.se.impl.interceptors.PINoOpHandlerImpl;
import com.sun.corba.se.impl.ior.IORTypeCheckRegistryImpl;
import com.sun.corba.se.impl.ior.TaggedComponentFactoryFinderImpl;
import com.sun.corba.se.impl.ior.TaggedProfileFactoryFinderImpl;
import com.sun.corba.se.impl.ior.TaggedProfileTemplateFactoryFinderImpl;
import com.sun.corba.se.impl.legacy.connection.LegacyServerSocketManagerImpl;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.oa.poa.BadServerIdHandler;
import com.sun.corba.se.impl.oa.poa.POAFactory;
import com.sun.corba.se.impl.oa.toa.TOAFactory;
import com.sun.corba.se.impl.orbutil.ORBConstants;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.orbutil.StackImpl;
import com.sun.corba.se.impl.orbutil.threadpool.ThreadPoolManagerImpl;
import com.sun.corba.se.impl.protocol.CorbaInvocationInfo;
import com.sun.corba.se.impl.protocol.RequestDispatcherRegistryImpl;
import com.sun.corba.se.impl.transport.CorbaTransportManagerImpl;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.pept.protocol.ClientInvocationInfo;
import com.sun.corba.se.pept.transport.TransportManager;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORTypeCheckRegistry;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyFactory;
import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.DataCollector;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBConfigurator;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.OperationFactory;
import com.sun.corba.se.spi.orb.ParserImplBase;
import com.sun.corba.se.spi.orb.PropertyParser;
import com.sun.corba.se.spi.orbutil.closure.ClosureFactory;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.spi.protocol.ClientDelegateFactory;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.protocol.PIHandler;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import com.sun.corba.se.spi.resolver.LocalResolver;
import com.sun.corba.se.spi.resolver.Resolver;
import com.sun.corba.se.spi.servicecontext.ServiceContextRegistry;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
import com.sun.corba.se.spi.transport.CorbaTransportManager;
import com.sun.org.omg.SendingContext.CodeBase;
import java.applet.Applet;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Security;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.WeakHashMap;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.Current;
import org.omg.CORBA.Environment;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyError;
import org.omg.CORBA.Request;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.UnionMember;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.WrongTransaction;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ValueFactory;
import org.omg.PortableServer.Servant;
import sun.corba.OutputStreamFactory;

public class ORBImpl extends ORB {
   protected TransportManager transportManager;
   protected LegacyServerSocketManager legacyServerSocketManager;
   private ThreadLocal OAInvocationInfoStack;
   private ThreadLocal clientInvocationInfoStack;
   private static IOR codeBaseIOR;
   private Vector dynamicRequests;
   private SynchVariable svResponseReceived;
   private Object runObj = new Object();
   private Object shutdownObj = new Object();
   private Object waitForCompletionObj = new Object();
   private static final byte STATUS_OPERATING = 1;
   private static final byte STATUS_SHUTTING_DOWN = 2;
   private static final byte STATUS_SHUTDOWN = 3;
   private static final byte STATUS_DESTROYED = 4;
   private byte status = 1;
   private Object invocationObj = new Object();
   private int numInvocations = 0;
   private ThreadLocal isProcessingInvocation = new ThreadLocal() {
      protected Object initialValue() {
         return Boolean.FALSE;
      }
   };
   private Map typeCodeForClassMap;
   private Hashtable valueFactoryCache = new Hashtable();
   private ThreadLocal orbVersionThreadLocal;
   private RequestDispatcherRegistry requestDispatcherRegistry;
   private CopierManager copierManager;
   private int transientServerId;
   private ServiceContextRegistry serviceContextRegistry;
   private IORTypeCheckRegistry iorTypeCheckRegistry;
   private TOAFactory toaFactory;
   private POAFactory poaFactory;
   private PIHandler pihandler;
   private ORBData configData;
   private BadServerIdHandler badServerIdHandler;
   private ClientDelegateFactory clientDelegateFactory;
   private CorbaContactInfoListFactory corbaContactInfoListFactory;
   private Resolver resolver;
   private LocalResolver localResolver;
   private Operation urlOperation;
   private final Object urlOperationLock = new Object();
   private CorbaServerRequestDispatcher insNamingDelegate;
   private final Object resolverLock = new Object();
   private static final String IORTYPECHECKREGISTRY_FILTER_PROPNAME = "com.sun.CORBA.ORBIorTypeCheckRegistryFilter";
   private TaggedComponentFactoryFinder taggedComponentFactoryFinder;
   private IdentifiableFactoryFinder taggedProfileFactoryFinder;
   private IdentifiableFactoryFinder taggedProfileTemplateFactoryFinder;
   private ObjectKeyFactory objectKeyFactory;
   private boolean orbOwnsThreadPoolManager = false;
   private ThreadPoolManager threadpoolMgr;
   private Object badServerIdHandlerAccessLock = new Object();
   private static String localHostString = null;
   private Object clientDelegateFactoryAccessorLock = new Object();
   private Object corbaContactInfoListFactoryAccessLock = new Object();
   private Object objectKeyFactoryAccessLock = new Object();
   private Object transportManagerAccessorLock = new Object();
   private Object legacyServerSocketManagerAccessLock = new Object();
   private Object threadPoolManagerAccessLock = new Object();

   private void dprint(String var1) {
      ORBUtility.dprint((Object)this, var1);
   }

   public ORBData getORBData() {
      return this.configData;
   }

   public PIHandler getPIHandler() {
      return this.pihandler;
   }

   public ORBVersion getORBVersion() {
      synchronized(this) {
         this.checkShutdownState();
      }

      return (ORBVersion)((ORBVersion)this.orbVersionThreadLocal.get());
   }

   public void setORBVersion(ORBVersion var1) {
      synchronized(this) {
         this.checkShutdownState();
      }

      this.orbVersionThreadLocal.set(var1);
   }

   private void preInit(String[] var1, Properties var2) {
      this.pihandler = new PINoOpHandlerImpl();
      this.transientServerId = (int)System.currentTimeMillis();
      this.orbVersionThreadLocal = new ThreadLocal() {
         protected Object initialValue() {
            return ORBVersionFactory.getORBVersion();
         }
      };
      this.requestDispatcherRegistry = new RequestDispatcherRegistryImpl(this, 2);
      this.copierManager = new CopierManagerImpl(this);
      this.taggedComponentFactoryFinder = new TaggedComponentFactoryFinderImpl(this);
      this.taggedProfileFactoryFinder = new TaggedProfileFactoryFinderImpl(this);
      this.taggedProfileTemplateFactoryFinder = new TaggedProfileTemplateFactoryFinderImpl(this);
      this.dynamicRequests = new Vector();
      this.svResponseReceived = new SynchVariable();
      this.OAInvocationInfoStack = new ThreadLocal() {
         protected Object initialValue() {
            return new StackImpl();
         }
      };
      this.clientInvocationInfoStack = new ThreadLocal() {
         protected Object initialValue() {
            return new StackImpl();
         }
      };
      this.serviceContextRegistry = new ServiceContextRegistry(this);
   }

   private void initIORTypeCheckRegistry() {
      String var1 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            String var1 = System.getProperty("com.sun.CORBA.ORBIorTypeCheckRegistryFilter");
            if (var1 == null) {
               var1 = Security.getProperty("com.sun.CORBA.ORBIorTypeCheckRegistryFilter");
            }

            return var1;
         }
      });
      if (var1 != null) {
         try {
            this.iorTypeCheckRegistry = new IORTypeCheckRegistryImpl(var1, this);
         } catch (Exception var3) {
            throw this.wrapper.bootstrapException((Throwable)var3);
         }

         if (this.orbInitDebugFlag) {
            this.dprint(".initIORTypeCheckRegistry, IORTypeCheckRegistryImpl created for properties == " + var1);
         }
      } else if (this.orbInitDebugFlag) {
         this.dprint(".initIORTypeCheckRegistry, IORTypeCheckRegistryImpl NOT created for properties == ");
      }

   }

   protected void setDebugFlags(String[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         String var3 = var1[var2];

         try {
            Field var4 = this.getClass().getField(var3 + "DebugFlag");
            int var5 = var4.getModifiers();
            if (Modifier.isPublic(var5) && !Modifier.isStatic(var5) && var4.getType() == Boolean.TYPE) {
               var4.setBoolean(this, true);
            }
         } catch (Exception var6) {
         }
      }

   }

   private void postInit(String[] var1, DataCollector var2) {
      this.configData = new ORBDataParserImpl(this, var2);
      this.setDebugFlags(this.configData.getORBDebugFlags());
      this.getTransportManager();
      this.getLegacyServerSocketManager();
      ORBImpl.ConfigParser var3 = new ORBImpl.ConfigParser();
      var3.init(var2);
      ORBConfigurator var4 = null;

      try {
         var4 = (ORBConfigurator)((ORBConfigurator)var3.configurator.newInstance());
      } catch (Exception var7) {
         throw this.wrapper.badOrbConfigurator((Throwable)var7, var3.configurator.getName());
      }

      try {
         var4.configure(var2, this);
      } catch (Exception var6) {
         throw this.wrapper.orbConfiguratorError((Throwable)var6);
      }

      this.pihandler = new PIHandlerImpl(this, var1);
      this.pihandler.initialize();
      this.getThreadPoolManager();
      super.getByteBufferPool();
      this.initIORTypeCheckRegistry();
   }

   private synchronized POAFactory getPOAFactory() {
      if (this.poaFactory == null) {
         this.poaFactory = (POAFactory)this.requestDispatcherRegistry.getObjectAdapterFactory(32);
      }

      return this.poaFactory;
   }

   private synchronized TOAFactory getTOAFactory() {
      if (this.toaFactory == null) {
         this.toaFactory = (TOAFactory)this.requestDispatcherRegistry.getObjectAdapterFactory(2);
      }

      return this.toaFactory;
   }

   public void set_parameters(Properties var1) {
      synchronized(this) {
         this.checkShutdownState();
      }

      this.preInit((String[])null, var1);
      DataCollector var2 = DataCollectorFactory.create(var1, this.getLocalHostName());
      this.postInit((String[])null, var2);
   }

   protected void set_parameters(Applet var1, Properties var2) {
      this.preInit((String[])null, var2);
      DataCollector var3 = DataCollectorFactory.create(var1, var2, this.getLocalHostName());
      this.postInit((String[])null, var3);
   }

   protected void set_parameters(String[] var1, Properties var2) {
      this.preInit(var1, var2);
      DataCollector var3 = DataCollectorFactory.create(var1, var2, this.getLocalHostName());
      this.postInit(var1, var3);
   }

   public synchronized OutputStream create_output_stream() {
      this.checkShutdownState();
      return OutputStreamFactory.newEncapsOutputStream(this);
   }

   /** @deprecated */
   public synchronized Current get_current() {
      this.checkShutdownState();
      throw this.wrapper.genericNoImpl();
   }

   public synchronized NVList create_list(int var1) {
      this.checkShutdownState();
      return new NVListImpl(this, var1);
   }

   public synchronized NVList create_operation_list(org.omg.CORBA.Object var1) {
      this.checkShutdownState();
      throw this.wrapper.genericNoImpl();
   }

   public synchronized NamedValue create_named_value(String var1, Any var2, int var3) {
      this.checkShutdownState();
      return new NamedValueImpl(this, var1, var2, var3);
   }

   public synchronized ExceptionList create_exception_list() {
      this.checkShutdownState();
      return new ExceptionListImpl();
   }

   public synchronized ContextList create_context_list() {
      this.checkShutdownState();
      return new ContextListImpl(this);
   }

   public synchronized Context get_default_context() {
      this.checkShutdownState();
      throw this.wrapper.genericNoImpl();
   }

   public synchronized Environment create_environment() {
      this.checkShutdownState();
      return new EnvironmentImpl();
   }

   public synchronized void send_multiple_requests_oneway(Request[] var1) {
      this.checkShutdownState();

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2].send_oneway();
      }

   }

   public synchronized void send_multiple_requests_deferred(Request[] var1) {
      this.checkShutdownState();

      int var2;
      for(var2 = 0; var2 < var1.length; ++var2) {
         this.dynamicRequests.addElement(var1[var2]);
      }

      for(var2 = 0; var2 < var1.length; ++var2) {
         AsynchInvoke var3 = new AsynchInvoke(this, (RequestImpl)var1[var2], true);
         (new Thread(var3)).start();
      }

   }

   public synchronized boolean poll_next_response() {
      this.checkShutdownState();
      Enumeration var2 = this.dynamicRequests.elements();

      Request var1;
      do {
         if (!var2.hasMoreElements()) {
            return false;
         }

         var1 = (Request)var2.nextElement();
      } while(!var1.poll_response());

      return true;
   }

   public Request get_next_response() throws WrongTransaction {
      synchronized(this) {
         this.checkShutdownState();
      }

      while(true) {
         synchronized(this.dynamicRequests) {
            Enumeration var2 = this.dynamicRequests.elements();

            while(true) {
               if (!var2.hasMoreElements()) {
                  break;
               }

               Request var3 = (Request)var2.nextElement();
               if (var3.poll_response()) {
                  var3.get_response();
                  this.dynamicRequests.removeElement(var3);
                  return var3;
               }
            }
         }

         synchronized(this.svResponseReceived) {
            while(!this.svResponseReceived.value()) {
               try {
                  this.svResponseReceived.wait();
               } catch (InterruptedException var6) {
               }
            }

            this.svResponseReceived.reset();
         }
      }
   }

   public void notifyORB() {
      synchronized(this) {
         this.checkShutdownState();
      }

      synchronized(this.svResponseReceived) {
         this.svResponseReceived.set();
         this.svResponseReceived.notify();
      }
   }

   public synchronized String object_to_string(org.omg.CORBA.Object var1) {
      this.checkShutdownState();
      IOR var2;
      if (var1 == null) {
         var2 = IORFactories.makeIOR((ORB)this);
         return var2.stringify();
      } else {
         var2 = null;

         try {
            var2 = ORBUtility.connectAndGetIOR(this, var1);
         } catch (BAD_PARAM var4) {
            if (var4.minor == 1398079694) {
               throw this.omgWrapper.notAnObjectImpl((Throwable)var4);
            }

            throw var4;
         }

         return var2.stringify();
      }
   }

   public org.omg.CORBA.Object string_to_object(String var1) {
      Operation var2;
      synchronized(this) {
         this.checkShutdownState();
         var2 = this.urlOperation;
      }

      if (var1 == null) {
         throw this.wrapper.nullParam();
      } else {
         synchronized(this.urlOperationLock) {
            org.omg.CORBA.Object var4 = (org.omg.CORBA.Object)var2.operate(var1);
            return var4;
         }
      }
   }

   public synchronized IOR getFVDCodeBaseIOR() {
      this.checkShutdownState();
      if (codeBaseIOR != null) {
         return codeBaseIOR;
      } else {
         ValueHandler var2 = ORBUtility.createValueHandler();
         CodeBase var1 = (CodeBase)var2.getRunTimeCodeBase();
         return ORBUtility.connectAndGetIOR(this, var1);
      }
   }

   public synchronized TypeCode get_primitive_tc(TCKind var1) {
      this.checkShutdownState();
      return this.get_primitive_tc(var1.value());
   }

   public synchronized TypeCode create_struct_tc(String var1, String var2, StructMember[] var3) {
      this.checkShutdownState();
      return new TypeCodeImpl(this, 15, var1, var2, var3);
   }

   public synchronized TypeCode create_union_tc(String var1, String var2, TypeCode var3, UnionMember[] var4) {
      this.checkShutdownState();
      return new TypeCodeImpl(this, 16, var1, var2, var3, var4);
   }

   public synchronized TypeCode create_enum_tc(String var1, String var2, String[] var3) {
      this.checkShutdownState();
      return new TypeCodeImpl(this, 17, var1, var2, var3);
   }

   public synchronized TypeCode create_alias_tc(String var1, String var2, TypeCode var3) {
      this.checkShutdownState();
      return new TypeCodeImpl(this, 21, var1, var2, var3);
   }

   public synchronized TypeCode create_exception_tc(String var1, String var2, StructMember[] var3) {
      this.checkShutdownState();
      return new TypeCodeImpl(this, 22, var1, var2, var3);
   }

   public synchronized TypeCode create_interface_tc(String var1, String var2) {
      this.checkShutdownState();
      return new TypeCodeImpl(this, 14, var1, var2);
   }

   public synchronized TypeCode create_string_tc(int var1) {
      this.checkShutdownState();
      return new TypeCodeImpl(this, 18, var1);
   }

   public synchronized TypeCode create_wstring_tc(int var1) {
      this.checkShutdownState();
      return new TypeCodeImpl(this, 27, var1);
   }

   public synchronized TypeCode create_sequence_tc(int var1, TypeCode var2) {
      this.checkShutdownState();
      return new TypeCodeImpl(this, 19, var1, var2);
   }

   public synchronized TypeCode create_recursive_sequence_tc(int var1, int var2) {
      this.checkShutdownState();
      return new TypeCodeImpl(this, 19, var1, var2);
   }

   public synchronized TypeCode create_array_tc(int var1, TypeCode var2) {
      this.checkShutdownState();
      return new TypeCodeImpl(this, 20, var1, var2);
   }

   public synchronized TypeCode create_native_tc(String var1, String var2) {
      this.checkShutdownState();
      return new TypeCodeImpl(this, 31, var1, var2);
   }

   public synchronized TypeCode create_abstract_interface_tc(String var1, String var2) {
      this.checkShutdownState();
      return new TypeCodeImpl(this, 32, var1, var2);
   }

   public synchronized TypeCode create_fixed_tc(short var1, short var2) {
      this.checkShutdownState();
      return new TypeCodeImpl(this, 28, var1, var2);
   }

   public synchronized TypeCode create_value_tc(String var1, String var2, short var3, TypeCode var4, ValueMember[] var5) {
      this.checkShutdownState();
      return new TypeCodeImpl(this, 29, var1, var2, var3, var4, var5);
   }

   public synchronized TypeCode create_recursive_tc(String var1) {
      this.checkShutdownState();
      return new TypeCodeImpl(this, var1);
   }

   public synchronized TypeCode create_value_box_tc(String var1, String var2, TypeCode var3) {
      this.checkShutdownState();
      return new TypeCodeImpl(this, 30, var1, var2, var3);
   }

   public synchronized Any create_any() {
      this.checkShutdownState();
      return new AnyImpl(this);
   }

   public synchronized void setTypeCodeForClass(Class var1, TypeCodeImpl var2) {
      this.checkShutdownState();
      if (this.typeCodeForClassMap == null) {
         this.typeCodeForClassMap = Collections.synchronizedMap(new WeakHashMap(64));
      }

      if (!this.typeCodeForClassMap.containsKey(var1)) {
         this.typeCodeForClassMap.put(var1, var2);
      }

   }

   public synchronized TypeCodeImpl getTypeCodeForClass(Class var1) {
      this.checkShutdownState();
      return this.typeCodeForClassMap == null ? null : (TypeCodeImpl)this.typeCodeForClassMap.get(var1);
   }

   public String[] list_initial_services() {
      Resolver var1;
      synchronized(this) {
         this.checkShutdownState();
         var1 = this.resolver;
      }

      synchronized(this.resolverLock) {
         Set var3 = var1.list();
         return (String[])((String[])var3.toArray(new String[var3.size()]));
      }
   }

   public org.omg.CORBA.Object resolve_initial_references(String var1) throws InvalidName {
      Resolver var2;
      synchronized(this) {
         this.checkShutdownState();
         var2 = this.resolver;
      }

      synchronized(this.resolverLock) {
         org.omg.CORBA.Object var4 = var2.resolve(var1);
         if (var4 == null) {
            throw new InvalidName();
         } else {
            return var4;
         }
      }
   }

   public void register_initial_reference(String var1, org.omg.CORBA.Object var2) throws InvalidName {
      synchronized(this) {
         this.checkShutdownState();
      }

      if (var1 != null && var1.length() != 0) {
         synchronized(this) {
            this.checkShutdownState();
         }

         CorbaServerRequestDispatcher var3;
         synchronized(this.resolverLock) {
            var3 = this.insNamingDelegate;
            org.omg.CORBA.Object var5 = this.localResolver.resolve(var1);
            if (var5 != null) {
               throw new InvalidName(var1 + " already registered");
            }

            this.localResolver.register(var1, ClosureFactory.makeConstant(var2));
         }

         synchronized(this) {
            if (StubAdapter.isStub(var2)) {
               this.requestDispatcherRegistry.registerServerRequestDispatcher(var3, var1);
            }

         }
      } else {
         throw new InvalidName();
      }
   }

   public void run() {
      synchronized(this) {
         this.checkShutdownState();
      }

      synchronized(this.runObj) {
         try {
            this.runObj.wait();
         } catch (InterruptedException var4) {
         }

      }
   }

   public void shutdown(boolean var1) {
      boolean var2 = false;
      synchronized(this) {
         this.checkShutdownState();
         if (var1 && this.isProcessingInvocation.get() == Boolean.TRUE) {
            throw this.omgWrapper.shutdownWaitForCompletionDeadlock();
         }

         if (this.status == 2) {
            if (!var1) {
               return;
            }

            var2 = true;
         }

         this.status = 2;
      }

      synchronized(this.shutdownObj) {
         if (!var2) {
            this.shutdownServants(var1);
            if (var1) {
               synchronized(this.waitForCompletionObj) {
                  while(this.numInvocations > 0) {
                     try {
                        this.waitForCompletionObj.wait();
                     } catch (InterruptedException var10) {
                     }
                  }
               }
            }

            synchronized(this.runObj) {
               this.runObj.notifyAll();
            }

            this.status = 3;
            this.shutdownObj.notifyAll();
         } else {
            while(true) {
               synchronized(this) {
                  if (this.status == 3) {
                     break;
                  }
               }

               try {
                  this.shutdownObj.wait();
               } catch (InterruptedException var11) {
               }
            }
         }

      }
   }

   protected void shutdownServants(boolean var1) {
      HashSet var2;
      synchronized(this) {
         var2 = new HashSet(this.requestDispatcherRegistry.getObjectAdapterFactories());
      }

      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         ObjectAdapterFactory var4 = (ObjectAdapterFactory)var3.next();
         var4.shutdown(var1);
      }

   }

   public void checkShutdownState() {
      if (this.status == 4) {
         throw this.wrapper.orbDestroyed();
      } else if (this.status == 3) {
         throw this.omgWrapper.badOperationAfterShutdown();
      }
   }

   public boolean isDuringDispatch() {
      synchronized(this) {
         this.checkShutdownState();
      }

      Boolean var1 = (Boolean)((Boolean)this.isProcessingInvocation.get());
      return var1;
   }

   public void startingDispatch() {
      synchronized(this) {
         this.checkShutdownState();
      }

      synchronized(this.invocationObj) {
         this.isProcessingInvocation.set(Boolean.TRUE);
         ++this.numInvocations;
      }
   }

   public void finishedDispatch() {
      synchronized(this) {
         this.checkShutdownState();
      }

      synchronized(this.invocationObj) {
         --this.numInvocations;
         this.isProcessingInvocation.set(false);
         if (this.numInvocations == 0) {
            synchronized(this.waitForCompletionObj) {
               this.waitForCompletionObj.notifyAll();
            }
         } else if (this.numInvocations < 0) {
            throw this.wrapper.numInvocationsAlreadyZero(CompletionStatus.COMPLETED_YES);
         }

      }
   }

   public void destroy() {
      boolean var1 = false;
      synchronized(this) {
         var1 = this.status == 1;
      }

      if (var1) {
         this.shutdown(true);
      }

      synchronized(this) {
         if (this.status < 4) {
            this.getCorbaTransportManager().close();
            this.getPIHandler().destroyInterceptors();
            this.status = 4;
         }
      }

      synchronized(this.threadPoolManagerAccessLock) {
         if (this.orbOwnsThreadPoolManager) {
            try {
               this.threadpoolMgr.close();
               this.threadpoolMgr = null;
            } catch (IOException var8) {
               this.wrapper.ioExceptionOnClose((Throwable)var8);
            }
         }
      }

      try {
         this.monitoringManager.close();
         this.monitoringManager = null;
      } catch (IOException var7) {
         this.wrapper.ioExceptionOnClose((Throwable)var7);
      }

      CachedCodeBase.cleanCache(this);

      try {
         this.pihandler.close();
      } catch (IOException var6) {
         this.wrapper.ioExceptionOnClose((Throwable)var6);
      }

      super.destroy();
      this.badServerIdHandlerAccessLock = null;
      this.clientDelegateFactoryAccessorLock = null;
      this.corbaContactInfoListFactoryAccessLock = null;
      this.objectKeyFactoryAccessLock = null;
      this.legacyServerSocketManagerAccessLock = null;
      this.threadPoolManagerAccessLock = null;
      this.transportManager = null;
      this.legacyServerSocketManager = null;
      this.OAInvocationInfoStack = null;
      this.clientInvocationInfoStack = null;
      codeBaseIOR = null;
      this.dynamicRequests = null;
      this.svResponseReceived = null;
      this.runObj = null;
      this.shutdownObj = null;
      this.waitForCompletionObj = null;
      this.invocationObj = null;
      this.isProcessingInvocation = null;
      this.typeCodeForClassMap = null;
      this.valueFactoryCache = null;
      this.orbVersionThreadLocal = null;
      this.requestDispatcherRegistry = null;
      this.copierManager = null;
      this.toaFactory = null;
      this.poaFactory = null;
      this.pihandler = null;
      this.configData = null;
      this.badServerIdHandler = null;
      this.clientDelegateFactory = null;
      this.corbaContactInfoListFactory = null;
      this.resolver = null;
      this.localResolver = null;
      this.insNamingDelegate = null;
      this.urlOperation = null;
      this.taggedComponentFactoryFinder = null;
      this.taggedProfileFactoryFinder = null;
      this.taggedProfileTemplateFactoryFinder = null;
      this.objectKeyFactory = null;
   }

   public synchronized ValueFactory register_value_factory(String var1, ValueFactory var2) {
      this.checkShutdownState();
      if (var1 != null && var2 != null) {
         return (ValueFactory)this.valueFactoryCache.put(var1, var2);
      } else {
         throw this.omgWrapper.unableRegisterValueFactory();
      }
   }

   public synchronized void unregister_value_factory(String var1) {
      this.checkShutdownState();
      if (this.valueFactoryCache.remove(var1) == null) {
         throw this.wrapper.nullParam();
      }
   }

   public synchronized ValueFactory lookup_value_factory(String var1) {
      this.checkShutdownState();
      ValueFactory var2 = (ValueFactory)this.valueFactoryCache.get(var1);
      if (var2 == null) {
         try {
            var2 = Utility.getFactory((Class)null, (String)null, (org.omg.CORBA.ORB)null, var1);
         } catch (MARSHAL var4) {
            throw this.wrapper.unableFindValueFactory((Throwable)var4);
         }
      }

      return var2;
   }

   public OAInvocationInfo peekInvocationInfo() {
      synchronized(this) {
         this.checkShutdownState();
      }

      StackImpl var1 = (StackImpl)((StackImpl)this.OAInvocationInfoStack.get());
      return (OAInvocationInfo)((OAInvocationInfo)var1.peek());
   }

   public void pushInvocationInfo(OAInvocationInfo var1) {
      synchronized(this) {
         this.checkShutdownState();
      }

      StackImpl var2 = (StackImpl)((StackImpl)this.OAInvocationInfoStack.get());
      var2.push(var1);
   }

   public OAInvocationInfo popInvocationInfo() {
      synchronized(this) {
         this.checkShutdownState();
      }

      StackImpl var1 = (StackImpl)((StackImpl)this.OAInvocationInfoStack.get());
      return (OAInvocationInfo)((OAInvocationInfo)var1.pop());
   }

   public void initBadServerIdHandler() {
      synchronized(this) {
         this.checkShutdownState();
      }

      synchronized(this.badServerIdHandlerAccessLock) {
         Class var2 = this.configData.getBadServerIdHandler();
         if (var2 != null) {
            try {
               Class[] var3 = new Class[]{org.omg.CORBA.ORB.class};
               Object[] var4 = new Object[]{this};
               Constructor var5 = var2.getConstructor(var3);
               this.badServerIdHandler = (BadServerIdHandler)var5.newInstance(var4);
            } catch (Exception var7) {
               throw this.wrapper.errorInitBadserveridhandler((Throwable)var7);
            }
         }

      }
   }

   public void setBadServerIdHandler(BadServerIdHandler var1) {
      synchronized(this) {
         this.checkShutdownState();
      }

      synchronized(this.badServerIdHandlerAccessLock) {
         this.badServerIdHandler = var1;
      }
   }

   public void handleBadServerId(ObjectKey var1) {
      synchronized(this) {
         this.checkShutdownState();
      }

      synchronized(this.badServerIdHandlerAccessLock) {
         if (this.badServerIdHandler == null) {
            throw this.wrapper.badServerId();
         } else {
            this.badServerIdHandler.handle(var1);
         }
      }
   }

   public synchronized Policy create_policy(int var1, Any var2) throws PolicyError {
      this.checkShutdownState();
      return this.pihandler.create_policy(var1, var2);
   }

   public synchronized void connect(org.omg.CORBA.Object var1) {
      this.checkShutdownState();
      if (this.getTOAFactory() == null) {
         throw this.wrapper.noToa();
      } else {
         try {
            String var2 = Util.getCodebase(var1.getClass());
            this.getTOAFactory().getTOA(var2).connect(var1);
         } catch (Exception var3) {
            throw this.wrapper.orbConnectError((Throwable)var3);
         }
      }
   }

   public synchronized void disconnect(org.omg.CORBA.Object var1) {
      this.checkShutdownState();
      if (this.getTOAFactory() == null) {
         throw this.wrapper.noToa();
      } else {
         try {
            this.getTOAFactory().getTOA().disconnect(var1);
         } catch (Exception var3) {
            throw this.wrapper.orbConnectError((Throwable)var3);
         }
      }
   }

   public int getTransientServerId() {
      synchronized(this) {
         this.checkShutdownState();
      }

      return this.configData.getORBServerIdPropertySpecified() ? this.configData.getPersistentServerId() : this.transientServerId;
   }

   public RequestDispatcherRegistry getRequestDispatcherRegistry() {
      synchronized(this) {
         this.checkShutdownState();
      }

      return this.requestDispatcherRegistry;
   }

   public ServiceContextRegistry getServiceContextRegistry() {
      synchronized(this) {
         this.checkShutdownState();
      }

      return this.serviceContextRegistry;
   }

   public boolean isLocalHost(String var1) {
      synchronized(this) {
         this.checkShutdownState();
      }

      return var1.equals(this.configData.getORBServerHost()) || var1.equals(this.getLocalHostName());
   }

   public boolean isLocalServerId(int var1, int var2) {
      synchronized(this) {
         this.checkShutdownState();
      }

      if (var1 >= 32 && var1 <= 63) {
         if (ORBConstants.isTransient(var1)) {
            return var2 == this.getTransientServerId();
         } else if (this.configData.getPersistentServerIdInitialized()) {
            return var2 == this.configData.getPersistentServerId();
         } else {
            return false;
         }
      } else {
         return var2 == this.getTransientServerId();
      }
   }

   private String getHostName(String var1) throws UnknownHostException {
      return InetAddress.getByName(var1).getHostAddress();
   }

   private synchronized String getLocalHostName() {
      if (localHostString == null) {
         try {
            localHostString = InetAddress.getLocalHost().getHostAddress();
         } catch (Exception var2) {
            throw this.wrapper.getLocalHostFailed((Throwable)var2);
         }
      }

      return localHostString;
   }

   public synchronized boolean work_pending() {
      this.checkShutdownState();
      throw this.wrapper.genericNoImpl();
   }

   public synchronized void perform_work() {
      this.checkShutdownState();
      throw this.wrapper.genericNoImpl();
   }

   public synchronized void set_delegate(Object var1) {
      this.checkShutdownState();
      POAFactory var2 = this.getPOAFactory();
      if (var2 != null) {
         ((Servant)var1)._set_delegate(var2.getDelegateImpl());
      } else {
         throw this.wrapper.noPoa();
      }
   }

   public ClientInvocationInfo createOrIncrementInvocationInfo() {
      synchronized(this) {
         this.checkShutdownState();
      }

      StackImpl var1 = (StackImpl)this.clientInvocationInfoStack.get();
      Object var2 = null;
      if (!var1.empty()) {
         var2 = (ClientInvocationInfo)var1.peek();
      }

      if (var2 == null || !((ClientInvocationInfo)var2).isRetryInvocation()) {
         var2 = new CorbaInvocationInfo(this);
         this.startingDispatch();
         var1.push(var2);
      }

      ((ClientInvocationInfo)var2).setIsRetryInvocation(false);
      ((ClientInvocationInfo)var2).incrementEntryCount();
      return (ClientInvocationInfo)var2;
   }

   public void releaseOrDecrementInvocationInfo() {
      synchronized(this) {
         this.checkShutdownState();
      }

      boolean var1 = true;
      ClientInvocationInfo var2 = null;
      StackImpl var3 = (StackImpl)this.clientInvocationInfoStack.get();
      if (!var3.empty()) {
         var2 = (ClientInvocationInfo)var3.peek();
         var2.decrementEntryCount();
         int var5 = var2.getEntryCount();
         if (var2.getEntryCount() == 0) {
            if (!var2.isRetryInvocation()) {
               var3.pop();
            }

            this.finishedDispatch();
         }

      } else {
         throw this.wrapper.invocationInfoStackEmpty();
      }
   }

   public ClientInvocationInfo getInvocationInfo() {
      synchronized(this) {
         this.checkShutdownState();
      }

      StackImpl var1 = (StackImpl)this.clientInvocationInfoStack.get();
      return (ClientInvocationInfo)var1.peek();
   }

   public void setClientDelegateFactory(ClientDelegateFactory var1) {
      synchronized(this) {
         this.checkShutdownState();
      }

      synchronized(this.clientDelegateFactoryAccessorLock) {
         this.clientDelegateFactory = var1;
      }
   }

   public ClientDelegateFactory getClientDelegateFactory() {
      synchronized(this) {
         this.checkShutdownState();
      }

      synchronized(this.clientDelegateFactoryAccessorLock) {
         return this.clientDelegateFactory;
      }
   }

   public void setCorbaContactInfoListFactory(CorbaContactInfoListFactory var1) {
      synchronized(this) {
         this.checkShutdownState();
      }

      synchronized(this.corbaContactInfoListFactoryAccessLock) {
         this.corbaContactInfoListFactory = var1;
      }
   }

   public synchronized CorbaContactInfoListFactory getCorbaContactInfoListFactory() {
      this.checkShutdownState();
      return this.corbaContactInfoListFactory;
   }

   public void setResolver(Resolver var1) {
      synchronized(this) {
         this.checkShutdownState();
      }

      synchronized(this.resolverLock) {
         this.resolver = var1;
      }
   }

   public Resolver getResolver() {
      synchronized(this) {
         this.checkShutdownState();
      }

      synchronized(this.resolverLock) {
         return this.resolver;
      }
   }

   public void setLocalResolver(LocalResolver var1) {
      synchronized(this) {
         this.checkShutdownState();
      }

      synchronized(this.resolverLock) {
         this.localResolver = var1;
      }
   }

   public LocalResolver getLocalResolver() {
      synchronized(this) {
         this.checkShutdownState();
      }

      synchronized(this.resolverLock) {
         return this.localResolver;
      }
   }

   public void setURLOperation(Operation var1) {
      synchronized(this) {
         this.checkShutdownState();
      }

      synchronized(this.urlOperationLock) {
         this.urlOperation = var1;
      }
   }

   public Operation getURLOperation() {
      synchronized(this) {
         this.checkShutdownState();
      }

      synchronized(this.urlOperationLock) {
         return this.urlOperation;
      }
   }

   public void setINSDelegate(CorbaServerRequestDispatcher var1) {
      synchronized(this) {
         this.checkShutdownState();
      }

      synchronized(this.resolverLock) {
         this.insNamingDelegate = var1;
      }
   }

   public TaggedComponentFactoryFinder getTaggedComponentFactoryFinder() {
      synchronized(this) {
         this.checkShutdownState();
      }

      return this.taggedComponentFactoryFinder;
   }

   public IdentifiableFactoryFinder getTaggedProfileFactoryFinder() {
      synchronized(this) {
         this.checkShutdownState();
      }

      return this.taggedProfileFactoryFinder;
   }

   public IdentifiableFactoryFinder getTaggedProfileTemplateFactoryFinder() {
      synchronized(this) {
         this.checkShutdownState();
      }

      return this.taggedProfileTemplateFactoryFinder;
   }

   public ObjectKeyFactory getObjectKeyFactory() {
      synchronized(this) {
         this.checkShutdownState();
      }

      synchronized(this.objectKeyFactoryAccessLock) {
         return this.objectKeyFactory;
      }
   }

   public void setObjectKeyFactory(ObjectKeyFactory var1) {
      synchronized(this) {
         this.checkShutdownState();
      }

      synchronized(this.objectKeyFactoryAccessLock) {
         this.objectKeyFactory = var1;
      }
   }

   public TransportManager getTransportManager() {
      synchronized(this.transportManagerAccessorLock) {
         if (this.transportManager == null) {
            this.transportManager = new CorbaTransportManagerImpl(this);
         }

         return this.transportManager;
      }
   }

   public CorbaTransportManager getCorbaTransportManager() {
      return (CorbaTransportManager)this.getTransportManager();
   }

   public LegacyServerSocketManager getLegacyServerSocketManager() {
      synchronized(this) {
         this.checkShutdownState();
      }

      synchronized(this.legacyServerSocketManagerAccessLock) {
         if (this.legacyServerSocketManager == null) {
            this.legacyServerSocketManager = new LegacyServerSocketManagerImpl(this);
         }

         return this.legacyServerSocketManager;
      }
   }

   public void setThreadPoolManager(ThreadPoolManager var1) {
      synchronized(this) {
         this.checkShutdownState();
      }

      synchronized(this.threadPoolManagerAccessLock) {
         this.threadpoolMgr = var1;
      }
   }

   public ThreadPoolManager getThreadPoolManager() {
      synchronized(this) {
         this.checkShutdownState();
      }

      synchronized(this.threadPoolManagerAccessLock) {
         if (this.threadpoolMgr == null) {
            this.threadpoolMgr = new ThreadPoolManagerImpl();
            this.orbOwnsThreadPoolManager = true;
         }

         return this.threadpoolMgr;
      }
   }

   public CopierManager getCopierManager() {
      synchronized(this) {
         this.checkShutdownState();
      }

      return this.copierManager;
   }

   public void validateIORClass(String var1) {
      if (this.iorTypeCheckRegistry != null && !this.iorTypeCheckRegistry.isValidIORType(var1)) {
         throw ORBUtilSystemException.get(this, "oa.ior").badStringifiedIor();
      }
   }

   private static class ConfigParser extends ParserImplBase {
      public Class configurator;

      private ConfigParser() {
         this.configurator = ORBConfiguratorImpl.class;
      }

      public PropertyParser makeParser() {
         PropertyParser var1 = new PropertyParser();
         var1.add("com.sun.CORBA.ORBConfigurator", OperationFactory.classAction(), "configurator");
         return var1;
      }

      // $FF: synthetic method
      ConfigParser(Object var1) {
         this();
      }
   }
}
