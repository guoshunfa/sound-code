package javax.rmi.ssl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMIServerSocketFactory;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SslRMIServerSocketFactory implements RMIServerSocketFactory {
   private static SSLSocketFactory defaultSSLSocketFactory = null;
   private final String[] enabledCipherSuites;
   private final String[] enabledProtocols;
   private final boolean needClientAuth;
   private List<String> enabledCipherSuitesList;
   private List<String> enabledProtocolsList;
   private SSLContext context;

   public SslRMIServerSocketFactory() {
      this((String[])null, (String[])null, false);
   }

   public SslRMIServerSocketFactory(String[] var1, String[] var2, boolean var3) throws IllegalArgumentException {
      this((SSLContext)null, var1, var2, var3);
   }

   public SslRMIServerSocketFactory(SSLContext var1, String[] var2, String[] var3, boolean var4) throws IllegalArgumentException {
      this.enabledCipherSuites = var2 == null ? null : (String[])var2.clone();
      this.enabledProtocols = var3 == null ? null : (String[])var3.clone();
      this.needClientAuth = var4;
      this.context = var1;
      SSLSocketFactory var5 = var1 == null ? getDefaultSSLSocketFactory() : var1.getSocketFactory();
      SSLSocket var6 = null;
      if (this.enabledCipherSuites != null || this.enabledProtocols != null) {
         try {
            var6 = (SSLSocket)var5.createSocket();
         } catch (Exception var9) {
            throw (IllegalArgumentException)(new IllegalArgumentException("Unable to check if the cipher suites and protocols to enable are supported")).initCause(var9);
         }
      }

      if (this.enabledCipherSuites != null) {
         var6.setEnabledCipherSuites(this.enabledCipherSuites);
         this.enabledCipherSuitesList = Arrays.asList(this.enabledCipherSuites);
      }

      if (this.enabledProtocols != null) {
         var6.setEnabledProtocols(this.enabledProtocols);
         this.enabledProtocolsList = Arrays.asList(this.enabledProtocols);
      }

   }

   public final String[] getEnabledCipherSuites() {
      return this.enabledCipherSuites == null ? null : (String[])this.enabledCipherSuites.clone();
   }

   public final String[] getEnabledProtocols() {
      return this.enabledProtocols == null ? null : (String[])this.enabledProtocols.clone();
   }

   public final boolean getNeedClientAuth() {
      return this.needClientAuth;
   }

   public ServerSocket createServerSocket(int var1) throws IOException {
      final SSLSocketFactory var2 = this.context == null ? getDefaultSSLSocketFactory() : this.context.getSocketFactory();
      return new ServerSocket(var1) {
         public Socket accept() throws IOException {
            Socket var1 = super.accept();
            SSLSocket var2x = (SSLSocket)var2.createSocket(var1, var1.getInetAddress().getHostName(), var1.getPort(), true);
            var2x.setUseClientMode(false);
            if (SslRMIServerSocketFactory.this.enabledCipherSuites != null) {
               var2x.setEnabledCipherSuites(SslRMIServerSocketFactory.this.enabledCipherSuites);
            }

            if (SslRMIServerSocketFactory.this.enabledProtocols != null) {
               var2x.setEnabledProtocols(SslRMIServerSocketFactory.this.enabledProtocols);
            }

            var2x.setNeedClientAuth(SslRMIServerSocketFactory.this.needClientAuth);
            return var2x;
         }
      };
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (var1 == this) {
         return true;
      } else if (!(var1 instanceof SslRMIServerSocketFactory)) {
         return false;
      } else {
         SslRMIServerSocketFactory var2 = (SslRMIServerSocketFactory)var1;
         return this.getClass().equals(var2.getClass()) && this.checkParameters(var2);
      }
   }

   private boolean checkParameters(SslRMIServerSocketFactory var1) {
      if (this.context == null) {
         if (var1.context != null) {
            return false;
         }
      } else if (!this.context.equals(var1.context)) {
         return false;
      }

      if (this.needClientAuth != var1.needClientAuth) {
         return false;
      } else if (this.enabledCipherSuites == null && var1.enabledCipherSuites != null || this.enabledCipherSuites != null && var1.enabledCipherSuites == null) {
         return false;
      } else {
         List var2;
         if (this.enabledCipherSuites != null && var1.enabledCipherSuites != null) {
            var2 = Arrays.asList(var1.enabledCipherSuites);
            if (!this.enabledCipherSuitesList.equals(var2)) {
               return false;
            }
         }

         if (this.enabledProtocols == null && var1.enabledProtocols != null || this.enabledProtocols != null && var1.enabledProtocols == null) {
            return false;
         } else {
            if (this.enabledProtocols != null && var1.enabledProtocols != null) {
               var2 = Arrays.asList(var1.enabledProtocols);
               if (!this.enabledProtocolsList.equals(var2)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public int hashCode() {
      return this.getClass().hashCode() + (this.context == null ? 0 : this.context.hashCode()) + (this.needClientAuth ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode()) + (this.enabledCipherSuites == null ? 0 : this.enabledCipherSuitesList.hashCode()) + (this.enabledProtocols == null ? 0 : this.enabledProtocolsList.hashCode());
   }

   private static synchronized SSLSocketFactory getDefaultSSLSocketFactory() {
      if (defaultSSLSocketFactory == null) {
         defaultSSLSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
      }

      return defaultSSLSocketFactory;
   }
}
