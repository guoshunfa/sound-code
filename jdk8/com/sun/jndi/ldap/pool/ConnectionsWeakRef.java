package com.sun.jndi.ldap.pool;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

class ConnectionsWeakRef extends WeakReference<ConnectionsRef> {
   private final Connections conns;

   ConnectionsWeakRef(ConnectionsRef var1, ReferenceQueue<? super ConnectionsRef> var2) {
      super(var1, var2);
      this.conns = var1.getConnections();
   }

   Connections getConnections() {
      return this.conns;
   }
}
