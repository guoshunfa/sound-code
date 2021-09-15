package org.omg.PortableServer;

import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;

public interface ServantLocatorOperations extends ServantManagerOperations {
   Servant preinvoke(byte[] var1, POA var2, String var3, CookieHolder var4) throws ForwardRequest;

   void postinvoke(byte[] var1, POA var2, String var3, Object var4, Servant var5);
}
