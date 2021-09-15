package java.security;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

public abstract class AuthProvider extends Provider {
   private static final long serialVersionUID = 4197859053084546461L;

   protected AuthProvider(String var1, double var2, String var4) {
      super(var1, var2, var4);
   }

   public abstract void login(Subject var1, CallbackHandler var2) throws LoginException;

   public abstract void logout() throws LoginException;

   public abstract void setCallbackHandler(CallbackHandler var1);
}
