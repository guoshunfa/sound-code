package jdk.management.cmm;

import java.lang.management.PlatformManagedObject;
import jdk.Exported;

@Exported
public interface SystemResourcePressureMXBean extends PlatformManagedObject {
   int getMemoryPressure();

   void setMemoryPressure(int var1);
}
