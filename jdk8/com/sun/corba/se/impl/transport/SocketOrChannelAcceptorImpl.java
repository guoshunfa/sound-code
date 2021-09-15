package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.encoding.CDRInputObject;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.EventHandler;
import com.sun.corba.se.pept.transport.InboundConnectionCache;
import com.sun.corba.se.pept.transport.Selector;
import com.sun.corba.se.spi.extension.RequestPartitioningPolicy;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.ior.iiop.AlternateIIOPAddressComponent;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.transport.CorbaAcceptor;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.SocketInfo;
import com.sun.corba.se.spi.transport.SocketOrChannelAcceptor;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectableChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import sun.corba.OutputStreamFactory;

public class SocketOrChannelAcceptorImpl extends EventHandlerBase implements CorbaAcceptor, SocketOrChannelAcceptor, Work, SocketInfo, LegacyServerSocketEndPointInfo {
   protected ServerSocketChannel serverSocketChannel;
   protected ServerSocket serverSocket;
   protected int port;
   protected long enqueueTime;
   protected boolean initialized;
   protected ORBUtilSystemException wrapper;
   protected InboundConnectionCache connectionCache;
   protected String type;
   protected String name;
   protected String hostname;
   protected int locatorPort;

   public SocketOrChannelAcceptorImpl(ORB var1) {
      this.type = "";
      this.name = "";
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.transport");
      this.setWork(this);
      this.initialized = false;
      this.hostname = var1.getORBData().getORBServerHost();
      this.name = "NO_NAME";
      this.locatorPort = -1;
   }

   public SocketOrChannelAcceptorImpl(ORB var1, int var2) {
      this(var1);
      this.port = var2;
   }

   public SocketOrChannelAcceptorImpl(ORB var1, int var2, String var3, String var4) {
      this(var1, var2);
      this.name = var3;
      this.type = var4;
   }

   public boolean initialize() {
      if (this.initialized) {
         return false;
      } else {
         if (this.orb.transportDebugFlag) {
            this.dprint(".initialize: " + this);
         }

         InetSocketAddress var1 = null;

         try {
            if (this.orb.getORBData().getListenOnAllInterfaces().equals("com.sun.CORBA.INTERNAL USE ONLY: listen on all interfaces")) {
               var1 = new InetSocketAddress(this.port);
            } else {
               String var2 = this.orb.getORBData().getORBServerHost();
               var1 = new InetSocketAddress(var2, this.port);
            }

            this.serverSocket = this.orb.getORBData().getSocketFactory().createServerSocket(this.type, var1);
            this.internalInitialize();
         } catch (Throwable var3) {
            throw this.wrapper.createListenerFailed((Throwable)var3, Integer.toString(this.port));
         }

         this.initialized = true;
         return true;
      }
   }

   protected void internalInitialize() throws Exception {
      this.port = this.serverSocket.getLocalPort();
      this.orb.getCorbaTransportManager().getInboundConnectionCache(this);
      this.serverSocketChannel = this.serverSocket.getChannel();
      if (this.serverSocketChannel != null) {
         this.setUseSelectThreadToWait(this.orb.getORBData().acceptorSocketUseSelectThreadToWait());
         this.serverSocketChannel.configureBlocking(!this.orb.getORBData().acceptorSocketUseSelectThreadToWait());
      } else {
         this.setUseSelectThreadToWait(false);
      }

      this.setUseWorkerThreadForEvent(this.orb.getORBData().acceptorSocketUseWorkerThreadForEvent());
   }

   public boolean initialized() {
      return this.initialized;
   }

   public String getConnectionCacheType() {
      return this.getClass().toString();
   }

   public void setConnectionCache(InboundConnectionCache var1) {
      this.connectionCache = var1;
   }

   public InboundConnectionCache getConnectionCache() {
      return this.connectionCache;
   }

   public boolean shouldRegisterAcceptEvent() {
      return true;
   }

   public void accept() {
      Selector var2;
      try {
         SocketChannel var1 = null;
         var2 = null;
         Socket var6;
         if (this.serverSocketChannel == null) {
            var6 = this.serverSocket.accept();
         } else {
            var1 = this.serverSocketChannel.accept();
            var6 = var1.socket();
         }

         this.orb.getORBData().getSocketFactory().setAcceptedSocketOptions(this, this.serverSocket, var6);
         if (this.orb.transportDebugFlag) {
            this.dprint(".accept: " + (this.serverSocketChannel == null ? this.serverSocket.toString() : this.serverSocketChannel.toString()));
         }

         SocketOrChannelConnectionImpl var3 = new SocketOrChannelConnectionImpl(this.orb, this, var6);
         if (this.orb.transportDebugFlag) {
            this.dprint(".accept: new: " + var3);
         }

         this.getConnectionCache().stampTime(var3);
         this.getConnectionCache().put(this, var3);
         if (var3.shouldRegisterServerReadEvent()) {
            Selector var4 = this.orb.getTransportManager().getSelector(0);
            if (var4 != null) {
               if (this.orb.transportDebugFlag) {
                  this.dprint(".accept: registerForEvent: " + var3);
               }

               var4.registerForEvent(var3.getEventHandler());
            }
         }

         this.getConnectionCache().reclaim();
      } catch (IOException var5) {
         if (this.orb.transportDebugFlag) {
            this.dprint(".accept:", var5);
         }

         var2 = this.orb.getTransportManager().getSelector(0);
         if (var2 != null) {
            var2.unregisterForEvent(this);
            var2.registerForEvent(this);
         }
      }

   }

   public void close() {
      try {
         if (this.orb.transportDebugFlag) {
            this.dprint(".close->:");
         }

         Selector var1 = this.orb.getTransportManager().getSelector(0);
         if (var1 != null) {
            var1.unregisterForEvent(this);
         }

         if (this.serverSocketChannel != null) {
            this.serverSocketChannel.close();
         }

         if (this.serverSocket != null) {
            this.serverSocket.close();
         }
      } catch (IOException var5) {
         if (this.orb.transportDebugFlag) {
            this.dprint(".close:", var5);
         }
      } finally {
         if (this.orb.transportDebugFlag) {
            this.dprint(".close<-:");
         }

      }

   }

   public EventHandler getEventHandler() {
      return this;
   }

   public String getObjectAdapterId() {
      return null;
   }

   public String getObjectAdapterManagerId() {
      return null;
   }

   public void addToIORTemplate(IORTemplate var1, Policies var2, String var3) {
      Iterator var4 = var1.iteratorById(0);
      String var5 = this.orb.getORBData().getORBServerHost();
      if (var4.hasNext()) {
         IIOPAddress var6 = IIOPFactories.makeIIOPAddress(this.orb, var5, this.port);
         AlternateIIOPAddressComponent var7 = IIOPFactories.makeAlternateIIOPAddressComponent(var6);

         while(var4.hasNext()) {
            TaggedProfileTemplate var8 = (TaggedProfileTemplate)var4.next();
            var8.add(var7);
         }
      } else {
         GIOPVersion var11 = this.orb.getORBData().getGIOPVersion();
         int var12;
         if (var2.forceZeroPort()) {
            var12 = 0;
         } else if (var2.isTransient()) {
            var12 = this.port;
         } else {
            var12 = this.orb.getLegacyServerSocketManager().legacyGetPersistentServerPort("IIOP_CLEAR_TEXT");
         }

         IIOPAddress var13 = IIOPFactories.makeIIOPAddress(this.orb, var5, var12);
         IIOPProfileTemplate var9 = IIOPFactories.makeIIOPProfileTemplate(this.orb, var11, var13);
         if (var11.supportsIORIIOPProfileComponents()) {
            var9.add(IIOPFactories.makeCodeSetsComponent(this.orb));
            var9.add(IIOPFactories.makeMaxStreamFormatVersionComponent());
            RequestPartitioningPolicy var10 = (RequestPartitioningPolicy)var2.get_effective_policy(1398079491);
            if (var10 != null) {
               var9.add(IIOPFactories.makeRequestPartitioningComponent(var10.getValue()));
            }

            if (var3 != null && var3 != "") {
               var9.add(IIOPFactories.makeJavaCodebaseComponent(var3));
            }

            if (this.orb.getORBData().isJavaSerializationEnabled()) {
               var9.add(IIOPFactories.makeJavaSerializationComponent());
            }
         }

         var1.add(var9);
      }

   }

   public String getMonitoringName() {
      return "AcceptedConnections";
   }

   public SelectableChannel getChannel() {
      return this.serverSocketChannel;
   }

   public int getInterestOps() {
      return 16;
   }

   public Acceptor getAcceptor() {
      return this;
   }

   public Connection getConnection() {
      throw new RuntimeException("Should not happen.");
   }

   public void doWork() {
      boolean var9 = false;

      Selector var1;
      label207: {
         label208: {
            label209: {
               try {
                  var9 = true;
                  if (this.orb.transportDebugFlag) {
                     this.dprint(".doWork->: " + this);
                  }

                  if (this.selectionKey.isAcceptable()) {
                     this.accept();
                     var9 = false;
                  } else if (this.orb.transportDebugFlag) {
                     this.dprint(".doWork: ! selectionKey.isAcceptable: " + this);
                     var9 = false;
                  } else {
                     var9 = false;
                  }
                  break label207;
               } catch (SecurityException var10) {
                  if (this.orb.transportDebugFlag) {
                     this.dprint(".doWork: ignoring SecurityException: " + var10 + " " + this);
                  }

                  String var2 = ORBUtility.getClassSecurityInfo(this.getClass());
                  this.wrapper.securityExceptionInAccept(var10, var2);
                  var9 = false;
               } catch (Exception var11) {
                  if (this.orb.transportDebugFlag) {
                     this.dprint(".doWork: ignoring Exception: " + var11 + " " + this);
                  }

                  this.wrapper.exceptionInAccept(var11);
                  var9 = false;
                  break label209;
               } catch (Throwable var12) {
                  if (this.orb.transportDebugFlag) {
                     this.dprint(".doWork: ignoring Throwable: " + var12 + " " + this);
                     var9 = false;
                     break label208;
                  }

                  var9 = false;
                  break label208;
               } finally {
                  if (var9) {
                     Selector var4 = this.orb.getTransportManager().getSelector(0);
                     if (var4 != null) {
                        var4.registerInterestOps(this);
                     }

                     if (this.orb.transportDebugFlag) {
                        this.dprint(".doWork<-:" + this);
                     }

                  }
               }

               var1 = this.orb.getTransportManager().getSelector(0);
               if (var1 != null) {
                  var1.registerInterestOps(this);
               }

               if (this.orb.transportDebugFlag) {
                  this.dprint(".doWork<-:" + this);
               }

               return;
            }

            var1 = this.orb.getTransportManager().getSelector(0);
            if (var1 != null) {
               var1.registerInterestOps(this);
            }

            if (this.orb.transportDebugFlag) {
               this.dprint(".doWork<-:" + this);
            }

            return;
         }

         var1 = this.orb.getTransportManager().getSelector(0);
         if (var1 != null) {
            var1.registerInterestOps(this);
         }

         if (this.orb.transportDebugFlag) {
            this.dprint(".doWork<-:" + this);
         }

         return;
      }

      var1 = this.orb.getTransportManager().getSelector(0);
      if (var1 != null) {
         var1.registerInterestOps(this);
      }

      if (this.orb.transportDebugFlag) {
         this.dprint(".doWork<-:" + this);
      }

   }

   public void setEnqueueTime(long var1) {
      this.enqueueTime = var1;
   }

   public long getEnqueueTime() {
      return this.enqueueTime;
   }

   public MessageMediator createMessageMediator(Broker var1, Connection var2) {
      SocketOrChannelContactInfoImpl var3 = new SocketOrChannelContactInfoImpl();
      return var3.createMessageMediator(var1, var2);
   }

   public MessageMediator finishCreatingMessageMediator(Broker var1, Connection var2, MessageMediator var3) {
      SocketOrChannelContactInfoImpl var4 = new SocketOrChannelContactInfoImpl();
      return var4.finishCreatingMessageMediator(var1, var2, var3);
   }

   public InputObject createInputObject(Broker var1, MessageMediator var2) {
      CorbaMessageMediator var3 = (CorbaMessageMediator)var2;
      return new CDRInputObject((ORB)var1, (CorbaConnection)var2.getConnection(), var3.getDispatchBuffer(), var3.getDispatchHeader());
   }

   public OutputObject createOutputObject(Broker var1, MessageMediator var2) {
      CorbaMessageMediator var3 = (CorbaMessageMediator)var2;
      return OutputStreamFactory.newCDROutputObject((ORB)var1, var3, var3.getReplyHeader(), var3.getStreamFormatVersion());
   }

   public ServerSocket getServerSocket() {
      return this.serverSocket;
   }

   public String toString() {
      String var1;
      if (this.serverSocketChannel == null) {
         if (this.serverSocket == null) {
            var1 = "(not initialized)";
         } else {
            var1 = this.serverSocket.toString();
         }
      } else {
         var1 = this.serverSocketChannel.toString();
      }

      return this.toStringName() + "[" + var1 + " " + this.type + " " + this.shouldUseSelectThreadToWait() + " " + this.shouldUseWorkerThreadForEvent() + "]";
   }

   protected String toStringName() {
      return "SocketOrChannelAcceptorImpl";
   }

   protected void dprint(String var1) {
      ORBUtility.dprint(this.toStringName(), var1);
   }

   protected void dprint(String var1, Throwable var2) {
      this.dprint(var1);
      var2.printStackTrace(System.out);
   }

   public String getType() {
      return this.type;
   }

   public String getHostName() {
      return this.hostname;
   }

   public String getHost() {
      return this.hostname;
   }

   public int getPort() {
      return this.port;
   }

   public int getLocatorPort() {
      return this.locatorPort;
   }

   public void setLocatorPort(int var1) {
      this.locatorPort = var1;
   }

   public String getName() {
      String var1 = this.name.equals("NO_NAME") ? this.toString() : this.name;
      return var1;
   }
}
