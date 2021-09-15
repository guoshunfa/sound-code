package com.sun.org.glassfish.external.probe.provider;

public class StatsProviderInfo {
   private String configElement;
   private PluginPoint pp;
   private String subTreeRoot;
   private Object statsProvider;
   private String configLevelStr;
   private final String invokerId;

   public StatsProviderInfo(String configElement, PluginPoint pp, String subTreeRoot, Object statsProvider) {
      this(configElement, pp, subTreeRoot, statsProvider, (String)null);
   }

   public StatsProviderInfo(String configElement, PluginPoint pp, String subTreeRoot, Object statsProvider, String invokerId) {
      this.configLevelStr = null;
      this.configElement = configElement;
      this.pp = pp;
      this.subTreeRoot = subTreeRoot;
      this.statsProvider = statsProvider;
      this.invokerId = invokerId;
   }

   public String getConfigElement() {
      return this.configElement;
   }

   public PluginPoint getPluginPoint() {
      return this.pp;
   }

   public String getSubTreeRoot() {
      return this.subTreeRoot;
   }

   public Object getStatsProvider() {
      return this.statsProvider;
   }

   public String getConfigLevel() {
      return this.configLevelStr;
   }

   public void setConfigLevel(String configLevelStr) {
      this.configLevelStr = configLevelStr;
   }

   public String getInvokerId() {
      return this.invokerId;
   }
}
