package sun.management;

import java.util.List;
import sun.management.counter.Counter;

public interface HotspotRuntimeMBean {
   long getSafepointCount();

   long getTotalSafepointTime();

   long getSafepointSyncTime();

   List<Counter> getInternalRuntimeCounters();
}
