package com.sun.jndi.ldap.sasl;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.RealmChoiceCallback;

final class DefaultCallbackHandler implements CallbackHandler {
   private char[] passwd;
   private String authenticationID;
   private String authRealm;

   DefaultCallbackHandler(String var1, Object var2, String var3) throws IOException {
      this.authenticationID = var1;
      this.authRealm = var3;
      if (var2 instanceof String) {
         this.passwd = ((String)var2).toCharArray();
      } else if (var2 instanceof char[]) {
         this.passwd = (char[])((char[])((char[])var2)).clone();
      } else if (var2 != null) {
         String var4 = new String((byte[])((byte[])var2), "UTF8");
         this.passwd = var4.toCharArray();
      }

   }

   public void handle(Callback[] var1) throws IOException, UnsupportedCallbackException {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (var1[var2] instanceof NameCallback) {
            ((NameCallback)var1[var2]).setName(this.authenticationID);
         } else if (var1[var2] instanceof PasswordCallback) {
            ((PasswordCallback)var1[var2]).setPassword(this.passwd);
         } else if (!(var1[var2] instanceof RealmChoiceCallback)) {
            if (!(var1[var2] instanceof RealmCallback)) {
               throw new UnsupportedCallbackException(var1[var2]);
            }

            RealmCallback var7 = (RealmCallback)var1[var2];
            if (this.authRealm != null) {
               var7.setText(this.authRealm);
            } else {
               String var8 = var7.getDefaultText();
               if (var8 != null) {
                  var7.setText(var8);
               } else {
                  var7.setText("");
               }
            }
         } else {
            String[] var3 = ((RealmChoiceCallback)var1[var2]).getChoices();
            int var4 = 0;
            if (this.authRealm != null && this.authRealm.length() > 0) {
               var4 = -1;

               for(int var5 = 0; var5 < var3.length; ++var5) {
                  if (var3[var5].equals(this.authRealm)) {
                     var4 = var5;
                  }
               }

               if (var4 == -1) {
                  StringBuffer var9 = new StringBuffer();

                  for(int var6 = 0; var6 < var3.length; ++var6) {
                     var9.append(var3[var6] + ",");
                  }

                  throw new IOException("Cannot match 'java.naming.security.sasl.realm' property value, '" + this.authRealm + "' with choices " + var9 + "in RealmChoiceCallback");
               }
            }

            ((RealmChoiceCallback)var1[var2]).setSelectedIndex(var4);
         }
      }

   }

   void clearPassword() {
      if (this.passwd != null) {
         for(int var1 = 0; var1 < this.passwd.length; ++var1) {
            this.passwd[var1] = 0;
         }

         this.passwd = null;
      }

   }

   protected void finalize() throws Throwable {
      this.clearPassword();
   }
}
