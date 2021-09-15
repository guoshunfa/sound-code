package com.sun.management;

import java.io.IOException;
import java.lang.management.PlatformManagedObject;
import java.util.List;
import jdk.Exported;

@Exported
public interface HotSpotDiagnosticMXBean extends PlatformManagedObject {
   void dumpHeap(String var1, boolean var2) throws IOException;

   List<VMOption> getDiagnosticOptions();

   VMOption getVMOption(String var1);

   void setVMOption(String var1, String var2);
}
