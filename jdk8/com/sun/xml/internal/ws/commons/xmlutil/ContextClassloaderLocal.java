package com.sun.xml.internal.ws.commons.xmlutil;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.WeakHashMap;

abstract class ContextClassloaderLocal<V> {
   private static final String FAILED_TO_CREATE_NEW_INSTANCE = "FAILED_TO_CREATE_NEW_INSTANCE";
   private WeakHashMap<ClassLoader, V> CACHE = new WeakHashMap();

   public V get() throws Error {
      ClassLoader tccl = getContextClassLoader();
      V instance = this.CACHE.get(tccl);
      if (instance == null) {
         instance = this.createNewInstance();
         this.CACHE.put(tccl, instance);
      }

      return instance;
   }

   public void set(V instance) {
      this.CACHE.put(getContextClassLoader(), instance);
   }

   protected abstract V initialValue() throws Exception;

   private V createNewInstance() {
      try {
         return this.initialValue();
      } catch (Exception var2) {
         throw new Error(format("FAILED_TO_CREATE_NEW_INSTANCE", this.getClass().getName()), var2);
      }
   }

   private static String format(String property, Object... args) {
      String text = ResourceBundle.getBundle(ContextClassloaderLocal.class.getName()).getString(property);
      return MessageFormat.format(text, args);
   }

   private static ClassLoader getContextClassLoader() {
      return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            ClassLoader cl = null;

            try {
               cl = Thread.currentThread().getContextClassLoader();
            } catch (SecurityException var3) {
            }

            return cl;
         }
      });
   }
}
