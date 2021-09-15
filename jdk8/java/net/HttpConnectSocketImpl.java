package java.net;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class HttpConnectSocketImpl extends PlainSocketImpl {
   private static final String httpURLClazzStr = "sun.net.www.protocol.http.HttpURLConnection";
   private static final String netClientClazzStr = "sun.net.NetworkClient";
   private static final String doTunnelingStr = "doTunneling";
   private static final Field httpField;
   private static final Field serverSocketField;
   private static final Method doTunneling;
   private final String server;
   private InetSocketAddress external_address;
   private HashMap<Integer, Object> optionsMap = new HashMap();

   HttpConnectSocketImpl(String var1, int var2) {
      this.server = var1;
      this.port = var2;
   }

   HttpConnectSocketImpl(Proxy var1) {
      SocketAddress var2 = var1.address();
      if (!(var2 instanceof InetSocketAddress)) {
         throw new IllegalArgumentException("Unsupported address type");
      } else {
         InetSocketAddress var3 = (InetSocketAddress)var2;
         this.server = var3.getHostString();
         this.port = var3.getPort();
      }
   }

   protected void connect(SocketAddress var1, int var2) throws IOException {
      if (var1 != null && var1 instanceof InetSocketAddress) {
         InetSocketAddress var3 = (InetSocketAddress)var1;
         String var4 = var3.isUnresolved() ? var3.getHostName() : var3.getAddress().getHostAddress();
         int var5 = var3.getPort();
         SecurityManager var6 = System.getSecurityManager();
         if (var6 != null) {
            var6.checkConnect(var4, var5);
         }

         String var7 = "http://" + var4 + ":" + var5;
         Socket var8 = this.privilegedDoTunnel(var7, var2);
         this.external_address = var3;
         this.close();
         AbstractPlainSocketImpl var9 = (AbstractPlainSocketImpl)var8.impl;
         this.getSocket().impl = var9;
         Set var10 = this.optionsMap.entrySet();

         try {
            Iterator var11 = var10.iterator();

            while(var11.hasNext()) {
               Map.Entry var12 = (Map.Entry)var11.next();
               var9.setOption((Integer)var12.getKey(), var12.getValue());
            }
         } catch (IOException var13) {
         }

      } else {
         throw new IllegalArgumentException("Unsupported address type");
      }
   }

   public void setOption(int var1, Object var2) throws SocketException {
      super.setOption(var1, var2);
      if (this.external_address == null) {
         this.optionsMap.put(var1, var2);
      }
   }

   private Socket privilegedDoTunnel(final String var1, final int var2) throws IOException {
      try {
         return (Socket)AccessController.doPrivileged(new PrivilegedExceptionAction<Socket>() {
            public Socket run() throws IOException {
               return HttpConnectSocketImpl.this.doTunnel(var1, var2);
            }
         });
      } catch (PrivilegedActionException var4) {
         throw (IOException)var4.getException();
      }
   }

   private Socket doTunnel(String var1, int var2) throws IOException {
      Proxy var3 = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(this.server, this.port));
      URL var4 = new URL(var1);
      HttpURLConnection var5 = (HttpURLConnection)var4.openConnection(var3);
      var5.setConnectTimeout(var2);
      var5.setReadTimeout(this.timeout);
      var5.connect();
      this.doTunneling(var5);

      try {
         Object var6 = httpField.get(var5);
         return (Socket)serverSocketField.get(var6);
      } catch (IllegalAccessException var7) {
         throw new InternalError("Should not reach here", var7);
      }
   }

   private void doTunneling(HttpURLConnection var1) {
      try {
         doTunneling.invoke(var1);
      } catch (ReflectiveOperationException var3) {
         throw new InternalError("Should not reach here", var3);
      }
   }

   protected InetAddress getInetAddress() {
      return this.external_address != null ? this.external_address.getAddress() : super.getInetAddress();
   }

   protected int getPort() {
      return this.external_address != null ? this.external_address.getPort() : super.getPort();
   }

   protected int getLocalPort() {
      if (this.socket != null) {
         return super.getLocalPort();
      } else {
         return this.external_address != null ? this.external_address.getPort() : super.getLocalPort();
      }
   }

   static {
      try {
         Class var0 = Class.forName("sun.net.www.protocol.http.HttpURLConnection", true, (ClassLoader)null);
         httpField = var0.getDeclaredField("http");
         doTunneling = var0.getDeclaredMethod("doTunneling");
         Class var1 = Class.forName("sun.net.NetworkClient", true, (ClassLoader)null);
         serverSocketField = var1.getDeclaredField("serverSocket");
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               HttpConnectSocketImpl.httpField.setAccessible(true);
               HttpConnectSocketImpl.serverSocketField.setAccessible(true);
               return null;
            }
         });
      } catch (ReflectiveOperationException var2) {
         throw new InternalError("Should not reach here", var2);
      }
   }
}
