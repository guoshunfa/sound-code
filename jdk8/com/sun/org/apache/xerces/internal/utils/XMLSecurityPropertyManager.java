package com.sun.org.apache.xerces.internal.utils;

public final class XMLSecurityPropertyManager {
   private final String[] values;
   private XMLSecurityPropertyManager.State[] states;

   public XMLSecurityPropertyManager() {
      this.states = new XMLSecurityPropertyManager.State[]{XMLSecurityPropertyManager.State.DEFAULT, XMLSecurityPropertyManager.State.DEFAULT};
      this.values = new String[XMLSecurityPropertyManager.Property.values().length];
      XMLSecurityPropertyManager.Property[] var1 = XMLSecurityPropertyManager.Property.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         XMLSecurityPropertyManager.Property property = var1[var3];
         this.values[property.ordinal()] = property.defaultValue();
      }

      this.readSystemProperties();
   }

   public boolean setValue(String propertyName, XMLSecurityPropertyManager.State state, Object value) {
      int index = this.getIndex(propertyName);
      if (index > -1) {
         this.setValue(index, state, (String)value);
         return true;
      } else {
         return false;
      }
   }

   public void setValue(XMLSecurityPropertyManager.Property property, XMLSecurityPropertyManager.State state, String value) {
      if (state.compareTo(this.states[property.ordinal()]) >= 0) {
         this.values[property.ordinal()] = value;
         this.states[property.ordinal()] = state;
      }

   }

   public void setValue(int index, XMLSecurityPropertyManager.State state, String value) {
      if (state.compareTo(this.states[index]) >= 0) {
         this.values[index] = value;
         this.states[index] = state;
      }

   }

   public String getValue(String propertyName) {
      int index = this.getIndex(propertyName);
      return index > -1 ? this.getValueByIndex(index) : null;
   }

   public String getValue(XMLSecurityPropertyManager.Property property) {
      return this.values[property.ordinal()];
   }

   public String getValueByIndex(int index) {
      return this.values[index];
   }

   public int getIndex(String propertyName) {
      XMLSecurityPropertyManager.Property[] var2 = XMLSecurityPropertyManager.Property.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         XMLSecurityPropertyManager.Property property = var2[var4];
         if (property.equalsName(propertyName)) {
            return property.ordinal();
         }
      }

      return -1;
   }

   private void readSystemProperties() {
      this.getSystemProperty(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD, "javax.xml.accessExternalDTD");
      this.getSystemProperty(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA, "javax.xml.accessExternalSchema");
   }

   private void getSystemProperty(XMLSecurityPropertyManager.Property property, String systemProperty) {
      try {
         String value = SecuritySupport.getSystemProperty(systemProperty);
         if (value != null) {
            this.values[property.ordinal()] = value;
            this.states[property.ordinal()] = XMLSecurityPropertyManager.State.SYSTEMPROPERTY;
            return;
         }

         value = SecuritySupport.readJAXPProperty(systemProperty);
         if (value != null) {
            this.values[property.ordinal()] = value;
            this.states[property.ordinal()] = XMLSecurityPropertyManager.State.JAXPDOTPROPERTIES;
         }
      } catch (NumberFormatException var4) {
      }

   }

   public static enum Property {
      ACCESS_EXTERNAL_DTD("http://javax.xml.XMLConstants/property/accessExternalDTD", "all"),
      ACCESS_EXTERNAL_SCHEMA("http://javax.xml.XMLConstants/property/accessExternalSchema", "all");

      final String name;
      final String defaultValue;

      private Property(String name, String value) {
         this.name = name;
         this.defaultValue = value;
      }

      public boolean equalsName(String propertyName) {
         return propertyName == null ? false : this.name.equals(propertyName);
      }

      String defaultValue() {
         return this.defaultValue;
      }
   }

   public static enum State {
      DEFAULT,
      FSP,
      JAXPDOTPROPERTIES,
      SYSTEMPROPERTY,
      APIPROPERTY;
   }
}
