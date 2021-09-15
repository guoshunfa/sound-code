package sun.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;

public class NetworkClient {
   public static final int DEFAULT_READ_TIMEOUT = -1;
   public static final int DEFAULT_CONNECT_TIMEOUT = -1;
   protected Proxy proxy;
   protected Socket serverSocket;
   public PrintStream serverOutput;
   public InputStream serverInput;
   protected static int defaultSoTimeout;
   protected static int defaultConnectTimeout;
   protected int readTimeout;
   protected int connectTimeout;
   protected static String encoding;

   private static boolean isASCIISuperset(String var0) throws Exception {
      String var1 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_.!~*'();/?:@&=+$,";
      byte[] var2 = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 45, 95, 46, 33, 126, 42, 39, 40, 41, 59, 47, 63, 58, 64, 38, 61, 43, 36, 44};
      byte[] var3 = var1.getBytes(var0);
      return Arrays.equals(var3, var2);
   }

   public void openServer(String var1, int var2) throws IOException, UnknownHostException {
      if (this.serverSocket != null) {
         this.closeServer();
      }

      this.serverSocket = this.doConnect(var1, var2);

      try {
         this.serverOutput = new PrintStream(new BufferedOutputStream(this.serverSocket.getOutputStream()), true, encoding);
      } catch (UnsupportedEncodingException var4) {
         throw new InternalError(encoding + "encoding not found", var4);
      }

      this.serverInput = new BufferedInputStream(this.serverSocket.getInputStream());
   }

   protected Socket doConnect(String var1, int var2) throws IOException, UnknownHostException {
      Socket var3;
      if (this.proxy != null) {
         if (this.proxy.type() == Proxy.Type.SOCKS) {
            var3 = (Socket)AccessController.doPrivileged(new PrivilegedAction<Socket>() {
               public Socket run() {
                  return new Socket(NetworkClient.this.proxy);
               }
            });
         } else if (this.proxy.type() == Proxy.Type.DIRECT) {
            var3 = this.createSocket();
         } else {
            var3 = new Socket(Proxy.NO_PROXY);
         }
      } else {
         var3 = this.createSocket();
      }

      if (this.connectTimeout >= 0) {
         var3.connect(new InetSocketAddress(var1, var2), this.connectTimeout);
      } else if (defaultConnectTimeout > 0) {
         var3.connect(new InetSocketAddress(var1, var2), defaultConnectTimeout);
      } else {
         var3.connect(new InetSocketAddress(var1, var2));
      }

      if (this.readTimeout >= 0) {
         var3.setSoTimeout(this.readTimeout);
      } else if (defaultSoTimeout > 0) {
         var3.setSoTimeout(defaultSoTimeout);
      }

      return var3;
   }

   protected Socket createSocket() throws IOException {
      return new Socket();
   }

   protected InetAddress getLocalAddress() throws IOException {
      if (this.serverSocket == null) {
         throw new IOException("not connected");
      } else {
         return (InetAddress)AccessController.doPrivileged(new PrivilegedAction<InetAddress>() {
            public InetAddress run() {
               return NetworkClient.this.serverSocket.getLocalAddress();
            }
         });
      }
   }

   public void closeServer() throws IOException {
      if (this.serverIsOpen()) {
         this.serverSocket.close();
         this.serverSocket = null;
         this.serverInput = null;
         this.serverOutput = null;
      }
   }

   public boolean serverIsOpen() {
      return this.serverSocket != null;
   }

   public NetworkClient(String var1, int var2) throws IOException {
      this.proxy = Proxy.NO_PROXY;
      this.serverSocket = null;
      this.readTimeout = -1;
      this.connectTimeout = -1;
      this.openServer(var1, var2);
   }

   public NetworkClient() {
      this.proxy = Proxy.NO_PROXY;
      this.serverSocket = null;
      this.readTimeout = -1;
      this.connectTimeout = -1;
   }

   public void setConnectTimeout(int var1) {
      this.connectTimeout = var1;
   }

   public int getConnectTimeout() {
      return this.connectTimeout;
   }

   public void setReadTimeout(int var1) {
      if (var1 == -1) {
         var1 = defaultSoTimeout;
      }

      if (this.serverSocket != null && var1 >= 0) {
         try {
            this.serverSocket.setSoTimeout(var1);
         } catch (IOException var3) {
         }
      }

      this.readTimeout = var1;
   }

   public int getReadTimeout() {
      return this.readTimeout;
   }

   static {
      final int[] var0 = new int[]{0, 0};
      final String[] var1 = new String[]{null};
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            var0[0] = Integer.getInteger("sun.net.client.defaultReadTimeout", 0);
            var0[1] = Integer.getInteger("sun.net.client.defaultConnectTimeout", 0);
            var1[0] = System.getProperty("file.encoding", "ISO8859_1");
            return null;
         }
      });
      if (var0[0] != 0) {
         defaultSoTimeout = var0[0];
      }

      if (var0[1] != 0) {
         defaultConnectTimeout = var0[1];
      }

      encoding = var1[0];

      try {
         if (!isASCIISuperset(encoding)) {
            encoding = "ISO8859_1";
         }
      } catch (Exception var3) {
         encoding = "ISO8859_1";
      }

   }
}
