package javax.management.openmbean;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Util;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import sun.misc.SharedSecrets;

public class TabularDataSupport implements TabularData, Map<Object, Object>, Cloneable, Serializable {
   static final long serialVersionUID = 5720150593236309827L;
   private Map<Object, CompositeData> dataMap;
   private final TabularType tabularType;
   private transient String[] indexNamesArray;

   public TabularDataSupport(TabularType var1) {
      this(var1, 16, 0.75F);
   }

   public TabularDataSupport(TabularType var1, int var2, float var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("Argument tabularType cannot be null.");
      } else {
         this.tabularType = var1;
         List var4 = var1.getIndexNames();
         this.indexNamesArray = (String[])var4.toArray(new String[var4.size()]);
         String var5 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("jmx.tabular.data.hash.map")));
         boolean var6 = "true".equalsIgnoreCase(var5);
         this.dataMap = (Map)(var6 ? new HashMap(var2, var3) : new LinkedHashMap(var2, var3));
      }
   }

   public TabularType getTabularType() {
      return this.tabularType;
   }

   public Object[] calculateIndex(CompositeData var1) {
      this.checkValueType(var1);
      return this.internalCalculateIndex(var1).toArray();
   }

   public boolean containsKey(Object var1) {
      Object[] var2;
      try {
         var2 = (Object[])((Object[])var1);
      } catch (ClassCastException var4) {
         return false;
      }

      return this.containsKey(var2);
   }

   public boolean containsKey(Object[] var1) {
      return var1 == null ? false : this.dataMap.containsKey(Arrays.asList(var1));
   }

   public boolean containsValue(CompositeData var1) {
      return this.dataMap.containsValue(var1);
   }

   public boolean containsValue(Object var1) {
      return this.dataMap.containsValue(var1);
   }

   public Object get(Object var1) {
      return this.get((Object[])((Object[])var1));
   }

   public CompositeData get(Object[] var1) {
      this.checkKeyType(var1);
      return (CompositeData)this.dataMap.get(Arrays.asList(var1));
   }

   public Object put(Object var1, Object var2) {
      this.internalPut((CompositeData)var2);
      return var2;
   }

   public void put(CompositeData var1) {
      this.internalPut(var1);
   }

   private CompositeData internalPut(CompositeData var1) {
      List var2 = this.checkValueAndIndex(var1);
      return (CompositeData)this.dataMap.put(var2, var1);
   }

   public Object remove(Object var1) {
      return this.remove((Object[])((Object[])var1));
   }

   public CompositeData remove(Object[] var1) {
      this.checkKeyType(var1);
      return (CompositeData)this.dataMap.remove(Arrays.asList(var1));
   }

   public void putAll(Map<?, ?> var1) {
      if (var1 != null && var1.size() != 0) {
         CompositeData[] var2;
         try {
            var2 = (CompositeData[])var1.values().toArray(new CompositeData[var1.size()]);
         } catch (ArrayStoreException var4) {
            throw new ClassCastException("Map argument t contains values which are not instances of <tt>CompositeData</tt>");
         }

         this.putAll(var2);
      }
   }

   public void putAll(CompositeData[] var1) {
      if (var1 != null && var1.length != 0) {
         ArrayList var2 = new ArrayList(var1.length + 1);

         int var4;
         for(var4 = 0; var4 < var1.length; ++var4) {
            List var3 = this.checkValueAndIndex(var1[var4]);
            if (var2.contains(var3)) {
               throw new KeyAlreadyExistsException("Argument elements values[" + var4 + "] and values[" + var2.indexOf(var3) + "] have the same indexes, calculated according to this TabularData instance's tabularType.");
            }

            var2.add(var3);
         }

         for(var4 = 0; var4 < var1.length; ++var4) {
            this.dataMap.put(var2.get(var4), var1[var4]);
         }

      }
   }

   public void clear() {
      this.dataMap.clear();
   }

   public int size() {
      return this.dataMap.size();
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public Set<Object> keySet() {
      return this.dataMap.keySet();
   }

   public Collection<Object> values() {
      return (Collection)Util.cast(this.dataMap.values());
   }

   public Set<Map.Entry<Object, Object>> entrySet() {
      return (Set)Util.cast(this.dataMap.entrySet());
   }

   public Object clone() {
      try {
         TabularDataSupport var1 = (TabularDataSupport)super.clone();
         var1.dataMap = new HashMap(var1.dataMap);
         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2.toString(), var2);
      }
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         TabularData var2;
         try {
            var2 = (TabularData)var1;
         } catch (ClassCastException var5) {
            return false;
         }

         if (!this.getTabularType().equals(var2.getTabularType())) {
            return false;
         } else if (this.size() != var2.size()) {
            return false;
         } else {
            Iterator var3 = this.dataMap.values().iterator();

            CompositeData var4;
            do {
               if (!var3.hasNext()) {
                  return true;
               }

               var4 = (CompositeData)var3.next();
            } while(var2.containsValue(var4));

            return false;
         }
      }
   }

   public int hashCode() {
      byte var1 = 0;
      int var4 = var1 + this.tabularType.hashCode();

      Object var3;
      for(Iterator var2 = this.values().iterator(); var2.hasNext(); var4 += var3.hashCode()) {
         var3 = var2.next();
      }

      return var4;
   }

   public String toString() {
      return this.getClass().getName() + "(tabularType=" + this.tabularType.toString() + ",contents=" + this.dataMap.toString() + ")";
   }

   private List<?> internalCalculateIndex(CompositeData var1) {
      return Collections.unmodifiableList(Arrays.asList(var1.getAll(this.indexNamesArray)));
   }

   private void checkKeyType(Object[] var1) {
      if (var1 != null && var1.length != 0) {
         if (var1.length != this.indexNamesArray.length) {
            throw new InvalidKeyException("Argument key's length=" + var1.length + " is different from the number of item values, which is " + this.indexNamesArray.length + ", specified for the indexing rows in this TabularData instance.");
         } else {
            for(int var3 = 0; var3 < var1.length; ++var3) {
               OpenType var2 = this.tabularType.getRowType().getType(this.indexNamesArray[var3]);
               if (var1[var3] != null && !var2.isValue(var1[var3])) {
                  throw new InvalidKeyException("Argument element key[" + var3 + "] is not a value for the open type expected for this element of the index, whose name is \"" + this.indexNamesArray[var3] + "\" and whose open type is " + var2);
               }
            }

         }
      } else {
         throw new NullPointerException("Argument key cannot be null or empty.");
      }
   }

   private void checkValueType(CompositeData var1) {
      if (var1 == null) {
         throw new NullPointerException("Argument value cannot be null.");
      } else if (!this.tabularType.getRowType().isValue(var1)) {
         throw new InvalidOpenTypeException("Argument value's composite type [" + var1.getCompositeType() + "] is not assignable to this TabularData instance's row type [" + this.tabularType.getRowType() + "].");
      }
   }

   private List<?> checkValueAndIndex(CompositeData var1) {
      this.checkValueType(var1);
      List var2 = this.internalCalculateIndex(var1);
      if (this.dataMap.containsKey(var2)) {
         throw new KeyAlreadyExistsException("Argument value's index, calculated according to this TabularData instance's tabularType, already refers to a value in this table.");
      } else {
         return var2;
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      List var2 = this.tabularType.getIndexNames();
      int var3 = var2.size();
      SharedSecrets.getJavaOISAccess().checkArray(var1, String[].class, var3);
      this.indexNamesArray = (String[])var2.toArray(new String[var3]);
   }
}
