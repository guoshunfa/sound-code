package sun.rmi.transport.tcp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.rmi.server.LogStream;
import java.rmi.server.RMIFailureHandler;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UID;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import sun.rmi.runtime.Log;
import sun.rmi.runtime.NewThreadAction;
import sun.rmi.transport.Connection;
import sun.rmi.transport.DGCAckHandler;
import sun.rmi.transport.Endpoint;
import sun.rmi.transport.StreamRemoteCall;
import sun.rmi.transport.Target;
import sun.rmi.transport.Transport;
import sun.rmi.transport.proxy.HttpReceiveSocket;
import sun.security.action.GetIntegerAction;
import sun.security.action.GetLongAction;
import sun.security.action.GetPropertyAction;

public class TCPTransport extends Transport {
   static final Log tcpLog = Log.getLog("sun.rmi.transport.tcp", "tcp", LogStream.parseLevel((String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.rmi.transport.tcp.logLevel")))));
   private static final int maxConnectionThreads = (Integer)AccessController.doPrivileged((PrivilegedAction)(new GetIntegerAction("sun.rmi.transport.tcp.maxConnectionThreads", Integer.MAX_VALUE)));
   private static final long threadKeepAliveTime = (Long)AccessController.doPrivileged((PrivilegedAction)(new GetLongAction("sun.rmi.transport.tcp.threadKeepAliveTime", 60000L)));
   private static final ExecutorService connectionThreadPool;
   private static final boolean disableIncomingHttp;
   private static final AtomicInteger connectionCount;
   private static final ThreadLocal<TCPTransport.ConnectionHandler> threadConnectionHandler;
   private static final AccessControlContext NOPERMS_ACC;
   private final LinkedList<TCPEndpoint> epList;
   private int exportCount = 0;
   private ServerSocket server = null;
   private final Map<TCPEndpoint, Reference<TCPChannel>> channelTable = new WeakHashMap();
   static final RMISocketFactory defaultSocketFactory;
   private static final int connectionReadTimeout;

   TCPTransport(LinkedList<TCPEndpoint> var1) {
      this.epList = var1;
      if (tcpLog.isLoggable(Log.BRIEF)) {
         tcpLog.log(Log.BRIEF, "Version = 2, ep = " + this.getEndpoint());
      }

   }

   public void shedConnectionCaches() {
      ArrayList var1;
      synchronized(this.channelTable) {
         var1 = new ArrayList(this.channelTable.values().size());
         Iterator var3 = this.channelTable.values().iterator();

         while(true) {
            if (!var3.hasNext()) {
               break;
            }

            Reference var4 = (Reference)var3.next();
            TCPChannel var5 = (TCPChannel)var4.get();
            if (var5 != null) {
               var1.add(var5);
            }
         }
      }

      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         TCPChannel var8 = (TCPChannel)var2.next();
         var8.shedCache();
      }

   }

   public TCPChannel getChannel(Endpoint var1) {
      TCPChannel var2 = null;
      if (var1 instanceof TCPEndpoint) {
         synchronized(this.channelTable) {
            Reference var4 = (Reference)this.channelTable.get(var1);
            if (var4 != null) {
               var2 = (TCPChannel)var4.get();
            }

            if (var2 == null) {
               TCPEndpoint var5 = (TCPEndpoint)var1;
               var2 = new TCPChannel(this, var5);
               this.channelTable.put(var5, new WeakReference(var2));
            }
         }
      }

      return var2;
   }

   public void free(Endpoint var1) {
      if (var1 instanceof TCPEndpoint) {
         synchronized(this.channelTable) {
            Reference var3 = (Reference)this.channelTable.remove(var1);
            if (var3 != null) {
               TCPChannel var4 = (TCPChannel)var3.get();
               if (var4 != null) {
                  var4.shedCache();
               }
            }
         }
      }

   }

   public void exportObject(Target var1) throws RemoteException {
      synchronized(this) {
         this.listen();
         ++this.exportCount;
      }

      boolean var2 = false;
      boolean var12 = false;

      try {
         var12 = true;
         super.exportObject(var1);
         var2 = true;
         var12 = false;
      } finally {
         if (var12) {
            if (!var2) {
               synchronized(this) {
                  this.decrementExportCount();
               }
            }

         }
      }

      if (!var2) {
         synchronized(this) {
            this.decrementExportCount();
         }
      }

   }

   protected synchronized void targetUnexported() {
      this.decrementExportCount();
   }

   private void decrementExportCount() {
      assert Thread.holdsLock(this);

      --this.exportCount;
      if (this.exportCount == 0 && this.getEndpoint().getListenPort() != 0) {
         ServerSocket var1 = this.server;
         this.server = null;

         try {
            var1.close();
         } catch (IOException var3) {
         }
      }

   }

   protected void checkAcceptPermission(AccessControlContext var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         TCPTransport.ConnectionHandler var3 = (TCPTransport.ConnectionHandler)threadConnectionHandler.get();
         if (var3 == null) {
            throw new Error("checkAcceptPermission not in ConnectionHandler thread");
         } else {
            var3.checkAcceptPermission(var2, var1);
         }
      }
   }

   private TCPEndpoint getEndpoint() {
      synchronized(this.epList) {
         return (TCPEndpoint)this.epList.getLast();
      }
   }

   private void listen() throws RemoteException {
      assert Thread.holdsLock(this);

      TCPEndpoint var1 = this.getEndpoint();
      int var2 = var1.getPort();
      if (this.server == null) {
         if (tcpLog.isLoggable(Log.BRIEF)) {
            tcpLog.log(Log.BRIEF, "(port " + var2 + ") create server socket");
         }

         try {
            this.server = var1.newServerSocket();
            Thread var3 = (Thread)AccessController.doPrivileged((PrivilegedAction)(new NewThreadAction(new TCPTransport.AcceptLoop(this.server), "TCP Accept-" + var2, true)));
            var3.start();
         } catch (BindException var4) {
            throw new ExportException("Port already in use: " + var2, var4);
         } catch (IOException var5) {
            throw new ExportException("Listen failed on port: " + var2, var5);
         }
      } else {
         SecurityManager var6 = System.getSecurityManager();
         if (var6 != null) {
            var6.checkListen(var2);
         }
      }

   }

   private static void closeSocket(Socket var0) {
      try {
         var0.close();
      } catch (IOException var2) {
      }

   }

   void handleMessages(Connection var1, boolean var2) {
      int var3 = this.getEndpoint().getPort();

      try {
         DataInputStream var4 = new DataInputStream(var1.getInputStream());

         do {
            int var5 = var4.read();
            if (var5 == -1) {
               if (tcpLog.isLoggable(Log.BRIEF)) {
                  tcpLog.log(Log.BRIEF, "(port " + var3 + ") connection closed");
               }

               return;
            }

            if (tcpLog.isLoggable(Log.BRIEF)) {
               tcpLog.log(Log.BRIEF, "(port " + var3 + ") op = " + var5);
            }

            switch(var5) {
            case 80:
               StreamRemoteCall var6 = new StreamRemoteCall(var1);
               if (!this.serviceCall(var6)) {
                  return;
               }
               break;
            case 81:
            case 83:
            default:
               throw new IOException("unknown transport op " + var5);
            case 82:
               DataOutputStream var7 = new DataOutputStream(var1.getOutputStream());
               var7.writeByte(83);
               var1.releaseOutputStream();
               break;
            case 84:
               DGCAckHandler.received(UID.read(var4));
            }
         } while(var2);

      } catch (IOException var17) {
         if (tcpLog.isLoggable(Log.BRIEF)) {
            tcpLog.log(Log.BRIEF, "(port " + var3 + ") exception: ", var17);
         }

      } finally {
         try {
            var1.close();
         } catch (IOException var16) {
         }

      }
   }

   public static String getClientHost() throws ServerNotActiveException {
      TCPTransport.ConnectionHandler var0 = (TCPTransport.ConnectionHandler)threadConnectionHandler.get();
      if (var0 != null) {
         return var0.getClientHost();
      } else {
         throw new ServerNotActiveException("not in a remote call");
      }
   }

   static {
      connectionThreadPool = new ThreadPoolExecutor(0, maxConnectionThreads, threadKeepAliveTime, TimeUnit.MILLISECONDS, new SynchronousQueue(), new ThreadFactory() {
         public Thread newThread(Runnable var1) {
            return (Thread)AccessController.doPrivileged((PrivilegedAction)(new NewThreadAction(var1, "TCP Connection(idle)", true, true)));
         }
      });
      disableIncomingHttp = ((String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.rmi.server.disableIncomingHttp", "true")))).equalsIgnoreCase("true");
      connectionCount = new AtomicInteger(0);
      threadConnectionHandler = new ThreadLocal();
      Permissions var0 = new Permissions();
      ProtectionDomain[] var1 = new ProtectionDomain[]{new ProtectionDomain((CodeSource)null, var0)};
      NOPERMS_ACC = new AccessControlContext(var1);
      defaultSocketFactory = RMISocketFactory.getDefaultSocketFactory();
      connectionReadTimeout = (Integer)AccessController.doPrivileged((PrivilegedAction)(new GetIntegerAction("sun.rmi.transport.tcp.readTimeout", 7200000)));
   }

   private class ConnectionHandler implements Runnable {
      private static final int POST = 1347375956;
      private AccessControlContext okContext;
      private Map<AccessControlContext, Reference<AccessControlContext>> authCache;
      private SecurityManager cacheSecurityManager = null;
      private Socket socket;
      private String remoteHost;

      ConnectionHandler(Socket var2, String var3) {
         this.socket = var2;
         this.remoteHost = var3;
      }

      String getClientHost() {
         return this.remoteHost;
      }

      void checkAcceptPermission(SecurityManager var1, AccessControlContext var2) {
         if (var1 != this.cacheSecurityManager) {
            this.okContext = null;
            this.authCache = new WeakHashMap();
            this.cacheSecurityManager = var1;
         }

         if (!var2.equals(this.okContext) && !this.authCache.containsKey(var2)) {
            InetAddress var3 = this.socket.getInetAddress();
            String var4 = var3 != null ? var3.getHostAddress() : "*";
            var1.checkAccept(var4, this.socket.getPort());
            this.authCache.put(var2, new SoftReference(var2));
            this.okContext = var2;
         }
      }

      public void run() {
         Thread var1 = Thread.currentThread();
         String var2 = var1.getName();

         try {
            var1.setName("RMI TCP Connection(" + TCPTransport.connectionCount.incrementAndGet() + ")-" + this.remoteHost);
            AccessController.doPrivileged(() -> {
               this.run0();
               return null;
            }, TCPTransport.NOPERMS_ACC);
         } finally {
            var1.setName(var2);
         }

      }

      private void run0() {
         TCPEndpoint var1 = TCPTransport.this.getEndpoint();
         int var2 = var1.getPort();
         TCPTransport.threadConnectionHandler.set(this);

         try {
            this.socket.setTcpNoDelay(true);
         } catch (Exception var31) {
         }

         try {
            if (TCPTransport.connectionReadTimeout > 0) {
               this.socket.setSoTimeout(TCPTransport.connectionReadTimeout);
            }
         } catch (Exception var30) {
         }

         try {
            InputStream var3 = this.socket.getInputStream();
            Object var4 = var3.markSupported() ? var3 : new BufferedInputStream(var3);
            ((InputStream)var4).mark(4);
            DataInputStream var5 = new DataInputStream((InputStream)var4);
            int var6 = var5.readInt();
            if (var6 == 1347375956) {
               if (TCPTransport.disableIncomingHttp) {
                  throw new RemoteException("RMI over HTTP is disabled");
               }

               TCPTransport.tcpLog.log(Log.BRIEF, "decoding HTTP-wrapped call");
               ((InputStream)var4).reset();

               try {
                  this.socket = new HttpReceiveSocket(this.socket, (InputStream)var4, (OutputStream)null);
                  this.remoteHost = "0.0.0.0";
                  var3 = this.socket.getInputStream();
                  var4 = new BufferedInputStream(var3);
                  var5 = new DataInputStream((InputStream)var4);
                  var6 = var5.readInt();
               } catch (IOException var29) {
                  throw new RemoteException("Error HTTP-unwrapping call", var29);
               }
            }

            short var7 = var5.readShort();
            if (var6 == 1246907721 && var7 == 2) {
               OutputStream var8 = this.socket.getOutputStream();
               BufferedOutputStream var9 = new BufferedOutputStream(var8);
               DataOutputStream var10 = new DataOutputStream(var9);
               int var11 = this.socket.getPort();
               if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
                  TCPTransport.tcpLog.log(Log.BRIEF, "accepted socket from [" + this.remoteHost + ":" + var11 + "]");
               }

               byte var15 = var5.readByte();
               TCPEndpoint var12;
               TCPChannel var13;
               TCPConnection var14;
               switch(var15) {
               case 75:
                  var10.writeByte(78);
                  if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
                     TCPTransport.tcpLog.log(Log.VERBOSE, "(port " + var2 + ") suggesting " + this.remoteHost + ":" + var11);
                  }

                  var10.writeUTF(this.remoteHost);
                  var10.writeInt(var11);
                  var10.flush();
                  String var16 = var5.readUTF();
                  int var17 = var5.readInt();
                  if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
                     TCPTransport.tcpLog.log(Log.VERBOSE, "(port " + var2 + ") client using " + var16 + ":" + var17);
                  }

                  var12 = new TCPEndpoint(this.remoteHost, this.socket.getLocalPort(), var1.getClientSocketFactory(), var1.getServerSocketFactory());
                  var13 = new TCPChannel(TCPTransport.this, var12);
                  var14 = new TCPConnection(var13, this.socket, (InputStream)var4, var9);
                  TCPTransport.this.handleMessages(var14, true);
                  return;
               case 76:
                  var12 = new TCPEndpoint(this.remoteHost, this.socket.getLocalPort(), var1.getClientSocketFactory(), var1.getServerSocketFactory());
                  var13 = new TCPChannel(TCPTransport.this, var12);
                  var14 = new TCPConnection(var13, this.socket, (InputStream)var4, var9);
                  TCPTransport.this.handleMessages(var14, false);
                  return;
               case 77:
                  if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
                     TCPTransport.tcpLog.log(Log.VERBOSE, "(port " + var2 + ") accepting multiplex protocol");
                  }

                  var10.writeByte(78);
                  if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
                     TCPTransport.tcpLog.log(Log.VERBOSE, "(port " + var2 + ") suggesting " + this.remoteHost + ":" + var11);
                  }

                  var10.writeUTF(this.remoteHost);
                  var10.writeInt(var11);
                  var10.flush();
                  var12 = new TCPEndpoint(var5.readUTF(), var5.readInt(), var1.getClientSocketFactory(), var1.getServerSocketFactory());
                  if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
                     TCPTransport.tcpLog.log(Log.VERBOSE, "(port " + var2 + ") client using " + var12.getHost() + ":" + var12.getPort());
                  }

                  ConnectionMultiplexer var18;
                  synchronized(TCPTransport.this.channelTable) {
                     var13 = TCPTransport.this.getChannel(var12);
                     var18 = new ConnectionMultiplexer(var13, (InputStream)var4, var8, false);
                     var13.useMultiplexer(var18);
                  }

                  var18.run();
                  return;
               default:
                  var10.writeByte(79);
                  var10.flush();
                  return;
               }
            }

            TCPTransport.closeSocket(this.socket);
         } catch (IOException var32) {
            TCPTransport.tcpLog.log(Log.BRIEF, "terminated with exception:", var32);
            return;
         } finally {
            TCPTransport.closeSocket(this.socket);
         }

      }
   }

   private class AcceptLoop implements Runnable {
      private final ServerSocket serverSocket;
      private long lastExceptionTime = 0L;
      private int recentExceptionCount;

      AcceptLoop(ServerSocket var2) {
         this.serverSocket = var2;
      }

      public void run() {
         try {
            this.executeAcceptLoop();
         } finally {
            try {
               this.serverSocket.close();
            } catch (IOException var7) {
            }

         }

      }

      private void executeAcceptLoop() {
         if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
            TCPTransport.tcpLog.log(Log.BRIEF, "listening on port " + TCPTransport.this.getEndpoint().getPort());
         }

         while(true) {
            Socket var1 = null;

            try {
               var1 = this.serverSocket.accept();
               InetAddress var16 = var1.getInetAddress();
               String var3 = var16 != null ? var16.getHostAddress() : "0.0.0.0";

               try {
                  TCPTransport.connectionThreadPool.execute(TCPTransport.this.new ConnectionHandler(var1, var3));
               } catch (RejectedExecutionException var11) {
                  TCPTransport.closeSocket(var1);
                  TCPTransport.tcpLog.log(Log.BRIEF, "rejected connection from " + var3);
               }
            } catch (Throwable var15) {
               Throwable var2 = var15;

               try {
                  if (this.serverSocket.isClosed()) {
                     return;
                  }

                  try {
                     if (TCPTransport.tcpLog.isLoggable(Level.WARNING)) {
                        TCPTransport.tcpLog.log(Level.WARNING, "accept loop for " + this.serverSocket + " throws", var2);
                     }
                  } catch (Throwable var13) {
                  }
               } finally {
                  if (var1 != null) {
                     TCPTransport.closeSocket(var1);
                  }

               }

               if (!(var15 instanceof SecurityException)) {
                  try {
                     TCPEndpoint.shedConnectionCaches();
                  } catch (Throwable var12) {
                  }
               }

               if (!(var15 instanceof Exception) && !(var15 instanceof OutOfMemoryError) && !(var15 instanceof NoClassDefFoundError)) {
                  if (var15 instanceof Error) {
                     throw (Error)var15;
                  }

                  throw new UndeclaredThrowableException(var15);
               }

               if (!this.continueAfterAcceptFailure(var15)) {
                  return;
               }
            }
         }
      }

      private boolean continueAfterAcceptFailure(Throwable var1) {
         RMIFailureHandler var2 = RMISocketFactory.getFailureHandler();
         if (var2 != null) {
            return var2.failure((Exception)(var1 instanceof Exception ? (Exception)var1 : new InvocationTargetException(var1)));
         } else {
            this.throttleLoopOnException();
            return true;
         }
      }

      private void throttleLoopOnException() {
         long var1 = System.currentTimeMillis();
         if (this.lastExceptionTime != 0L && var1 - this.lastExceptionTime <= 5000L) {
            if (++this.recentExceptionCount >= 10) {
               try {
                  Thread.sleep(10000L);
               } catch (InterruptedException var4) {
               }
            }
         } else {
            this.lastExceptionTime = var1;
            this.recentExceptionCount = 0;
         }

      }
   }
}
