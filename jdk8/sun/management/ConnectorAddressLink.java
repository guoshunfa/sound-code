package sun.management;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import sun.management.counter.Counter;
import sun.management.counter.Units;
import sun.management.counter.perf.PerfInstrumentation;
import sun.misc.Perf;

public class ConnectorAddressLink {
   private static final String CONNECTOR_ADDRESS_COUNTER = "sun.management.JMXConnectorServer.address";
   private static final String REMOTE_CONNECTOR_COUNTER_PREFIX = "sun.management.JMXConnectorServer.";
   private static AtomicInteger counter = new AtomicInteger();

   public static void export(String var0) {
      if (var0 != null && var0.length() != 0) {
         Perf var1 = Perf.getPerf();
         var1.createString("sun.management.JMXConnectorServer.address", 1, Units.STRING.intValue(), var0);
      } else {
         throw new IllegalArgumentException("address not specified");
      }
   }

   public static String importFrom(int var0) throws IOException {
      Perf var1 = Perf.getPerf();

      ByteBuffer var2;
      try {
         var2 = var1.attach(var0, "r");
      } catch (IllegalArgumentException var6) {
         throw new IOException(var6.getMessage());
      }

      List var3 = (new PerfInstrumentation(var2)).findByPattern("sun.management.JMXConnectorServer.address");
      Iterator var4 = var3.iterator();
      if (var4.hasNext()) {
         Counter var5 = (Counter)var4.next();
         return (String)var5.getValue();
      } else {
         return null;
      }
   }

   public static void exportRemote(Map<String, String> var0) {
      int var1 = counter.getAndIncrement();
      Perf var2 = Perf.getPerf();
      Iterator var3 = var0.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry var4 = (Map.Entry)var3.next();
         var2.createString("sun.management.JMXConnectorServer." + var1 + "." + (String)var4.getKey(), 1, Units.STRING.intValue(), (String)var4.getValue());
      }

   }

   public static Map<String, String> importRemoteFrom(int var0) throws IOException {
      Perf var1 = Perf.getPerf();

      ByteBuffer var2;
      try {
         var2 = var1.attach(var0, "r");
      } catch (IllegalArgumentException var8) {
         throw new IOException(var8.getMessage());
      }

      List var3 = (new PerfInstrumentation(var2)).getAllCounters();
      HashMap var4 = new HashMap();
      Iterator var5 = var3.iterator();

      while(var5.hasNext()) {
         Counter var6 = (Counter)var5.next();
         String var7 = var6.getName();
         if (var7.startsWith("sun.management.JMXConnectorServer.") && !var7.equals("sun.management.JMXConnectorServer.address")) {
            var4.put(var7, var6.getValue().toString());
         }
      }

      return var4;
   }
}
