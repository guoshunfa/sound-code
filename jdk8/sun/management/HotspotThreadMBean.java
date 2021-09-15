package sun.management;

import java.util.List;
import java.util.Map;
import sun.management.counter.Counter;

public interface HotspotThreadMBean {
   int getInternalThreadCount();

   Map<String, Long> getInternalThreadCpuTimes();

   List<Counter> getInternalThreadingCounters();
}
