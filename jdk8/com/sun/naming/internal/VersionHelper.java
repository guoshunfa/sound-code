package com.sun.naming.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.naming.NamingEnumeration;

public abstract class VersionHelper {
   private static VersionHelper helper = null;
   static final String[] PROPS = new String[]{"java.naming.factory.initial", "java.naming.factory.object", "java.naming.factory.url.pkgs", "java.naming.factory.state", "java.naming.provider.url", "java.naming.dns.url", "java.naming.factory.control"};
   public static final int INITIAL_CONTEXT_FACTORY = 0;
   public static final int OBJECT_FACTORIES = 1;
   public static final int URL_PKG_PREFIXES = 2;
   public static final int STATE_FACTORIES = 3;
   public static final int PROVIDER_URL = 4;
   public static final int DNS_URL = 5;
   public static final int CONTROL_FACTORIES = 6;

   VersionHelper() {
   }

   public static VersionHelper getVersionHelper() {
      return helper;
   }

   public abstract Class<?> loadClass(String var1) throws ClassNotFoundException;

   abstract Class<?> loadClass(String var1, ClassLoader var2) throws ClassNotFoundException;

   public abstract Class<?> loadClass(String var1, String var2) throws ClassNotFoundException, MalformedURLException;

   abstract String getJndiProperty(int var1);

   abstract String[] getJndiProperties();

   abstract InputStream getResourceAsStream(Class<?> var1, String var2);

   abstract InputStream getJavaHomeLibStream(String var1);

   abstract NamingEnumeration<InputStream> getResources(ClassLoader var1, String var2) throws IOException;

   abstract ClassLoader getContextClassLoader();

   protected static URL[] getUrlArray(String var0) throws MalformedURLException {
      StringTokenizer var1 = new StringTokenizer(var0);
      Vector var2 = new Vector(10);

      while(var1.hasMoreTokens()) {
         var2.addElement(var1.nextToken());
      }

      String[] var3 = new String[var2.size()];

      for(int var4 = 0; var4 < var3.length; ++var4) {
         var3[var4] = (String)var2.elementAt(var4);
      }

      URL[] var6 = new URL[var3.length];

      for(int var5 = 0; var5 < var6.length; ++var5) {
         var6[var5] = new URL(var3[var5]);
      }

      return var6;
   }

   static {
      helper = new VersionHelper12();
   }
}
