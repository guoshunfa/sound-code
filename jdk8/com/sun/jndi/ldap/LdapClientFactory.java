package com.sun.jndi.ldap;

import com.sun.jndi.ldap.pool.PoolCallback;
import com.sun.jndi.ldap.pool.PooledConnection;
import com.sun.jndi.ldap.pool.PooledConnectionFactory;
import java.io.OutputStream;
import javax.naming.NamingException;

final class LdapClientFactory implements PooledConnectionFactory {
   private final String host;
   private final int port;
   private final String socketFactory;
   private final int connTimeout;
   private final int readTimeout;
   private final OutputStream trace;

   LdapClientFactory(String var1, int var2, String var3, int var4, int var5, OutputStream var6) {
      this.host = var1;
      this.port = var2;
      this.socketFactory = var3;
      this.connTimeout = var4;
      this.readTimeout = var5;
      this.trace = var6;
   }

   public PooledConnection createPooledConnection(PoolCallback var1) throws NamingException {
      return new LdapClient(this.host, this.port, this.socketFactory, this.connTimeout, this.readTimeout, this.trace, var1);
   }

   public String toString() {
      return this.host + ":" + this.port;
   }
}
