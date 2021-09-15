package javax.security.auth.login;

public abstract class ConfigurationSpi {
   protected abstract AppConfigurationEntry[] engineGetAppConfigurationEntry(String var1);

   protected void engineRefresh() {
   }
}
