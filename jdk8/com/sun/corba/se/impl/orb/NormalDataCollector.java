package com.sun.corba.se.impl.orb;

import java.util.Properties;

public class NormalDataCollector extends DataCollectorBase {
   private String[] args;

   public NormalDataCollector(String[] var1, Properties var2, String var3, String var4) {
      super(var2, var3, var4);
      this.args = var1;
   }

   public boolean isApplet() {
      return false;
   }

   protected void collect() {
      this.checkPropertyDefaults();
      this.findPropertiesFromFile();
      this.findPropertiesFromSystem();
      this.findPropertiesFromProperties();
      this.findPropertiesFromArgs(this.args);
   }
}
