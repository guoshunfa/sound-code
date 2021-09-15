package javax.net.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public abstract class SSLServerSocket extends ServerSocket {
   protected SSLServerSocket() throws IOException {
   }

   protected SSLServerSocket(int var1) throws IOException {
      super(var1);
   }

   protected SSLServerSocket(int var1, int var2) throws IOException {
      super(var1, var2);
   }

   protected SSLServerSocket(int var1, int var2, InetAddress var3) throws IOException {
      super(var1, var2, var3);
   }

   public abstract String[] getEnabledCipherSuites();

   public abstract void setEnabledCipherSuites(String[] var1);

   public abstract String[] getSupportedCipherSuites();

   public abstract String[] getSupportedProtocols();

   public abstract String[] getEnabledProtocols();

   public abstract void setEnabledProtocols(String[] var1);

   public abstract void setNeedClientAuth(boolean var1);

   public abstract boolean getNeedClientAuth();

   public abstract void setWantClientAuth(boolean var1);

   public abstract boolean getWantClientAuth();

   public abstract void setUseClientMode(boolean var1);

   public abstract boolean getUseClientMode();

   public abstract void setEnableSessionCreation(boolean var1);

   public abstract boolean getEnableSessionCreation();

   public SSLParameters getSSLParameters() {
      SSLParameters var1 = new SSLParameters();
      var1.setCipherSuites(this.getEnabledCipherSuites());
      var1.setProtocols(this.getEnabledProtocols());
      if (this.getNeedClientAuth()) {
         var1.setNeedClientAuth(true);
      } else if (this.getWantClientAuth()) {
         var1.setWantClientAuth(true);
      }

      return var1;
   }

   public void setSSLParameters(SSLParameters var1) {
      String[] var2 = var1.getCipherSuites();
      if (var2 != null) {
         this.setEnabledCipherSuites(var2);
      }

      var2 = var1.getProtocols();
      if (var2 != null) {
         this.setEnabledProtocols(var2);
      }

      if (var1.getNeedClientAuth()) {
         this.setNeedClientAuth(true);
      } else if (var1.getWantClientAuth()) {
         this.setWantClientAuth(true);
      } else {
         this.setWantClientAuth(false);
      }

   }
}
