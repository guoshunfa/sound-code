package com.sun.org.glassfish.external.probe.provider;

public interface StatsProviderManagerDelegate {
   void register(StatsProviderInfo var1);

   void unregister(Object var1);

   boolean hasListeners(String var1);
}
