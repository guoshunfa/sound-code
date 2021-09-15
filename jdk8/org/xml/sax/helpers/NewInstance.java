package org.xml.sax.helpers;

class NewInstance {
   private static final String DEFAULT_PACKAGE = "com.sun.org.apache.xerces.internal";

   static Object newInstance(ClassLoader classLoader, String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
      boolean internal = false;
      if (System.getSecurityManager() != null && className != null && className.startsWith("com.sun.org.apache.xerces.internal")) {
         internal = true;
      }

      Class driverClass;
      if (classLoader != null && !internal) {
         driverClass = classLoader.loadClass(className);
      } else {
         driverClass = Class.forName(className);
      }

      return driverClass.newInstance();
   }
}
