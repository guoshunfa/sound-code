package com.sun.jndi.url.dns;

import com.sun.jndi.dns.DnsContextFactory;
import com.sun.jndi.dns.DnsUrl;
import com.sun.jndi.toolkit.url.GenericURLDirContext;
import java.net.MalformedURLException;
import java.util.Hashtable;
import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.spi.ResolveResult;

public class dnsURLContext extends GenericURLDirContext {
   public dnsURLContext(Hashtable<?, ?> var1) {
      super(var1);
   }

   protected ResolveResult getRootURLContext(String var1, Hashtable<?, ?> var2) throws NamingException {
      DnsUrl var3;
      try {
         var3 = new DnsUrl(var1);
      } catch (MalformedURLException var6) {
         throw new InvalidNameException(var6.getMessage());
      }

      DnsUrl[] var4 = new DnsUrl[]{var3};
      String var5 = var3.getDomain();
      return new ResolveResult(DnsContextFactory.getContext(".", var4, var2), (new CompositeName()).add(var5));
   }
}
