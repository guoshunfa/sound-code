package com.sun.security.auth.login;

import java.net.URI;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import jdk.Exported;

@Exported
public class ConfigFile extends Configuration {
   private final sun.security.provider.ConfigFile.Spi spi;

   public ConfigFile() {
      this.spi = new sun.security.provider.ConfigFile.Spi();
   }

   public ConfigFile(URI var1) {
      this.spi = new sun.security.provider.ConfigFile.Spi(var1);
   }

   public AppConfigurationEntry[] getAppConfigurationEntry(String var1) {
      return this.spi.engineGetAppConfigurationEntry(var1);
   }

   public void refresh() {
      this.spi.engineRefresh();
   }
}
