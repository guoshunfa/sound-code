package com.oracle.webservices.internal.api.message;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

public interface PropertySet {
   boolean containsKey(Object var1);

   Object get(Object var1);

   Object put(String var1, Object var2);

   boolean supports(Object var1);

   Object remove(Object var1);

   /** @deprecated */
   @Deprecated
   Map<String, Object> createMapView();

   Map<String, Object> asMap();

   @Inherited
   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD, ElementType.METHOD})
   public @interface Property {
      String[] value();
   }
}
