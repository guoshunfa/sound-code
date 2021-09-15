package sun.net.www.protocol.http.spnego;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Arrays;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import sun.net.www.protocol.http.HttpCallerInfo;
import sun.security.jgss.LoginConfigImpl;

public class NegotiateCallbackHandler implements CallbackHandler {
   private String username;
   private char[] password;
   private boolean answered;
   private final HttpCallerInfo hci;

   public NegotiateCallbackHandler(HttpCallerInfo var1) {
      this.hci = var1;
   }

   private void getAnswer() {
      if (!this.answered) {
         this.answered = true;
         if (LoginConfigImpl.HTTP_USE_GLOBAL_CREDS) {
            PasswordAuthentication var1 = Authenticator.requestPasswordAuthentication(this.hci.host, this.hci.addr, this.hci.port, this.hci.protocol, this.hci.prompt, this.hci.scheme, this.hci.url, this.hci.authType);
            if (var1 != null) {
               this.username = var1.getUserName();
               this.password = var1.getPassword();
            }
         }
      }

   }

   public void handle(Callback[] var1) throws UnsupportedCallbackException, IOException {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         Callback var3 = var1[var2];
         if (var3 instanceof NameCallback) {
            this.getAnswer();
            ((NameCallback)var3).setName(this.username);
         } else {
            if (!(var3 instanceof PasswordCallback)) {
               throw new UnsupportedCallbackException(var3, "Call back not supported");
            }

            this.getAnswer();
            ((PasswordCallback)var3).setPassword(this.password);
            if (this.password != null) {
               Arrays.fill(this.password, ' ');
            }
         }
      }

   }
}
