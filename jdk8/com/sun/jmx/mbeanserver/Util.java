package com.sun.jmx.mbeanserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class Util {
   public static ObjectName newObjectName(String var0) {
      try {
         return new ObjectName(var0);
      } catch (MalformedObjectNameException var2) {
         throw new IllegalArgumentException(var2);
      }
   }

   static <K, V> Map<K, V> newMap() {
      return new HashMap();
   }

   static <K, V> Map<K, V> newSynchronizedMap() {
      return Collections.synchronizedMap(newMap());
   }

   static <K, V> IdentityHashMap<K, V> newIdentityHashMap() {
      return new IdentityHashMap();
   }

   static <K, V> Map<K, V> newSynchronizedIdentityHashMap() {
      IdentityHashMap var0 = newIdentityHashMap();
      return Collections.synchronizedMap(var0);
   }

   static <K, V> SortedMap<K, V> newSortedMap() {
      return new TreeMap();
   }

   static <K, V> SortedMap<K, V> newSortedMap(Comparator<? super K> var0) {
      return new TreeMap(var0);
   }

   static <K, V> Map<K, V> newInsertionOrderMap() {
      return new LinkedHashMap();
   }

   static <E> Set<E> newSet() {
      return new HashSet();
   }

   static <E> Set<E> newSet(Collection<E> var0) {
      return new HashSet(var0);
   }

   static <E> List<E> newList() {
      return new ArrayList();
   }

   static <E> List<E> newList(Collection<E> var0) {
      return new ArrayList(var0);
   }

   public static <T> T cast(Object var0) {
      return var0;
   }

   public static int hashCode(String[] var0, Object[] var1) {
      int var2 = 0;

      for(int var3 = 0; var3 < var0.length; ++var3) {
         Object var4 = var1[var3];
         int var5;
         if (var4 == null) {
            var5 = 0;
         } else if (var4 instanceof Object[]) {
            var5 = Arrays.deepHashCode((Object[])((Object[])var4));
         } else if (var4.getClass().isArray()) {
            var5 = Arrays.deepHashCode(new Object[]{var4}) - 31;
         } else {
            var5 = var4.hashCode();
         }

         var2 += var0[var3].toLowerCase().hashCode() ^ var5;
      }

      return var2;
   }

   private static boolean wildmatch(String var0, String var1, int var2, int var3, int var4, int var5) {
      int var7 = -1;
      int var6 = -1;

      while(true) {
         label32:
         while(true) {
            if (var4 >= var5) {
               if (var2 == var3) {
                  return true;
               }
               break;
            }

            char var8 = var1.charAt(var4);
            switch(var8) {
            case '*':
               ++var4;
               var7 = var4;
               var6 = var2;
               break;
            case '?':
               if (var2 == var3) {
                  break label32;
               }

               ++var2;
               ++var4;
               break;
            default:
               if (var2 >= var3 || var0.charAt(var2) != var8) {
                  break label32;
               }

               ++var2;
               ++var4;
            }
         }

         if (var7 < 0 || var6 == var3) {
            return false;
         }

         var4 = var7;
         ++var6;
         var2 = var6;
      }
   }

   public static boolean wildmatch(String var0, String var1) {
      return wildmatch(var0, var1, 0, var0.length(), 0, var1.length());
   }
}
