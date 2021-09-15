package javax.security.sasl;

import java.util.Map;
import javax.security.auth.callback.CallbackHandler;

public interface SaslServerFactory {
   SaslServer createSaslServer(String var1, String var2, String var3, Map<String, ?> var4, CallbackHandler var5) throws SaslException;

   String[] getMechanismNames(Map<String, ?> var1);
}
