package javax.net.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public abstract class SSLSocket extends Socket {
   protected SSLSocket() {
   }

   protected SSLSocket(String var1, int var2) throws IOException, UnknownHostException {
      super(var1, var2);
   }

   protected SSLSocket(InetAddress var1, int var2) throws IOException {
      super(var1, var2);
   }

   protected SSLSocket(String var1, int var2, InetAddress var3, int var4) throws IOException, UnknownHostException {
      super(var1, var2, var3, var4);
   }

   protected SSLSocket(InetAddress var1, int var2, InetAddress var3, int var4) throws IOException {
      super(var1, var2, var3, var4);
   }

   public abstract String[] getSupportedCipherSuites();

   public abstract String[] getEnabledCipherSuites();

   public abstract void setEnabledCipherSuites(String[] var1);

   public abstract String[] getSupportedProtocols();

   public abstract String[] getEnabledProtocols();

   public abstract void setEnabledProtocols(String[] var1);

   public abstract SSLSession getSession();

   public SSLSession getHandshakeSession() {
      throw new UnsupportedOperationException();
   }

   public abstract void addHandshakeCompletedListener(HandshakeCompletedListener var1);

   public abstract void removeHandshakeCompletedListener(HandshakeCompletedListener var1);

   public abstract void startHandshake() throws IOException;

   public abstract void setUseClientMode(boolean var1);

   public abstract boolean getUseClientMode();

   public abstract void setNeedClientAuth(boolean var1);

   public abstract boolean getNeedClientAuth();

   public abstract void setWantClientAuth(boolean var1);

   public abstract boolean getWantClientAuth();

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
