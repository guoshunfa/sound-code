package com.sun.org.glassfish.external.probe.provider;

import java.util.Iterator;
import java.util.Vector;

public class StatsProviderManager {
   static StatsProviderManagerDelegate spmd;
   static Vector<StatsProviderInfo> toBeRegistered = new Vector();

   private StatsProviderManager() {
   }

   public static boolean register(String configElement, PluginPoint pp, String subTreeRoot, Object statsProvider) {
      return register((PluginPoint)pp, (String)configElement, subTreeRoot, statsProvider, (String)null);
   }

   public static boolean register(PluginPoint pp, String configElement, String subTreeRoot, Object statsProvider, String invokerId) {
      StatsProviderInfo spInfo = new StatsProviderInfo(configElement, pp, subTreeRoot, statsProvider, invokerId);
      return registerStatsProvider(spInfo);
   }

   public static boolean register(String configElement, PluginPoint pp, String subTreeRoot, Object statsProvider, String configLevelStr) {
      return register(configElement, pp, subTreeRoot, statsProvider, configLevelStr, (String)null);
   }

   public static boolean register(String configElement, PluginPoint pp, String subTreeRoot, Object statsProvider, String configLevelStr, String invokerId) {
      StatsProviderInfo spInfo = new StatsProviderInfo(configElement, pp, subTreeRoot, statsProvider, invokerId);
      spInfo.setConfigLevel(configLevelStr);
      return registerStatsProvider(spInfo);
   }

   private static boolean registerStatsProvider(StatsProviderInfo spInfo) {
      if (spmd == null) {
         toBeRegistered.add(spInfo);
         return false;
      } else {
         spmd.register(spInfo);
         return true;
      }
   }

   public static boolean unregister(Object statsProvider) {
      if (spmd != null) {
         spmd.unregister(statsProvider);
         return true;
      } else {
         Iterator var1 = toBeRegistered.iterator();

         while(var1.hasNext()) {
            StatsProviderInfo spInfo = (StatsProviderInfo)var1.next();
            if (spInfo.getStatsProvider() == statsProvider) {
               toBeRegistered.remove(spInfo);
               break;
            }
         }

         return false;
      }
   }

   public static boolean hasListeners(String probeStr) {
      return spmd == null ? false : spmd.hasListeners(probeStr);
   }

   public static void setStatsProviderManagerDelegate(StatsProviderManagerDelegate lspmd) {
      if (lspmd != null) {
         spmd = lspmd;
         Iterator var1 = toBeRegistered.iterator();

         while(var1.hasNext()) {
            StatsProviderInfo spInfo = (StatsProviderInfo)var1.next();
            spmd.register(spInfo);
         }

         toBeRegistered.clear();
      }
   }
}
