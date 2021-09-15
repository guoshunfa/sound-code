package java.util.logging;

import java.util.List;

public interface LoggingMXBean {
   List<String> getLoggerNames();

   String getLoggerLevel(String var1);

   void setLoggerLevel(String var1, String var2);

   String getParentLoggerName(String var1);
}
