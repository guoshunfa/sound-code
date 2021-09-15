package com.oracle.webservices.internal.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class BaseDistributedPropertySet extends BasePropertySet implements DistributedPropertySet {
   private final Map<Class<? extends PropertySet>, PropertySet> satellites = new IdentityHashMap();
   private final Map<String, Object> viewthis = super.createView();

   public void addSatellite(@NotNull PropertySet satellite) {
      this.addSatellite(satellite.getClass(), satellite);
   }

   public void addSatellite(@NotNull Class<? extends PropertySet> keyClass, @NotNull PropertySet satellite) {
      this.satellites.put(keyClass, satellite);
   }

   public void removeSatellite(PropertySet satellite) {
      this.satellites.remove(satellite.getClass());
   }

   public void copySatelliteInto(@NotNull DistributedPropertySet r) {
      Iterator var2 = this.satellites.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<Class<? extends PropertySet>, PropertySet> entry = (Map.Entry)var2.next();
         r.addSatellite((Class)entry.getKey(), (PropertySet)entry.getValue());
      }

   }

   public void copySatelliteInto(MessageContext r) {
      this.copySatelliteInto((DistributedPropertySet)r);
   }

   @Nullable
   public <T extends PropertySet> T getSatellite(Class<T> satelliteClass) {
      T satellite = (PropertySet)this.satellites.get(satelliteClass);
      if (satellite != null) {
         return satellite;
      } else {
         Iterator var3 = this.satellites.values().iterator();

         while(var3.hasNext()) {
            PropertySet child = (PropertySet)var3.next();
            if (satelliteClass.isInstance(child)) {
               return (PropertySet)satelliteClass.cast(child);
            }

            if (DistributedPropertySet.class.isInstance(child)) {
               satellite = ((DistributedPropertySet)DistributedPropertySet.class.cast(child)).getSatellite(satelliteClass);
               if (satellite != null) {
                  return satellite;
               }
            }
         }

         return null;
      }
   }

   public Map<Class<? extends PropertySet>, PropertySet> getSatellites() {
      return this.satellites;
   }

   public Object get(Object key) {
      Iterator var2 = this.satellites.values().iterator();

      PropertySet child;
      do {
         if (!var2.hasNext()) {
            return super.get(key);
         }

         child = (PropertySet)var2.next();
      } while(!child.supports(key));

      return child.get(key);
   }

   public Object put(String key, Object value) {
      Iterator var3 = this.satellites.values().iterator();

      PropertySet child;
      do {
         if (!var3.hasNext()) {
            return super.put(key, value);
         }

         child = (PropertySet)var3.next();
      } while(!child.supports(key));

      return child.put(key, value);
   }

   public boolean containsKey(Object key) {
      if (this.viewthis.containsKey(key)) {
         return true;
      } else {
         Iterator var2 = this.satellites.values().iterator();

         PropertySet child;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            child = (PropertySet)var2.next();
         } while(!child.containsKey(key));

         return true;
      }
   }

   public boolean supports(Object key) {
      Iterator var2 = this.satellites.values().iterator();

      PropertySet child;
      do {
         if (!var2.hasNext()) {
            return super.supports(key);
         }

         child = (PropertySet)var2.next();
      } while(!child.supports(key));

      return true;
   }

   public Object remove(Object key) {
      Iterator var2 = this.satellites.values().iterator();

      PropertySet child;
      do {
         if (!var2.hasNext()) {
            return super.remove(key);
         }

         child = (PropertySet)var2.next();
      } while(!child.supports(key));

      return child.remove(key);
   }

   protected void createEntrySet(Set<Map.Entry<String, Object>> core) {
      super.createEntrySet(core);
      Iterator var2 = this.satellites.values().iterator();

      while(var2.hasNext()) {
         PropertySet child = (PropertySet)var2.next();
         ((BasePropertySet)child).createEntrySet(core);
      }

   }

   protected Map<String, Object> asMapLocal() {
      return this.viewthis;
   }

   protected boolean supportsLocal(Object key) {
      return super.supports(key);
   }

   protected Map<String, Object> createView() {
      return new BaseDistributedPropertySet.DistributedMapView();
   }

   class DistributedMapView extends AbstractMap<String, Object> {
      public Object get(Object key) {
         Iterator var2 = BaseDistributedPropertySet.this.satellites.values().iterator();

         PropertySet child;
         do {
            if (!var2.hasNext()) {
               return BaseDistributedPropertySet.this.viewthis.get(key);
            }

            child = (PropertySet)var2.next();
         } while(!child.supports(key));

         return child.get(key);
      }

      public int size() {
         int size = BaseDistributedPropertySet.this.viewthis.size();

         PropertySet child;
         for(Iterator var2 = BaseDistributedPropertySet.this.satellites.values().iterator(); var2.hasNext(); size += child.asMap().size()) {
            child = (PropertySet)var2.next();
         }

         return size;
      }

      public boolean containsKey(Object key) {
         if (BaseDistributedPropertySet.this.viewthis.containsKey(key)) {
            return true;
         } else {
            Iterator var2 = BaseDistributedPropertySet.this.satellites.values().iterator();

            PropertySet child;
            do {
               if (!var2.hasNext()) {
                  return false;
               }

               child = (PropertySet)var2.next();
            } while(!child.containsKey(key));

            return true;
         }
      }

      public Set<Map.Entry<String, Object>> entrySet() {
         Set<Map.Entry<String, Object>> entries = new HashSet();
         Iterator var2 = BaseDistributedPropertySet.this.satellites.values().iterator();

         while(var2.hasNext()) {
            PropertySet child = (PropertySet)var2.next();
            Iterator var4 = child.asMap().entrySet().iterator();

            while(var4.hasNext()) {
               Map.Entry<String, Object> entryx = (Map.Entry)var4.next();
               entries.add(new AbstractMap.SimpleImmutableEntry(entryx.getKey(), entryx.getValue()));
            }
         }

         var2 = BaseDistributedPropertySet.this.viewthis.entrySet().iterator();

         while(var2.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry)var2.next();
            entries.add(new AbstractMap.SimpleImmutableEntry(entry.getKey(), entry.getValue()));
         }

         return entries;
      }

      public Object put(String key, Object value) {
         Iterator var3 = BaseDistributedPropertySet.this.satellites.values().iterator();

         PropertySet child;
         do {
            if (!var3.hasNext()) {
               return BaseDistributedPropertySet.this.viewthis.put(key, value);
            }

            child = (PropertySet)var3.next();
         } while(!child.supports(key));

         return child.put(key, value);
      }

      public void clear() {
         BaseDistributedPropertySet.this.satellites.clear();
         BaseDistributedPropertySet.this.viewthis.clear();
      }

      public Object remove(Object key) {
         Iterator var2 = BaseDistributedPropertySet.this.satellites.values().iterator();

         PropertySet child;
         do {
            if (!var2.hasNext()) {
               return BaseDistributedPropertySet.this.viewthis.remove(key);
            }

            child = (PropertySet)var2.next();
         } while(!child.supports(key));

         return child.remove(key);
      }
   }
}
