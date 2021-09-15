package com.sun.java.browser.net;

import java.io.IOException;
import java.net.URL;

public class ProxyService {
   private static ProxyServiceProvider provider = null;

   public static void setProvider(ProxyServiceProvider var0) throws IOException {
      if (null == provider) {
         provider = var0;
      } else {
         throw new IOException("Proxy service provider has already been set.");
      }
   }

   public static ProxyInfo[] getProxyInfo(URL var0) throws IOException {
      if (null == provider) {
         throw new IOException("Proxy service provider is not yet set");
      } else {
         return provider.getProxyInfo(var0);
      }
   }
}
