package javax.net.ssl;

import java.nio.ByteBuffer;

public abstract class SSLEngine {
   private String peerHost = null;
   private int peerPort = -1;

   protected SSLEngine() {
   }

   protected SSLEngine(String var1, int var2) {
      this.peerHost = var1;
      this.peerPort = var2;
   }

   public String getPeerHost() {
      return this.peerHost;
   }

   public int getPeerPort() {
      return this.peerPort;
   }

   public SSLEngineResult wrap(ByteBuffer var1, ByteBuffer var2) throws SSLException {
      return this.wrap(new ByteBuffer[]{var1}, 0, 1, var2);
   }

   public SSLEngineResult wrap(ByteBuffer[] var1, ByteBuffer var2) throws SSLException {
      if (var1 == null) {
         throw new IllegalArgumentException("src == null");
      } else {
         return this.wrap(var1, 0, var1.length, var2);
      }
   }

   public abstract SSLEngineResult wrap(ByteBuffer[] var1, int var2, int var3, ByteBuffer var4) throws SSLException;

   public SSLEngineResult unwrap(ByteBuffer var1, ByteBuffer var2) throws SSLException {
      return this.unwrap(var1, new ByteBuffer[]{var2}, 0, 1);
   }

   public SSLEngineResult unwrap(ByteBuffer var1, ByteBuffer[] var2) throws SSLException {
      if (var2 == null) {
         throw new IllegalArgumentException("dsts == null");
      } else {
         return this.unwrap(var1, var2, 0, var2.length);
      }
   }

   public abstract SSLEngineResult unwrap(ByteBuffer var1, ByteBuffer[] var2, int var3, int var4) throws SSLException;

   public abstract Runnable getDelegatedTask();

   public abstract void closeInbound() throws SSLException;

   public abstract boolean isInboundDone();

   public abstract void closeOutbound();

   public abstract boolean isOutboundDone();

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

   public abstract void beginHandshake() throws SSLException;

   public abstract SSLEngineResult.HandshakeStatus getHandshakeStatus();

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
