package com.sun.jndi.ldap.pool;

import javax.naming.NamingException;

public interface PooledConnectionFactory {
   PooledConnection createPooledConnection(PoolCallback var1) throws NamingException;
}
