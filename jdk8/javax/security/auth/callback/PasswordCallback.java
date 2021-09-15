package javax.security.auth.callback;

import java.io.Serializable;

public class PasswordCallback implements Callback, Serializable {
   private static final long serialVersionUID = 2267422647454909926L;
   private String prompt;
   private boolean echoOn;
   private char[] inputPassword;

   public PasswordCallback(String var1, boolean var2) {
      if (var1 != null && var1.length() != 0) {
         this.prompt = var1;
         this.echoOn = var2;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public String getPrompt() {
      return this.prompt;
   }

   public boolean isEchoOn() {
      return this.echoOn;
   }

   public void setPassword(char[] var1) {
      this.inputPassword = var1 == null ? null : (char[])var1.clone();
   }

   public char[] getPassword() {
      return this.inputPassword == null ? null : (char[])this.inputPassword.clone();
   }

   public void clearPassword() {
      if (this.inputPassword != null) {
         for(int var1 = 0; var1 < this.inputPassword.length; ++var1) {
            this.inputPassword[var1] = ' ';
         }
      }

   }
}
