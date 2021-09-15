package sun.management;

import java.util.List;
import sun.management.counter.Counter;

public interface HotspotMemoryMBean {
   List<Counter> getInternalMemoryCounters();
}
