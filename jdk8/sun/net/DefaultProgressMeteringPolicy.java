package sun.net;

import java.net.URL;

class DefaultProgressMeteringPolicy implements ProgressMeteringPolicy {
   public boolean shouldMeterInput(URL var1, String var2) {
      return false;
   }

   public int getProgressUpdateThreshold() {
      return 8192;
   }
}
