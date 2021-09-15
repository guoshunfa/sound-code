package java.lang.management;

public interface MemoryManagerMXBean extends PlatformManagedObject {
   String getName();

   boolean isValid();

   String[] getMemoryPoolNames();
}
