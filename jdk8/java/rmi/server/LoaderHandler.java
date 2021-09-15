package java.rmi.server;

import java.net.MalformedURLException;
import java.net.URL;

/** @deprecated */
@Deprecated
public interface LoaderHandler {
   String packagePrefix = "sun.rmi.server";

   /** @deprecated */
   @Deprecated
   Class<?> loadClass(String var1) throws MalformedURLException, ClassNotFoundException;

   /** @deprecated */
   @Deprecated
   Class<?> loadClass(URL var1, String var2) throws MalformedURLException, ClassNotFoundException;

   /** @deprecated */
   @Deprecated
   Object getSecurityContext(ClassLoader var1);
}
