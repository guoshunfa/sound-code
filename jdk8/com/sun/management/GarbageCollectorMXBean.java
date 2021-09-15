package com.sun.management;

import jdk.Exported;

@Exported
public interface GarbageCollectorMXBean extends java.lang.management.GarbageCollectorMXBean {
   GcInfo getLastGcInfo();
}
