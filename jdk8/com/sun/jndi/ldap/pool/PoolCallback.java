package com.sun.jndi.ldap.pool;

public interface PoolCallback {
   boolean releasePooledConnection(PooledConnection var1);

   boolean removePooledConnection(PooledConnection var1);
}
