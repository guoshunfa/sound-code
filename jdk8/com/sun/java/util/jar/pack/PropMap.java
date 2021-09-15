package com.sun.java.util.jar.pack;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

final class PropMap implements SortedMap<String, String> {
   private final TreeMap<String, String> theMap = new TreeMap();
   private final List<Object> listenerList = new ArrayList(1);
   private static Map<String, String> defaultProps;

   void addListener(Object var1) {
      assert PropMap.Beans.isPropertyChangeListener(var1);

      this.listenerList.add(var1);
   }

   void removeListener(Object var1) {
      assert PropMap.Beans.isPropertyChangeListener(var1);

      this.listenerList.remove(var1);
   }

   public String put(String var1, String var2) {
      String var3 = (String)this.theMap.put(var1, var2);
      if (var2 != var3 && !this.listenerList.isEmpty()) {
         assert PropMap.Beans.isBeansPresent();

         Object var4 = PropMap.Beans.newPropertyChangeEvent(this, var1, var3, var2);
         Iterator var5 = this.listenerList.iterator();

         while(var5.hasNext()) {
            Object var6 = var5.next();
            PropMap.Beans.invokePropertyChange(var6, var4);
         }
      }

      return var3;
   }

   PropMap() {
      this.theMap.putAll(defaultProps);
   }

   SortedMap<String, String> prefixMap(String var1) {
      int var2 = var1.length();
      if (var2 == 0) {
         return this;
      } else {
         char var3 = (char)(var1.charAt(var2 - 1) + 1);
         String var4 = var1.substring(0, var2 - 1) + var3;
         return this.subMap(var1, var4);
      }
   }

   String getProperty(String var1) {
      return this.get(var1);
   }

   String getProperty(String var1, String var2) {
      String var3 = this.getProperty(var1);
      return var3 == null ? var2 : var3;
   }

   String setProperty(String var1, String var2) {
      return this.put(var1, var2);
   }

   List<String> getProperties(String var1) {
      Collection var2 = this.prefixMap(var1).values();
      ArrayList var3 = new ArrayList(var2.size());
      var3.addAll(var2);

      while(var3.remove((Object)null)) {
      }

      return var3;
   }

   private boolean toBoolean(String var1) {
      return Boolean.valueOf(var1);
   }

   boolean getBoolean(String var1) {
      return this.toBoolean(this.getProperty(var1));
   }

   boolean setBoolean(String var1, boolean var2) {
      return this.toBoolean(this.setProperty(var1, String.valueOf(var2)));
   }

   int toInteger(String var1) {
      return this.toInteger(var1, 0);
   }

   int toInteger(String var1, int var2) {
      if (var1 == null) {
         return var2;
      } else if ("true".equals(var1)) {
         return 1;
      } else {
         return "false".equals(var1) ? 0 : Integer.parseInt(var1);
      }
   }

   int getInteger(String var1, int var2) {
      return this.toInteger(this.getProperty(var1), var2);
   }

   int getInteger(String var1) {
      return this.toInteger(this.getProperty(var1));
   }

   int setInteger(String var1, int var2) {
      return this.toInteger(this.setProperty(var1, String.valueOf(var2)));
   }

   long toLong(String var1) {
      try {
         return var1 == null ? 0L : Long.parseLong(var1);
      } catch (NumberFormatException var3) {
         throw new IllegalArgumentException("Invalid value");
      }
   }

   long getLong(String var1) {
      return this.toLong(this.getProperty(var1));
   }

   long setLong(String var1, long var2) {
      return this.toLong(this.setProperty(var1, String.valueOf(var2)));
   }

   int getTime(String var1) {
      String var2 = this.getProperty(var1, "0");
      if ("now".equals(var2)) {
         return (int)((System.currentTimeMillis() + 500L) / 1000L);
      } else {
         long var3 = this.toLong(var2);
         if (var3 < 10000000000L && !"0".equals(var2)) {
            Utils.log.warning("Supplied modtime appears to be seconds rather than milliseconds: " + var2);
         }

         return (int)((var3 + 500L) / 1000L);
      }
   }

   void list(PrintStream var1) {
      PrintWriter var2 = new PrintWriter(var1);
      this.list(var2);
      var2.flush();
   }

   void list(PrintWriter var1) {
      var1.println("#PACK200[");
      Set var2 = defaultProps.entrySet();
      Iterator var3 = this.theMap.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry var4 = (Map.Entry)var3.next();
         if (!var2.contains(var4)) {
            var1.println("  " + (String)var4.getKey() + " = " + (String)var4.getValue());
         }
      }

      var1.println("#]");
   }

   public int size() {
      return this.theMap.size();
   }

   public boolean isEmpty() {
      return this.theMap.isEmpty();
   }

   public boolean containsKey(Object var1) {
      return this.theMap.containsKey(var1);
   }

   public boolean containsValue(Object var1) {
      return this.theMap.containsValue(var1);
   }

   public String get(Object var1) {
      return (String)this.theMap.get(var1);
   }

   public String remove(Object var1) {
      return (String)this.theMap.remove(var1);
   }

   public void putAll(Map<? extends String, ? extends String> var1) {
      this.theMap.putAll(var1);
   }

   public void clear() {
      this.theMap.clear();
   }

   public Set<String> keySet() {
      return this.theMap.keySet();
   }

   public Collection<String> values() {
      return this.theMap.values();
   }

   public Set<Map.Entry<String, String>> entrySet() {
      return this.theMap.entrySet();
   }

   public Comparator<? super String> comparator() {
      return this.theMap.comparator();
   }

   public SortedMap<String, String> subMap(String var1, String var2) {
      return this.theMap.subMap(var1, var2);
   }

   public SortedMap<String, String> headMap(String var1) {
      return this.theMap.headMap(var1);
   }

   public SortedMap<String, String> tailMap(String var1) {
      return this.theMap.tailMap(var1);
   }

   public String firstKey() {
      return (String)this.theMap.firstKey();
   }

   public String lastKey() {
      return (String)this.theMap.lastKey();
   }

   static {
      Properties var0 = new Properties();
      var0.put("com.sun.java.util.jar.pack.disable.native", String.valueOf(Boolean.getBoolean("com.sun.java.util.jar.pack.disable.native")));
      var0.put("com.sun.java.util.jar.pack.verbose", String.valueOf((Object)Integer.getInteger("com.sun.java.util.jar.pack.verbose", 0)));
      var0.put("com.sun.java.util.jar.pack.default.timezone", String.valueOf(Boolean.getBoolean("com.sun.java.util.jar.pack.default.timezone")));
      var0.put("pack.segment.limit", "-1");
      var0.put("pack.keep.file.order", "true");
      var0.put("pack.modification.time", "keep");
      var0.put("pack.deflate.hint", "keep");
      var0.put("pack.unknown.attribute", "pass");
      var0.put("com.sun.java.util.jar.pack.class.format.error", System.getProperty("com.sun.java.util.jar.pack.class.format.error", "pass"));
      var0.put("pack.effort", "5");
      String var1 = "intrinsic.properties";

      try {
         InputStream var2 = PackerImpl.class.getResourceAsStream(var1);
         Throwable var3 = null;

         try {
            if (var2 == null) {
               throw new RuntimeException(var1 + " cannot be loaded");
            }

            var0.load(var2);
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  var2.close();
               }
            }

         }
      } catch (IOException var15) {
         throw new RuntimeException(var15);
      }

      Iterator var16 = var0.entrySet().iterator();

      while(var16.hasNext()) {
         Map.Entry var18 = (Map.Entry)var16.next();
         String var4 = (String)var18.getKey();
         String var5 = (String)var18.getValue();
         if (var4.startsWith("attribute.")) {
            var18.setValue(Attribute.normalizeLayoutString(var5));
         }
      }

      HashMap var17 = new HashMap(var0);
      defaultProps = var17;
   }

   private static class Beans {
      private static final Class<?> propertyChangeListenerClass = getClass("java.beans.PropertyChangeListener");
      private static final Class<?> propertyChangeEventClass = getClass("java.beans.PropertyChangeEvent");
      private static final Method propertyChangeMethod;
      private static final Constructor<?> propertyEventCtor;

      private static Class<?> getClass(String var0) {
         try {
            return Class.forName(var0, true, PropMap.Beans.class.getClassLoader());
         } catch (ClassNotFoundException var2) {
            return null;
         }
      }

      private static Constructor<?> getConstructor(Class<?> var0, Class<?>... var1) {
         try {
            return var0 == null ? null : var0.getDeclaredConstructor(var1);
         } catch (NoSuchMethodException var3) {
            throw new AssertionError(var3);
         }
      }

      private static Method getMethod(Class<?> var0, String var1, Class<?>... var2) {
         try {
            return var0 == null ? null : var0.getMethod(var1, var2);
         } catch (NoSuchMethodException var4) {
            throw new AssertionError(var4);
         }
      }

      static boolean isBeansPresent() {
         return propertyChangeListenerClass != null && propertyChangeEventClass != null;
      }

      static boolean isPropertyChangeListener(Object var0) {
         return propertyChangeListenerClass == null ? false : propertyChangeListenerClass.isInstance(var0);
      }

      static Object newPropertyChangeEvent(Object var0, String var1, Object var2, Object var3) {
         try {
            return propertyEventCtor.newInstance(var0, var1, var2, var3);
         } catch (IllegalAccessException | InstantiationException var6) {
            throw new AssertionError(var6);
         } catch (InvocationTargetException var7) {
            Throwable var5 = var7.getCause();
            if (var5 instanceof Error) {
               throw (Error)var5;
            } else if (var5 instanceof RuntimeException) {
               throw (RuntimeException)var5;
            } else {
               throw new AssertionError(var7);
            }
         }
      }

      static void invokePropertyChange(Object var0, Object var1) {
         try {
            propertyChangeMethod.invoke(var0, var1);
         } catch (IllegalAccessException var4) {
            throw new AssertionError(var4);
         } catch (InvocationTargetException var5) {
            Throwable var3 = var5.getCause();
            if (var3 instanceof Error) {
               throw (Error)var3;
            } else if (var3 instanceof RuntimeException) {
               throw (RuntimeException)var3;
            } else {
               throw new AssertionError(var5);
            }
         }
      }

      static {
         propertyChangeMethod = getMethod(propertyChangeListenerClass, "propertyChange", propertyChangeEventClass);
         propertyEventCtor = getConstructor(propertyChangeEventClass, Object.class, String.class, Object.class, Object.class);
      }
   }
}
