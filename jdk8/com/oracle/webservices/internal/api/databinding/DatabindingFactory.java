package com.oracle.webservices.internal.api.databinding;

import java.util.Map;

public abstract class DatabindingFactory {
   static final String ImplClass = "com.sun.xml.internal.ws.db.DatabindingFactoryImpl";

   public abstract Databinding.Builder createBuilder(Class<?> var1, Class<?> var2);

   public abstract Map<String, Object> properties();

   public static DatabindingFactory newInstance() {
      try {
         Class<?> cls = Class.forName("com.sun.xml.internal.ws.db.DatabindingFactoryImpl");
         return convertIfNecessary(cls);
      } catch (Exception var1) {
         var1.printStackTrace();
         return null;
      }
   }

   private static DatabindingFactory convertIfNecessary(Class<?> cls) throws InstantiationException, IllegalAccessException {
      return (DatabindingFactory)cls.newInstance();
   }
}
