package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.DataCollector;
import java.applet.Applet;
import java.net.URL;
import java.util.Properties;

public abstract class DataCollectorFactory {
   private DataCollectorFactory() {
   }

   public static DataCollector create(Applet var0, Properties var1, String var2) {
      String var3 = var2;
      if (var0 != null) {
         URL var4 = var0.getCodeBase();
         if (var4 != null) {
            var3 = var4.getHost();
         }
      }

      return new AppletDataCollector(var0, var1, var2, var3);
   }

   public static DataCollector create(String[] var0, Properties var1, String var2) {
      return new NormalDataCollector(var0, var1, var2, var2);
   }

   public static DataCollector create(Properties var0, String var1) {
      return new PropertyOnlyDataCollector(var0, var1, var1);
   }
}
