package com.sun.org.glassfish.external.amx;

import com.sun.org.glassfish.external.arc.Stability;
import com.sun.org.glassfish.external.arc.Taxonomy;
import javax.management.ObjectName;
import javax.management.remote.JMXServiceURL;

@Taxonomy(
   stability = Stability.UNCOMMITTED
)
public interface BootAMXMBean {
   String BOOT_AMX_OPERATION_NAME = "bootAMX";

   ObjectName bootAMX();

   JMXServiceURL[] getJMXServiceURLs();
}
