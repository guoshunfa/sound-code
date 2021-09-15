package com.sun.corba.se.impl.orb;

import java.applet.Applet;
import java.util.Properties;

public class AppletDataCollector extends DataCollectorBase {
   private Applet applet;

   AppletDataCollector(Applet var1, Properties var2, String var3, String var4) {
      super(var2, var3, var4);
      this.applet = var1;
   }

   public boolean isApplet() {
      return true;
   }

   protected void collect() {
      this.checkPropertyDefaults();
      this.findPropertiesFromFile();
      this.findPropertiesFromProperties();
      this.findPropertiesFromApplet(this.applet);
   }
}
