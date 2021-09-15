package com.sun.org.apache.xalan.internal.utils;

public abstract class FeaturePropertyBase {
   String[] values = null;
   FeaturePropertyBase.State[] states;

   public FeaturePropertyBase() {
      this.states = new FeaturePropertyBase.State[]{FeaturePropertyBase.State.DEFAULT, FeaturePropertyBase.State.DEFAULT};
   }

   public void setValue(Enum property, FeaturePropertyBase.State state, String value) {
      if (state.compareTo(this.states[property.ordinal()]) >= 0) {
         this.values[property.ordinal()] = value;
         this.states[property.ordinal()] = state;
      }

   }

   public void setValue(int index, FeaturePropertyBase.State state, String value) {
      if (state.compareTo(this.states[index]) >= 0) {
         this.values[index] = value;
         this.states[index] = state;
      }

   }

   public boolean setValue(String propertyName, FeaturePropertyBase.State state, Object value) {
      int index = this.getIndex(propertyName);
      if (index > -1) {
         this.setValue(index, state, (String)value);
         return true;
      } else {
         return false;
      }
   }

   public boolean setValue(String propertyName, FeaturePropertyBase.State state, boolean value) {
      int index = this.getIndex(propertyName);
      if (index > -1) {
         if (value) {
            this.setValue(index, state, "true");
         } else {
            this.setValue(index, state, "false");
         }

         return true;
      } else {
         return false;
      }
   }

   public String getValue(Enum property) {
      return this.values[property.ordinal()];
   }

   public String getValue(String property) {
      int index = this.getIndex(property);
      return index > -1 ? this.getValueByIndex(index) : null;
   }

   public String getValueAsString(String propertyName) {
      int index = this.getIndex(propertyName);
      return index > -1 ? this.getValueByIndex(index) : null;
   }

   public String getValueByIndex(int index) {
      return this.values[index];
   }

   public abstract int getIndex(String var1);

   public <E extends Enum<E>> int getIndex(Class<E> property, String propertyName) {
      Enum[] var3 = (Enum[])property.getEnumConstants();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Enum<E> enumItem = var3[var5];
         if (enumItem.toString().equals(propertyName)) {
            return enumItem.ordinal();
         }
      }

      return -1;
   }

   void getSystemProperty(Enum property, String systemProperty) {
      try {
         String value = SecuritySupport.getSystemProperty(systemProperty);
         if (value != null) {
            this.values[property.ordinal()] = value;
            this.states[property.ordinal()] = FeaturePropertyBase.State.SYSTEMPROPERTY;
            return;
         }

         value = SecuritySupport.readJAXPProperty(systemProperty);
         if (value != null) {
            this.values[property.ordinal()] = value;
            this.states[property.ordinal()] = FeaturePropertyBase.State.JAXPDOTPROPERTIES;
         }
      } catch (NumberFormatException var4) {
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
