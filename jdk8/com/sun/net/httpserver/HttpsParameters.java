package com.sun.net.httpserver;

import java.net.InetSocketAddress;
import javax.net.ssl.SSLParameters;
import jdk.Exported;

@Exported
public abstract class HttpsParameters {
   private String[] cipherSuites;
   private String[] protocols;
   private boolean wantClientAuth;
   private boolean needClientAuth;

   protected HttpsParameters() {
   }

   public abstract HttpsConfigurator getHttpsConfigurator();

   public abstract InetSocketAddress getClientAddress();

   public abstract void setSSLParameters(SSLParameters var1);

   public String[] getCipherSuites() {
      return this.cipherSuites != null ? (String[])this.cipherSuites.clone() : null;
   }

   public void setCipherSuites(String[] var1) {
      this.cipherSuites = var1 != null ? (String[])var1.clone() : null;
   }

   public String[] getProtocols() {
      return this.protocols != null ? (String[])this.protocols.clone() : null;
   }

   public void setProtocols(String[] var1) {
      this.protocols = var1 != null ? (String[])var1.clone() : null;
   }

   public boolean getWantClientAuth() {
      return this.wantClientAuth;
   }

   public void setWantClientAuth(boolean var1) {
      this.wantClientAuth = var1;
   }

   public boolean getNeedClientAuth() {
      return this.needClientAuth;
   }

   public void setNeedClientAuth(boolean var1) {
      this.needClientAuth = var1;
   }
}
