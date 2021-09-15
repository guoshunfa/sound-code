package com.sun.jndi.url.iiop;

import com.sun.jndi.cosnaming.CorbanameUrl;
import com.sun.jndi.cosnaming.IiopUrl;
import com.sun.jndi.toolkit.url.GenericURLContext;
import java.net.MalformedURLException;
import java.util.Hashtable;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ResolveResult;

public class iiopURLContext extends GenericURLContext {
   iiopURLContext(Hashtable<?, ?> var1) {
      super(var1);
   }

   protected ResolveResult getRootURLContext(String var1, Hashtable<?, ?> var2) throws NamingException {
      return iiopURLContextFactory.getUsingURLIgnoreRest(var1, var2);
   }

   protected Name getURLSuffix(String var1, String var2) throws NamingException {
      try {
         if (!var2.startsWith("iiop://") && !var2.startsWith("iiopname://")) {
            if (var2.startsWith("corbaname:")) {
               CorbanameUrl var5 = new CorbanameUrl(var2);
               return var5.getCosName();
            } else {
               throw new MalformedURLException("Not a valid URL: " + var2);
            }
         } else {
            IiopUrl var3 = new IiopUrl(var2);
            return var3.getCosName();
         }
      } catch (MalformedURLException var4) {
         throw new InvalidNameException(var4.getMessage());
      }
   }
}
