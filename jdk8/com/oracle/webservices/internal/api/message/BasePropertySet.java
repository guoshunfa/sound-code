package com.oracle.webservices.internal.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class BasePropertySet implements PropertySet {
   private Map<String, Object> mapView;

   protected BasePropertySet() {
   }

   protected abstract BasePropertySet.PropertyMap getPropertyMap();

   protected static BasePropertySet.PropertyMap parse(final Class clazz) {
      return (BasePropertySet.PropertyMap)AccessController.doPrivileged(new PrivilegedAction<BasePropertySet.PropertyMap>() {
         public BasePropertySet.PropertyMap run() {
            BasePropertySet.PropertyMap props = new BasePropertySet.PropertyMap();

            for(Class c = clazz; c != null; c = c.getSuperclass()) {
               Field[] var3 = c.getDeclaredFields();
               int var4 = var3.length;

               int var5;
               PropertySet.Property cp;
               for(var5 = 0; var5 < var4; ++var5) {
                  Field f = var3[var5];
                  cp = (PropertySet.Property)f.getAnnotation(PropertySet.Property.class);
                  if (cp != null) {
                     String[] var8 = cp.value();
                     int var9 = var8.length;

                     for(int var10 = 0; var10 < var9; ++var10) {
                        String value = var8[var10];
                        props.put(value, new BasePropertySet.FieldAccessor(f, value));
                     }
                  }
               }

               Method[] var16 = c.getDeclaredMethods();
               var4 = var16.length;

               for(var5 = 0; var5 < var4; ++var5) {
                  Method m = var16[var5];
                  cp = (PropertySet.Property)m.getAnnotation(PropertySet.Property.class);
                  if (cp != null) {
                     String name = m.getName();

                     assert name.startsWith("get") || name.startsWith("is");

                     String setName = name.startsWith("is") ? "set" + name.substring(2) : 's' + name.substring(1);

                     Method setter;
                     try {
                        setter = clazz.getMethod(setName, m.getReturnType());
                     } catch (NoSuchMethodException var15) {
                        setter = null;
                     }

                     String[] var21 = cp.value();
                     int var12 = var21.length;

                     for(int var13 = 0; var13 < var12; ++var13) {
                        String valuex = var21[var13];
                        props.put(valuex, new BasePropertySet.MethodAccessor(m, setter, valuex));
                     }
                  }
               }
            }

            return props;
         }
      });
   }

   public boolean containsKey(Object key) {
      BasePropertySet.Accessor sp = (BasePropertySet.Accessor)this.getPropertyMap().get(key);
      if (sp != null) {
         return sp.get(this) != null;
      } else {
         return false;
      }
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

   /** @deprecated */
   @Deprecated
   public final Map<String, Object> createMapView() {
      final Set<Map.Entry<String, Object>> core = new HashSet();
      this.createEntrySet(core);
      return new AbstractMap<String, Object>() {
         public Set<Map.Entry<String, Object>> entrySet() {
            return core;
         }
      };
   }

   public Map<String, Object> asMap() {
      if (this.mapView == null) {
         this.mapView = this.createView();
      }

      return this.mapView;
   }

   protected Map<String, Object> createView() {
      return new BasePropertySet.MapView(this.mapAllowsAdditionalProperties());
   }

   protected boolean mapAllowsAdditionalProperties() {
      return false;
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
               return ((BasePropertySet.Accessor)e.getValue()).get(BasePropertySet.this);
            }

            public Object setValue(Object value) {
               BasePropertySet.Accessor acc = (BasePropertySet.Accessor)e.getValue();
               Object old = acc.get(BasePropertySet.this);
               acc.set(BasePropertySet.this, value);
               return old;
            }
         });
      }

   }

   final class MapView extends HashMap<String, Object> {
      boolean extensible;

      MapView(boolean extensible) {
         super(BasePropertySet.this.getPropertyMap().getPropertyMapEntries().length);
         this.extensible = extensible;
         this.initialize();
      }

      public void initialize() {
         BasePropertySet.PropertyMapEntry[] entries = BasePropertySet.this.getPropertyMap().getPropertyMapEntries();
         BasePropertySet.PropertyMapEntry[] var2 = entries;
         int var3 = entries.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            BasePropertySet.PropertyMapEntry entry = var2[var4];
            super.put(entry.key, entry.value);
         }

      }

      public Object get(Object key) {
         Object o = super.get(key);
         return o instanceof BasePropertySet.Accessor ? ((BasePropertySet.Accessor)o).get(BasePropertySet.this) : o;
      }

      public Set<Map.Entry<String, Object>> entrySet() {
         Set<Map.Entry<String, Object>> entries = new HashSet();
         Iterator var2 = this.keySet().iterator();

         while(var2.hasNext()) {
            String key = (String)var2.next();
            entries.add(new AbstractMap.SimpleImmutableEntry(key, this.get(key)));
         }

         return entries;
      }

      public Object put(String key, Object value) {
         Object o = super.get(key);
         if (o != null && o instanceof BasePropertySet.Accessor) {
            Object oldValue = ((BasePropertySet.Accessor)o).get(BasePropertySet.this);
            ((BasePropertySet.Accessor)o).set(BasePropertySet.this, value);
            return oldValue;
         } else if (this.extensible) {
            return super.put(key, value);
         } else {
            throw new IllegalStateException("Unknown property [" + key + "] for PropertySet [" + BasePropertySet.this.getClass().getName() + "]");
         }
      }

      public void clear() {
         Iterator var1 = this.keySet().iterator();

         while(var1.hasNext()) {
            String key = (String)var1.next();
            this.remove(key);
         }

      }

      public Object remove(Object key) {
         Object o = super.get(key);
         if (o instanceof BasePropertySet.Accessor) {
            ((BasePropertySet.Accessor)o).set(BasePropertySet.this, (Object)null);
         }

         return super.remove(key);
      }
   }

   static final class MethodAccessor implements BasePropertySet.Accessor {
      @NotNull
      private final Method getter;
      @Nullable
      private final Method setter;
      private final String name;

      protected MethodAccessor(Method getter, Method setter, String value) {
         this.getter = getter;
         this.setter = setter;
         this.name = value;
         getter.setAccessible(true);
         if (setter != null) {
            setter.setAccessible(true);
         }

      }

      public String getName() {
         return this.name;
      }

      public boolean hasValue(PropertySet props) {
         return this.get(props) != null;
      }

      public Object get(PropertySet props) {
         try {
            return this.getter.invoke(props);
         } catch (IllegalAccessException var3) {
            throw new AssertionError();
         } catch (InvocationTargetException var4) {
            this.handle(var4);
            return 0;
         }
      }

      public void set(PropertySet props, Object value) {
         if (this.setter == null) {
            throw new ReadOnlyPropertyException(this.getName());
         } else {
            try {
               this.setter.invoke(props, value);
            } catch (IllegalAccessException var4) {
               throw new AssertionError();
            } catch (InvocationTargetException var5) {
               this.handle(var5);
            }

         }
      }

      private Exception handle(InvocationTargetException e) {
         Throwable t = e.getTargetException();
         if (t instanceof Error) {
            throw (Error)t;
         } else if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
         } else {
            throw new Error(e);
         }
      }
   }

   static final class FieldAccessor implements BasePropertySet.Accessor {
      private final Field f;
      private final String name;

      protected FieldAccessor(Field f, String name) {
         this.f = f;
         f.setAccessible(true);
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public boolean hasValue(PropertySet props) {
         return this.get(props) != null;
      }

      public Object get(PropertySet props) {
         try {
            return this.f.get(props);
         } catch (IllegalAccessException var3) {
            throw new AssertionError();
         }
      }

      public void set(PropertySet props, Object value) {
         try {
            this.f.set(props, value);
         } catch (IllegalAccessException var4) {
            throw new AssertionError();
         }
      }
   }

   protected interface Accessor {
      String getName();

      boolean hasValue(PropertySet var1);

      Object get(PropertySet var1);

      void set(PropertySet var1, Object var2);
   }

   public static class PropertyMapEntry {
      String key;
      BasePropertySet.Accessor value;

      public PropertyMapEntry(String k, BasePropertySet.Accessor v) {
         this.key = k;
         this.value = v;
      }
   }

   protected static class PropertyMap extends HashMap<String, BasePropertySet.Accessor> {
      transient BasePropertySet.PropertyMapEntry[] cachedEntries = null;

      BasePropertySet.PropertyMapEntry[] getPropertyMapEntries() {
         if (this.cachedEntries == null) {
            this.cachedEntries = this.createPropertyMapEntries();
         }

         return this.cachedEntries;
      }

      private BasePropertySet.PropertyMapEntry[] createPropertyMapEntries() {
         BasePropertySet.PropertyMapEntry[] modelEntries = new BasePropertySet.PropertyMapEntry[this.size()];
         int i = 0;

         Map.Entry e;
         for(Iterator var3 = this.entrySet().iterator(); var3.hasNext(); modelEntries[i++] = new BasePropertySet.PropertyMapEntry((String)e.getKey(), (BasePropertySet.Accessor)e.getValue())) {
            e = (Map.Entry)var3.next();
         }

         return modelEntries;
      }
   }
}
