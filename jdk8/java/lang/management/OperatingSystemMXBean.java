package java.lang.management;

public interface OperatingSystemMXBean extends PlatformManagedObject {
   String getName();

   String getArch();

   String getVersion();

   int getAvailableProcessors();

   double getSystemLoadAverage();
}
