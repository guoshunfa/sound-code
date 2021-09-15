package com.sun.org.apache.xalan.internal.utils;

public final class XMLSecurityPropertyManager extends FeaturePropertyBase {
   public XMLSecurityPropertyManager() {
      this.values = new String[XMLSecurityPropertyManager.Property.values().length];
      XMLSecurityPropertyManager.Property[] var1 = XMLSecurityPropertyManager.Property.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         XMLSecurityPropertyManager.Property property = var1[var3];
         this.values[property.ordinal()] = property.defaultValue();
      }

      this.readSystemProperties();
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
      this.getSystemProperty(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_STYLESHEET, "javax.xml.accessExternalStylesheet");
   }

   public static enum Property {
      ACCESS_EXTERNAL_DTD("http://javax.xml.XMLConstants/property/accessExternalDTD", "all"),
      ACCESS_EXTERNAL_STYLESHEET("http://javax.xml.XMLConstants/property/accessExternalStylesheet", "all");

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
}
