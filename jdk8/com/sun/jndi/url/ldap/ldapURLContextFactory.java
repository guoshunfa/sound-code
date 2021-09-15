package com.sun.jndi.url.ldap;

import com.sun.jndi.ldap.LdapCtx;
import com.sun.jndi.ldap.LdapCtxFactory;
import com.sun.jndi.ldap.LdapURL;
import java.util.Hashtable;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.ResolveResult;

public class ldapURLContextFactory implements ObjectFactory {
   public Object getObjectInstance(Object var1, Name var2, Context var3, Hashtable<?, ?> var4) throws Exception {
      return var1 == null ? new ldapURLContext(var4) : LdapCtxFactory.getLdapCtxInstance(var1, var4);
   }

   static ResolveResult getUsingURLIgnoreRootDN(String var0, Hashtable<?, ?> var1) throws NamingException {
      LdapURL var2 = new LdapURL(var0);
      LdapCtx var3 = new LdapCtx("", var2.getHost(), var2.getPort(), var1, var2.useSsl());
      String var4 = var2.getDN() != null ? var2.getDN() : "";
      CompositeName var5 = new CompositeName();
      if (!"".equals(var4)) {
         var5.add(var4);
      }

      return new ResolveResult(var3, var5);
   }
}
