package com.sun.org.apache.xml.internal.security.utils;

public abstract class XPathFactory {
   private static boolean xalanInstalled;

   protected static synchronized boolean isXalanInstalled() {
      return xalanInstalled;
   }

   public static XPathFactory newInstance() {
      if (!isXalanInstalled()) {
         return new JDKXPathFactory();
      } else {
         return (XPathFactory)(XalanXPathAPI.isInstalled() ? new XalanXPathFactory() : new JDKXPathFactory());
      }
   }

   public abstract XPathAPI newXPathAPI();

   static {
      try {
         Class var0 = ClassLoaderUtils.loadClass("com.sun.org.apache.xpath.internal.compiler.FunctionTable", XPathFactory.class);
         if (var0 != null) {
            xalanInstalled = true;
         }
      } catch (Exception var1) {
      }

   }
}
