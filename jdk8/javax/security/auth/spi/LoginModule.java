package javax.security.auth.spi;

import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

public interface LoginModule {
   void initialize(Subject var1, CallbackHandler var2, Map<String, ?> var3, Map<String, ?> var4);

   boolean login() throws LoginException;

   boolean commit() throws LoginException;

   boolean abort() throws LoginException;

   boolean logout() throws LoginException;
}
