package com.sun.xml.internal.ws.api.server;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public enum LazyMOMProvider {
   INSTANCE;

   private final Set<LazyMOMProvider.WSEndpointScopeChangeListener> endpointsWaitingForMOM = new HashSet();
   private final Set<LazyMOMProvider.DefaultScopeChangeListener> listeners = new HashSet();
   private volatile LazyMOMProvider.Scope scope;

   private LazyMOMProvider() {
      this.scope = LazyMOMProvider.Scope.STANDALONE;
   }

   public void initMOMForScope(LazyMOMProvider.Scope scope) {
      if (this.scope != LazyMOMProvider.Scope.GLASSFISH_JMX && (scope != LazyMOMProvider.Scope.STANDALONE || this.scope != LazyMOMProvider.Scope.GLASSFISH_JMX && this.scope != LazyMOMProvider.Scope.GLASSFISH_NO_JMX) && this.scope != scope) {
         this.scope = scope;
         this.fireScopeChanged();
      }
   }

   private void fireScopeChanged() {
      Iterator var1 = this.endpointsWaitingForMOM.iterator();

      LazyMOMProvider.ScopeChangeListener listener;
      while(var1.hasNext()) {
         listener = (LazyMOMProvider.ScopeChangeListener)var1.next();
         listener.scopeChanged(this.scope);
      }

      var1 = this.listeners.iterator();

      while(var1.hasNext()) {
         listener = (LazyMOMProvider.ScopeChangeListener)var1.next();
         listener.scopeChanged(this.scope);
      }

   }

   public void registerListener(LazyMOMProvider.DefaultScopeChangeListener listener) {
      this.listeners.add(listener);
      if (!this.isProviderInDefaultScope()) {
         listener.scopeChanged(this.scope);
      }

   }

   private boolean isProviderInDefaultScope() {
      return this.scope == LazyMOMProvider.Scope.STANDALONE;
   }

   public LazyMOMProvider.Scope getScope() {
      return this.scope;
   }

   public void registerEndpoint(LazyMOMProvider.WSEndpointScopeChangeListener wsEndpoint) {
      this.endpointsWaitingForMOM.add(wsEndpoint);
      if (!this.isProviderInDefaultScope()) {
         wsEndpoint.scopeChanged(this.scope);
      }

   }

   public void unregisterEndpoint(LazyMOMProvider.WSEndpointScopeChangeListener wsEndpoint) {
      this.endpointsWaitingForMOM.remove(wsEndpoint);
   }

   public interface WSEndpointScopeChangeListener extends LazyMOMProvider.ScopeChangeListener {
   }

   public interface DefaultScopeChangeListener extends LazyMOMProvider.ScopeChangeListener {
   }

   public interface ScopeChangeListener {
      void scopeChanged(LazyMOMProvider.Scope var1);
   }

   public static enum Scope {
      STANDALONE,
      GLASSFISH_NO_JMX,
      GLASSFISH_JMX;
   }
}
