package com.sun.xml.internal.ws.api;

import com.oracle.webservices.internal.api.message.BasePropertySet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/** @deprecated */
public abstract class PropertySet extends BasePropertySet {
   /** @deprecated */
   protected static PropertySet.PropertyMap parse(Class clazz) {
      BasePropertySet.PropertyMap pm = BasePropertySet.parse(clazz);
      PropertySet.PropertyMap map = new PropertySet.PropertyMap();
      map.putAll(pm);
      return map;
   }

   public Object get(Object key) {
      BasePropertySet.Accessor sp = (BasePropertySet.Accessor)this.getPropertyMap().get(key);
      if (sp != null) {
         return sp.get(this);
      } else {
         throw new IllegalArgumentException("Undefined property " + key);
      }
   }

   public Object put(String key, Object value) {
      BasePropertySet.Accessor sp = (BasePropertySet.Accessor)this.getPropertyMap().get(key);
      if (sp != null) {
         Object old = sp.get(this);
         sp.set(this, value);
         return old;
      } else {
         throw new IllegalArgumentException("Undefined property " + key);
      }
   }

   public boolean supports(Object key) {
      return this.getPropertyMap().containsKey(key);
   }

   public Object remove(Object key) {
      BasePropertySet.Accessor sp = (BasePropertySet.Accessor)this.getPropertyMap().get(key);
      if (sp != null) {
         Object old = sp.get(this);
         sp.set(this, (Object)null);
         return old;
      } else {
         throw new IllegalArgumentException("Undefined property " + key);
      }
   }

   protected void createEntrySet(Set<Map.Entry<String, Object>> core) {
      Iterator var2 = this.getPropertyMap().entrySet().iterator();

      while(var2.hasNext()) {
         final Map.Entry<String, BasePropertySet.Accessor> e = (Map.Entry)var2.next();
         core.add(new Map.Entry<String, Object>() {
            public String getKey() {
               return (String)e.getKey();
            }

            public Object getValue() {
               return ((BasePropertySet.Accessor)e.getValue()).get(PropertySet.this);
            }

            public Object setValue(Object value) {
               BasePropertySet.Accessor acc = (BasePropertySet.Accessor)e.getValue();
               Object old = acc.get(PropertySet.this);
               acc.set(PropertySet.this, value);
               return old;
            }
         });
      }

   }

   protected abstract PropertySet.PropertyMap getPropertyMap();

   /** @deprecated */
   protected static class PropertyMap extends BasePropertySet.PropertyMap {
   }
}
