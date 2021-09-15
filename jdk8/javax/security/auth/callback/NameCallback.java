package javax.security.auth.callback;

import java.io.Serializable;

public class NameCallback implements Callback, Serializable {
   private static final long serialVersionUID = 3770938795909392253L;
   private String prompt;
   private String defaultName;
   private String inputName;

   public NameCallback(String var1) {
      if (var1 != null && var1.length() != 0) {
         this.prompt = var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public NameCallback(String var1, String var2) {
      if (var1 != null && var1.length() != 0 && var2 != null && var2.length() != 0) {
         this.prompt = var1;
         this.defaultName = var2;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public String getPrompt() {
      return this.prompt;
   }

   public String getDefaultName() {
      return this.defaultName;
   }

   public void setName(String var1) {
      this.inputName = var1;
   }

   public String getName() {
      return this.inputName;
   }
}
