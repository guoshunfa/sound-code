package java.rmi.server;

import java.net.MalformedURLException;

public abstract class RMIClassLoaderSpi {
   public abstract Class<?> loadClass(String var1, String var2, ClassLoader var3) throws MalformedURLException, ClassNotFoundException;

   public abstract Class<?> loadProxyClass(String var1, String[] var2, ClassLoader var3) throws MalformedURLException, ClassNotFoundException;

   public abstract ClassLoader getClassLoader(String var1) throws MalformedURLException;

   public abstract String getClassAnnotation(Class<?> var1);
}
