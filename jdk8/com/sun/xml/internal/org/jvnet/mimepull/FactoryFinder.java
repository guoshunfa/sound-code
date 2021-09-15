package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

class FactoryFinder {
   private static ClassLoader cl = FactoryFinder.class.getClassLoader();

   static Object find(String factoryId) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
      String systemProp = System.getProperty(factoryId);
      if (systemProp != null) {
         return newInstance(systemProp);
      } else {
         String providerName = findJarServiceProviderName(factoryId);
         return providerName != null && providerName.trim().length() > 0 ? newInstance(providerName) : null;
      }
   }

   static Object newInstance(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
      Class providerClass = cl.loadClass(className);
      Object instance = providerClass.newInstance();
      return instance;
   }

   private static String findJarServiceProviderName(String factoryId) {
      String serviceId = "META-INF/services/" + factoryId;
      InputStream is = cl.getResourceAsStream(serviceId);
      if (is == null) {
         return null;
      } else {
         BufferedReader rd = null;

         Object var6;
         try {
            try {
               rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            } catch (UnsupportedEncodingException var17) {
               rd = new BufferedReader(new InputStreamReader(is));
            }

            try {
               String factoryClassName = rd.readLine();
               return factoryClassName;
            } catch (IOException var18) {
               var6 = null;
            }
         } finally {
            if (rd != null) {
               try {
                  rd.close();
               } catch (IOException var16) {
                  Logger.getLogger(FactoryFinder.class.getName()).log(Level.INFO, (String)null, (Throwable)var16);
               }
            }

         }

         return (String)var6;
      }
   }
}
