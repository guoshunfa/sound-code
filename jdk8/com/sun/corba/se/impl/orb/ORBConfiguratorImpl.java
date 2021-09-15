package com.sun.corba.se.impl.orb;

import com.sun.corba.se.impl.dynamicany.DynAnyFactoryImpl;
import com.sun.corba.se.impl.legacy.connection.SocketFactoryAcceptorImpl;
import com.sun.corba.se.impl.legacy.connection.SocketFactoryContactInfoListImpl;
import com.sun.corba.se.impl.legacy.connection.USLPort;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBConstants;
import com.sun.corba.se.impl.transport.SocketOrChannelAcceptorImpl;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import com.sun.corba.se.spi.activation.EndPointInfo;
import com.sun.corba.se.spi.activation.Locator;
import com.sun.corba.se.spi.activation.LocatorHelper;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.copyobject.CopyobjectDefaults;
import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import com.sun.corba.se.spi.legacy.connection.ORBSocketFactory;
import com.sun.corba.se.spi.oa.OADefault;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.DataCollector;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBConfigurator;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.OperationFactory;
import com.sun.corba.se.spi.orb.ParserImplBase;
import com.sun.corba.se.spi.orb.PropertyParser;
import com.sun.corba.se.spi.orbutil.closure.Closure;
import com.sun.corba.se.spi.orbutil.closure.ClosureFactory;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcherFactory;
import com.sun.corba.se.spi.protocol.RequestDispatcherDefault;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import com.sun.corba.se.spi.resolver.LocalResolver;
import com.sun.corba.se.spi.resolver.Resolver;
import com.sun.corba.se.spi.resolver.ResolverDefault;
import com.sun.corba.se.spi.servicecontext.CodeSetServiceContext;
import com.sun.corba.se.spi.servicecontext.MaxStreamFormatVersionServiceContext;
import com.sun.corba.se.spi.servicecontext.ORBVersionServiceContext;
import com.sun.corba.se.spi.servicecontext.SendingContextServiceContext;
import com.sun.corba.se.spi.servicecontext.ServiceContextRegistry;
import com.sun.corba.se.spi.servicecontext.UEInfoServiceContext;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
import com.sun.corba.se.spi.transport.TransportDefault;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.Iterator;
import org.omg.CORBA.CompletionStatus;

public class ORBConfiguratorImpl implements ORBConfigurator {
   private ORBUtilSystemException wrapper;
   private static final int ORB_STREAM = 0;

   public void configure(DataCollector var1, ORB var2) {
      this.wrapper = ORBUtilSystemException.get(var2, "orb.lifecycle");
      this.initObjectCopiers(var2);
      this.initIORFinders(var2);
      var2.setClientDelegateFactory(TransportDefault.makeClientDelegateFactory(var2));
      this.initializeTransport(var2);
      this.initializeNaming(var2);
      this.initServiceContextRegistry(var2);
      this.initRequestDispatcherRegistry(var2);
      this.registerInitialReferences(var2);
      this.persistentServerInitialization(var2);
      this.runUserConfigurators(var1, var2);
   }

   private void runUserConfigurators(DataCollector var1, ORB var2) {
      ORBConfiguratorImpl.ConfigParser var3 = new ORBConfiguratorImpl.ConfigParser();
      var3.init(var1);
      if (var3.userConfigurators != null) {
         for(int var4 = 0; var4 < var3.userConfigurators.length; ++var4) {
            Class var5 = var3.userConfigurators[var4];

            try {
               ORBConfigurator var6 = (ORBConfigurator)((ORBConfigurator)var5.newInstance());
               var6.configure(var1, var2);
            } catch (Exception var7) {
            }
         }
      }

   }

   private void persistentServerInitialization(ORB var1) {
      ORBData var2 = var1.getORBData();
      if (var2.getServerIsORBActivated()) {
         try {
            Locator var3 = LocatorHelper.narrow(var1.resolve_initial_references("ServerLocator"));
            Activator var4 = ActivatorHelper.narrow(var1.resolve_initial_references("ServerActivator"));
            Collection var5 = var1.getCorbaTransportManager().getAcceptors((String)null, (ObjectAdapterId)null);
            EndPointInfo[] var6 = new EndPointInfo[var5.size()];
            Iterator var7 = var5.iterator();
            int var8 = 0;

            while(var7.hasNext()) {
               Object var9 = var7.next();
               if (var9 instanceof LegacyServerSocketEndPointInfo) {
                  LegacyServerSocketEndPointInfo var10 = (LegacyServerSocketEndPointInfo)var9;
                  int var11 = var3.getEndpoint(var10.getType());
                  if (var11 == -1) {
                     var11 = var3.getEndpoint("IIOP_CLEAR_TEXT");
                     if (var11 == -1) {
                        throw new Exception("ORBD must support IIOP_CLEAR_TEXT");
                     }
                  }

                  var10.setLocatorPort(var11);
                  var6[var8++] = new EndPointInfo(var10.getType(), var10.getPort());
               }
            }

            var4.registerEndpoints(var2.getPersistentServerId(), var2.getORBId(), var6);
         } catch (Exception var12) {
            throw this.wrapper.persistentServerInitError(CompletionStatus.COMPLETED_MAYBE, var12);
         }
      }

   }

   private void initializeTransport(final ORB var1) {
      ORBData var2 = var1.getORBData();
      CorbaContactInfoListFactory var3 = var2.getCorbaContactInfoListFactory();
      Acceptor[] var4 = var2.getAcceptors();
      ORBSocketFactory var5 = var2.getLegacySocketFactory();
      USLPort[] var6 = var2.getUserSpecifiedListenPorts();
      this.setLegacySocketFactoryORB(var1, var5);
      if (var5 != null && var3 != null) {
         throw this.wrapper.socketFactoryAndContactInfoListAtSameTime();
      } else if (var4.length != 0 && var5 != null) {
         throw this.wrapper.acceptorsAndLegacySocketFactoryAtSameTime();
      } else {
         var2.getSocketFactory().setORB(var1);
         if (var5 != null) {
            var3 = new CorbaContactInfoListFactory() {
               public void setORB(ORB var1x) {
               }

               public CorbaContactInfoList create(IOR var1x) {
                  return new SocketFactoryContactInfoListImpl(var1, var1x);
               }
            };
         } else if (var3 != null) {
            var3.setORB(var1);
         } else {
            var3 = TransportDefault.makeCorbaContactInfoListFactory(var1);
         }

         var1.setCorbaContactInfoListFactory(var3);
         int var7 = -1;
         if (var2.getORBServerPort() != 0) {
            var7 = var2.getORBServerPort();
         } else if (var2.getPersistentPortInitialized()) {
            var7 = var2.getPersistentServerPort();
         } else if (var4.length == 0) {
            var7 = 0;
         }

         if (var7 != -1) {
            this.createAndRegisterAcceptor(var1, var5, var7, "DEFAULT_ENDPOINT", "IIOP_CLEAR_TEXT");
         }

         for(int var8 = 0; var8 < var4.length; ++var8) {
            var1.getCorbaTransportManager().registerAcceptor(var4[var8]);
         }

         USLPort[] var10 = var2.getUserSpecifiedListenPorts();
         if (var10 != null) {
            for(int var9 = 0; var9 < var10.length; ++var9) {
               this.createAndRegisterAcceptor(var1, var5, var10[var9].getPort(), "NO_NAME", var10[var9].getType());
            }
         }

      }
   }

   private void createAndRegisterAcceptor(ORB var1, ORBSocketFactory var2, int var3, String var4, String var5) {
      Object var6;
      if (var2 == null) {
         var6 = new SocketOrChannelAcceptorImpl(var1, var3, var4, var5);
      } else {
         var6 = new SocketFactoryAcceptorImpl(var1, var3, var4, var5);
      }

      var1.getTransportManager().registerAcceptor((Acceptor)var6);
   }

   private void setLegacySocketFactoryORB(final ORB var1, final ORBSocketFactory var2) {
      if (var2 != null) {
         try {
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
               public Object run() throws InstantiationException, IllegalAccessException {
                  RuntimeException var2x;
                  try {
                     Class[] var1x = new Class[]{ORB.class};
                     Method var7 = var2.getClass().getMethod("setORB", var1x);
                     Object[] var3 = new Object[]{var1};
                     var7.invoke(var2, var3);
                  } catch (NoSuchMethodException var4) {
                  } catch (IllegalAccessException var5) {
                     var2x = new RuntimeException();
                     var2x.initCause(var5);
                     throw var2x;
                  } catch (InvocationTargetException var6) {
                     var2x = new RuntimeException();
                     var2x.initCause(var6);
                     throw var2x;
                  }

                  return null;
               }
            });
         } catch (Throwable var4) {
            throw this.wrapper.unableToSetSocketFactoryOrb(var4);
         }
      }
   }

   private void initializeNaming(ORB var1) {
      LocalResolver var2 = ResolverDefault.makeLocalResolver();
      var1.setLocalResolver(var2);
      Resolver var3 = ResolverDefault.makeBootstrapResolver(var1, var1.getORBData().getORBInitialHost(), var1.getORBData().getORBInitialPort());
      Operation var4 = ResolverDefault.makeINSURLOperation(var1, var3);
      var1.setURLOperation(var4);
      Resolver var5 = ResolverDefault.makeORBInitRefResolver(var4, var1.getORBData().getORBInitialReferences());
      Resolver var6 = ResolverDefault.makeORBDefaultInitRefResolver(var4, var1.getORBData().getORBDefaultInitialReference());
      Resolver var7 = ResolverDefault.makeCompositeResolver(var2, ResolverDefault.makeCompositeResolver(var5, ResolverDefault.makeCompositeResolver(var6, var3)));
      var1.setResolver(var7);
   }

   private void initServiceContextRegistry(ORB var1) {
      ServiceContextRegistry var2 = var1.getServiceContextRegistry();
      var2.register(UEInfoServiceContext.class);
      var2.register(CodeSetServiceContext.class);
      var2.register(SendingContextServiceContext.class);
      var2.register(ORBVersionServiceContext.class);
      var2.register(MaxStreamFormatVersionServiceContext.class);
   }

   private void registerInitialReferences(final ORB var1) {
      Closure var2 = new Closure() {
         public Object evaluate() {
            return new DynAnyFactoryImpl(var1);
         }
      };
      Closure var3 = ClosureFactory.makeFuture(var2);
      var1.getLocalResolver().register("DynAnyFactory", var3);
   }

   private void initObjectCopiers(ORB var1) {
      ObjectCopierFactory var2 = CopyobjectDefaults.makeORBStreamObjectCopierFactory(var1);
      CopierManager var3 = var1.getCopierManager();
      var3.setDefaultId(0);
      var3.registerObjectCopierFactory(var2, 0);
   }

   private void initIORFinders(ORB var1) {
      IdentifiableFactoryFinder var2 = var1.getTaggedProfileFactoryFinder();
      var2.registerFactory(IIOPFactories.makeIIOPProfileFactory());
      IdentifiableFactoryFinder var3 = var1.getTaggedProfileTemplateFactoryFinder();
      var3.registerFactory(IIOPFactories.makeIIOPProfileTemplateFactory());
      TaggedComponentFactoryFinder var4 = var1.getTaggedComponentFactoryFinder();
      var4.registerFactory(IIOPFactories.makeCodeSetsComponentFactory());
      var4.registerFactory(IIOPFactories.makeJavaCodebaseComponentFactory());
      var4.registerFactory(IIOPFactories.makeORBTypeComponentFactory());
      var4.registerFactory(IIOPFactories.makeMaxStreamFormatVersionComponentFactory());
      var4.registerFactory(IIOPFactories.makeAlternateIIOPAddressComponentFactory());
      var4.registerFactory(IIOPFactories.makeRequestPartitioningComponentFactory());
      var4.registerFactory(IIOPFactories.makeJavaSerializationComponentFactory());
      IORFactories.registerValueFactories(var1);
      var1.setObjectKeyFactory(IORFactories.makeObjectKeyFactory(var1));
   }

   private void initRequestDispatcherRegistry(ORB var1) {
      RequestDispatcherRegistry var2 = var1.getRequestDispatcherRegistry();
      ClientRequestDispatcher var3 = RequestDispatcherDefault.makeClientRequestDispatcher();
      var2.registerClientRequestDispatcher(var3, 2);
      var2.registerClientRequestDispatcher(var3, 32);
      var2.registerClientRequestDispatcher(var3, ORBConstants.PERSISTENT_SCID);
      var2.registerClientRequestDispatcher(var3, 36);
      var2.registerClientRequestDispatcher(var3, ORBConstants.SC_PERSISTENT_SCID);
      var2.registerClientRequestDispatcher(var3, 40);
      var2.registerClientRequestDispatcher(var3, ORBConstants.IISC_PERSISTENT_SCID);
      var2.registerClientRequestDispatcher(var3, 44);
      var2.registerClientRequestDispatcher(var3, ORBConstants.MINSC_PERSISTENT_SCID);
      CorbaServerRequestDispatcher var4 = RequestDispatcherDefault.makeServerRequestDispatcher(var1);
      var2.registerServerRequestDispatcher(var4, 2);
      var2.registerServerRequestDispatcher(var4, 32);
      var2.registerServerRequestDispatcher(var4, ORBConstants.PERSISTENT_SCID);
      var2.registerServerRequestDispatcher(var4, 36);
      var2.registerServerRequestDispatcher(var4, ORBConstants.SC_PERSISTENT_SCID);
      var2.registerServerRequestDispatcher(var4, 40);
      var2.registerServerRequestDispatcher(var4, ORBConstants.IISC_PERSISTENT_SCID);
      var2.registerServerRequestDispatcher(var4, 44);
      var2.registerServerRequestDispatcher(var4, ORBConstants.MINSC_PERSISTENT_SCID);
      var1.setINSDelegate(RequestDispatcherDefault.makeINSServerRequestDispatcher(var1));
      LocalClientRequestDispatcherFactory var5 = RequestDispatcherDefault.makeJIDLLocalClientRequestDispatcherFactory(var1);
      var2.registerLocalClientRequestDispatcherFactory(var5, 2);
      var5 = RequestDispatcherDefault.makePOALocalClientRequestDispatcherFactory(var1);
      var2.registerLocalClientRequestDispatcherFactory(var5, 32);
      var2.registerLocalClientRequestDispatcherFactory(var5, ORBConstants.PERSISTENT_SCID);
      var5 = RequestDispatcherDefault.makeFullServantCacheLocalClientRequestDispatcherFactory(var1);
      var2.registerLocalClientRequestDispatcherFactory(var5, 36);
      var2.registerLocalClientRequestDispatcherFactory(var5, ORBConstants.SC_PERSISTENT_SCID);
      var5 = RequestDispatcherDefault.makeInfoOnlyServantCacheLocalClientRequestDispatcherFactory(var1);
      var2.registerLocalClientRequestDispatcherFactory(var5, 40);
      var2.registerLocalClientRequestDispatcherFactory(var5, ORBConstants.IISC_PERSISTENT_SCID);
      var5 = RequestDispatcherDefault.makeMinimalServantCacheLocalClientRequestDispatcherFactory(var1);
      var2.registerLocalClientRequestDispatcherFactory(var5, 44);
      var2.registerLocalClientRequestDispatcherFactory(var5, ORBConstants.MINSC_PERSISTENT_SCID);
      CorbaServerRequestDispatcher var6 = RequestDispatcherDefault.makeBootstrapServerRequestDispatcher(var1);
      var2.registerServerRequestDispatcher(var6, "INIT");
      var2.registerServerRequestDispatcher(var6, "TINI");
      ObjectAdapterFactory var7 = OADefault.makeTOAFactory(var1);
      var2.registerObjectAdapterFactory(var7, 2);
      var7 = OADefault.makePOAFactory(var1);
      var2.registerObjectAdapterFactory(var7, 32);
      var2.registerObjectAdapterFactory(var7, ORBConstants.PERSISTENT_SCID);
      var2.registerObjectAdapterFactory(var7, 36);
      var2.registerObjectAdapterFactory(var7, ORBConstants.SC_PERSISTENT_SCID);
      var2.registerObjectAdapterFactory(var7, 40);
      var2.registerObjectAdapterFactory(var7, ORBConstants.IISC_PERSISTENT_SCID);
      var2.registerObjectAdapterFactory(var7, 44);
      var2.registerObjectAdapterFactory(var7, ORBConstants.MINSC_PERSISTENT_SCID);
   }

   public static class ConfigParser extends ParserImplBase {
      public Class[] userConfigurators = null;

      public PropertyParser makeParser() {
         PropertyParser var1 = new PropertyParser();
         Operation var2 = OperationFactory.compose(OperationFactory.suffixAction(), OperationFactory.classAction());
         var1.addPrefix("com.sun.CORBA.ORBUserConfigurators", var2, "userConfigurators", Class.class);
         return var1;
      }
   }
}
