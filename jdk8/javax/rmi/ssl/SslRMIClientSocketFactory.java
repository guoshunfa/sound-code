package javax.rmi.ssl;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;
import java.util.StringTokenizer;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SslRMIClientSocketFactory implements RMIClientSocketFactory, Serializable {
   private static SocketFactory defaultSocketFactory = null;
   private static final long serialVersionUID = -8310631444933958385L;

   public Socket createSocket(String var1, int var2) throws IOException {
      SocketFactory var3 = getDefaultClientSocketFactory();
      SSLSocket var4 = (SSLSocket)var3.createSocket(var1, var2);
      String var5 = System.getProperty("javax.rmi.ssl.client.enabledCipherSuites");
      if (var5 != null) {
         StringTokenizer var6 = new StringTokenizer(var5, ",");
         int var7 = var6.countTokens();
         String[] var8 = new String[var7];

         for(int var9 = 0; var9 < var7; ++var9) {
            var8[var9] = var6.nextToken();
         }

         try {
            var4.setEnabledCipherSuites(var8);
         } catch (IllegalArgumentException var12) {
            throw (IOException)(new IOException(var12.getMessage())).initCause(var12);
         }
      }

      String var13 = System.getProperty("javax.rmi.ssl.client.enabledProtocols");
      if (var13 != null) {
         StringTokenizer var14 = new StringTokenizer(var13, ",");
         int var15 = var14.countTokens();
         String[] var16 = new String[var15];

         for(int var10 = 0; var10 < var15; ++var10) {
            var16[var10] = var14.nextToken();
         }

         try {
            var4.setEnabledProtocols(var16);
         } catch (IllegalArgumentException var11) {
            throw (IOException)(new IOException(var11.getMessage())).initCause(var11);
         }
      }

      return var4;
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         return var1 == this ? true : this.getClass().equals(var1.getClass());
      }
   }

   public int hashCode() {
      return this.getClass().hashCode();
   }

   private static synchronized SocketFactory getDefaultClientSocketFactory() {
      if (defaultSocketFactory == null) {
         defaultSocketFactory = SSLSocketFactory.getDefault();
      }

      return defaultSocketFactory;
   }
}
