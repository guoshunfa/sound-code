package java.lang.management;

import java.util.List;

public interface PlatformLoggingMXBean extends PlatformManagedObject {
   List<String> getLoggerNames();

   String getLoggerLevel(String var1);

   void setLoggerLevel(String var1, String var2);

   String getParentLoggerName(String var1);
}
