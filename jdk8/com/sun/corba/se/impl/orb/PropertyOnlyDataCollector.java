package com.sun.corba.se.impl.orb;

import java.util.Properties;

public class PropertyOnlyDataCollector extends DataCollectorBase {
   public PropertyOnlyDataCollector(Properties var1, String var2, String var3) {
      super(var1, var2, var3);
   }

   public boolean isApplet() {
      return false;
   }

   protected void collect() {
      this.checkPropertyDefaults();
      this.findPropertiesFromProperties();
   }
}
