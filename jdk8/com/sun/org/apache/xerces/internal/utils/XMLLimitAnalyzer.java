package com.sun.org.apache.xerces.internal.utils;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

public final class XMLLimitAnalyzer {
   private final int[] values = new int[XMLSecurityManager.Limit.values().length];
   private final String[] names = new String[XMLSecurityManager.Limit.values().length];
   private final int[] totalValue = new int[XMLSecurityManager.Limit.values().length];
   private final Map[] caches = new Map[XMLSecurityManager.Limit.values().length];
   private String entityStart;
   private String entityEnd;

   public void addValue(XMLSecurityManager.Limit limit, String entityName, int value) {
      this.addValue(limit.ordinal(), entityName, value);
   }

   public void addValue(int index, String entityName, int value) {
      int[] var10000;
      if (index != XMLSecurityManager.Limit.ENTITY_EXPANSION_LIMIT.ordinal() && index != XMLSecurityManager.Limit.MAX_OCCUR_NODE_LIMIT.ordinal() && index != XMLSecurityManager.Limit.ELEMENT_ATTRIBUTE_LIMIT.ordinal() && index != XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT.ordinal() && index != XMLSecurityManager.Limit.ENTITY_REPLACEMENT_LIMIT.ordinal()) {
         if (index != XMLSecurityManager.Limit.MAX_ELEMENT_DEPTH_LIMIT.ordinal() && index != XMLSecurityManager.Limit.MAX_NAME_LIMIT.ordinal()) {
            Object cache;
            if (this.caches[index] == null) {
               cache = new HashMap(10);
               this.caches[index] = (Map)cache;
            } else {
               cache = this.caches[index];
            }

            int accumulatedValue = value;
            if (((Map)cache).containsKey(entityName)) {
               accumulatedValue = value + (Integer)((Map)cache).get(entityName);
               ((Map)cache).put(entityName, accumulatedValue);
            } else {
               ((Map)cache).put(entityName, value);
            }

            if (accumulatedValue > this.values[index]) {
               this.values[index] = accumulatedValue;
               this.names[index] = entityName;
            }

            if (index == XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT.ordinal() || index == XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT.ordinal()) {
               var10000 = this.totalValue;
               int var10001 = XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT.ordinal();
               var10000[var10001] += value;
            }

         } else {
            this.values[index] = value;
            this.totalValue[index] = value;
         }
      } else {
         var10000 = this.totalValue;
         var10000[index] += value;
      }
   }

   public int getValue(XMLSecurityManager.Limit limit) {
      return this.getValue(limit.ordinal());
   }

   public int getValue(int index) {
      return index == XMLSecurityManager.Limit.ENTITY_REPLACEMENT_LIMIT.ordinal() ? this.totalValue[index] : this.values[index];
   }

   public int getTotalValue(XMLSecurityManager.Limit limit) {
      return this.totalValue[limit.ordinal()];
   }

   public int getTotalValue(int index) {
      return this.totalValue[index];
   }

   public int getValueByIndex(int index) {
      return this.values[index];
   }

   public void startEntity(String name) {
      this.entityStart = name;
   }

   public boolean isTracking(String name) {
      return this.entityStart == null ? false : this.entityStart.equals(name);
   }

   public void endEntity(XMLSecurityManager.Limit limit, String name) {
      this.entityStart = "";
      Map<String, Integer> cache = this.caches[limit.ordinal()];
      if (cache != null) {
         cache.remove(name);
      }

   }

   public void reset(XMLSecurityManager.Limit limit) {
      if (limit.ordinal() == XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT.ordinal()) {
         this.totalValue[limit.ordinal()] = 0;
      } else if (limit.ordinal() == XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT.ordinal()) {
         this.names[limit.ordinal()] = null;
         this.values[limit.ordinal()] = 0;
         this.caches[limit.ordinal()] = null;
         this.totalValue[limit.ordinal()] = 0;
      }

   }

   public void debugPrint(XMLSecurityManager securityManager) {
      Formatter formatter = new Formatter();
      System.out.println((Object)formatter.format("%30s %15s %15s %15s %30s", "Property", "Limit", "Total size", "Size", "Entity Name"));
      XMLSecurityManager.Limit[] var3 = XMLSecurityManager.Limit.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         XMLSecurityManager.Limit limit = var3[var5];
         formatter = new Formatter();
         System.out.println((Object)formatter.format("%30s %15d %15d %15d %30s", limit.name(), securityManager.getLimit(limit), this.totalValue[limit.ordinal()], this.values[limit.ordinal()], this.names[limit.ordinal()]));
      }

   }

   public static enum NameMap {
      ENTITY_EXPANSION_LIMIT("jdk.xml.entityExpansionLimit", "entityExpansionLimit"),
      MAX_OCCUR_NODE_LIMIT("jdk.xml.maxOccurLimit", "maxOccurLimit"),
      ELEMENT_ATTRIBUTE_LIMIT("jdk.xml.elementAttributeLimit", "elementAttributeLimit");

      final String newName;
      final String oldName;

      private NameMap(String newName, String oldName) {
         this.newName = newName;
         this.oldName = oldName;
      }

      String getOldName(String newName) {
         return newName.equals(this.newName) ? this.oldName : null;
      }
   }
}
