package com.sun.xml.internal.ws.api.databinding;

import com.sun.xml.internal.ws.db.DatabindingFactoryImpl;
import java.util.Map;

public abstract class DatabindingFactory extends com.oracle.webservices.internal.api.databinding.DatabindingFactory {
   static final String ImplClass = DatabindingFactoryImpl.class.getName();

   public abstract com.oracle.webservices.internal.api.databinding.Databinding createRuntime(DatabindingConfig var1);

   public abstract Map<String, Object> properties();

   public static DatabindingFactory newInstance() {
      try {
         Class<?> cls = Class.forName(ImplClass);
         return (DatabindingFactory)cls.newInstance();
      } catch (Exception var1) {
         var1.printStackTrace();
         return null;
      }
   }
}
