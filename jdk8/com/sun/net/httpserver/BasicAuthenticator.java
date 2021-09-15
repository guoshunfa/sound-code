package com.sun.net.httpserver;

import java.util.Base64;
import jdk.Exported;

@Exported
public abstract class BasicAuthenticator extends Authenticator {
   protected String realm;

   public BasicAuthenticator(String var1) {
      this.realm = var1;
   }

   public String getRealm() {
      return this.realm;
   }

   public Authenticator.Result authenticate(HttpExchange var1) {
      Headers var2 = var1.getRequestHeaders();
      String var3 = var2.getFirst("Authorization");
      if (var3 == null) {
         Headers var11 = var1.getResponseHeaders();
         var11.set("WWW-Authenticate", "Basic realm=\"" + this.realm + "\"");
         return new Authenticator.Retry(401);
      } else {
         int var4 = var3.indexOf(32);
         if (var4 != -1 && var3.substring(0, var4).equals("Basic")) {
            byte[] var5 = Base64.getDecoder().decode(var3.substring(var4 + 1));
            String var6 = new String(var5);
            int var7 = var6.indexOf(58);
            String var8 = var6.substring(0, var7);
            String var9 = var6.substring(var7 + 1);
            if (this.checkCredentials(var8, var9)) {
               return new Authenticator.Success(new HttpPrincipal(var8, this.realm));
            } else {
               Headers var10 = var1.getResponseHeaders();
               var10.set("WWW-Authenticate", "Basic realm=\"" + this.realm + "\"");
               return new Authenticator.Failure(401);
            }
         } else {
            return new Authenticator.Failure(401);
         }
      }
   }

   public abstract boolean checkCredentials(String var1, String var2);
}
