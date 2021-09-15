package com.sun.jndi.ldap.pool;

final class ConnectionsRef {
   private final Connections conns;

   ConnectionsRef(Connections var1) {
      this.conns = var1;
   }

   Connections getConnections() {
      return this.conns;
   }
}
