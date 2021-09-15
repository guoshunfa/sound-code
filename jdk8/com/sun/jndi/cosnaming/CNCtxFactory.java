package com.sun.jndi.cosnaming;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

public class CNCtxFactory implements InitialContextFactory {
   public Context getInitialContext(Hashtable<?, ?> var1) throws NamingException {
      return new CNCtx(var1);
   }
}
