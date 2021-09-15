package com.sun.org.apache.xerces.internal.util;

public final class SecurityManager {
   private static final int DEFAULT_ENTITY_EXPANSION_LIMIT = 64000;
   private static final int DEFAULT_MAX_OCCUR_NODE_LIMIT = 5000;
   private static final int DEFAULT_ELEMENT_ATTRIBUTE_LIMIT = 10000;
   private int entityExpansionLimit = 64000;
   private int maxOccurLimit = 5000;
   private int fElementAttributeLimit = 10000;

   public SecurityManager() {
      this.readSystemProperties();
   }

   public void setEntityExpansionLimit(int limit) {
      this.entityExpansionLimit = limit;
   }

   public int getEntityExpansionLimit() {
      return this.entityExpansionLimit;
   }

   public void setMaxOccurNodeLimit(int limit) {
      this.maxOccurLimit = limit;
   }

   public int getMaxOccurNodeLimit() {
      return this.maxOccurLimit;
   }

   public int getElementAttrLimit() {
      return this.fElementAttributeLimit;
   }

   public void setElementAttrLimit(int limit) {
      this.fElementAttributeLimit = limit;
   }

   private void readSystemProperties() {
      String value;
      try {
         value = System.getProperty("entityExpansionLimit");
         if (value != null && !value.equals("")) {
            this.entityExpansionLimit = Integer.parseInt(value);
            if (this.entityExpansionLimit < 0) {
               this.entityExpansionLimit = 64000;
            }
         } else {
            this.entityExpansionLimit = 64000;
         }
      } catch (Exception var4) {
      }

      try {
         value = System.getProperty("maxOccurLimit");
         if (value != null && !value.equals("")) {
            this.maxOccurLimit = Integer.parseInt(value);
            if (this.maxOccurLimit < 0) {
               this.maxOccurLimit = 5000;
            }
         } else {
            this.maxOccurLimit = 5000;
         }
      } catch (Exception var3) {
      }

      try {
         value = System.getProperty("elementAttributeLimit");
         if (value != null && !value.equals("")) {
            this.fElementAttributeLimit = Integer.parseInt(value);
            if (this.fElementAttributeLimit < 0) {
               this.fElementAttributeLimit = 10000;
            }
         } else {
            this.fElementAttributeLimit = 10000;
         }
      } catch (Exception var2) {
      }

   }
}
