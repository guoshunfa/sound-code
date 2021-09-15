package sun.rmi.transport.tcp;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.ConnectIOException;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMISocketFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import sun.rmi.runtime.Log;
import sun.rmi.runtime.NewThreadAction;
import sun.rmi.transport.Channel;
import sun.rmi.transport.Endpoint;
import sun.rmi.transport.Target;
import sun.rmi.transport.Transport;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetIntegerAction;
import sun.security.action.GetPropertyAction;

public class TCPEndpoint implements Endpoint {
   private String host;
   private int port;
   private final RMIClientSocketFactory csf;
   private final RMIServerSocketFactory ssf;
   private int listenPort;
   private TCPTransport transport;
   private static String localHost = getHostnameProperty();
   private static boolean localHostKnown = true;
   private static final Map<TCPEndpoint, LinkedList<TCPEndpoint>> localEndpoints;
   private static final int FORMAT_HOST_PORT = 0;
   private static final int FORMAT_HOST_PORT_FACTORY = 1;

   private static int getInt(String var0, int var1) {
      return (Integer)AccessController.doPrivileged((PrivilegedAction)(new GetIntegerAction(var0, var1)));
   }

   private static boolean getBoolean(String var0) {
      return (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction(var0)));
   }

   private static String getHostnameProperty() {
      return (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.rmi.server.hostname")));
   }

   public TCPEndpoint(String var1, int var2) {
      this(var1, var2, (RMIClientSocketFactory)null, (RMIServerSocketFactory)null);
   }

   public TCPEndpoint(String var1, int var2, RMIClientSocketFactory var3, RMIServerSocketFactory var4) {
      this.listenPort = -1;
      this.transport = null;
      if (var1 == null) {
         var1 = "";
      }

      this.host = var1;
      this.port = var2;
      this.csf = var3;
      this.ssf = var4;
   }

   public static TCPEndpoint getLocalEndpoint(int var0) {
      return getLocalEndpoint(var0, (RMIClientSocketFactory)null, (RMIServerSocketFactory)null);
   }

   public static TCPEndpoint getLocalEndpoint(int var0, RMIClientSocketFactory var1, RMIServerSocketFactory var2) {
      TCPEndpoint var3 = null;
      synchronized(localEndpoints) {
         TCPEndpoint var5 = new TCPEndpoint((String)null, var0, var1, var2);
         LinkedList var6 = (LinkedList)localEndpoints.get(var5);
         String var7 = resampleLocalHost();
         if (var6 == null) {
            var3 = new TCPEndpoint(var7, var0, var1, var2);
            var6 = new LinkedList();
            var6.add(var3);
            var3.listenPort = var0;
            var3.transport = new TCPTransport(var6);
            localEndpoints.put(var5, var6);
            if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
               TCPTransport.tcpLog.log(Log.BRIEF, "created local endpoint for socket factory " + var2 + " on port " + var0);
            }
         } else {
            synchronized(var6) {
               var3 = (TCPEndpoint)var6.getLast();
               String var9 = var3.host;
               int var10 = var3.port;
               TCPTransport var11 = var3.transport;
               if (var7 != null && !var7.equals(var9)) {
                  if (var10 != 0) {
                     var6.clear();
                  }

                  var3 = new TCPEndpoint(var7, var10, var1, var2);
                  var3.listenPort = var0;
                  var3.transport = var11;
                  var6.add(var3);
               }
            }
         }

         return var3;
      }
   }

   private static String resampleLocalHost() {
      String var0 = getHostnameProperty();
      synchronized(localEndpoints) {
         if (var0 != null) {
            if (!localHostKnown) {
               setLocalHost(var0);
            } else if (!var0.equals(localHost)) {
               localHost = var0;
               if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
                  TCPTransport.tcpLog.log(Log.BRIEF, "updated local hostname to: " + localHost);
               }
            }
         }

         return localHost;
      }
   }

   static void setLocalHost(String var0) {
      synchronized(localEndpoints) {
         if (!localHostKnown) {
            localHost = var0;
            localHostKnown = true;
            if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
               TCPTransport.tcpLog.log(Log.BRIEF, "local host set to " + var0);
            }

            Iterator var2 = localEndpoints.values().iterator();

            while(var2.hasNext()) {
               LinkedList var3 = (LinkedList)var2.next();
               TCPEndpoint var6;
               synchronized(var3) {
                  for(Iterator var5 = var3.iterator(); var5.hasNext(); var6.host = var0) {
                     var6 = (TCPEndpoint)var5.next();
                  }
               }
            }
         }

      }
   }

   static void setDefaultPort(int var0, RMIClientSocketFactory var1, RMIServerSocketFactory var2) {
      TCPEndpoint var3 = new TCPEndpoint((String)null, 0, var1, var2);
      synchronized(localEndpoints) {
         LinkedList var5 = (LinkedList)localEndpoints.get(var3);
         synchronized(var5) {
            int var7 = var5.size();
            TCPEndpoint var8 = (TCPEndpoint)var5.getLast();
            Iterator var9 = var5.iterator();

            while(true) {
               if (!var9.hasNext()) {
                  if (var7 > 1) {
                     var5.clear();
                     var5.add(var8);
                  }
                  break;
               }

               TCPEndpoint var10 = (TCPEndpoint)var9.next();
               var10.port = var0;
            }
         }

         TCPEndpoint var6 = new TCPEndpoint((String)null, var0, var1, var2);
         localEndpoints.put(var6, var5);
         if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
            TCPTransport.tcpLog.log(Log.BRIEF, "default port for server socket factory " + var2 + " and client socket factory " + var1 + " set to " + var0);
         }

      }
   }

   public Transport getOutboundTransport() {
      TCPEndpoint var1 = getLocalEndpoint(0, (RMIClientSocketFactory)null, (RMIServerSocketFactory)null);
      return var1.transport;
   }

   private static Collection<TCPTransport> allKnownTransports() {
      synchronized(localEndpoints) {
         HashSet var0 = new HashSet(localEndpoints.size());
         Iterator var2 = localEndpoints.values().iterator();

         while(var2.hasNext()) {
            LinkedList var3 = (LinkedList)var2.next();
            TCPEndpoint var4 = (TCPEndpoint)var3.getFirst();
            var0.add(var4.transport);
         }

         return var0;
      }
   }

   public static void shedConnectionCaches() {
      Iterator var0 = allKnownTransports().iterator();

      while(var0.hasNext()) {
         TCPTransport var1 = (TCPTransport)var0.next();
         var1.shedConnectionCaches();
      }

   }

   public void exportObject(Target var1) throws RemoteException {
      this.transport.exportObject(var1);
   }

   public Channel getChannel() {
      return this.getOutboundTransport().getChannel(this);
   }

   public String getHost() {
      return this.host;
   }

   public int getPort() {
      return this.port;
   }

   public int getListenPort() {
      return this.listenPort;
   }

   public Transport getInboundTransport() {
      return this.transport;
   }

   public RMIClientSocketFactory getClientSocketFactory() {
      return this.csf;
   }

   public RMIServerSocketFactory getServerSocketFactory() {
      return this.ssf;
   }

   public String toString() {
      return "[" + this.host + ":" + this.port + (this.ssf != null ? "," + this.ssf : "") + (this.csf != null ? "," + this.csf : "") + "]";
   }

   public int hashCode() {
      return this.port;
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof TCPEndpoint) {
         TCPEndpoint var2 = (TCPEndpoint)var1;
         if (this.port == var2.port && this.host.equals(var2.host)) {
            if (!(this.csf == null ^ var2.csf == null) && !(this.ssf == null ^ var2.ssf == null)) {
               if (this.csf == null || this.csf.getClass() == var2.csf.getClass() && this.csf.equals(var2.csf)) {
                  return this.ssf == null || this.ssf.getClass() == var2.ssf.getClass() && this.ssf.equals(var2.ssf);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void write(ObjectOutput var1) throws IOException {
      if (this.csf == null) {
         var1.writeByte(0);
         var1.writeUTF(this.host);
         var1.writeInt(this.port);
      } else {
         var1.writeByte(1);
         var1.writeUTF(this.host);
         var1.writeInt(this.port);
         var1.writeObject(this.csf);
      }

   }

   public static TCPEndpoint read(ObjectInput var0) throws IOException, ClassNotFoundException {
      RMIClientSocketFactory var3 = null;
      byte var4 = var0.readByte();
      String var1;
      int var2;
      switch(var4) {
      case 0:
         var1 = var0.readUTF();
         var2 = var0.readInt();
         break;
      case 1:
         var1 = var0.readUTF();
         var2 = var0.readInt();
         var3 = (RMIClientSocketFactory)var0.readObject();
         break;
      default:
         throw new IOException("invalid endpoint format");
      }

      return new TCPEndpoint(var1, var2, var3, (RMIServerSocketFactory)null);
   }

   public void writeHostPortFormat(DataOutput var1) throws IOException {
      if (this.csf != null) {
         throw new InternalError("TCPEndpoint.writeHostPortFormat: called for endpoint with non-null socket factory");
      } else {
         var1.writeUTF(this.host);
         var1.writeInt(this.port);
      }
   }

   public static TCPEndpoint readHostPortFormat(DataInput var0) throws IOException {
      String var1 = var0.readUTF();
      int var2 = var0.readInt();
      return new TCPEndpoint(var1, var2);
   }

   private static RMISocketFactory chooseFactory() {
      RMISocketFactory var0 = RMISocketFactory.getSocketFactory();
      if (var0 == null) {
         var0 = TCPTransport.defaultSocketFactory;
      }

      return var0;
   }

   Socket newSocket() throws RemoteException {
      if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
         TCPTransport.tcpLog.log(Log.VERBOSE, "opening socket to " + this);
      }

      Socket var1;
      try {
         Object var2 = this.csf;
         if (var2 == null) {
            var2 = chooseFactory();
         }

         var1 = ((RMIClientSocketFactory)var2).createSocket(this.host, this.port);
      } catch (UnknownHostException var7) {
         throw new java.rmi.UnknownHostException("Unknown host: " + this.host, var7);
      } catch (ConnectException var8) {
         throw new java.rmi.ConnectException("Connection refused to host: " + this.host, var8);
      } catch (IOException var9) {
         try {
            shedConnectionCaches();
         } catch (Exception | OutOfMemoryError var4) {
         }

         throw new ConnectIOException("Exception creating connection to: " + this.host, var9);
      }

      try {
         var1.setTcpNoDelay(true);
      } catch (Exception var6) {
      }

      try {
         var1.setKeepAlive(true);
      } catch (Exception var5) {
      }

      return var1;
   }

   ServerSocket newServerSocket() throws IOException {
      if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
         TCPTransport.tcpLog.log(Log.VERBOSE, "creating server socket on " + this);
      }

      Object var1 = this.ssf;
      if (var1 == null) {
         var1 = chooseFactory();
      }

      ServerSocket var2 = ((RMIServerSocketFactory)var1).createServerSocket(this.listenPort);
      if (this.listenPort == 0) {
         setDefaultPort(var2.getLocalPort(), this.csf, this.ssf);
      }

      return var2;
   }

   static {
      if (localHost == null) {
         try {
            InetAddress var0 = InetAddress.getLocalHost();
            byte[] var1 = var0.getAddress();
            if (var1[0] == 127 && var1[1] == 0 && var1[2] == 0 && var1[3] == 1) {
               localHostKnown = false;
            }

            if (getBoolean("java.rmi.server.useLocalHostName")) {
               localHost = TCPEndpoint.FQDN.attemptFQDN(var0);
            } else {
               localHost = var0.getHostAddress();
            }
         } catch (Exception var2) {
            localHostKnown = false;
            localHost = null;
         }
      }

      if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
         TCPTransport.tcpLog.log(Log.BRIEF, "localHostKnown = " + localHostKnown + ", localHost = " + localHost);
      }

      localEndpoints = new HashMap();
   }

   private static class FQDN implements Runnable {
      private String reverseLookup;
      private String hostAddress;

      private FQDN(String var1) {
         this.hostAddress = var1;
      }

      static String attemptFQDN(InetAddress var0) throws UnknownHostException {
         String var1 = var0.getHostName();
         if (var1.indexOf(46) < 0) {
            String var2 = var0.getHostAddress();
            TCPEndpoint.FQDN var3 = new TCPEndpoint.FQDN(var2);
            int var4 = TCPEndpoint.getInt("sun.rmi.transport.tcp.localHostNameTimeOut", 10000);

            try {
               synchronized(var3) {
                  var3.getFQDN();
                  var3.wait((long)var4);
               }
            } catch (InterruptedException var8) {
               Thread.currentThread().interrupt();
            }

            var1 = var3.getHost();
            if (var1 == null || var1.equals("") || var1.indexOf(46) < 0) {
               var1 = var2;
            }
         }

         return var1;
      }

      private void getFQDN() {
         Thread var1 = (Thread)AccessController.doPrivileged((PrivilegedAction)(new NewThreadAction(this, "FQDN Finder", true)));
         var1.start();
      }

      private synchronized String getHost() {
         return this.reverseLookup;
      }

      public void run() {
         String var1 = null;
         boolean var13 = false;

         label89: {
            try {
               var13 = true;
               var1 = InetAddress.getByName(this.hostAddress).getHostName();
               var13 = false;
               break label89;
            } catch (UnknownHostException var17) {
               var13 = false;
            } finally {
               if (var13) {
                  synchronized(this) {
                     this.reverseLookup = var1;
                     this.notify();
                  }
               }
            }

            synchronized(this) {
               this.reverseLookup = var1;
               this.notify();
               return;
            }
         }

         synchronized(this) {
            this.reverseLookup = var1;
            this.notify();
         }

      }
   }
}
