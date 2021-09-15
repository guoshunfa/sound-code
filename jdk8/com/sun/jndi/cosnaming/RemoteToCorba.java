package com.sun.jndi.cosnaming;

import com.sun.jndi.toolkit.corba.CorbaUtils;
import java.rmi.Remote;
import java.util.Hashtable;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.StateFactory;

public class RemoteToCorba implements StateFactory {
   public Object getStateToBind(Object var1, Name var2, Context var3, Hashtable<?, ?> var4) throws NamingException {
      if (var1 instanceof org.omg.CORBA.Object) {
         return null;
      } else if (var1 instanceof Remote) {
         try {
            return CorbaUtils.remoteToCorba((Remote)var1, ((CNCtx)var3)._orb);
         } catch (ClassNotFoundException var6) {
            throw new ConfigurationException("javax.rmi packages not available");
         }
      } else {
         return null;
      }
   }
}
