package com.sun.org.apache.xerces.internal.utils;

import com.sun.org.apache.xerces.internal.util.SecurityManager;
import java.util.concurrent.CopyOnWriteArrayList;
import org.xml.sax.SAXException;

public final class XMLSecurityManager {
   private static final int NO_LIMIT = 0;
   private final int[] values;
   private XMLSecurityManager.State[] states;
   boolean secureProcessing;
   private boolean[] isSet;
   private final int indexEntityCountInfo;
   private String printEntityCountInfo;
   private static final CopyOnWriteArrayList<String> printedWarnings = new CopyOnWriteArrayList();

   public XMLSecurityManager() {
      this(false);
   }

   public XMLSecurityManager(boolean secureProcessing) {
      this.indexEntityCountInfo = 10000;
      this.printEntityCountInfo = "";
      this.values = new int[XMLSecurityManager.Limit.values().length];
      this.states = new XMLSecurityManager.State[XMLSecurityManager.Limit.values().length];
      this.isSet = new boolean[XMLSecurityManager.Limit.values().length];
      this.secureProcessing = secureProcessing;
      XMLSecurityManager.Limit[] var2 = XMLSecurityManager.Limit.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         XMLSecurityManager.Limit limit = var2[var4];
         if (secureProcessing) {
            this.values[limit.ordinal()] = limit.secureValue;
            this.states[limit.ordinal()] = XMLSecurityManager.State.FSP;
         } else {
            this.values[limit.ordinal()] = limit.defaultValue();
            this.states[limit.ordinal()] = XMLSecurityManager.State.DEFAULT;
         }
      }

      this.readSystemProperties();
   }

   public void setSecureProcessing(boolean secure) {
      this.secureProcessing = secure;
      XMLSecurityManager.Limit[] var2 = XMLSecurityManager.Limit.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         XMLSecurityManager.Limit limit = var2[var4];
         if (secure) {
            this.setLimit(limit.ordinal(), XMLSecurityManager.State.FSP, limit.secureValue());
         } else {
            this.setLimit(limit.ordinal(), XMLSecurityManager.State.FSP, limit.defaultValue());
         }
      }

   }

   public boolean isSecureProcessing() {
      return this.secureProcessing;
   }

   public boolean setLimit(String propertyName, XMLSecurityManager.State state, Object value) {
      int index = this.getIndex(propertyName);
      if (index > -1) {
         this.setLimit(index, state, value);
         return true;
      } else {
         return false;
      }
   }

   public void setLimit(XMLSecurityManager.Limit limit, XMLSecurityManager.State state, int value) {
      this.setLimit(limit.ordinal(), state, value);
   }

   public void setLimit(int index, XMLSecurityManager.State state, Object value) {
      if (index == 10000) {
         this.printEntityCountInfo = (String)value;
      } else {
         int temp;
         if (Integer.class.isAssignableFrom(value.getClass())) {
            temp = (Integer)value;
         } else {
            temp = Integer.parseInt((String)value);
            if (temp < 0) {
               temp = 0;
            }
         }

         this.setLimit(index, state, temp);
      }

   }

   public void setLimit(int index, XMLSecurityManager.State state, int value) {
      if (index == 10000) {
         this.printEntityCountInfo = "yes";
      } else if (state.compareTo(this.states[index]) >= 0) {
         this.values[index] = value;
         this.states[index] = state;
         this.isSet[index] = true;
      }

   }

   public String getLimitAsString(String propertyName) {
      int index = this.getIndex(propertyName);
      return index > -1 ? this.getLimitValueByIndex(index) : null;
   }

   public int getLimit(XMLSecurityManager.Limit limit) {
      return this.values[limit.ordinal()];
   }

   public String getLimitValueAsString(XMLSecurityManager.Limit limit) {
      return Integer.toString(this.values[limit.ordinal()]);
   }

   public String getLimitValueByIndex(int index) {
      return index == 10000 ? this.printEntityCountInfo : Integer.toString(this.values[index]);
   }

   public XMLSecurityManager.State getState(XMLSecurityManager.Limit limit) {
      return this.states[limit.ordinal()];
   }

   public String getStateLiteral(XMLSecurityManager.Limit limit) {
      return this.states[limit.ordinal()].literal();
   }

   public int getIndex(String propertyName) {
      XMLSecurityManager.Limit[] var2 = XMLSecurityManager.Limit.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         XMLSecurityManager.Limit limit = var2[var4];
         if (limit.equalsAPIPropertyName(propertyName)) {
            return limit.ordinal();
         }
      }

      if (propertyName.equals("http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo")) {
         return 10000;
      } else {
         return -1;
      }
   }

   public boolean isNoLimit(int limit) {
      return limit == 0;
   }

   public boolean isOverLimit(XMLSecurityManager.Limit limit, String entityName, int size, XMLLimitAnalyzer limitAnalyzer) {
      return this.isOverLimit(limit.ordinal(), entityName, size, limitAnalyzer);
   }

   public boolean isOverLimit(int index, String entityName, int size, XMLLimitAnalyzer limitAnalyzer) {
      if (this.values[index] == 0) {
         return false;
      } else if (size > this.values[index]) {
         limitAnalyzer.addValue(index, entityName, size);
         return true;
      } else {
         return false;
      }
   }

   public boolean isOverLimit(XMLSecurityManager.Limit limit, XMLLimitAnalyzer limitAnalyzer) {
      return this.isOverLimit(limit.ordinal(), limitAnalyzer);
   }

   public boolean isOverLimit(int index, XMLLimitAnalyzer limitAnalyzer) {
      if (this.values[index] == 0) {
         return false;
      } else if (index != XMLSecurityManager.Limit.ELEMENT_ATTRIBUTE_LIMIT.ordinal() && index != XMLSecurityManager.Limit.ENTITY_EXPANSION_LIMIT.ordinal() && index != XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT.ordinal() && index != XMLSecurityManager.Limit.ENTITY_REPLACEMENT_LIMIT.ordinal() && index != XMLSecurityManager.Limit.MAX_ELEMENT_DEPTH_LIMIT.ordinal() && index != XMLSecurityManager.Limit.MAX_NAME_LIMIT.ordinal()) {
         return limitAnalyzer.getValue(index) > this.values[index];
      } else {
         return limitAnalyzer.getTotalValue(index) > this.values[index];
      }
   }

   public void debugPrint(XMLLimitAnalyzer limitAnalyzer) {
      if (this.printEntityCountInfo.equals("yes")) {
         limitAnalyzer.debugPrint(this);
      }

   }

   public boolean isSet(int index) {
      return this.isSet[index];
   }

   public boolean printEntityCountInfo() {
      return this.printEntityCountInfo.equals("yes");
   }

   private void readSystemProperties() {
      XMLSecurityManager.Limit[] var1 = XMLSecurityManager.Limit.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         XMLSecurityManager.Limit limit = var1[var3];
         if (!this.getSystemProperty(limit, limit.systemProperty())) {
            XMLSecurityManager.NameMap[] var5 = XMLSecurityManager.NameMap.values();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               XMLSecurityManager.NameMap nameMap = var5[var7];
               String oldName = nameMap.getOldName(limit.systemProperty());
               if (oldName != null) {
                  this.getSystemProperty(limit, oldName);
               }
            }
         }
      }

   }

   public static void printWarning(String parserClassName, String propertyName, SAXException exception) {
      String key = parserClassName + ":" + propertyName;
      if (printedWarnings.addIfAbsent(key)) {
         System.err.println("Warning: " + parserClassName + ": " + exception.getMessage());
      }

   }

   private boolean getSystemProperty(XMLSecurityManager.Limit limit, String sysPropertyName) {
      try {
         String value = SecuritySupport.getSystemProperty(sysPropertyName);
         if (value != null && !value.equals("")) {
            this.values[limit.ordinal()] = Integer.parseInt(value);
            this.states[limit.ordinal()] = XMLSecurityManager.State.SYSTEMPROPERTY;
            return true;
         } else {
            value = SecuritySupport.readJAXPProperty(sysPropertyName);
            if (value != null && !value.equals("")) {
               this.values[limit.ordinal()] = Integer.parseInt(value);
               this.states[limit.ordinal()] = XMLSecurityManager.State.JAXPDOTPROPERTIES;
               return true;
            } else {
               return false;
            }
         }
      } catch (NumberFormatException var4) {
         throw new NumberFormatException("Invalid setting for system property: " + limit.systemProperty());
      }
   }

   public static XMLSecurityManager convert(Object value, XMLSecurityManager securityManager) {
      if (value == null) {
         if (securityManager == null) {
            securityManager = new XMLSecurityManager(true);
         }

         return securityManager;
      } else if (XMLSecurityManager.class.isAssignableFrom(value.getClass())) {
         return (XMLSecurityManager)value;
      } else {
         if (securityManager == null) {
            securityManager = new XMLSecurityManager(true);
         }

         if (SecurityManager.class.isAssignableFrom(value.getClass())) {
            SecurityManager origSM = (SecurityManager)value;
            securityManager.setLimit(XMLSecurityManager.Limit.MAX_OCCUR_NODE_LIMIT, XMLSecurityManager.State.APIPROPERTY, origSM.getMaxOccurNodeLimit());
            securityManager.setLimit(XMLSecurityManager.Limit.ENTITY_EXPANSION_LIMIT, XMLSecurityManager.State.APIPROPERTY, origSM.getEntityExpansionLimit());
            securityManager.setLimit(XMLSecurityManager.Limit.ELEMENT_ATTRIBUTE_LIMIT, XMLSecurityManager.State.APIPROPERTY, origSM.getElementAttrLimit());
         }

         return securityManager;
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

   public static enum Limit {
      ENTITY_EXPANSION_LIMIT("EntityExpansionLimit", "http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", "jdk.xml.entityExpansionLimit", 0, 64000),
      MAX_OCCUR_NODE_LIMIT("MaxOccurLimit", "http://www.oracle.com/xml/jaxp/properties/maxOccurLimit", "jdk.xml.maxOccurLimit", 0, 5000),
      ELEMENT_ATTRIBUTE_LIMIT("ElementAttributeLimit", "http://www.oracle.com/xml/jaxp/properties/elementAttributeLimit", "jdk.xml.elementAttributeLimit", 0, 10000),
      TOTAL_ENTITY_SIZE_LIMIT("TotalEntitySizeLimit", "http://www.oracle.com/xml/jaxp/properties/totalEntitySizeLimit", "jdk.xml.totalEntitySizeLimit", 0, 50000000),
      GENERAL_ENTITY_SIZE_LIMIT("MaxEntitySizeLimit", "http://www.oracle.com/xml/jaxp/properties/maxGeneralEntitySizeLimit", "jdk.xml.maxGeneralEntitySizeLimit", 0, 0),
      PARAMETER_ENTITY_SIZE_LIMIT("MaxEntitySizeLimit", "http://www.oracle.com/xml/jaxp/properties/maxParameterEntitySizeLimit", "jdk.xml.maxParameterEntitySizeLimit", 0, 1000000),
      MAX_ELEMENT_DEPTH_LIMIT("MaxElementDepthLimit", "http://www.oracle.com/xml/jaxp/properties/maxElementDepth", "jdk.xml.maxElementDepth", 0, 0),
      MAX_NAME_LIMIT("MaxXMLNameLimit", "http://www.oracle.com/xml/jaxp/properties/maxXMLNameLimit", "jdk.xml.maxXMLNameLimit", 1000, 1000),
      ENTITY_REPLACEMENT_LIMIT("EntityReplacementLimit", "http://www.oracle.com/xml/jaxp/properties/entityReplacementLimit", "jdk.xml.entityReplacementLimit", 0, 3000000);

      final String key;
      final String apiProperty;
      final String systemProperty;
      final int defaultValue;
      final int secureValue;

      private Limit(String key, String apiProperty, String systemProperty, int value, int secureValue) {
         this.key = key;
         this.apiProperty = apiProperty;
         this.systemProperty = systemProperty;
         this.defaultValue = value;
         this.secureValue = secureValue;
      }

      public boolean equalsAPIPropertyName(String propertyName) {
         return propertyName == null ? false : this.apiProperty.equals(propertyName);
      }

      public boolean equalsSystemPropertyName(String propertyName) {
         return propertyName == null ? false : this.systemProperty.equals(propertyName);
      }

      public String key() {
         return this.key;
      }

      public String apiProperty() {
         return this.apiProperty;
      }

      String systemProperty() {
         return this.systemProperty;
      }

      public int defaultValue() {
         return this.defaultValue;
      }

      int secureValue() {
         return this.secureValue;
      }
   }

   public static enum State {
      DEFAULT("default"),
      FSP("FEATURE_SECURE_PROCESSING"),
      JAXPDOTPROPERTIES("jaxp.properties"),
      SYSTEMPROPERTY("system property"),
      APIPROPERTY("property");

      final String literal;

      private State(String literal) {
         this.literal = literal;
      }

      String literal() {
         return this.literal;
      }
   }
}
