package javax.security.sasl;

import java.io.Serializable;
import javax.security.auth.callback.Callback;

public class AuthorizeCallback implements Callback, Serializable {
   private String authenticationID;
   private String authorizationID;
   private String authorizedID;
   private boolean authorized;
   private static final long serialVersionUID = -2353344186490470805L;

   public AuthorizeCallback(String var1, String var2) {
      this.authenticationID = var1;
      this.authorizationID = var2;
   }

   public String getAuthenticationID() {
      return this.authenticationID;
   }

   public String getAuthorizationID() {
      return this.authorizationID;
   }

   public boolean isAuthorized() {
      return this.authorized;
   }

   public void setAuthorized(boolean var1) {
      this.authorized = var1;
   }

   public String getAuthorizedID() {
      if (!this.authorized) {
         return null;
      } else {
         return this.authorizedID == null ? this.authorizationID : this.authorizedID;
      }
   }

   public void setAuthorizedID(String var1) {
      this.authorizedID = var1;
   }
}
