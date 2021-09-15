package com.sun.java.browser.dom;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;

public abstract class DOMService {
   public static DOMService getService(Object var0) throws DOMUnsupportedException {
      try {
         String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("com.sun.java.browser.dom.DOMServiceProvider")));
         Class var10000 = DOMService.class;
         Class var2 = Class.forName("sun.plugin.dom.DOMService");
         return (DOMService)var2.newInstance();
      } catch (Throwable var3) {
         throw new DOMUnsupportedException(var3.toString());
      }
   }

   public abstract Object invokeAndWait(DOMAction var1) throws DOMAccessException;

   public abstract void invokeLater(DOMAction var1);
}
