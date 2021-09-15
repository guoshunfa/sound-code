package jdk.xml.internal;

public class JdkXmlFeatures {
   public static final String ORACLE_JAXP_PROPERTY_PREFIX = "http://www.oracle.com/xml/jaxp/properties/";
   public static final String XML_FEATURE_MANAGER = "http://www.oracle.com/xml/jaxp/properties/XmlFeatureManager";
   public static final String ORACLE_FEATURE_SERVICE_MECHANISM = "http://www.oracle.com/feature/use-service-mechanism";
   public static final String ORACLE_ENABLE_EXTENSION_FUNCTION = "http://www.oracle.com/xml/jaxp/properties/enableExtensionFunctions";
   public static final String SP_ENABLE_EXTENSION_FUNCTION = "javax.xml.enableExtensionFunctions";
   public static final String SP_ENABLE_EXTENSION_FUNCTION_SPEC = "jdk.xml.enableExtensionFunctions";
   private final boolean[] featureValues = new boolean[JdkXmlFeatures.XmlFeature.values().length];
   private final JdkXmlFeatures.State[] states = new JdkXmlFeatures.State[JdkXmlFeatures.XmlFeature.values().length];
   boolean secureProcessing;

   public JdkXmlFeatures(boolean secureProcessing) {
      this.secureProcessing = secureProcessing;
      JdkXmlFeatures.XmlFeature[] var2 = JdkXmlFeatures.XmlFeature.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         JdkXmlFeatures.XmlFeature f = var2[var4];
         if (secureProcessing && f.enforced()) {
            this.featureValues[f.ordinal()] = f.enforcedValue();
            this.states[f.ordinal()] = JdkXmlFeatures.State.FSP;
         } else {
            this.featureValues[f.ordinal()] = f.defaultValue();
            this.states[f.ordinal()] = JdkXmlFeatures.State.DEFAULT;
         }
      }

      this.readSystemProperties();
   }

   public void update() {
      this.readSystemProperties();
   }

   public boolean setFeature(String propertyName, JdkXmlFeatures.State state, Object value) {
      int index = this.getIndex(propertyName);
      if (index > -1) {
         this.setFeature(index, state, value);
         return true;
      } else {
         return false;
      }
   }

   public void setFeature(JdkXmlFeatures.XmlFeature feature, JdkXmlFeatures.State state, boolean value) {
      this.setFeature(feature.ordinal(), state, value);
   }

   public boolean getFeature(JdkXmlFeatures.XmlFeature feature) {
      return this.featureValues[feature.ordinal()];
   }

   public boolean getFeature(int index) {
      return this.featureValues[index];
   }

   public void setFeature(int index, JdkXmlFeatures.State state, Object value) {
      boolean temp;
      if (Boolean.class.isAssignableFrom(value.getClass())) {
         temp = (Boolean)value;
      } else {
         temp = Boolean.parseBoolean((String)value);
      }

      this.setFeature(index, state, temp);
   }

   public void setFeature(int index, JdkXmlFeatures.State state, boolean value) {
      if (state.compareTo(this.states[index]) >= 0) {
         this.featureValues[index] = value;
         this.states[index] = state;
      }

   }

   public int getIndex(String propertyName) {
      JdkXmlFeatures.XmlFeature[] var2 = JdkXmlFeatures.XmlFeature.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         JdkXmlFeatures.XmlFeature feature = var2[var4];
         if (feature.equalsPropertyName(propertyName)) {
            return feature.ordinal();
         }
      }

      return -1;
   }

   private void readSystemProperties() {
      JdkXmlFeatures.XmlFeature[] var1 = JdkXmlFeatures.XmlFeature.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         JdkXmlFeatures.XmlFeature feature = var1[var3];
         if (!this.getSystemProperty(feature, feature.systemProperty())) {
            String oldName = feature.systemPropertyOld();
            if (oldName != null) {
               this.getSystemProperty(feature, oldName);
            }
         }
      }

   }

   private boolean getSystemProperty(JdkXmlFeatures.XmlFeature feature, String sysPropertyName) {
      try {
         String value = SecuritySupport.getSystemProperty(sysPropertyName);
         if (value != null && !value.equals("")) {
            this.setFeature(feature, JdkXmlFeatures.State.SYSTEMPROPERTY, Boolean.parseBoolean(value));
            return true;
         } else {
            value = SecuritySupport.readJAXPProperty(sysPropertyName);
            if (value != null && !value.equals("")) {
               this.setFeature(feature, JdkXmlFeatures.State.JAXPDOTPROPERTIES, Boolean.parseBoolean(value));
               return true;
            } else {
               return false;
            }
         }
      } catch (NumberFormatException var4) {
         throw new NumberFormatException("Invalid setting for system property: " + feature.systemProperty());
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

   public static enum XmlFeature {
      ENABLE_EXTENSION_FUNCTION("http://www.oracle.com/xml/jaxp/properties/enableExtensionFunctions", "jdk.xml.enableExtensionFunctions", "http://www.oracle.com/xml/jaxp/properties/enableExtensionFunctions", "javax.xml.enableExtensionFunctions", true, false, true, true),
      JDK_OVERRIDE_PARSER("jdk.xml.overrideDefaultParser", "jdk.xml.overrideDefaultParser", "http://www.oracle.com/feature/use-service-mechanism", "http://www.oracle.com/feature/use-service-mechanism", false, false, true, false);

      private final String name;
      private final String nameSP;
      private final String nameOld;
      private final String nameOldSP;
      private final boolean valueDefault;
      private final boolean valueEnforced;
      private final boolean hasSystem;
      private final boolean enforced;

      private XmlFeature(String name, String nameSP, String nameOld, String nameOldSP, boolean value, boolean valueEnforced, boolean hasSystem, boolean enforced) {
         this.name = name;
         this.nameSP = nameSP;
         this.nameOld = nameOld;
         this.nameOldSP = nameOldSP;
         this.valueDefault = value;
         this.valueEnforced = valueEnforced;
         this.hasSystem = hasSystem;
         this.enforced = enforced;
      }

      boolean equalsPropertyName(String propertyName) {
         return this.name.equals(propertyName) || this.nameOld != null && this.nameOld.equals(propertyName);
      }

      public String apiProperty() {
         return this.name;
      }

      String systemProperty() {
         return this.nameSP;
      }

      String systemPropertyOld() {
         return this.nameOldSP;
      }

      public boolean defaultValue() {
         return this.valueDefault;
      }

      public boolean enforcedValue() {
         return this.valueEnforced;
      }

      boolean hasSystemProperty() {
         return this.hasSystem;
      }

      boolean enforced() {
         return this.enforced;
      }
   }
}
