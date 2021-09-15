package javax.security.sasl;

import java.util.Map;
import javax.security.auth.callback.CallbackHandler;

public interface SaslClientFactory {
   SaslClient createSaslClient(String[] var1, String var2, String var3, String var4, Map<String, ?> var5, CallbackHandler var6) throws SaslException;

   String[] getMechanismNames(Map<String, ?> var1);
}
